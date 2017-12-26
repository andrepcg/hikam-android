package cn.com.streamax.miotp.p2p.jni;

import java.util.Arrays;

public class P2pOrgInfo {
    public int obj_org_id;
    public byte[] obj_org_name = new byte[32];
    public int parent_id;

    public String toString() {
        return "P2pOrgInfo [obj_org_id=" + this.obj_org_id + ", parent_id=" + this.parent_id + ", obj_org_name=" + Arrays.toString(this.obj_org_name) + "]";
    }
}
