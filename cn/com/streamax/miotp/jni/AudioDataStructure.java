package cn.com.streamax.miotp.jni;

import java.util.Arrays;

public class AudioDataStructure {
    public byte[] buffer;

    public String toString() {
        return "AudioDataStructure [buffer=" + Arrays.toString(this.buffer) + "]";
    }
}
