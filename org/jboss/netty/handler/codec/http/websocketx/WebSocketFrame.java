package org.jboss.netty.handler.codec.http.websocketx;

import org.jboss.netty.buffer.ChannelBuffer;

public abstract class WebSocketFrame {
    private ChannelBuffer binaryData;
    private boolean finalFragment = true;
    private int rsv;

    public ChannelBuffer getBinaryData() {
        return this.binaryData;
    }

    public void setBinaryData(ChannelBuffer binaryData) {
        this.binaryData = binaryData;
    }

    public boolean isFinalFragment() {
        return this.finalFragment;
    }

    public void setFinalFragment(boolean finalFragment) {
        this.finalFragment = finalFragment;
    }

    public int getRsv() {
        return this.rsv;
    }

    public void setRsv(int rsv) {
        this.rsv = rsv;
    }
}
