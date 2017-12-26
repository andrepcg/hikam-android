package com.jwkj.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class DBDecouple {
    private Context context;
    private String newUserId;
    private String oldUserId;

    public DBDecouple(Context context, String oldUserId, String newUserId) {
        this.context = context;
        this.oldUserId = oldUserId;
        this.newUserId = newUserId;
    }

    public void doAlarmMaskDecouple(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT * FROM alarm_mask", null);
        List<AlarmMask> lists = new ArrayList();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String deviceId = cursor.getString(cursor.getColumnIndex("deviceId"));
                String user = cursor.getString(cursor.getColumnIndex("activeUser"));
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                AlarmMask alarmMask = new AlarmMask();
                alarmMask.deviceId = deviceId;
                alarmMask.activeUser = user;
                alarmMask.id = id;
                lists.add(alarmMask);
            }
            cursor.close();
        }
        db.execSQL("DROP TABLE IF EXISTS alarm_mask");
        db.execSQL(AlarmRecordDB.getCreateTableString());
        for (AlarmMask alarmMask2 : lists) {
            AlarmMaskDB alarmMaskDB = new AlarmMaskDB(db);
            if (this.oldUserId.equalsIgnoreCase(alarmMask2.activeUser)) {
                alarmMask2.activeUser = this.newUserId;
                Log.e("few", "db new" + this.newUserId + " old " + this.oldUserId);
            } else {
                Log.e("few", "db new" + this.newUserId + " old " + this.oldUserId);
            }
            alarmMaskDB.insert(alarmMask2);
        }
    }

    public void doAlarmRecordDecouple(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT * FROM alarm_record", null);
        List<AlarmRecord> lists = new ArrayList();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String deviceId = cursor.getString(cursor.getColumnIndex("deviceId"));
                String user = cursor.getString(cursor.getColumnIndex("activeUser"));
                int type = cursor.getInt(cursor.getColumnIndex(AlarmRecordDB.COLUMN_ALARM_TYPE));
                String time = cursor.getString(cursor.getColumnIndex(AlarmRecordDB.COLUMN_ALARM_TIME));
                int group = cursor.getInt(cursor.getColumnIndex(AlarmRecordDB.COLUMN_ALARM_GROUP));
                int item = cursor.getInt(cursor.getColumnIndex(AlarmRecordDB.COLUMN_ALARM_ITEM));
                String uuid = cursor.getString(cursor.getColumnIndex(AlarmRecordDB.COLUMN_ALARM_UUID));
                AlarmRecord alarmRecord = new AlarmRecord();
                alarmRecord.deviceId = deviceId;
                alarmRecord.activeUser = user;
                alarmRecord.alarmType = type;
                alarmRecord.alarmTime = time;
                alarmRecord.group = group;
                alarmRecord.item = item;
                alarmRecord.uuid = uuid;
                lists.add(alarmRecord);
            }
            cursor.close();
        }
        db.execSQL("DROP TABLE IF EXISTS alarm_record");
        db.execSQL(AlarmRecordDB.getCreateTableString());
        for (AlarmRecord alarmRecord2 : lists) {
            AlarmRecordDB alarmRecordDB = new AlarmRecordDB(db);
            if (this.oldUserId.equalsIgnoreCase(alarmRecord2.activeUser)) {
                alarmRecord2.activeUser = this.newUserId;
                Log.e("few", "dba new" + this.newUserId + " old " + this.oldUserId);
            } else {
                Log.e("few", "dba new" + this.newUserId + " old " + this.oldUserId);
            }
            alarmRecordDB.insert(alarmRecord2);
        }
    }

    public void doContactDecouple(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT * FROM contact", null);
        List<Contact> lists = new ArrayList();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String contactName = cursor.getString(cursor.getColumnIndex(ContactDB.COLUMN_CONTACT_NAME));
                String contactModel = cursor.getString(cursor.getColumnIndex(ContactDB.COLUMN_CONTACT_MODEL));
                String contactId = cursor.getString(cursor.getColumnIndex(ContactDB.COLUMN_CONTACT_ID));
                if (!(contactId.length() == 0 || contactId.charAt(0) == '0')) {
                    String contactPassword = cursor.getString(cursor.getColumnIndex(ContactDB.COLUMN_CONTACT_PASSWORD));
                    int contactType = cursor.getInt(cursor.getColumnIndex(ContactDB.COLUMN_CONTACT_TYPE));
                    int messageCount = cursor.getInt(cursor.getColumnIndex(ContactDB.COLUMN_MESSAGE_COUNT));
                    String activeUser = cursor.getString(cursor.getColumnIndex("activeUser"));
                    String userpwd = cursor.getString(cursor.getColumnIndex(ContactDB.COLUMN_ACTIVE_USERPWD));
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
        db.execSQL("DROP TABLE IF EXISTS contact");
        db.execSQL(ContactDB.getCreateTableString());
        for (Contact contact : lists) {
            ContactDB contactDB = new ContactDB(db);
            Log.e("few", "dba new" + this.newUserId + " old " + this.oldUserId + " acuser" + contact.activeUser);
            if (this.oldUserId.equalsIgnoreCase(contact.activeUser) || contact.activeUser == null) {
                contact.activeUser = this.newUserId;
            }
            contactDB.insert(contact);
        }
    }

    public void doMessageDecouple(SQLiteDatabase db) {
    }

    public void doNearlyTellDecouple(SQLiteDatabase db) {
    }

    public void doSysMessageDecouple(SQLiteDatabase db) {
    }
}
