package cn.com.streamax.miotp.jni;

public class DeviceInfo {
    public byte[] assetid = new byte[32];
    public int channlnums;
    public byte[] devModel = new byte[32];
    public byte[] deviceid = new byte[36];
    public byte[] deviceip = new byte[32];
    public byte[] devicetype = new byte[32];
    public byte[] devversion = new byte[32];
    public byte[] nodename = new byte[32];
    public byte[] objtype = new byte[32];
    public int onlinestatus;
    public int orgid;
    public byte[] regtime = new byte[32];
    public byte[] tamip = new byte[32];
    public byte[] vehicleid = new byte[32];

    public String toString() {
        return "DeviceInfo [deviceid=" + new String(this.deviceid).trim() + ", devicetype=" + new String(this.devicetype).trim() + ", deviceip=" + new String(this.deviceip).trim() + ", vehicleid=" + new String(this.vehicleid).trim() + ", nodename=" + new String(this.nodename).trim() + ", objtype=" + new String(this.objtype).trim() + ", regtime=" + new String(this.regtime).trim() + ", orgid=" + this.orgid + ", assetid=" + new String(this.assetid).trim() + ", tamip=" + new String(this.tamip).trim() + ", devversion=" + new String(this.devversion).trim() + ", channlnums=" + this.channlnums + ", onlinestatus=" + this.onlinestatus + ", devModel=" + new String(this.devModel).trim() + "]";
    }
}
