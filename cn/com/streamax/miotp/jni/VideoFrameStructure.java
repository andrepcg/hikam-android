package cn.com.streamax.miotp.jni;

public class VideoFrameStructure {
    public int UVStride;
    public int YStride;
    public int channel;
    public int dataType;
    public byte[] deviceId;
    public int height;
    public int[] pRgb;
    public byte[] pU;
    public byte[] pV;
    public byte[] pY;
    public int pts;
    public byte[] vehicleId;
    public int width;

    public String toString() {
        return "VideoFrameStructure [width=" + this.width + ", height=" + this.height + ", YStride=" + this.YStride + ", UVStride=" + this.UVStride + ", pts=" + this.pts + "]";
    }
}
