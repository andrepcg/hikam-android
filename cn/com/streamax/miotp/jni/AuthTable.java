package cn.com.streamax.miotp.jni;

public class AuthTable {
    public int funcId;
    public int orgId;
    public int role;
    public int value;

    public String toString() {
        return "AuthTable [role=" + this.role + ", orgId=" + this.orgId + ", funcId=" + this.funcId + ", value=" + this.value + "]";
    }
}
