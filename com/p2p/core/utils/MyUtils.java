package com.p2p.core.utils;

import android.content.Context;
import android.os.Build;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.http.cookie.ClientCookie;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MyUtils {
    public static boolean checkP2pLicense(String text) {
        if (text == null || text.isEmpty() || !text.matches("[A-Z]{3,7}[-][0-9]{6}[-][A-Z]{5}")) {
            return false;
        }
        return true;
    }

    public static String getShortDevID(String devID) {
        String shortDevID = devID;
        if (!checkP2pLicense(devID)) {
            return shortDevID;
        }
        String[] tmp = devID.split("-");
        return "" + tmp[0].charAt(tmp[0].length() - 1) + tmp[1];
    }

    public static boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        return Pattern.compile("[0-9]*").matcher(str).matches();
    }

    public static int getCPUVesion() {
        int version = 0;
        try {
            String ver = Build.CPU_ABI.substring(9, 10);
            if (isNumeric(ver)) {
                version = Integer.parseInt(ver);
            }
        } catch (Exception e) {
        }
        return version;
    }

    public static boolean isZh(Context context) {
        if (context.getResources().getConfiguration().locale.getLanguage().endsWith("zh")) {
            return true;
        }
        return false;
    }

    public static String convertPlanTime(int time) {
        int minute_to = time & 255;
        int minute_from = (time >> 8) & 255;
        int hour_to = (time >> 16) & 255;
        int hour_from = (time >> 24) & 255;
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

    public static int convertPlanTime(String time) {
        try {
            String[] times = time.split("-");
            String[] time_from = times[0].split(":");
            String[] time_to = times[1].split(":");
            return (((Integer.parseInt(time_from[0]) << 24) | (Integer.parseInt(time_to[0]) << 16)) | (Integer.parseInt(time_from[1]) << 8)) | (Integer.parseInt(time_to[1]) << 0);
        } catch (Exception e) {
            return 0;
        }
    }

    public static int dip2px(Context context, int dipValue) {
        return (int) ((((float) dipValue) * context.getResources().getDisplayMetrics().density) + 0.5f);
    }

    public static String convertDeviceTime(int iTime) {
        int year = ((iTime >> 24) & 63) + 2000;
        int month = (iTime >> 18) & 63;
        int day = (iTime >> 12) & 63;
        int hour = (iTime >> 6) & 63;
        int minute = (iTime >> 0) & 63;
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

    public static String getBitProcessingVersion() {
        try {
            String[] parseVerson = getVersion().split("\\.");
            int a = Integer.parseInt(parseVerson[0]) << 24;
            int b = Integer.parseInt(parseVerson[1]) << 16;
            int c = Integer.parseInt(parseVerson[2]) << 8;
            return String.valueOf(((a | b) | c) | Integer.parseInt(parseVerson[3]));
        } catch (Exception e) {
            return "9999";
        }
    }

    public static String getVersion() {
        String version = "";
        try {
            return (String) parseXml(MyUtils.class.getClassLoader().getResourceAsStream("version.xml")).get(ClientCookie.VERSION_ATTR);
        } catch (Exception e) {
            e.printStackTrace();
            return version;
        }
    }

    public static long convertTimeStringToInterval(String time) {
        long interval = 0;
        try {
            interval = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(time).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return interval;
    }

    private static HashMap<String, String> parseXml(InputStream input) throws Exception {
        HashMap<String, String> hashMap = new HashMap();
        NodeList childNodes = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(input).getDocumentElement().getChildNodes();
        for (int j = 0; j < childNodes.getLength(); j++) {
            Node childNode = childNodes.item(j);
            if (childNode.getNodeType() == (short) 1) {
                Element childElement = (Element) childNode;
                if (ClientCookie.VERSION_ATTR.equals(childElement.getNodeName())) {
                    hashMap.put(ClientCookie.VERSION_ATTR, childElement.getFirstChild().getNodeValue());
                }
            }
        }
        return hashMap;
    }
}
