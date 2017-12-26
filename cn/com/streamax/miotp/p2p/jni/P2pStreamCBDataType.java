package cn.com.streamax.miotp.p2p.jni;

public class P2pStreamCBDataType {
    public int data_type;
    public int image_format;
    public int sync_flag;

    public String toString() {
        return "P2pStreamCBDataType [data_type=" + this.data_type + ", image_format=" + this.image_format + ", sync_flag=" + this.sync_flag + "]";
    }
}
