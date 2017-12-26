package cn.com.streamax.miotp.p2p.jni;

public class P2pDevInfo {
    public P2pDevAbility dev_ability = null;
    public byte[] dev_name = new byte[32];
    public byte[] dev_serial = new byte[64];
    public int obj_id;
    public int obj_org_id;
    public int onlinestatus;
    public byte[] p2p_license = new byte[64];
    public int status;
    public byte[] update_time = new byte[64];

    public String toString() {
        return "P2pDevInfo [dev_name=" + new String(this.dev_name) + ", dev_serial=" + new String(this.dev_serial) + ", p2p_license=" + new String(this.p2p_license) + ", obj_id=" + this.obj_id + ", obj_org_id=" + this.obj_org_id + ", onlinestatus=" + this.onlinestatus + ", status=" + this.status + ", update_time=" + new String(this.update_time) + ", dev_ability=" + this.dev_ability + "]";
    }
}
