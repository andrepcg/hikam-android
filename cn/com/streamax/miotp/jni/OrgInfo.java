package cn.com.streamax.miotp.jni;

public class OrgInfo {
    public byte[] idLevel = new byte[32];
    public byte[] orgName = new byte[32];
    public int orgid;
    public int parentid;

    public String toString() {
        return "OrgInfo [orgid=" + this.orgid + ", parentid=" + this.parentid + ", orgName=" + new String(this.orgName).trim() + ", idLevel=" + new String(this.idLevel).trim() + "]";
    }
}
