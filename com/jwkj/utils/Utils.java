package com.jwkj.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import com.hikam.C0291R;
import com.jwkj.data.SharedPreferencesManager;
import com.jwkj.data.SysMessage;
import com.jwkj.global.Constants.Action;
import com.jwkj.global.MyApp;
import com.jwkj.widget.NormalDialog;
import com.p2p.core.update.UpdateManager;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

public class Utils {
    public static boolean checkP2pLicense(String text) {
        if (text == null || text.isEmpty() || !text.matches("[A-Z]{3,7}[-][0-9]{6}[-][A-Z]{5}")) {
            return false;
        }
        return true;
    }

    public static String showShortDevID(String devID) {
        String shortDevID = devID;
        if (!checkP2pLicense(devID)) {
            return shortDevID;
        }
        String[] tmp = devID.split("-");
        return "" + tmp[0].charAt(tmp[0].length() - 1) + tmp[1];
    }

    public static boolean checkDevID(String text) {
        if (text == null || text.isEmpty() || !text.matches("[a-zA-Z0-9]{7,9}")) {
            return false;
        }
        return true;
    }

    public static String MD5(String source) {
        char[] hexDigits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        try {
            byte[] btInput = source.getBytes();
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(btInput);
            char[] str = new char[(md_len * 2)];
            int k = 0;
            for (byte byte0 : mdInst.digest()) {
                int i = k + 1;
                str[k] = hexDigits[(byte0 >>> 4) & 15];
                k = i + 1;
                str[i] = hexDigits[byte0 & 15];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean checkEmail(String email) {
        try {
            return Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*", 2).matcher(email).matches();
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean checkUsername(String username) {
        try {
            return Pattern.compile("[a-zA-Z1-9][a-zA-Z0-9\\.\\-_]{2,19}").matcher(username).matches();
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean checkDevicePwd(String devicePwd) {
        try {
            return Pattern.compile("[a-zA-Z0-9]{6,30}").matcher(devicePwd).matches();
        } catch (Exception e) {
            return false;
        }
    }

    public static String[] getMsgInfo(SysMessage msg, Context context) {
        if (msg.msgType != 2) {
            return null;
        }
        String title = context.getResources().getString(C0291R.string.system_administrator);
        String content = msg.msg;
        return new String[]{title, content};
    }

    public static boolean hasDigit(String content) {
        if (Pattern.compile(".*\\d+.*").matcher(content).matches()) {
            return true;
        }
        return false;
    }

    public static String ConvertTimeByString(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(time);
            sdf.applyPattern("yyyy-MM-dd HH:mm");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return sdf.format(date);
    }

    public static String ConvertTimeByLong(long time) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(time));
    }

    public static String getDefenceAreaByGroup(Context context, int group) {
        switch (group) {
            case 0:
                return context.getResources().getString(C0291R.string.remote);
            case 1:
                return context.getResources().getString(C0291R.string.hall);
            case 2:
                return context.getResources().getString(C0291R.string.window);
            case 3:
                return context.getResources().getString(C0291R.string.balcony);
            case 4:
                return context.getResources().getString(C0291R.string.bedroom);
            case 5:
                return context.getResources().getString(C0291R.string.kitchen);
            case 6:
                return context.getResources().getString(C0291R.string.courtyard);
            case 7:
                return context.getResources().getString(C0291R.string.door_lock);
            case 8:
                return context.getResources().getString(C0291R.string.other);
            default:
                return "";
        }
    }

    public static Bitmap montageBitmap(Bitmap frame, Bitmap src, int x, int y) {
        int w = src.getWidth();
        int h = src.getHeight();
        Bitmap sizeFrame = Bitmap.createScaledBitmap(frame, w, h, true);
        Bitmap newBM = Bitmap.createBitmap(w, h, Config.ARGB_8888);
        Canvas canvas = new Canvas(newBM);
        canvas.drawBitmap(src, 0.0f, 0.0f, null);
        canvas.drawBitmap(sizeFrame, 0.0f, 0.0f, null);
        return newBM;
    }

    public static boolean isZh(Context context) {
        if (context.getResources().getConfiguration().locale.getLanguage().endsWith("zh")) {
            return true;
        }
        return false;
    }

    public static void upDate(final Context context) {
        new Thread() {
            public void run() {
                boolean isOk = false;
                try {
                    Timestamp time = new Timestamp(System.currentTimeMillis());
                    String recent_checkTime = SharedPreferencesManager.getInstance().getData(context, SharedPreferencesManager.SP_FILE_GWELL, SharedPreferencesManager.KEY_UPDATE_CHECKTIME);
                    if (recent_checkTime.equals("")) {
                        isOk = true;
                    } else {
                        if (time.getTime() - Timestamp.valueOf(recent_checkTime).getTime() > 86400000) {
                            isOk = true;
                        }
                    }
                    if (isOk && UpdateManager.getInstance().checkUpdate() && isOk) {
                        SharedPreferencesManager.getInstance().putData(context, SharedPreferencesManager.SP_FILE_GWELL, SharedPreferencesManager.KEY_UPDATE_CHECKTIME, time.toString());
                        Intent i = new Intent(Action.ACTION_UPDATE);
                        String data = "";
                        if (Utils.isZh(MyApp.app)) {
                            data = UpdateManager.getInstance().getUpdateDescription();
                        } else {
                            data = UpdateManager.getInstance().getUpdateDescription_en();
                        }
                        i.putExtra("updateDescription", data);
                        context.sendBroadcast(i);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public static void showPromptDialog(Context context, int title, int content) {
        NormalDialog dialog = new NormalDialog(context, context.getResources().getString(title), context.getResources().getString(content), "", "");
        dialog.setStyle(5);
        dialog.showDialog();
    }

    public static String getPathFromUri(Context mContext, Uri contentUri) {
        Cursor cursor = new CursorLoader(mContext, contentUri, new String[]{"_data"}, null, null, null).loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow("_data");
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public static String intToIp(int ip) {
        return (ip & 255) + "." + ((ip >> 8) & 255) + "." + ((ip >> 16) & 255) + "." + ((ip >> 24) & 255);
    }

    public static HashMap getHash(String string) {
        try {
            HashMap hashMap = new HashMap();
            for (String split : string.split(",")) {
                String[] info = split.split(":");
                hashMap.put("" + info[0], info[1]);
            }
            return hashMap;
        } catch (Exception e) {
            return null;
        }
    }

    public static String getFormatTellDate(Context context, String time) {
        if (time.length() < 12) {
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTimeInMillis(Long.parseLong(time) * 1000);
            return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(gc.getTime());
        }
        String year = context.getString(C0291R.string.year_format);
        String month = context.getString(C0291R.string.month_format);
        String day = context.getString(C0291R.string.day_format);
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date dt = null;
        try {
            dt = new Date(Long.parseLong(time));
        } catch (Exception e) {
        }
        String s = "";
        if (dt != null) {
            return sd.format(dt);
        }
        return s;
    }

    public static void sleepThread(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String[] getDeleteAlarmIdArray(String[] data, int position) {
        if (data.length == 1) {
            return new String[]{"0"};
        }
        String[] array = new String[(data.length - 1)];
        int count = 0;
        for (int i = 0; i < data.length; i++) {
            if (position != i) {
                array[count] = data[i];
                count++;
            }
        }
        return array;
    }

    public static String convertDeviceTime(int iYear, int iMonth, int iDay, int iHour, int iMinute) {
        int year = iYear + 2000;
        int month = iMonth;
        int day = iDay;
        int hour = iHour;
        int minute = iMinute;
        StringBuilder sb = new StringBuilder();
        sb.append(year + "-");
        if (month < 10) {
            sb.append("0" + month + "-");
        } else {
            sb.append(month + "-");
        }
        if (day < 10) {
            sb.append("0" + day + " ");
        } else {
            sb.append(day + " ");
        }
        if (hour < 10) {
            sb.append("0" + hour + ":");
        } else {
            sb.append(hour + ":");
        }
        if (minute < 10) {
            sb.append("0" + minute);
        } else {
            sb.append("" + minute);
        }
        return sb.toString();
    }

    public static String convertPlanTime(int hour_from, int minute_from, int hour_to, int minute_to) {
        StringBuilder sb = new StringBuilder();
        if (hour_from < 10) {
            sb.append("0" + hour_from + ":");
        } else {
            sb.append(hour_from + ":");
        }
        if (minute_from < 10) {
            sb.append("0" + minute_from + "-");
        } else {
            sb.append(minute_from + "-");
        }
        if (hour_to < 10) {
            sb.append("0" + hour_to + ":");
        } else {
            sb.append(hour_to + ":");
        }
        if (minute_to < 10) {
            sb.append("0" + minute_to);
        } else {
            sb.append("" + minute_to);
        }
        return sb.toString();
    }

    public static boolean isNumeric(String str) {
        if (str == null || "".equals(str)) {
            return false;
        }
        return Pattern.compile("[0-9]*").matcher(str).matches();
    }

    public static void deleteFile(File file) {
        if (file.isFile()) {
            file.delete();
        } else if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                file.delete();
                return;
            }
            for (File f : childFile) {
                deleteFile(f);
            }
            file.delete();
        }
    }

    public static int dip2px(Context context, int dipValue) {
        return (int) ((((float) dipValue) * context.getResources().getDisplayMetrics().density) + 0.5f);
    }

    public static boolean isWifiOpen(ScanResult result) {
        if (result.capabilities.toLowerCase().indexOf("wep") == -1 && result.capabilities.toLowerCase().indexOf("wpa") == -1) {
            return true;
        }
        return false;
    }

    public static boolean hasSDCard() {
        if (Environment.getExternalStorageState().equals("mounted")) {
            return true;
        }
        return false;
    }

    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            NetworkInfo mNetworkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public static boolean checkNum(String s) {
        try {
            return Pattern.compile("^[0-9]*$").matcher(s).matches();
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isBackground(Context context) {
        List<RunningTaskInfo> tasks = ((ActivityManager) context.getSystemService("activity")).getRunningTasks(1);
        if (tasks.isEmpty() || ((RunningTaskInfo) tasks.get(0)).topActivity.getPackageName().equals(context.getPackageName())) {
            return false;
        }
        return true;
    }

    public static String getLocalIpAddress(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService("wifi");
        if (wifiManager.isWifiEnabled()) {
            return intToIp(wifiManager.getConnectionInfo().getIpAddress());
        }
        return "unknown ip addr";
    }

    public static String getRecordTime(int count) {
        StringBuilder builder = new StringBuilder();
        if (count == 0) {
            return "00:00:00";
        }
        int minutes;
        int seconds;
        if (count >= 3600) {
            int hour = count / 3600;
            minutes = (count % 3600) / 60;
            seconds = (count % 3600) % 60;
            if (hour > 9) {
                builder.append(hour);
            } else {
                builder.append("0");
                builder.append(hour);
            }
            builder.append(":");
            if (minutes > 9) {
                builder.append(minutes);
            } else {
                builder.append("0");
                builder.append(minutes);
            }
            builder.append(":");
            if (seconds > 9) {
                builder.append(seconds);
            } else {
                builder.append("0");
                builder.append(seconds);
            }
        } else if (count >= 60) {
            minutes = count / 60;
            seconds = count % 60;
            builder.append("00:");
            if (minutes > 9) {
                builder.append(minutes);
            } else {
                builder.append("0");
                builder.append(minutes);
            }
            builder.append(":");
            if (seconds > 9) {
                builder.append(seconds);
            } else {
                builder.append("0");
                builder.append(seconds);
            }
        } else {
            builder.append("00:00:");
            if (count > 9) {
                builder.append(count);
            } else {
                builder.append("0");
                builder.append(count);
            }
        }
        return builder.toString();
    }

    public static String getPlayTime(int count) {
        StringBuilder builder = new StringBuilder();
        if (count == 0) {
            return "00:00";
        }
        if (count >= 60) {
            int minutes = count / 60;
            int seconds = count % 60;
            if (minutes > 9) {
                builder.append(minutes);
            } else {
                builder.append("0");
                builder.append(minutes);
            }
            builder.append(":");
            if (seconds > 9) {
                builder.append(seconds);
            } else {
                builder.append("0");
                builder.append(seconds);
            }
        } else {
            builder.append("00:");
            if (count > 9) {
                builder.append(count);
            } else {
                builder.append(0);
                builder.append(count);
            }
        }
        return builder.toString();
    }

    public static ArrayList<String> deCompressTargz(File targzFile, String destpath) throws IOException {
        byte[] buf = new byte[1024];
        TarArchiveInputStream tais = new TarArchiveInputStream(new GzipCompressorInputStream(new BufferedInputStream(new FileInputStream(targzFile))));
        ArrayList<String> list = new ArrayList();
        while (true) {
            TarArchiveEntry tae = tais.getNextTarEntry();
            if (tae != null) {
                File f = new File(destpath + "/" + tae.getName());
                if (tae.isDirectory()) {
                    f.mkdirs();
                } else {
                    File parent = f.getParentFile();
                    if (!parent.exists()) {
                        parent.mkdirs();
                    }
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(f));
                    while (true) {
                        int len = tais.read(buf);
                        if (len == -1) {
                            break;
                        }
                        bos.write(buf, 0, len);
                    }
                    bos.flush();
                    bos.close();
                }
                list.add(f.getPath());
                Log.e("few", "deCompress result:" + f.getPath());
            } else {
                tais.close();
                targzFile.delete();
                return list;
            }
        }
    }

    public static String getTimeAgo(String time) {
        String s = String.valueOf(System.currentTimeMillis() / 1000);
        int diff = ((int) (System.currentTimeMillis() / 1000)) - Integer.valueOf(time).intValue();
        if (diff == 0) {
            return MyApp.app.getString(C0291R.string.ago_n);
        }
        if (diff < 60) {
            return diff + MyApp.app.getString(C0291R.string.ago_s);
        }
        if (diff < 3600) {
            return (diff / 60) + MyApp.app.getString(C0291R.string.ago_m);
        } else if (diff < 86400) {
            return (diff / 3600) + MyApp.app.getString(C0291R.string.ago_h);
        } else {
            return (diff / 86400) + MyApp.app.getString(C0291R.string.ago_d);
        }
    }
}
