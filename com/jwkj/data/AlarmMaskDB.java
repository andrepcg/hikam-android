package com.jwkj.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AlarmMaskDB {
    public static final String COLUMN_ACTIVE_USER = "activeUser";
    public static final String COLUMN_ACTIVE_USER_DATA_TYPE = "varchar";
    public static final String COLUMN_DEVICEID = "deviceId";
    public static final String COLUMN_DEVICEID_DATA_TYPE = "varchar";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_ID_DATA_TYPE = "integer PRIMARY KEY AUTOINCREMENT";
    public static final String TABLE_NAME = "alarm_mask";
    private SQLiteDatabase mDBStore;

    public AlarmMaskDB(SQLiteDatabase store) {
        this.mDBStore = store;
    }

    public static String getDeleteTableSQLString() {
        return SqlHelper.formDeleteTableSqlString(TABLE_NAME);
    }

    public static String getCreateTableString() {
        HashMap<String, String> columnNameAndType = new HashMap();
        columnNameAndType.put("id", "integer PRIMARY KEY AUTOINCREMENT");
        columnNameAndType.put("deviceId", "varchar");
        columnNameAndType.put("activeUser", "varchar");
        return SqlHelper.formCreateTableSqlString(TABLE_NAME, columnNameAndType);
    }

    public long insert(AlarmMask alarmMask) {
        long isResut = -1;
        if (alarmMask != null) {
            ContentValues values = new ContentValues();
            values.put("deviceId", alarmMask.deviceId);
            values.put("activeUser", alarmMask.activeUser);
            try {
                isResut = this.mDBStore.insertOrThrow(TABLE_NAME, "", values);
            } catch (SQLiteConstraintException e) {
                e.printStackTrace();
            }
        }
        return isResut;
    }

    public List<AlarmMask> findByActiveUserId(String activeUserId) {
        List<AlarmMask> lists = new ArrayList();
        Cursor cursor = this.mDBStore.rawQuery("SELECT * FROM alarm_mask WHERE activeUser=?", new String[]{activeUserId});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String deviceId = cursor.getString(cursor.getColumnIndex("deviceId"));
                String activeUser = cursor.getString(cursor.getColumnIndex("activeUser"));
                AlarmMask data = new AlarmMask();
                data.id = id;
                data.deviceId = deviceId;
                data.activeUser = activeUser;
                lists.add(data);
            }
            cursor.close();
        }
        return lists;
    }

    public int deleteByActiveUserAndDeviceId(String activeUserId, String deviceId) {
        return this.mDBStore.delete(TABLE_NAME, "activeUser=? AND deviceId=?", new String[]{activeUserId, deviceId});
    }
}
