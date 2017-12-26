package cn.com.streamax.miotp.p2p.jni;

public class P2pClientID {
    public String account;
    public int clientType;
    public String passwd;

    public String toString() {
        return "P2pClientID [clientType=" + this.clientType + ", account=" + this.account + ", passwd=" + this.passwd + "]";
    }
}
