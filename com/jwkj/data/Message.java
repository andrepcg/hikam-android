package com.jwkj.data;

public class Message implements Comparable {
    public String activeUser;
    public String fromId;
    public int id;
    public String msg;
    public String msgFlag;
    public String msgState;
    public String msgTime;
    public String toId;

    public int compareTo(Object o) {
        Message msg = (Message) o;
        if (msg.id > this.id) {
            return -1;
        }
        if (msg.id < this.id) {
            return 1;
        }
        return 0;
    }
}
