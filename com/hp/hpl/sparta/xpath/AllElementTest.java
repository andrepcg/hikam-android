package com.hp.hpl.sparta.xpath;

import org.jboss.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;

public class AllElementTest extends NodeTest {
    static final AllElementTest INSTANCE = new AllElementTest();

    private AllElementTest() {
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public boolean isStringValue() {
        return false;
    }

    public String toString() {
        return WebSocketServerHandshaker.SUB_PROTOCOL_WILDCARD;
    }
}
