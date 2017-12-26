package cn.com.streamax.miotp.p2p.jni;

import java.util.Arrays;

public class P2pObjInfo {
    public int obj_id;
    public byte[] obj_number = new byte[64];
    public int obj_org_id;

    public String toString() {
        return "P2pObjInfo [obj_org_id=" + this.obj_org_id + ", obj_id=" + this.obj_id + ", obj_number=" + Arrays.toString(this.obj_number) + "]";
    }
}
