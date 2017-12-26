package cn.com.streamax.miotp.p2p.jni;

import java.io.Serializable;

public class P2pStreamDescr implements Serializable {
    private static final long serialVersionUID = 1;
    public int AudioBitsRate;
    public int AudioSampleBits;
    public int AudioSampleFreq;
    public int VideoBitsRate;
    public int VideoFrameRate;
    public int VideoHeight;
    public int VideoWidth;

    public String toString() {
        return "P2pStreamDescr [VideoFrameRate=" + this.VideoFrameRate + ", VideoBitsRate=" + this.VideoBitsRate + ", VideoWidth=" + this.VideoWidth + ", VideoHeight=" + this.VideoHeight + ", AudioBitsRate=" + this.AudioBitsRate + ", AudioSampleFreq=" + this.AudioSampleFreq + ", AudioSampleBits=" + this.AudioSampleBits + "]";
    }
}
