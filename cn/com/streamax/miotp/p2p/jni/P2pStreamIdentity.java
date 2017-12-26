package cn.com.streamax.miotp.p2p.jni;

import java.io.Serializable;

public class P2pStreamIdentity implements Serializable {
    private static final long serialVersionUID = 1;
    public int aud_track;
    public int audio_en;
    public int channel;
    public String devPwd;
    public String devid;
    public int trackid;
}
