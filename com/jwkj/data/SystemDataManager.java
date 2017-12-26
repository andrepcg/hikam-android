package com.jwkj.data;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore.Audio.Media;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.http.cookie.ClientCookie;

public class SystemDataManager {
    private static SystemDataManager manager = null;

    private SystemDataManager() {
    }

    public static synchronized SystemDataManager getInstance() {
        SystemDataManager systemDataManager;
        synchronized (SystemDataManager.class) {
            if (manager == null) {
                synchronized (SystemDataManager.class) {
                    if (manager == null) {
                        manager = new SystemDataManager();
                    }
                }
            }
            systemDataManager = manager;
        }
        return systemDataManager;
    }

    public ArrayList<HashMap<String, String>> getSysBells(Context context) {
        ArrayList<HashMap<String, String>> bells = new ArrayList();
        Cursor result = context.getContentResolver().query(Media.INTERNAL_CONTENT_URI, null, null, null, "title_key");
        while (result.moveToNext()) {
            HashMap<String, String> bell = new HashMap();
            int bellId = result.getInt(result.getColumnIndex("_id"));
            bell.put("bellName", result.getString(result.getColumnIndex("title")));
            bell.put("bellId", bellId + "");
            bells.add(bell);
        }
        result.close();
        return bells;
    }

    public ArrayList<HashMap<String, String>> getSdBells(Context context) {
        ArrayList<HashMap<String, String>> bells = new ArrayList();
        Cursor result = context.getContentResolver().query(Media.EXTERNAL_CONTENT_URI, null, null, null, "title_key");
        if (result != null) {
            while (result.moveToNext()) {
                HashMap<String, String> bell = new HashMap();
                int bellId = result.getInt(result.getColumnIndex("_id"));
                bell.put("bellName", result.getString(result.getColumnIndex("title")));
                bell.put("bellId", bellId + "");
                bells.add(bell);
            }
            result.close();
        }
        return bells;
    }

    public HashMap<String, String> findSystemBellById(Context context, int bellId) {
        HashMap<String, String> data = new HashMap();
        Cursor result = context.getContentResolver().query(Media.INTERNAL_CONTENT_URI, null, "_id=?", new String[]{String.valueOf(bellId)}, "title_key");
        while (result.moveToNext()) {
            data = new HashMap();
            String path = result.getString(result.getColumnIndex("_data"));
            String bellName = result.getString(result.getColumnIndex("title"));
            data.put(ClientCookie.PATH_ATTR, path);
            data.put("bellName", bellName);
        }
        result.close();
        return data;
    }

    public HashMap<String, String> findSdBellById(Context context, int bellId) {
        HashMap<String, String> data = new HashMap();
        Cursor result = context.getContentResolver().query(Media.EXTERNAL_CONTENT_URI, null, "_id=?", new String[]{String.valueOf(bellId)}, "title_key");
        while (result.moveToNext()) {
            data = new HashMap();
            String path = result.getString(result.getColumnIndex("_data"));
            String bellName = result.getString(result.getColumnIndex("title"));
            data.put(ClientCookie.PATH_ATTR, path);
            data.put("bellName", bellName);
        }
        result.close();
        return data;
    }
}
