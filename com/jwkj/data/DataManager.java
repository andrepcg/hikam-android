package com.jwkj.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.util.List;

public class DataManager {
    public static final String DataBaseName = "NpcDatabase.db";
    public static final int DataBaseVersion = 26;
    public static final String TAG = "NpcData";

    public static synchronized List<Message> findMessageByActiveUserAndChatId(Context context, String activeUserId, String chatId) {
        List<Message> list;
        synchronized (DataManager.class) {
            synchronized (DataManager.class) {
                SQLiteDatabase db = new DBHelper(context, DataBaseName, null, 26).getWritableDatabase();
                list = new MessageDB(db).findMessageByActiveUserAndChatId(activeUserId, chatId);
                db.close();
            }
        }
        return list;
    }

    public static synchronized void insertMessage(Context context, Message msg) {
        synchronized (DataManager.class) {
            synchronized (DataManager.class) {
                SQLiteDatabase db = new DBHelper(context, DataBaseName, null, 26).getWritableDatabase();
                new MessageDB(db).insert(msg);
                db.close();
            }
        }
    }

    public static synchronized void clearMessageByActiveUserAndChatId(Context context, String activeUserId, String chatId) {
        synchronized (DataManager.class) {
            synchronized (DataManager.class) {
                SQLiteDatabase db = new DBHelper(context, DataBaseName, null, 26).getWritableDatabase();
                new MessageDB(db).deleteByActiveUserAndChatId(activeUserId, chatId);
                db.close();
            }
        }
    }

    public static synchronized void updateMessageStateByFlag(Context context, String msgFlag, int msgState) {
        synchronized (DataManager.class) {
            synchronized (DataManager.class) {
                SQLiteDatabase db = new DBHelper(context, DataBaseName, null, 26).getWritableDatabase();
                new MessageDB(db).updateStateByFlag(msgFlag, String.valueOf(msgState));
                db.close();
            }
        }
    }

    public static synchronized List<SysMessage> findSysMessageByActiveUser(Context context, String activeUserId) {
        List<SysMessage> lists;
        synchronized (DataManager.class) {
            synchronized (DataManager.class) {
                SQLiteDatabase db = new DBHelper(context, DataBaseName, null, 26).getWritableDatabase();
                lists = new SysMessageDB(db).findByActiveUserId(activeUserId);
                db.close();
            }
        }
        return lists;
    }

    public static synchronized void insertSysMessage(Context context, SysMessage msg) {
        synchronized (DataManager.class) {
            synchronized (DataManager.class) {
                SQLiteDatabase db = new DBHelper(context, DataBaseName, null, 26).getWritableDatabase();
                new SysMessageDB(db).insert(msg);
                db.close();
            }
        }
    }

    public static synchronized void deleteSysMessage(Context context, int id) {
        synchronized (DataManager.class) {
            synchronized (DataManager.class) {
                SQLiteDatabase db = new DBHelper(context, DataBaseName, null, 26).getWritableDatabase();
                new SysMessageDB(db).delete(id);
                db.close();
            }
        }
    }

    public static synchronized void updateSysMessageState(Context context, int id, int state) {
        synchronized (DataManager.class) {
            synchronized (DataManager.class) {
                SQLiteDatabase db = new DBHelper(context, DataBaseName, null, 26).getWritableDatabase();
                new SysMessageDB(db).updateSysMsgState(id, state);
                db.close();
            }
        }
    }

    public static synchronized List<AlarmMask> findAlarmMaskByActiveUser(Context context, String activeUserId) {
        List<AlarmMask> lists;
        synchronized (DataManager.class) {
            synchronized (DataManager.class) {
                SQLiteDatabase db = new DBHelper(context, DataBaseName, null, 26).getWritableDatabase();
                lists = new AlarmMaskDB(db).findByActiveUserId(activeUserId);
                db.close();
            }
        }
        return lists;
    }

    public static synchronized void insertAlarmMask(Context context, AlarmMask alarmMask) {
        synchronized (DataManager.class) {
            synchronized (DataManager.class) {
                SQLiteDatabase db = new DBHelper(context, DataBaseName, null, 26).getWritableDatabase();
                new AlarmMaskDB(db).insert(alarmMask);
                db.close();
            }
        }
    }

    public static synchronized void deleteAlarmMask(Context context, String activeUserId, String deviceId) {
        synchronized (DataManager.class) {
            synchronized (DataManager.class) {
                SQLiteDatabase db = new DBHelper(context, DataBaseName, null, 26).getWritableDatabase();
                new AlarmMaskDB(db).deleteByActiveUserAndDeviceId(activeUserId, deviceId);
                db.close();
            }
        }
    }

