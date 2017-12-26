package cn.com.streamax.miotp.p2p.jni;

import java.util.Arrays;

public class P2pSubuserInfo {
    public int[] obj_id_list;
    public int obj_org_id;
    public String root_user_name;
    public String sub_user_name;
    public String sub_user_password;

    public String toString() {
        return "P2pSubuserInfo [root_user_name=" + this.root_user_name + ", sub_user_name=" + this.sub_user_name + ", sub_user_password=" + this.sub_user_password + ", obj_org_id=" + this.obj_org_id + ", obj_id_list=" + Arrays.toString(this.obj_id_list) + "]";
    }
}
