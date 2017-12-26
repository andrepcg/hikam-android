package com.jwkj.widget.playback;

import android.util.Log;
import com.jwkj.data.Contact;
import com.p2p.core.P2PHandler;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PlayBackManager {
    public static final int PLAYBACK_CUSTOM = 0;
    public static final int PLAYBACK_ONE_DAY = 1;
    public static final int PLAYBACK_ONE_MONTH = 30;
    public static final int PLAYBACK_THREE_DAY = 3;
    private static PlayBackManager instance = new PlayBackManager();
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    private PlayBackManager() {
    }

    public static PlayBackManager getInstance() {
        return instance;
    }

    public Date getOneDayEndDate() {
        return null;
    }

    public Date getThreeDayEndDate() {
        return null;
    }

    public Date getStartTimeByIndex(int time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Calendar ca = Calendar.getInstance();
        ca.setTime(new Date());
        Date resultDate = null;
        switch (time) {
            case 1:
                ca.add(1, 0);
                ca.add(2, 0);
                ca.add(5, -1);
                Log.e("few", "----------------------------1");
                resultDate = ca.getTime();
                break;
            case 3:
                ca.add(1, 0);
                ca.add(2, 0);
                ca.add(5, -3);
                resultDate = ca.getTime();
                break;
            case 30:
                ca.add(1, 0);
                ca.add(2, -1);
                ca.add(5, 0);
                resultDate = ca.getTime();
                break;
        }
        if (resultDate == null) {
            return null;
        }
        try {
            resultDate = sdf.parse(sdf.format(resultDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return resultDate;
    }

    public void searchIndex(int time, Contact contact) {
        Date endDate = new Date(System.currentTimeMillis() + 60000);
        String endTime = this.sdf.format(endDate);
        P2PHandler.getInstance().getRecordFiles(contact.contactModel, contact.contactId, contact.contactPassword, getStartTimeByIndex(time), endDate);
    }

    public void searchNextPager(Date start, Date end, Contact contact) {
        P2PHandler.getInstance().getRecordFiles(contact.contactModel, contact.contactId, contact.contactPassword, start, end);
    }

    public Date getDataByTimeString(String timeStr) {
        String result = timeStr.substring("/tmp/mmc/".length(), 21).replace("_", " ").replace(".", ":");
        StringBuilder resultBuilder = new StringBuilder();
        resultBuilder.append("20");
        resultBuilder.append(result.substring(0, 2));
        resultBuilder.append("-");
        resultBuilder.append(result.substring(2, 4));
        resultBuilder.append("-");
        resultBuilder.append(result.substring(4, result.length()));
        try {
            return this.sdf.parse(resultBuilder.toString());
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