    public static synchronized List<AlarmRecord> findAlarmRecordByActiveUser(Context context, String activeUserId) {
        List<AlarmRecord> lists;
        synchronized (DataManager.class) {
            synchronized (DataManager.class) {
                SQLiteDatabase db = new DBHelper(context, DataBaseName, null, 26).getWritableDatabase();
                lists = new AlarmRecordDB(db).findByActiveUserId(activeUserId);
                List<Contact> contacts = new ContactDB(db).findByActiveUserId(activeUserId);
                Log.e("DB", "findAlarmRecordByActiveUser end1:" + System.currentTimeMillis());
                int i = 0;
                while (i < lists.size()) {
                    int j = 0;
                    while (j < contacts.size()) {
                        if (contacts != null && contacts.size() > 0 && ((Contact) contacts.get(j)).contactId.equals(((AlarmRecord) lists.get(i)).deviceId)) {
                            ((AlarmRecord) lists.get(i)).deviceName = ((Contact) contacts.get(j)).contactName;
                            break;
                        }
                        j++;
                    }
                    i++;
                }
                Log.e("DB", "findAlarmRecordByActiveUser end2:" + System.currentTimeMillis());
                db.close();
            }
        }
        return lists;
    }

    public static synchronized List<AlarmRecord> findAlarmRecordByActiveUserAndDeviceId(Context context, String activeUserId, String deviceId) {
        List<AlarmRecord> lists;
        synchronized (DataManager.class) {
            Log.e("DB", "findAlarmRecordByActiveUserAndDeviceId start:" + System.currentTimeMillis());
            synchronized (DataManager.class) {
                SQLiteDatabase db = new DBHelper(context, DataBaseName, null, 26).getWritableDatabase();
                lists = new AlarmRecordDB(db).findByActiveUserIdAndDeviceId(activeUserId, deviceId);
                List<Contact> contacts = new ContactDB(db).findByActiveUserId(activeUserId);
                Log.e("DB", "findAlarmRecordByActiveUserAndDeviceId end1:" + System.currentTimeMillis());
                for (int i = 0; i < lists.size(); i++) {
                    for (int j = 0; j < contacts.size(); j++) {
                        if (contacts != null && contacts.size() > 0) {
                            ((AlarmRecord) lists.get(i)).deviceName = ((Contact) contacts.get(j)).contactName;
                            break;
                        }
                    }
                }
                db.close();
                Log.e("DB", "findAlarmRecordByActiveUserAndDeviceId end2:" + System.currentTimeMillis());
            }
        }
        return lists;
    }

    public static synchronized List<AlarmRecord> findAlarmRecordByActiveUserAndDeviceId2(Context context, String activeUserId, String deviceId, String name) {
        List<AlarmRecord> lists;
        synchronized (DataManager.class) {
            Log.e("DB", "findAlarmRecordByActiveUserAndDeviceId start:" + System.currentTimeMillis());
            synchronized (DataManager.class) {
                SQLiteDatabase db = new DBHelper(context, DataBaseName, null, 26).getWritableDatabase();
                lists = new AlarmRecordDB(db).findByActiveUserIdAndDeviceId(activeUserId, deviceId);
                Log.e("DB", "findAlarmRecordByActiveUserAndDeviceId end1:" + System.currentTimeMillis());
                for (int i = 0; i < lists.size(); i++) {
                    ((AlarmRecord) lists.get(i)).deviceName = name;
                }
                db.close();
                Log.e("DB", "findAlarmRecordByActiveUserAndDeviceId end2:" + System.currentTimeMillis());
            }
        }
        return lists;
    }

    public static synchronized void insertAlarmRecord(Context context, AlarmRecord alarmRecord) {
        synchronized (DataManager.class) {
            synchronized (DataManager.class) {
                SQLiteDatabase db = new DBHelper(context, DataBaseName, null, 26).getWritableDatabase();
                new AlarmRecordDB(db).insert(alarmRecord);
                db.close();
            }
        }
    }

    public static synchronized void deleteAlarmRecordById(Context context, int id) {
        synchronized (DataManager.class) {
            synchronized (DataManager.class) {
                SQLiteDatabase db = new DBHelper(context, DataBaseName, null, 26).getWritableDatabase();
                new AlarmRecordDB(db).deleteById(id);
                db.close();
            }
        }
    }

    public static synchronized void clearAlarmRecord(Context context, String activeUserId) {
        synchronized (DataManager.class) {
            synchronized (DataManager.class) {
                SQLiteDatabase db = new DBHelper(context, DataBaseName, null, 26).getWritableDatabase();
                new AlarmRecordDB(db).deleteByActiveUser(activeUserId);
                db.close();
            }
        }
    }

    public static synchronized List<NearlyTell> findNearlyTellByActiveUser(Context context, String activeUserId) {
        List<NearlyTell> lists;
        synchronized (DataManager.class) {
            synchronized (DataManager.class) {
                SQLiteDatabase db = new DBHelper(context, DataBaseName, null, 26).getWritableDatabase();
                lists = new NearlyTellDB(db).findByActiveUserId(activeUserId);
                db.close();
            }
        }
        return lists;
    }

