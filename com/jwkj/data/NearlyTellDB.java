package com.jwkj.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NearlyTellDB {
    public static final String COLUMN_ACTIVE_USER = "activeUser";
    public static final String COLUMN_ACTIVE_USER_DATA_TYPE = "varchar";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_ID_DATA_TYPE = "integer PRIMARY KEY AUTOINCREMENT";
    public static final String COLUMN_TELL_ID = "tellId";
    public static final String COLUMN_TELL_ID_DATA_TYPE = "varchar";
    public static final String COLUMN_TELL_STATE = "tellState";
    public static final String COLUMN_TELL_STATE_DATA_TYPE = "varchar";
    public static final String COLUMN_TELL_TIME = "tellTime";
    public static final String COLUMN_TELL_TIME_DATA_TYPE = "varchar";
    public static final String COLUMN_TELL_TYPE = "tellType";
    public static final String COLUMN_TELL_TYPE_DATA_TYPE = "varchar";
    public static final String TABLE_NAME = "nearly_tell";
    private SQLiteDatabase mDBStore;

    public NearlyTellDB(SQLiteDatabase store) {
        this.mDBStore = store;
    }

    public static String getDeleteTableSQLString() {
        return SqlHelper.formDeleteTableSqlString(TABLE_NAME);
    }

    public static String getCreateTableString() {
        HashMap<String, String> columnNameAndType = new HashMap();
        columnNameAndType.put("id", "integer PRIMARY KEY AUTOINCREMENT");
        columnNameAndType.put(COLUMN_TELL_ID, "varchar");
        columnNameAndType.put(COLUMN_TELL_STATE, "varchar");
        columnNameAndType.put(COLUMN_TELL_TYPE, "varchar");
        columnNameAndType.put(COLUMN_TELL_TIME, "varchar");
        columnNameAndType.put("activeUser", "varchar");
        return SqlHelper.formCreateTableSqlString(TABLE_NAME, columnNameAndType);
    }

    public long insert(NearlyTell nearlyTell) {
        long isResut = -1;
        if (nearlyTell != null) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_TELL_ID, nearlyTell.tellId);
            values.put(COLUMN_TELL_STATE, Integer.valueOf(nearlyTell.tellState));
            values.put(COLUMN_TELL_TYPE, Integer.valueOf(nearlyTell.tellType));
            values.put(COLUMN_TELL_TIME, nearlyTell.tellTime);
            values.put("activeUser", nearlyTell.activeUser);
            try {
                isResut = this.mDBStore.insertOrThrow(TABLE_NAME, null, values);
            } catch (SQLiteConstraintException e) {
                e.printStackTrace();
            }
        }
        return isResut;
    }

    public List<NearlyTell> findByActiveUserId(String activeUserId) {
        List<NearlyTell> lists = new ArrayList();
        Cursor cursor = this.mDBStore.rawQuery("SELECT * FROM nearly_tell WHERE activeUser=?", new String[]{activeUserId});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String tellId = cursor.getString(cursor.getColumnIndex(COLUMN_TELL_ID));
                int tellType = cursor.getInt(cursor.getColumnIndex(COLUMN_TELL_TYPE));
                int tellState = cursor.getInt(cursor.getColumnIndex(COLUMN_TELL_STATE));
                String tellTime = cursor.getString(cursor.getColumnIndex(COLUMN_TELL_TIME));
                String activeUser = cursor.getString(cursor.getColumnIndex("activeUser"));
                NearlyTell data = new NearlyTell();
                data.id = id;
                data.tellId = tellId;
                data.tellState = tellState;
                data.tellType = tellType;
                data.tellTime = tellTime;
                data.activeUser = activeUser;
                lists.add(data);
            }
            cursor.close();
        }
        return lists;
    }

    public List<NearlyTell> findByActiveUserIdAndTellId(String activeUserId, String tell) {
        List<NearlyTell> lists = new ArrayList();
        Cursor cursor = this.mDBStore.rawQuery("SELECT * FROM nearly_tell WHERE activeUser=? AND tellId=?", new String[]{activeUserId, tell});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String tellId = cursor.getString(cursor.getColumnIndex(COLUMN_TELL_ID));
                int tellType = cursor.getInt(cursor.getColumnIndex(COLUMN_TELL_TYPE));
                int tellState = cursor.getInt(cursor.getColumnIndex(COLUMN_TELL_STATE));
                String tellTime = cursor.getString(cursor.getColumnIndex(COLUMN_TELL_TIME));
                String activeUser = cursor.getString(cursor.getColumnIndex("activeUser"));
                NearlyTell data = new NearlyTell();
                data.id = id;
                data.tellId = tellId;
                data.tellState = tellState;
                data.tellType = tellType;
                data.tellTime = tellTime;
                data.activeUser = activeUser;
                lists.add(data);
            }
            cursor.close();
        }
        return lists;
    }

    public int deleteByActiveUserId(String activeUserId) {
        return this.mDBStore.delete(TABLE_NAME, "activeUser=?", new String[]{activeUserId});
    }

    public int deleteById(int id) {
        return this.mDBStore.delete(TABLE_NAME, "id=?", new String[]{String.valueOf(id)});
    }

    public int deleteByTellId(String tellId) {
        return this.mDBStore.delete(TABLE_NAME, "tellId=?", new String[]{tellId});
    }
}
