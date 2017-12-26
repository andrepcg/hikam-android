package com.jwkj.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessageDB {
    public static final String COLUMN_ACTIVE_USER = "active_user";
    public static final String COLUMN_ACTIVE_USER_DATA_TYPE = "varchar";
    public static final String COLUMN_FROMID = "fromId";
    public static final String COLUMN_FROMID_DATA_TYPE = "varchar";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_ID_DATA_TYPE = "integer PRIMARY KEY AUTOINCREMENT";
    public static final String COLUMN_MSG = "msg";
    public static final String COLUMN_MSGTIME = "msgTime";
    public static final String COLUMN_MSGTIME_DATA_TYPE = "varchar";
    public static final String COLUMN_MSG_DATA_TYPE = "varchar";
    public static final String COLUMN_MSG_FLAG = "msg_flag";
    public static final String COLUMN_MSG_FLAG_DATA_TYPE = "varchar";
    public static final String COLUMN_MSG_STATE = "msg_state";
    public static final String COLUMN_MSG_STATE_DATA_TYPE = "varchar";
    public static final String COLUMN_TOID = "toId";
    public static final String COLUMN_TOID_DATA_TYPE = "varchar";
    public static final String TABLE_NAME = "message";
    private SQLiteDatabase myDatabase;

    public MessageDB(SQLiteDatabase myDatabase) {
        this.myDatabase = myDatabase;
    }

    public static String getDeleteTableSQLString() {
        return SqlHelper.formDeleteTableSqlString(TABLE_NAME);
    }

    public static String getCreateTableString() {
        HashMap<String, String> columnNameAndType = new HashMap();
        columnNameAndType.put("id", "integer PRIMARY KEY AUTOINCREMENT");
        columnNameAndType.put(COLUMN_FROMID, "varchar");
        columnNameAndType.put(COLUMN_TOID, "varchar");
        columnNameAndType.put("msg", "varchar");
        columnNameAndType.put("msgTime", "varchar");
        columnNameAndType.put("active_user", "varchar");
        columnNameAndType.put(COLUMN_MSG_STATE, "varchar");
        columnNameAndType.put(COLUMN_MSG_FLAG, "varchar");
        return SqlHelper.formCreateTableSqlString(TABLE_NAME, columnNameAndType);
    }

    public long insert(Message msg) {
        long isResut = -1;
        if (msg != null) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_FROMID, msg.fromId);
            values.put(COLUMN_TOID, msg.toId);
            values.put("msg", msg.msg);
            values.put("msgTime", msg.msgTime);
            values.put("active_user", msg.activeUser);
            values.put(COLUMN_MSG_STATE, msg.msgState);
            values.put(COLUMN_MSG_FLAG, msg.msgFlag);
            try {
                isResut = this.myDatabase.insertOrThrow(TABLE_NAME, null, values);
            } catch (SQLiteConstraintException e) {
                e.printStackTrace();
            }
        }
        return isResut;
    }

    public void delete(int msgId) {
        this.myDatabase.delete(TABLE_NAME, "id=?", new String[]{String.valueOf(msgId)});
    }

    public void deleteByActiveUserAndChatId(String activeUserId, String chatId) {
        this.myDatabase.delete(TABLE_NAME, "active_user=? AND toId=?", new String[]{activeUserId, chatId});
        this.myDatabase.delete(TABLE_NAME, "active_user=? AND fromId=?", new String[]{activeUserId, chatId});
    }

    public void update(Message msg) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_FROMID, msg.fromId);
        values.put(COLUMN_TOID, msg.toId);
        values.put("msg", msg.msg);
        values.put("msgTime", msg.msgTime);
        values.put("active_user", msg.activeUser);
        values.put(COLUMN_MSG_STATE, msg.msgState);
        values.put(COLUMN_MSG_FLAG, msg.msgFlag);
        try {
            this.myDatabase.update(TABLE_NAME, values, "id=?", new String[]{String.valueOf(msg.id)});
        } catch (SQLiteConstraintException e) {
            e.printStackTrace();
        }
    }

    public void updateStateByFlag(String msgFlag, String msgState) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_MSG_STATE, msgState);
        values.put(COLUMN_MSG_FLAG, "-1");
        try {
            this.myDatabase.update(TABLE_NAME, values, "msg_flag=?", new String[]{msgFlag});
        } catch (SQLiteConstraintException e) {
            e.printStackTrace();
        }
    }

    public List<Message> findMessageByActiveUserAndChatId(String activeUserId, String chatId) {
        List<Message> lists = new ArrayList();
        if (!chatId.equals(activeUserId)) {
            Cursor cursor = this.myDatabase.rawQuery("SELECT * FROM message WHERE active_user=? AND (fromId=? OR toId=?)", new String[]{activeUserId, chatId, chatId});
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndex("id"));
                    String fromId = cursor.getString(cursor.getColumnIndex(COLUMN_FROMID));
                    String toId = cursor.getString(cursor.getColumnIndex(COLUMN_TOID));
                    String msg = cursor.getString(cursor.getColumnIndex("msg"));
                    String msgTime = cursor.getString(cursor.getColumnIndex("msgTime"));
                    String activeUser = cursor.getString(cursor.getColumnIndex("active_user"));
                    String msgState = cursor.getString(cursor.getColumnIndex(COLUMN_MSG_STATE));
                    String msgFlag = cursor.getString(cursor.getColumnIndex(COLUMN_MSG_FLAG));
                    Message data = new Message();
                    data.id = id;
                    data.fromId = fromId;
                    data.toId = toId;
                    data.msg = msg;
                    data.msgTime = msgTime;
                    data.activeUser = activeUser;
                    data.msgState = msgState;
                    data.msgFlag = msgFlag;
                    lists.add(data);
                }
                cursor.close();
            }
        }
        return lists;
    }
}
