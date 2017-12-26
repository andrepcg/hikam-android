package com.jwkj.data;

import java.io.Serializable;

public class Contact implements Serializable, Comparable {
    public String activeUser;
    public String contactId;
    public String contactModel;
    public String contactName;
    public String contactPassword;
    public int contactType;
    public int defenceState = 2;
    public int id;
    public boolean isClickGetDefenceState = false;
    public int messageCount;
    public int onLineState = 0;
    public String userPassword;

    public int compareTo(Object arg0) {
        return 0;
    }
}
