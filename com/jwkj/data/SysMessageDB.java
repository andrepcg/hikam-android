package com.jwkj.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SysMessageDB {
    public static final String COLUMN_ACTIVE_USER = "active_user";
    public static final String COLUMN_ACTIVE_USER_DATA_TYPE = "varchar";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_ID_DATA_TYPE = "integer PRIMARY KEY AUTOINCREMENT";
    public static final String COLUMN_MSG = "msg";
    public static final String COLUMN_MSGSTATE = "msgState";
    public static final String COLUMN_MSGSTATE_DATA_TYPE = "integer";
    public static final String COLUMN_MSGSTYPE = "msgType";
    public static final String COLUMN_MSGSTYPE_DATA_TYPE = "integer";
    public static final String COLUMN_MSGTIME = "msgTime";
    public static final String COLUMN_MSGTIME_DATA_TYPE = "varchar";
    public static final String COLUMN_MSG_DATA_TYPE = "varchar";
    public static final String COLUMN_MSG_EN = "msg_en";
    public static final String COLUMN_MSG_EN_DATA_TYPE = "varchar";
    public static final String TABLE_NAME = "sys_message";
    private SQLiteDatabase myDatabase;

    public SysMessageDB(SQLiteDatabase myDatabase) {
        this.myDatabase = myDatabase;
    }

    public static String getDeleteTableSQLString() {
        return SqlHelper.formDeleteTableSqlString(TABLE_NAME);
    }

    public static String getCreateTableString() {
        HashMap<String, String> columnNameAndType = new HashMap();
        columnNameAndType.put("id", "integer PRIMARY KEY AUTOINCREMENT");
        columnNameAndType.put("msg", "varchar");
        columnNameAndType.put("msgTime", "varchar");
        columnNameAndType.put("active_user", "varchar");
        columnNameAndType.put(COLUMN_MSGSTATE, "integer");
        columnNameAndType.put(COLUMN_MSGSTYPE, "integer");
        columnNameAndType.put(COLUMN_MSG_EN, "varchar");
        return SqlHelper.formCreateTableSqlString(TABLE_NAME, columnNameAndType);
    }

    public long insert(SysMessage msg) {
        long isResut = -1;
        if (msg != null) {
            ContentValues values = new ContentValues();
            values.put("msg", msg.msg);
            values.put(COLUMN_MSG_EN, msg.msg_en);
            values.put("msgTime", msg.msg_time);
            values.put("active_user", msg.activeUser);
            values.put(COLUMN_MSGSTATE, Integer.valueOf(msg.msgState));
            values.put(COLUMN_MSGSTYPE, Integer.valueOf(msg.msgType));
            try {
                isResut = this.myDatabase.insertOrThrow(TABLE_NAME, null, values);
            } catch (SQLiteConstraintException e) {
                e.printStackTrace();
            }
        }
        return isResut;
    }

    public List<SysMessage> findByActiveUserId(String userId) {
        List<SysMessage> lists = new ArrayList();
        Cursor cursor = this.myDatabase.rawQuery("SELECT * FROM sys_message WHERE active_user=?", new String[]{userId});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String msg = cursor.getString(cursor.getColumnIndex("msg"));
                String msg_en = cursor.getString(cursor.getColumnIndex(COLUMN_MSG_EN));
                String msg_time = cursor.getString(cursor.getColumnIndex("msgTime"));
                String activeUser = cursor.getString(cursor.getColumnIndex("active_user"));
                int msgState = cursor.getInt(cursor.getColumnIndex(COLUMN_MSGSTATE));
                int msgType = cursor.getInt(cursor.getColumnIndex(COLUMN_MSGSTYPE));
                SysMessage data = new SysMessage();
                data.id = id;
                data.msg = msg;
                data.msg_en = msg_en;
                data.msg_time = msg_time;
                data.activeUser = activeUser;
                data.msgState = msgState;
                data.msgType = msgType;
                lists.add(data);
            }
            cursor.close();
        }
        return lists;
    }

    public void updateSysMsgState(int id, int state) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_MSGSTATE, Integer.valueOf(state));
        try {
            this.myDatabase.update(TABLE_NAME, values, "id=?", new String[]{String.valueOf(id)});
        } catch (SQLiteConstraintException e) {
            e.printStackTrace();
        }
    }

    public int delete(int id) {
        return this.myDatabase.delete(TABLE_NAME, "id=?", new String[]{String.valueOf(id)});
    }
}
