package com.jwkj.utils;

import com.jwkj.data.Contact;
import java.util.Comparator;

public class ComparatorUserByFilterUser implements Comparator {
    private String searchKey;

    public ComparatorUserByFilterUser(String searchKey) {
        this.searchKey = searchKey;
    }

    public int compare(Object arg1, Object arg2) {
        Contact user2 = (Contact) arg2;
        String account1 = ((Contact) arg1).contactId;
        String account2 = user2.contactId;
        int index1 = account1.indexOf(this.searchKey);
        int index2 = account2.indexOf(this.searchKey);
        if (index1 < index2) {
            return -1;
        }
        if (index1 > index2) {
            return 1;
        }
        return 0;
    }
}
