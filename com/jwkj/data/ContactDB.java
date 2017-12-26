package com.jwkj.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ContactDB {
    public static final String COLUMN_ACTIVE_USER = "activeUser";
    public static final String COLUMN_ACTIVE_USERPWD = "userPwd";
    public static final String COLUMN_ACTIVE_USERPWD_DATA_TYPE = "varchar";
    public static final String COLUMN_ACTIVE_USER_DATA_TYPE = "varchar";
    public static final String COLUMN_CONTACT_ID = "contactId";
    public static final String COLUMN_CONTACT_ID_DATA_TYPE = "varchar";
    public static final String COLUMN_CONTACT_MODEL = "contactModel";
    public static final String COLUMN_CONTACT_MODEL_DATA_TYPE = "varchar";
    public static final String COLUMN_CONTACT_NAME = "contactName";
    public static final String COLUMN_CONTACT_NAME_DATA_TYPE = "varchar";
    public static final String COLUMN_CONTACT_PASSWORD = "contactPassword";
    public static final String COLUMN_CONTACT_PASSWORD_DATA_TYPE = "varchar";
    public static final String COLUMN_CONTACT_TYPE = "contactType";
    public static final String COLUMN_CONTACT_TYPE_DATA_TYPE = "integer";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_ID_DATA_TYPE = "integer PRIMARY KEY AUTOINCREMENT";
    public static final String COLUMN_MESSAGE_COUNT = "messageCount";
    public static final String COLUMN_MESSAGE_COUNT_DATA_TYPE = "integer";
    public static final String TABLE_NAME = "contact";
    private SQLiteDatabase myDatabase;

    public ContactDB(SQLiteDatabase myDatabase) {
        this.myDatabase = myDatabase;
    }

    public static String getDeleteTableSQLString() {
        return SqlHelper.formDeleteTableSqlString(TABLE_NAME);
    }

    public static String getCreateTableString() {
        HashMap<String, String> columnNameAndType = new HashMap();
        columnNameAndType.put("id", "integer PRIMARY KEY AUTOINCREMENT");
        columnNameAndType.put(COLUMN_CONTACT_NAME, "varchar");
        columnNameAndType.put(COLUMN_CONTACT_MODEL, "varchar");
        columnNameAndType.put(COLUMN_CONTACT_ID, "varchar");
        columnNameAndType.put(COLUMN_CONTACT_PASSWORD, "varchar");
        columnNameAndType.put(COLUMN_CONTACT_TYPE, "integer");
        columnNameAndType.put(COLUMN_MESSAGE_COUNT, "integer");
        columnNameAndType.put("activeUser", "varchar");
        columnNameAndType.put(COLUMN_ACTIVE_USERPWD, "varchar");
        return SqlHelper.formCreateTableSqlString(TABLE_NAME, columnNameAndType);
    }

    public long insert(Contact contact) {
        long resultId = 0;
        if (contact != null) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_CONTACT_NAME, contact.contactName);
            values.put(COLUMN_CONTACT_MODEL, contact.contactModel);
            values.put(COLUMN_CONTACT_ID, contact.contactId);
            values.put(COLUMN_CONTACT_PASSWORD, contact.contactPassword);
            values.put(COLUMN_CONTACT_TYPE, Integer.valueOf(contact.contactType));
            values.put(COLUMN_MESSAGE_COUNT, Integer.valueOf(contact.messageCount));
            values.put("activeUser", contact.activeUser);
            values.put(COLUMN_ACTIVE_USERPWD, contact.userPassword);
            try {
                resultId = this.myDatabase.insertOrThrow(TABLE_NAME, null, values);
            } catch (SQLiteConstraintException e) {
                e.printStackTrace();
            }
        }
        return resultId;
    }

    public void update(Contact contact) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_CONTACT_NAME, contact.contactName);
        values.put(COLUMN_CONTACT_MODEL, contact.contactModel);
        values.put(COLUMN_CONTACT_ID, contact.contactId);
        values.put(COLUMN_CONTACT_PASSWORD, contact.contactPassword);
        values.put(COLUMN_CONTACT_TYPE, Integer.valueOf(contact.contactType));
        values.put(COLUMN_MESSAGE_COUNT, Integer.valueOf(contact.messageCount));
        values.put("activeUser", contact.activeUser);
        values.put(COLUMN_ACTIVE_USERPWD, contact.userPassword);
        try {
            this.myDatabase.update(TABLE_NAME, values, "activeUser=? AND contactId=?", new String[]{contact.activeUser, contact.contactId});
        } catch (SQLiteConstraintException e) {
            e.printStackTrace();
        }
    }

    public List<Contact> findByActiveUserId(String activeUserId) {
        List<Contact> lists = new ArrayList();
        Cursor cursor = this.myDatabase.rawQuery("SELECT * FROM contact WHERE activeUser=? order by contactName", new String[]{activeUserId});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String contactName = cursor.getString(cursor.getColumnIndex(COLUMN_CONTACT_NAME));
                String contactModel = cursor.getString(cursor.getColumnIndex(COLUMN_CONTACT_MODEL));
                String contactId = cursor.getString(cursor.getColumnIndex(COLUMN_CONTACT_ID));
                if (!(contactId.length() == 0 || contactId.charAt(0) == '0')) {
                    String contactPassword = cursor.getString(cursor.getColumnIndex(COLUMN_CONTACT_PASSWORD));
                    int contactType = cursor.getInt(cursor.getColumnIndex(COLUMN_CONTACT_TYPE));
                    int messageCount = cursor.getInt(cursor.getColumnIndex(COLUMN_MESSAGE_COUNT));
                    String activeUser = cursor.getString(cursor.getColumnIndex("activeUser"));
                    String userpwd = cursor.getString(cursor.getColumnIndex(COLUMN_ACTIVE_USERPWD));
                    Contact data = new Contact();
                    data.id = id;
                    data.contactName = contactName;
                    data.contactModel = contactModel;
                    data.contactId = contactId;
                    data.contactPassword = contactPassword;
                    data.contactType = contactType;
                    data.messageCount = messageCount;
                    data.activeUser = activeUser;
                    data.userPassword = userpwd;
                    lists.add(data);
                }
            }
            cursor.close();
        }
        return lists;
    }

    public List<Contact> findByActiveUserIdAndContactId(String activeUserId, String ContactId) {
        List<Contact> lists = new ArrayList();
        Cursor cursor = this.myDatabase.rawQuery("SELECT * FROM contact WHERE activeUser=? AND contactId=?", new String[]{activeUserId, ContactId});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String contactName = cursor.getString(cursor.getColumnIndex(COLUMN_CONTACT_NAME));
                String contactModel = cursor.getString(cursor.getColumnIndex(COLUMN_CONTACT_MODEL));
                String contactId = cursor.getString(cursor.getColumnIndex(COLUMN_CONTACT_ID));
                String contactPassword = cursor.getString(cursor.getColumnIndex(COLUMN_CONTACT_PASSWORD));
                int contactType = cursor.getInt(cursor.getColumnIndex(COLUMN_CONTACT_TYPE));
                int messageCount = cursor.getInt(cursor.getColumnIndex(COLUMN_MESSAGE_COUNT));
                String activeUser = cursor.getString(cursor.getColumnIndex("activeUser"));
                String userpwd = cursor.getString(cursor.getColumnIndex(COLUMN_ACTIVE_USERPWD));
                Contact data = new Contact();
                data.id = id;
                data.contactName = contactName;
                data.contactModel = contactModel;
                data.contactId = contactId;
                data.contactPassword = contactPassword;
                data.contactType = contactType;
                data.messageCount = messageCount;
                data.activeUser = activeUser;
                data.userPassword = userpwd;
                lists.add(data);
            }
            cursor.close();
        }
        return lists;
    }

    public int deleteById(int id) {
        return this.myDatabase.delete(TABLE_NAME, "id=?", new String[]{String.valueOf(id)});
    }

    public int deleteByActiveUserIdAndContactId(String activeUserId, String contactId) {
        return this.myDatabase.delete(TABLE_NAME, "activeUser=? AND contactId=?", new String[]{activeUserId, contactId});
    }
}
