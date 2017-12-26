package com.jwkj.adapter;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.hikam.C0291R;
import com.jwkj.entity.Account;
import com.jwkj.global.AccountPersist;
import com.jwkj.global.NpcCommon;
import com.jwkj.utils.ImageUtils;
import com.jwkj.utils.Utils;
import com.jwkj.widget.AlarmHeaderView;
import com.p2p.core.network.AlarmRecordResult.SAlarmRecord;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AlarmRecordAapter2 extends BaseAdapter {
    List<SAlarmRecord> list;
    ImageThread mImageThread;
    Context mcontext;
    int showCount = 20;

    public class ImageFile {
        public String name;

        public ImageFile(String name) {
            this.name = name;
        }

        public boolean equals(Object object) {
            if (this.name.equals(((ImageFile) object).name)) {
                return true;
            }
            return false;
        }
    }

    private class ImageThread extends Thread {
        private boolean isRunImageThread;

        private ImageThread() {
            this.isRunImageThread = false;
        }

        public void kill() {
            this.isRunImageThread = false;
        }

        public void run() {
            this.isRunImageThread = true;
            while (this.isRunImageThread) {
                int count = AlarmRecordAapter2.this.list.size();
                int iUserId = Integer.parseInt(NpcCommon.mThreeNum) | Integer.MIN_VALUE;
                Account account = AccountPersist.getInstance().getActiveAccountInfo(AlarmRecordAapter2.this.mcontext);
                for (int i = 0; i < count; i++) {
                    SAlarmRecord ar = (SAlarmRecord) AlarmRecordAapter2.this.list.get(i);
                    if (!AlarmRecordAapter2.this.isExistImage(ar.messgeId)) {
                        String url = ar.pictureUrl + "&UserID=" + iUserId + "&SessionID=" + account.sessionId;
                        Log.e("my", url);
                        byte[] bImage = ImageUtils.getImageFromNetByUrl(url);
                        if (bImage != null && bImage.length > 0) {
                            ImageUtils.writeImageToDisk(bImage, Environment.getExternalStorageDirectory().getPath() + "/screenshot/tempHead/alarm/" + NpcCommon.mThreeNum, ar.pictureUrl + ".jpg");
                        }
                    }
                    Utils.sleepThread(100);
                }
            }
        }
    }

    class ViewHolder {
        private TextView allarmTime;
        private TextView allarmType;
        private AlarmHeaderView headerView;
        private TextView robotId;

        ViewHolder() {
        }

        public AlarmHeaderView getHeaderView() {
            return this.headerView;
        }

        public void setHeaderView(AlarmHeaderView headerView) {
            this.headerView = headerView;
        }

        public TextView getRobotId() {
            return this.robotId;
        }

        public void setRobotId(TextView robotId) {
            this.robotId = robotId;
        }

        public TextView getAllarmType() {
            return this.allarmType;
        }

        public void setAllarmType(TextView allarmType) {
            this.allarmType = allarmType;
        }

        public TextView getAllarmTime() {
            return this.allarmTime;
        }

        public void setAllarmTime(TextView allarmTime) {
            this.allarmTime = allarmTime;
        }
    }

    public AlarmRecordAapter2(Context context) {
        this.mcontext = context;
        this.list = new ArrayList();
    }

    public int getCount() {
        if (this.list.size() < this.showCount) {
            return this.list.size();
        }
        return this.showCount;
    }

    public Object getItem(int arg0) {
        return null;
    }

    public long getItemId(int arg0) {
        return 0;
    }

    public View getView(int arg0, View arg1, ViewGroup arg2) {
        ViewHolder holder;
        View view = arg1;
        if (view == null) {
            view = LayoutInflater.from(this.mcontext).inflate(C0291R.layout.list_alarm_record_item2, null);
            holder = new ViewHolder();
            holder.setHeaderView((AlarmHeaderView) view.findViewById(C0291R.id.header_img));
            holder.setRobotId((TextView) view.findViewById(C0291R.id.robot_id));
            holder.setAllarmType((TextView) view.findViewById(C0291R.id.allarm_type));
            holder.setAllarmTime((TextView) view.findViewById(C0291R.id.allarm_time));
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        SAlarmRecord ar = (SAlarmRecord) this.list.get(arg0);
        holder.getHeaderView().updateImage(NpcCommon.mThreeNum, ar.messgeId);
        holder.getRobotId().setText(ar.sourceId);
        switch (ar.alarmType) {
            case 1:
                holder.getAllarmType().setText(C0291R.string.allarm_type1);
                break;
            case 2:
                holder.getAllarmType().setText(C0291R.string.allarm_type2);
                break;
            case 3:
                holder.getAllarmType().setText(C0291R.string.allarm_type3);
                break;
            case 5:
                holder.getAllarmType().setText(C0291R.string.allarm_type5);
                break;
            case 6:
                holder.getAllarmType().setText(C0291R.string.allarm_type6);
                break;
            case 7:
                holder.getAllarmType().setText(C0291R.string.allarm_type4);
                break;
            case 8:
                holder.getAllarmType().setText(C0291R.string.defence);
                break;
            case 9:
                holder.getAllarmType().setText(C0291R.string.no_defence);
                break;
            case 10:
                holder.getAllarmType().setText(C0291R.string.battery_low_alarm);
                break;
        }
        holder.getAllarmTime().setText(Utils.ConvertTimeByLong(ar.alarmTime));
        return view;
    }

    public void updateNewDate(List<SAlarmRecord> datas) {
        if (datas.size() > 0) {
            Collections.sort(datas);
            if (!this.list.contains(datas.get(datas.size() - 1))) {
                this.list.clear();
            }
            for (SAlarmRecord gxar : datas) {
                if (!this.list.contains(gxar)) {
                    this.list.add(gxar);
                    Log.e("alarm", "messgeIds=" + gxar.messgeId + "sourceId=" + gxar.sourceId + "pictureUrl=" + gxar.pictureUrl + "alarmTime=" + gxar.alarmTime + "alarmType=" + gxar.alarmType + "defenceArea=" + gxar.defenceArea + "channel=" + gxar.channel + "serverReceiveTime=" + gxar.serverReceiveTime);
                }
            }
            Collections.sort(this.list);
            Log.e("my", "AlarmRecordCount:" + this.list.size());
            notifyDataSetChanged();
        }
    }

    public void updateHistoryData(List<SAlarmRecord> data) {
        if (data.size() > 0) {
            int count = 0;
            for (SAlarmRecord gxar : data) {
                if (!this.list.contains(gxar)) {
                    this.list.add(gxar);
                    count++;
                }
            }
            Collections.sort(this.list);
            this.showCount = this.list.size();
            Log.e("my", "AlarmRecordCount:" + this.list.size() + "->showCount:" + this.showCount);
            notifyDataSetChanged();
        }
    }

    private boolean isExistImage(String index) {
        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/screenshot/tempHead/alarm/" + NpcCommon.mThreeNum);
        if (!file.exists()) {
            file.mkdirs();
        }
        String[] filenames = file.list();
        List<ImageFile> list = new ArrayList();
        for (int j = 0; j < filenames.length; j++) {
            list.add(new ImageFile(filenames[j].substring(0, filenames[j].indexOf("."))));
        }
        if (list.contains(new ImageFile(index))) {
            return true;
        }
        return false;
    }

    public String getLastIndex() {
        if (this.list.size() > 0) {
            return ((SAlarmRecord) this.list.get(this.list.size() - 1)).messgeId;
        }
        return "";
    }

    public void runImageThread() {
        if (this.mImageThread != null) {
            this.mImageThread.kill();
            this.mImageThread = null;
        }
        this.mImageThread = new ImageThread();
        this.mImageThread.start();
    }

    public void stopImageThread() {
        if (this.mImageThread != null) {
            this.mImageThread.kill();
            this.mImageThread = null;
        }
    }
}
