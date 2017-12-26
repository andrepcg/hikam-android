package com.jwkj.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.hikam.C0291R;
import com.jwkj.data.AlarmRecord;
import com.jwkj.data.DataManager;
import com.jwkj.global.Constants.Action;
import com.jwkj.global.NpcCommon;
import com.jwkj.utils.Utils;
import com.jwkj.widget.NormalDialog;
import com.jwkj.widget.NormalDialog.OnButtonOkListener;
import java.io.File;
import java.util.List;

public class AlarmRecordAdapter extends BaseAdapter {
    private String deviceId;
    private String devicePwd;
    private File file = null;
    List<AlarmRecord> list;
    Context mContext;
    public OnItemClick onItemClick;
    private StringBuilder sb = new StringBuilder();

    public interface OnItemClick {
        void onPlay(int i);
    }

    class ViewHolder {
        private TextView allarmTime;
        private TextView allarmType;
        private ImageView img;
        private ImageView imgIcon;
        private LinearLayout layout_extern;
        private TextView robotId;
        private TextView text_group;
        private TextView text_item;
        private TextView tv_tip;

        ViewHolder() {
        }

        public void setTextTip(TextView tv_tip) {
            this.tv_tip = tv_tip;
        }

        public TextView getTextTip() {
            return this.tv_tip;
        }

        public void setImageIcon(ImageView imgIcon) {
            this.imgIcon = imgIcon;
        }

        public ImageView getImgeIcon() {
            return this.imgIcon;
        }

        public void setImageView(ImageView img) {
            this.img = img;
        }

        public ImageView getImgeView() {
            return this.img;
        }

        public LinearLayout getLayout_extern() {
            return this.layout_extern;
        }

        public void setLayout_extern(LinearLayout layout_extern) {
            this.layout_extern = layout_extern;
        }

        public TextView getText_group() {
            return this.text_group;
        }

        public void setText_group(TextView text_group) {
            this.text_group = text_group;
        }

        public TextView getText_item() {
            return this.text_item;
        }