    public static synchronized List<NearlyTell> findNearlyTellByActiveUserAndTellId(Context context, String activeUserId, String tellId) {
        List<NearlyTell> lists;
        synchronized (DataManager.class) {
            synchronized (DataManager.class) {
                SQLiteDatabase db = new DBHelper(context, DataBaseName, null, 26).getWritableDatabase();
                lists = new NearlyTellDB(db).findByActiveUserIdAndTellId(activeUserId, tellId);
                db.close();
            }
        }
        return lists;
    }

    public static synchronized void insertNearlyTell(Context context, NearlyTell nearlyTell) {
        synchronized (DataManager.class) {
            synchronized (DataManager.class) {
                SQLiteDatabase db = new DBHelper(context, DataBaseName, null, 26).getWritableDatabase();
                new NearlyTellDB(db).insert(nearlyTell);
                db.close();
            }
        }
    }

    public static synchronized void deleteNearlyTellById(Context context, int id) {
        synchronized (DataManager.class) {
            synchronized (DataManager.class) {
                SQLiteDatabase db = new DBHelper(context, DataBaseName, null, 26).getWritableDatabase();
                new NearlyTellDB(db).deleteById(id);
                db.close();
            }
        }
    }

    public static synchronized void deleteNearlyTellByTellId(Context context, String tellId) {
        synchronized (DataManager.class) {
            synchronized (DataManager.class) {
                SQLiteDatabase db = new DBHelper(context, DataBaseName, null, 26).getWritableDatabase();
                new NearlyTellDB(db).deleteByTellId(tellId);
                db.close();
            }
        }
    }

    public static synchronized void clearNearlyTell(Context context, String activeUserId) {
        synchronized (DataManager.class) {
            synchronized (DataManager.class) {
                SQLiteDatabase db = new DBHelper(context, DataBaseName, null, 26).getWritableDatabase();
                new NearlyTellDB(db).deleteByActiveUserId(activeUserId);
                db.close();
            }
        }
    }

    public static synchronized List<Contact> findContactByActiveUser(Context context, String activeUserId) {
        List<Contact> lists;
        synchronized (DataManager.class) {
            synchronized (DataManager.class) {
                SQLiteDatabase db = new DBHelper(context, DataBaseName, null, 26).getWritableDatabase();
                lists = new ContactDB(db).findByActiveUserId(activeUserId);
                db.close();
            }
        }
        return lists;
    }

    public static synchronized Contact findContactByActiveUserAndContactId(Context context, String activeUserId, String contactId) {
        Contact contact = null;
        synchronized (DataManager.class) {
            synchronized (DataManager.class) {
                SQLiteDatabase db = new DBHelper(context, DataBaseName, null, 26).getWritableDatabase();
                List<Contact> lists = new ContactDB(db).findByActiveUserIdAndContactId(activeUserId, contactId);
                db.close();
                if (lists.size() > 0) {
                    contact = (Contact) lists.get(0);
                }
            }
        }
        return contact;
    }

    public static synchronized void insertContact(Context context, Contact contact) {
        synchronized (DataManager.class) {
            synchronized (DataManager.class) {
                SQLiteDatabase db = new DBHelper(context, DataBaseName, null, 26).getWritableDatabase();
                new ContactDB(db).insert(contact);
                db.close();
            }
        }
    }

    public static synchronized void updateContact(Context context, Contact contact) {
        synchronized (DataManager.class) {
            synchronized (DataManager.class) {
                SQLiteDatabase db = new DBHelper(context, DataBaseName, null, 26).getWritableDatabase();
                new ContactDB(db).update(contact);
                db.close();
            }
        }
    }

    public static synchronized void deleteContactByActiveUserAndContactId(Context context, String activeUserId, String contactId) {
        synchronized (DataManager.class) {
            synchronized (DataManager.class) {
                SQLiteDatabase db = new DBHelper(context, DataBaseName, null, 26).getWritableDatabase();
                new ContactDB(db).deleteByActiveUserIdAndContactId(activeUserId, contactId);
                db.close();
            }
        }
    }

    public static synchronized void deleteContactById(Context context, int id) {
        synchronized (DataManager.class) {
            synchronized (DataManager.class) {
                SQLiteDatabase db = new DBHelper(context, DataBaseName, null, 26).getWritableDatabase();
                new ContactDB(db).deleteById(id);
                db.close();
            }
        }
    }

    public static synchronized int deCouple(Context context, String oldUserId, String newUserId) {
        synchronized (DataManager.class) {
            synchronized (DataManager.class) {
                SQLiteDatabase db = new DBHelper(context, DataBaseName, null, 26).getWritableDatabase();
                DBDecouple dbDecouple = new DBDecouple(context, oldUserId, newUserId);
                dbDecouple.doAlarmRecordDecouple(db);
                dbDecouple.doContactDecouple(db);
                SharedPreferencesManager.getInstance().setDecoupleUser(context, newUserId);
            }
        }
        return 0;
    }
}
