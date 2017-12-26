package com.jwkj.data;

public class SysMessage implements Comparable {
    public static final int MESSAGE_STATE_NO_READ = 0;
    public static final int MESSAGE_STATE_READED = 1;
    public static final int MESSAGE_TYPE_ADMIN = 2;
    public String activeUser;
    public int id;
    public String msg;
    public int msgState;
    public int msgType;
    public String msg_en;
    public String msg_time;

    public int compareTo(Object o) {
        Message msg = (Message) o;
        if (msg.id > this.id) {
            return 1;
        }
        if (msg.id < this.id) {
            return -1;
        }
        return 0;
    }
}