        public void setText_item(TextView text_item) {
            this.text_item = text_item;
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

    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public AlarmRecordAdapter(Context context, List<AlarmRecord> list, String id, String pwd) {
        this.mContext = context;
        this.list = list;
        this.deviceId = id;
        this.devicePwd = pwd;
    }

    public int getCount() {
        return this.list.size();
    }

    public Object getItem(int arg0) {
        return this.list.get(arg0);
    }

    public long getItemId(int arg0) {
        return 0;
    }

    public View getView(final int arg0, View arg1, ViewGroup arg2) {
        ViewHolder holder;
        View view = arg1;
        if (view == null) {
            view = LayoutInflater.from(this.mContext).inflate(C0291R.layout.list_alarm_record_item, null);
            holder = new ViewHolder();
            holder.setRobotId((TextView) view.findViewById(C0291R.id.robot_id));
            holder.setAllarmType((TextView) view.findViewById(C0291R.id.allarm_type));
            holder.setAllarmTime((TextView) view.findViewById(C0291R.id.allarm_time));
            holder.setLayout_extern((LinearLayout) view.findViewById(C0291R.id.layout_extern));
            holder.setText_group((TextView) view.findViewById(C0291R.id.text_group));
            holder.setText_item((TextView) view.findViewById(C0291R.id.text_item));
            holder.setImageView((ImageView) view.findViewById(C0291R.id.img));
            holder.setImageIcon((ImageView) view.findViewById(C0291R.id.img_icon));
            holder.setTextTip((TextView) view.findViewById(C0291R.id.tv_time_tip));
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        final AlarmRecord ar = (AlarmRecord) this.list.get(arg0);
        if (ar.deviceName == null || ar.deviceName.equals("")) {
            holder.getRobotId().setText(Utils.showShortDevID(ar.deviceId));
        } else {
            holder.getRobotId().setText(ar.deviceName);
        }
        holder.getAllarmTime().setText(Utils.getFormatTellDate(this.mContext, ar.alarmTime));
        holder.getLayout_extern().setVisibility(8);
        switch (ar.alarmType) {
            case 1:
                holder.getAllarmType().setText(C0291R.string.allarm_type1);
                if (ar.group >= 0 && ar.item >= 0) {
                    holder.getLayout_extern().setVisibility(0);
                    holder.getText_group().setText(this.mContext.getResources().getString(C0291R.string.area) + ":" + Utils.getDefenceAreaByGroup(this.mContext, ar.group));
                    holder.getText_item().setText(this.mContext.getResources().getString(C0291R.string.channel) + ":" + (ar.item + 1));
                }
                Glide.with(this.mContext).load(Integer.valueOf(C0291R.drawable.alarm_area)).into(holder.getImgeView());
                break;
            case 2:
                holder.getAllarmType().setText(C0291R.string.allarm_type2);
                Glide.with(this.mContext).load(Integer.valueOf(C0291R.drawable.alarm_md)).into(holder.getImgeView());
                break;
            case 3:
                holder.getAllarmType().setText(C0291R.string.allarm_type3);
                break;
            case 5:
                holder.getAllarmType().setText(C0291R.string.allarm_type5);
                break;
            case 6:
                holder.getAllarmType().setText(C0291R.string.allarm_type6);
                if (ar.group >= 0 && ar.item >= 0) {
                    holder.getLayout_extern().setVisibility(0);
                    holder.getText_group().setText(this.mContext.getResources().getString(C0291R.string.area) + ":" + Utils.getDefenceAreaByGroup(this.mContext, ar.group));
                    holder.getText_item().setText(this.mContext.getResources().getString(C0291R.string.channel) + ":" + (ar.item + 1));
                    break;
                }
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
            case 13:
                holder.getAllarmType().setText(C0291R.string.door_bell);
                break;
            case 31:
                holder.getAllarmType().setText(C0291R.string.humanoid_detection);
                Glide.with(this.mContext).load(Integer.valueOf(C0291R.drawable.alarm_hd)).into(holder.getImgeView());
                break;
            case 32:
                holder.getAllarmType().setText(C0291R.string.humanoid_detection_fallback);
                Glide.with(this.mContext).load(Integer.valueOf(C0291R.drawable.alarm_md)).into(holder.getImgeView());
                break;
        }
        view.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (AlarmRecordAdapter.this.onItemClick != null) {
                    AlarmRecordAdapter.this.onItemClick.onPlay(arg0);
                }
            }
        });
        view.setOnLongClickListener(new OnLongClickListener() {

            class C10941 implements OnButtonOkListener {
                C10941() {
                }

                public void onClick() {
                    DataManager.deleteAlarmRecordById(AlarmRecordAdapter.this.mContext, ar.id);
                    Intent refreshAlarm = new Intent();
                    refreshAlarm.setAction(Action.REFRESH_ALARM_RECORD);
                    refreshAlarm.putExtra("operate", "reduce");
                    AlarmRecordAdapter.this.mContext.sendBroadcast(refreshAlarm);
                }
            }

            public boolean onLongClick(View arg0) {
                NormalDialog dialog = new NormalDialog(AlarmRecordAdapter.this.mContext, AlarmRecordAdapter.this.mContext.getResources().getString(C0291R.string.delete_alarm_records), AlarmRecordAdapter.this.mContext.getResources().getString(C0291R.string.are_you_sure_delete) + " " + Utils.showShortDevID(ar.deviceId) + "?", AlarmRecordAdapter.this.mContext.getResources().getString(C0291R.string.delete), AlarmRecordAdapter.this.mContext.getResources().getString(C0291R.string.cancel));
                dialog.setOnButtonOkListener(new C10941());
                dialog.showDialog();
                return true;
            }
        });
        if (ar.getUuid() != null) {
            this.sb.delete(0, this.sb.length());
            this.sb.append("/storage/emulated/0/hikam_shortav/tmp/mmc/clips/images/");
            this.sb.append(ar.getUuid());
            this.sb.append(".jpg");
            this.sb.toString();
            this.file = new File(this.sb.toString());
            if (this.file.isFile()) {
                holder.getImgeView().setScaleType(ScaleType.FIT_XY);
                Glide.with(this.mContext).load(this.sb.toString()).into(holder.getImgeView());
                holder.getImgeIcon().setVisibility(0);
            } else {
                setAlarmImage(holder.getImgeView(), ar.alarmType);
                holder.getImgeIcon().setVisibility(8);
            }
        } else {
            setAlarmImage(holder.getImgeView(), ar.alarmType);
            holder.getImgeIcon().setVisibility(8);
        }
        if (arg0 >= 6) {
            holder.getTextTip().setVisibility(8);
        } else if (ar.alarmTime.length() <= 10) {
            holder.getTextTip().setText(Utils.getTimeAgo(ar.alarmTime));
            holder.getTextTip().setVisibility(0);
        }
        return view;
    }

    public void updateData() {
        this.list = DataManager.findAlarmRecordByActiveUser(this.mContext, NpcCommon.mThreeNum);
    }

    public void updateData(String deviceId, String deviceName) {
        if (deviceId != null && !deviceId.equals("")) {
            this.list = DataManager.findAlarmRecordByActiveUserAndDeviceId2(this.mContext, NpcCommon.mThreeNum, deviceId, deviceName);
        }
    }

    public void setAlarmImage(ImageView img, int type) {
        img.setScaleType(ScaleType.FIT_CENTER);
        switch (type) {
            case 1:
                Glide.with(this.mContext).load(Integer.valueOf(C0291R.drawable.alarm_area)).into(img);
                return;
            case 2:
                Glide.with(this.mContext).load(Integer.valueOf(C0291R.drawable.alarm_md)).into(img);
                return;
            case 7:
                Glide.with(this.mContext).load(Integer.valueOf(C0291R.drawable.alarm_pir)).into(img);
                return;
            case 13:
                Glide.with(this.mContext).load(Integer.valueOf(C0291R.drawable.alarm_ss)).into(img);
                return;
            case 31:
                Glide.with(this.mContext).load(Integer.valueOf(C0291R.drawable.alarm_hd)).into(img);
                return;
            case 32:
                Glide.with(this.mContext).load(Integer.valueOf(C0291R.drawable.alarm_md)).into(img);
                return;
            default:
                Glide.with(this.mContext).load(Integer.valueOf(C0291R.drawable.heard_icon_1)).into(img);
                return;
        }
    }

    public List<AlarmRecord> getList() {
        return this.list;
    }
}
