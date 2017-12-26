package org.jboss.netty.handler.codec.marshalling;

import org.jboss.marshalling.Marshaller;
import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.MarshallingConfiguration;
import org.jboss.netty.channel.ChannelHandlerContext;

public class ThreadLocalMarshallerProvider implements MarshallerProvider {
    private final MarshallingConfiguration config;
    private final MarshallerFactory factory;
    private final ThreadLocal<Marshaller> marshallers = new ThreadLocal();

    public ThreadLocalMarshallerProvider(MarshallerFactory factory, MarshallingConfiguration config) {
        this.factory = factory;
        this.config = config;
    }

    public Marshaller getMarshaller(ChannelHandlerContext ctx) throws Exception {
        Marshaller marshaller = (Marshaller) this.marshallers.get();
        if (marshaller != null) {
            return marshaller;
        }
        marshaller = this.factory.createMarshaller(this.config);
        this.marshallers.set(marshaller);
        return marshaller;
    }
}
