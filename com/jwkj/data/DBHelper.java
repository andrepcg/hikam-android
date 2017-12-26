package com.jwkj.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.jwkj.entity.Account;
import com.jwkj.global.AccountPersist;
import com.jwkj.global.MyApp;
import com.jwkj.global.NpcCommon;
import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private Context context;
    private SQLiteDatabase mDB = null;

    public DBHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    public void onCreate(SQLiteDatabase db) {
        if (this.mDB == null) {
            this.mDB = db;
        }
        try {
            db.execSQL(MessageDB.getCreateTableString());
        } catch (Exception e) {
            Log.e("my", "db existed");
        }
        try {
            db.execSQL(SysMessageDB.getCreateTableString());
        } catch (Exception e2) {
            Log.e("my", "db existed");
        }
        try {
            db.execSQL(AlarmMaskDB.getCreateTableString());
        } catch (Exception e3) {
            Log.e("my", "db existed");
        }
        try {
            db.execSQL(AlarmRecordDB.getCreateTableString());
        } catch (Exception e4) {
            Log.e("my", "db existed");
        }
        try {
            db.execSQL(NearlyTellDB.getCreateTableString());
        } catch (Exception e5) {
            Log.e("my", "db existed");
        }
        try {
            db.execSQL(ContactDB.getCreateTableString());
        } catch (Exception e6) {
            Log.e("my", "db existed");
        }
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Cursor cursor;
        List<Contact> lists;
        String activeUser;
        List<AlarmRecord> lists2;
        if (oldVersion < 10) {
            String recentName = SharedPreferencesManager.getInstance().getData(MyApp.app, SharedPreferencesManager.SP_FILE_GWELL, SharedPreferencesManager.KEY_RECENTNAME);
            if (!recentName.equals("")) {
                if (recentName.charAt(0) != '0') {
                    SharedPreferencesManager.getInstance().putData(MyApp.app, SharedPreferencesManager.SP_FILE_GWELL, SharedPreferencesManager.KEY_RECENTNAME_EMAIL, "0" + recentName);
                } else {
                    SharedPreferencesManager.getInstance().putData(MyApp.app, SharedPreferencesManager.SP_FILE_GWELL, SharedPreferencesManager.KEY_RECENTNAME_EMAIL, recentName);
                }
            }
            SharedPreferencesManager.getInstance().putData(MyApp.app, SharedPreferencesManager.SP_FILE_GWELL, SharedPreferencesManager.KEY_RECENTNAME, "");
            SharedPreferencesManager.getInstance().putRecentLoginType(MyApp.app, 1);
        }
        if (oldVersion < 13) {
            Account account = AccountPersist.getInstance().getActiveAccountInfo(MyApp.app);
            if (account != null) {
                account.three_number = "0" + account.three_number;
                AccountPersist.getInstance().setActiveAccount(MyApp.app, account);
                NpcCommon.mThreeNum = AccountPersist.getInstance().getActiveAccountInfo(MyApp.app).three_number;
            }
        }
        if (oldVersion < 21) {
            db.execSQL("DROP TABLE IF EXISTS message");
            db.execSQL(MessageDB.getCreateTableString());
            db.execSQL("DROP TABLE IF EXISTS sysMsg");
            db.execSQL(SysMessageDB.getCreateTableString());
            db.execSQL("DROP TABLE IF EXISTS allarm_mask");
            db.execSQL(AlarmMaskDB.getCreateTableString());
            db.execSQL("DROP TABLE IF EXISTS alarm_record");
            db.execSQL(AlarmRecordDB.getCreateTableString());
            db.execSQL("DROP TABLE IF EXISTS nearly_tell");
            db.execSQL(NearlyTellDB.getCreateTableString());
            cursor = db.rawQuery("SELECT * FROM contant_friends", null);
            lists = new ArrayList();
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String contactName = cursor.getString(cursor.getColumnIndex(HttpPostBodyUtil.NAME));
                    String contactId = cursor.getString(cursor.getColumnIndex("threeAccount"));
                    String contactPassword = cursor.getString(cursor.getColumnIndex("threePwd"));
                    int contactType = cursor.getInt(cursor.getColumnIndex("device_type"));
                    int messageCount = cursor.getInt(cursor.getColumnIndex("msgCount"));
                    activeUser = cursor.getString(cursor.getColumnIndex("uId"));
                    Contact data = new Contact();
                    data.contactName = contactName;
                    data.contactId = contactId;
                    data.contactPassword = contactPassword;
                    data.contactType = contactType;
                    data.messageCount = messageCount;
                    if (activeUser.charAt(0) != '0') {
                        activeUser = "0" + activeUser;
                    }
                    data.activeUser = activeUser;
                    lists.add(data);
                }
                cursor.close();
            }
            db.execSQL("DROP TABLE IF EXISTS contant_friends");
            db.execSQL(ContactDB.getCreateTableString());
            for (Contact contact : lists) {
                new ContactDB(db).insert(contact);
            }
        }
        if (oldVersion < 22) {
            lists2 = new ArrayList();
            cursor = db.rawQuery("SELECT * FROM alarm_record", null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String deviceId = cursor.getString(cursor.getColumnIndex("deviceId"));
                    int alarmType = cursor.getInt(cursor.getColumnIndex(AlarmRecordDB.COLUMN_ALARM_TYPE));
                    String alarmTime = cursor.getString(cursor.getColumnIndex(AlarmRecordDB.COLUMN_ALARM_TIME));
                    activeUser = cursor.getString(cursor.getColumnIndex("activeUser"));
                    AlarmRecord data2 = new AlarmRecord();
                    data2.deviceId = deviceId;
                    data2.alarmType = alarmType;
                    data2.alarmTime = alarmTime;
                    data2.activeUser = activeUser;
                    data2.group = -1;
                    data2.item = -1;
                    lists2.add(data2);
                }
                cursor.close();
            }
            db.execSQL("DROP TABLE IF EXISTS alarm_record");
            db.execSQL(AlarmRecordDB.getCreateTableString());
            for (AlarmRecord record : lists2) {
                new AlarmRecordDB(db).insert(record);
            }
        }
        if (oldVersion < 23) {
            lists = new ArrayList();
            cursor = db.rawQuery("SELECT * FROM contact", null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    contactName = cursor.getString(cursor.getColumnIndex(ContactDB.COLUMN_CONTACT_NAME));
                    contactId = cursor.getString(cursor.getColumnIndex(ContactDB.COLUMN_CONTACT_ID));
                    contactPassword = cursor.getString(cursor.getColumnIndex(ContactDB.COLUMN_CONTACT_PASSWORD));
                    contactType = cursor.getInt(cursor.getColumnIndex(ContactDB.COLUMN_CONTACT_TYPE));
                    messageCount = cursor.getInt(cursor.getColumnIndex(ContactDB.COLUMN_MESSAGE_COUNT));
                    activeUser = cursor.getString(cursor.getColumnIndex("activeUser"));
                    data = new Contact();
                    data.contactName = contactName;
                    data.contactId = contactId;
                    data.contactPassword = contactPassword;
                    data.contactType = contactType;
                    data.messageCount = messageCount;
                    data.activeUser = activeUser;
                    data.contactModel = "";
                    lists.add(data);
                }
                cursor.close();
            }
            db.execSQL("DROP TABLE IF EXISTS contact");
            db.execSQL(ContactDB.getCreateTableString());
            for (Contact contact2 : lists) {
                new ContactDB(db).insert(contact2);
            }
        }
        if (oldVersion < 24) {
            cursor = db.rawQuery("SELECT * FROM contact", null);
            lists = new ArrayList();
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    contactName = cursor.getString(cursor.getColumnIndex(ContactDB.COLUMN_CONTACT_NAME));
                    String contactModel = cursor.getString(cursor.getColumnIndex(ContactDB.COLUMN_CONTACT_MODEL));
                    contactId = cursor.getString(cursor.getColumnIndex(ContactDB.COLUMN_CONTACT_ID));
                    contactPassword = cursor.getString(cursor.getColumnIndex(ContactDB.COLUMN_CONTACT_PASSWORD));
                    contactType = cursor.getInt(cursor.getColumnIndex(ContactDB.COLUMN_CONTACT_TYPE));
                    messageCount = cursor.getInt(cursor.getColumnIndex(ContactDB.COLUMN_MESSAGE_COUNT));
                    activeUser = cursor.getString(cursor.getColumnIndex("activeUser"));
                    data = new Contact();
                    data.contactName = contactName;
                    data.contactModel = contactModel;
                    data.contactId = contactId;
                    data.contactPassword = contactPassword;
                    data.contactType = contactType;
                    data.messageCount = messageCount;
                    data.userPassword = contactPassword;
                    if (activeUser.charAt(0) != '0') {
                        activeUser = "0" + activeUser;
                    }
                    data.activeUser = activeUser;
                    lists.add(data);
                }
                cursor.close();
            }
            db.execSQL("DROP TABLE IF EXISTS contact");
            db.execSQL(ContactDB.getCreateTableString());
            for (Contact contact22 : lists) {
                new ContactDB(db).insert(contact22);
            }
        }
        if (oldVersion < 25) {
            AlarmRecord alarmRecord;
            cursor = db.rawQuery("SELECT * FROM alarm_record", null);
            lists2 = new ArrayList();
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    deviceId = cursor.getString(cursor.getColumnIndex("deviceId"));
                    String user = cursor.getString(cursor.getColumnIndex("activeUser"));
                    int type = cursor.getInt(cursor.getColumnIndex(AlarmRecordDB.COLUMN_ALARM_TYPE));
                    String time = cursor.getString(cursor.getColumnIndex(AlarmRecordDB.COLUMN_ALARM_TIME));
                    int group = cursor.getInt(cursor.getColumnIndex(AlarmRecordDB.COLUMN_ALARM_GROUP));
                    int item = cursor.getInt(cursor.getColumnIndex(AlarmRecordDB.COLUMN_ALARM_ITEM));
                    alarmRecord = new AlarmRecord();
                    alarmRecord.deviceId = deviceId;
                    alarmRecord.activeUser = user;
                    alarmRecord.alarmType = type;
                    alarmRecord.alarmTime = time;
                    alarmRecord.group = group;
                    alarmRecord.item = item;
                    alarmRecord.uuid = " ";
                    lists2.add(alarmRecord);
                }
                cursor.close();
            }
            db.execSQL("DROP TABLE IF EXISTS alarm_record");
            db.execSQL(AlarmRecordDB.getCreateTableString());
            for (AlarmRecord alarmRecord2 : lists2) {
                new AlarmRecordDB(db).insert(alarmRecord2);
            }
        }
        onCreate(db);
    }
}
