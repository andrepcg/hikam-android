package cn.com.streamax.miotp.jni;

import java.util.Arrays;

public class LoginResult {
    public AuthTable[] authArr;
    public int count;
    public int result;

    public String toString() {
        return "LoginResult [result=" + this.result + ", count=" + this.count + ", auth=" + Arrays.toString(this.authArr) + "]";
    }
}
