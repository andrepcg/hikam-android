package org.jboss.netty.handler.codec.marshalling;

import org.jboss.marshalling.Marshaller;
import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.MarshallingConfiguration;
import org.jboss.netty.channel.ChannelHandlerContext;

public class DefaultMarshallerProvider implements MarshallerProvider {
    private final MarshallingConfiguration config;
    private final MarshallerFactory factory;

    public DefaultMarshallerProvider(MarshallerFactory factory, MarshallingConfiguration config) {
        this.factory = factory;
        this.config = config;
    }

    public Marshaller getMarshaller(ChannelHandlerContext ctx) throws Exception {
        return this.factory.createMarshaller(this.config);
    }
}
