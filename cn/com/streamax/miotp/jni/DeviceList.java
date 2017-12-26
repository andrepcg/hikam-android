package cn.com.streamax.miotp.jni;

import java.util.Arrays;

public class DeviceList {
    public int count;
    public DeviceInfo[] data;

    public String toString() {
        return "DeviceList [count=" + this.count + ", data=" + Arrays.toString(this.data) + "]";
    }
}
