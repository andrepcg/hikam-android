package com.jwkj.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AlarmRecordDB {
    public static final String COLUMN_ACTIVE_USER = "activeUser";
    public static final String COLUMN_ACTIVE_USER_DATA_TYPE = "varchar";
    public static final String COLUMN_ALARM_GROUP = "alarmGroup";
    public static final String COLUMN_ALARM_GROUP_DATA_TYPE = "integer";
    public static final String COLUMN_ALARM_ITEM = "alarmItem";
    public static final String COLUMN_ALARM_ITEM_DATA_TYPE = "integer";
    public static final String COLUMN_ALARM_TIME = "alarmTime";
    public static final String COLUMN_ALARM_TIME_DATA_TYPE = "varchar";
    public static final String COLUMN_ALARM_TYPE = "alarmType";
    public static final String COLUMN_ALARM_TYPE_DATA_TYPE = "integer";
    public static final String COLUMN_ALARM_UUID = "alarmUuid";
    public static final String COLUMN_ALARM_UUID_DATA_TYPE = "varchar";
    public static final String COLUMN_DEVICEID = "deviceId";
    public static final String COLUMN_DEVICEID_DATA_TYPE = "varchar";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_ID_DATA_TYPE = "integer PRIMARY KEY AUTOINCREMENT";
    public static final String TABLE_NAME = "alarm_record";
    private SQLiteDatabase myDatabase;

    public AlarmRecordDB(SQLiteDatabase myDatabase) {
        this.myDatabase = myDatabase;
    }

    public static String getDeleteTableSQLString() {
        return SqlHelper.formDeleteTableSqlString(TABLE_NAME);
    }

    public static String getCreateTableString() {
        HashMap<String, String> columnNameAndType = new HashMap();
        columnNameAndType.put("id", "integer PRIMARY KEY AUTOINCREMENT");
        columnNameAndType.put("deviceId", "varchar");
        columnNameAndType.put("activeUser", "varchar");
        columnNameAndType.put(COLUMN_ALARM_TYPE, "integer");
        columnNameAndType.put(COLUMN_ALARM_TIME, "varchar");
        columnNameAndType.put(COLUMN_ALARM_GROUP, "integer");
        columnNameAndType.put(COLUMN_ALARM_ITEM, "integer");
        columnNameAndType.put(COLUMN_ALARM_UUID, "varchar");
        return SqlHelper.formCreateTableSqlString(TABLE_NAME, columnNameAndType);
    }

    public long insert(AlarmRecord alarmRecord) {
        long isResut = -1;
        if (alarmRecord != null) {
            ContentValues values = new ContentValues();
            values.put("deviceId", alarmRecord.deviceId);
            values.put("activeUser", alarmRecord.activeUser);
            values.put(COLUMN_ALARM_TYPE, Integer.valueOf(alarmRecord.alarmType));
            values.put(COLUMN_ALARM_TIME, alarmRecord.alarmTime);
            values.put(COLUMN_ALARM_GROUP, Integer.valueOf(alarmRecord.group));
            values.put(COLUMN_ALARM_ITEM, Integer.valueOf(alarmRecord.item));
            values.put(COLUMN_ALARM_UUID, alarmRecord.uuid);
            try {
                isResut = this.myDatabase.insertOrThrow(TABLE_NAME, null, values);
            } catch (SQLiteConstraintException e) {
                e.printStackTrace();
            }
        }
        return isResut;
    }

    public List<AlarmRecord> findByActiveUserId(String activeUserId) {
        List<AlarmRecord> lists = new ArrayList();
        Cursor cursor = this.myDatabase.rawQuery("SELECT * FROM alarm_record WHERE activeUser=? order by alarmTime desc ", new String[]{activeUserId});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String deviceId = cursor.getString(cursor.getColumnIndex("deviceId"));
                int alarmType = cursor.getInt(cursor.getColumnIndex(COLUMN_ALARM_TYPE));
                String alarmTime = cursor.getString(cursor.getColumnIndex(COLUMN_ALARM_TIME));
                String activeUser = cursor.getString(cursor.getColumnIndex("activeUser"));
                int group = cursor.getInt(cursor.getColumnIndex(COLUMN_ALARM_GROUP));
                int item = cursor.getInt(cursor.getColumnIndex(COLUMN_ALARM_ITEM));
                String alarmUuid = cursor.getString(cursor.getColumnIndex(COLUMN_ALARM_UUID));
                AlarmRecord data = new AlarmRecord();
                data.id = id;
                data.deviceId = deviceId;
                data.alarmType = alarmType;
                data.alarmTime = alarmTime;
                data.activeUser = activeUser;
                data.group = group;
                data.item = item;
                data.uuid = alarmUuid;
                lists.add(data);
            }
            cursor.close();
        }
        return lists;
    }

    public List<AlarmRecord> findByActiveUserIdAndDeviceId(String activeUserId, String deviceId) {
        List<AlarmRecord> lists = new ArrayList();
        Cursor cursor = this.myDatabase.rawQuery("SELECT * FROM alarm_record WHERE activeUser=? AND deviceId=? order by alarmTime desc ", new String[]{activeUserId, deviceId});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String deviceId1 = cursor.getString(cursor.getColumnIndex("deviceId"));
                int alarmType = cursor.getInt(cursor.getColumnIndex(COLUMN_ALARM_TYPE));
                String alarmTime = cursor.getString(cursor.getColumnIndex(COLUMN_ALARM_TIME));
                String activeUser = cursor.getString(cursor.getColumnIndex("activeUser"));
                int group = cursor.getInt(cursor.getColumnIndex(COLUMN_ALARM_GROUP));
                int item = cursor.getInt(cursor.getColumnIndex(COLUMN_ALARM_ITEM));
                String alarmUuid = cursor.getString(cursor.getColumnIndex(COLUMN_ALARM_UUID));
                AlarmRecord data = new AlarmRecord();
                data.id = id;
                data.deviceId = deviceId1;
                data.alarmType = alarmType;
                data.alarmTime = alarmTime;
                data.activeUser = activeUser;
                data.group = group;
                data.item = item;
                data.uuid = alarmUuid;
                lists.add(data);
            }
            cursor.close();
        }
        return lists;
    }

    public int deleteByActiveUser(String activeUserId) {
        return this.myDatabase.delete(TABLE_NAME, "activeUser=?", new String[]{activeUserId});
    }

    public int deleteById(int id) {
        return this.myDatabase.delete(TABLE_NAME, "id=?", new String[]{String.valueOf(id)});
    }
}
