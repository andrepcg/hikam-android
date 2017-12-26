package com.jwkj.entity;

import java.net.InetAddress;

public class LocalDevice {
    public InetAddress address;
    public String contactId;
    public String contactModel;
    public int flag;
    public int type;

    public String getContactModel() {
        return this.contactModel;
    }

    public void setContactModel(String contactModel) {
        this.contactModel = contactModel;
    }

    public String getContactId() {
        return this.contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public int getFlag() {
        return this.flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public InetAddress getAddress() {
        return this.address;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }

    public boolean equals(Object o) {
        if (((LocalDevice) o).contactId.equals(this.contactId)) {
            return true;
        }
        return false;
    }
}
