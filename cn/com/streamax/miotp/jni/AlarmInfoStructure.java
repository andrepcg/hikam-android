package cn.com.streamax.miotp.jni;

import java.util.Arrays;

public class AlarmInfoStructure {
    public int channel;
    public byte[] happenTime;
    public byte[] peer;
    public int type;

    public String toString() {
        return "AlarmInfoStructure [peer=" + Arrays.toString(this.peer) + ", channel=" + this.channel + ", type=" + this.type + ", happenTime=" + Arrays.toString(this.happenTime) + "]";
    }
}
