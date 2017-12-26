package com.jwkj.data;

import java.io.Serializable;
import java.sql.Timestamp;

public class NearlyTell implements Serializable, Comparable {
    public static final int TELL_STATE_CALL_IN_ACCEPT = 1;
    public static final int TELL_STATE_CALL_IN_REJECT = 0;
    public static final int TELL_STATE_CALL_OUT_ACCEPT = 3;
    public static final int TELL_STATE_CALL_OUT_REJECT = 2;
    public String activeUser;
    public int count;
    public int id;
    public String tellId;
    public int tellState;
    public String tellTime;
    public int tellType;

    public int compareTo(Object o) {
        NearlyTell nearlyTell = (NearlyTell) o;
        Timestamp user1Time = new Timestamp(Long.parseLong(this.tellTime));
        Timestamp user2Time = new Timestamp(Long.parseLong(nearlyTell.tellTime));
        if (user1Time.after(user2Time)) {
            return -1;
        }
        if (user1Time.after(user2Time)) {
            return 0;
        }
        return 1;
    }
}
