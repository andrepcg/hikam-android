package cn.com.streamax.miotp.p2p.jni;

import java.io.Serializable;

public class P2PStreamResource implements Serializable {
    private static final long serialVersionUID = 1;
    public P2pStreamIdentity stream_id;
    public P2pStreamDescr stream_info;
    public int userdata;

    public String toString() {
        return "P2PStreamResource [stream_id=" + this.stream_id + ", stream_info=" + this.stream_info + ", userdata=" + this.userdata + "]";
    }
}
