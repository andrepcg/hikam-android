package org.jboss.netty.handler.codec.serialization;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.StreamCorruptedException;

class CompactObjectInputStream extends ObjectInputStream {
    private final ClassResolver classResolver;

    CompactObjectInputStream(InputStream in, ClassResolver classResolver) throws IOException {
        super(in);
        if (classResolver == null) {
            throw new NullPointerException("classResolver");
        }
        this.classResolver = classResolver;
    }

    protected void readStreamHeader() throws IOException {
        int version = readByte() & 255;
        if (version != 5) {
            throw new StreamCorruptedException("Unsupported version: " + version);
        }
    }

    protected ObjectStreamClass readClassDescriptor() throws IOException, ClassNotFoundException {
        int type = read();
        if (type < 0) {
            throw new EOFException();
        }
        switch (type) {
            case 0:
                return super.readClassDescriptor();
            case 1:
                Class<?> clazz = this.classResolver.resolve(readUTF());
                ObjectStreamClass streamClass = ObjectStreamClass.lookup(clazz);
                if (streamClass == null) {
                    return ObjectStreamClass.lookupAny(clazz);
                }
                return streamClass;
            default:
                throw new StreamCorruptedException("Unexpected class descriptor type: " + type);
        }
    }

    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
        try {
            return this.classResolver.resolve(desc.getName());
        } catch (ClassNotFoundException e) {
            return super.resolveClass(desc);
        }
    }
}
