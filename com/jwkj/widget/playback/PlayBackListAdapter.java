package com.jwkj.widget.playback;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.hikam.C0291R;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PlayBackListAdapter extends BaseAdapter {
    private Context context;
    private Date endTime;
    private List<String> nameList = new ArrayList();
    private List<Integer> rateList = new ArrayList();
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private Date startTime;

    class ViewHolder {
        public TextView record_name;

        ViewHolder() {
        }

        public TextView getRecord_name() {
            return this.record_name;
        }

        public void setRecord_name(TextView record_name) {
            this.record_name = record_name;
        }
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getStartTime() {
        return this.startTime;
    }

    public List<String> getNameList() {
        return this.nameList;
    }

    public List<Integer> getRateList() {
        return this.rateList;
    }

    public Date getEndTime() {
        if (this.nameList.size() <= 0) {
            return null;
        }
        int position = this.nameList.size() - 1;
        if (((String) this.nameList.get(position)).startsWith("disc1/")) {
            try {
                return this.sdf.parse(((String) this.nameList.get(position)).substring(6, 22).replace("_", " "));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            if (((String) this.nameList.get(position)).startsWith("/tmp/mmc/")) {
                String lastTime = ((String) this.nameList.get(position)).substring("/tmp/mmc/".length(), 21).replace("_", " ").replace(".", ":");
                StringBuilder lastTimeResult = new StringBuilder();
                lastTimeResult.append("20");
                lastTimeResult.append(lastTime.substring(0, 2));
                lastTimeResult.append("-");
                lastTimeResult.append(lastTime.substring(2, 4));
                lastTimeResult.append("-");
                lastTimeResult.append(lastTime.substring(4, lastTime.length()));
                try {
                    return this.sdf.parse(lastTimeResult.toString());
                } catch (ParseException e2) {
                    e2.printStackTrace();
                }
            }
            return null;
        }
    }

    public PlayBackListAdapter(Context context, List<String> list, List<Integer> rateList) {
        this.context = context;
        this.nameList = list;
        this.rateList = rateList;
    }

    public int getCount() {
        return this.nameList.size();
    }

    public Object getItem(int position) {
        return this.nameList.get(position);
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(this.context).inflate(C0291R.layout.list_record_item, null);
            holder = new ViewHolder();
            holder.setRecord_name((TextView) view.findViewById(C0291R.id.rName));
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        if (((String) this.nameList.get(position)).startsWith("disc1/")) {
            holder.getRecord_name().setText(((String) this.nameList.get(position)).substring("disc1/".length()));
        } else if (((String) this.nameList.get(position)).startsWith("/tmp/mmc/")) {
            String fileFullName = ((String) this.nameList.get(position)).substring("/tmp/mmc/".length());
            String fileSuffix = fileFullName.substring(fileFullName.length() - ".avx".length());
            String fileName = fileFullName.substring(0, fileFullName.length() - ".avx".length()).replaceAll("\\.", ":");
            holder.getRecord_name().setText(fileName + "(" + getDuration(fileName) + "s)");
        } else {
            holder.getRecord_name().setText((CharSequence) this.nameList.get(position));
        }
        return view;
    }

    public int getDuration(String s) {
        String[] attrs = s.split("_");
        String[] time1 = attrs[1].split(":");
        String[] time2 = attrs[2].split(":");
        int[] int_t = new int[3];
        int[] int_t2 = new int[3];
        for (int i = 0; i < 3; i++) {
            int_t[i] = Integer.valueOf(time1[i]).intValue();
            int_t2[i] = Integer.valueOf(time2[i]).intValue();
        }
        return (((int_t2[0] * 3600) + (int_t2[1] * 60)) + int_t2[2]) - (((int_t[0] * 3600) + (int_t[1] * 60)) + int_t[2]);
    }
}
