package com.jwkj.widget.playback;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.hikam.C0291R;
import com.jwkj.entity.RecordVideo;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

public class RecordListAdapter extends BaseAdapter {
    private Context context;
    private List<RecordVideo> list = new ArrayList();

    class C06521 implements FileFilter {
        C06521() {
        }

        public boolean accept(File pathname) {
            if (pathname.getName().endsWith(".mp4")) {
                return true;
            }
            return false;
        }
    }

    static class ViewHolder {
        RelativeLayout rl;
        TextView tv;

        ViewHolder(View view) {
            this.rl = (RelativeLayout) view.findViewById(C0291R.id.rl_item);
            this.tv = (TextView) view.findViewById(C0291R.id.tv);
        }
    }

    public RecordListAdapter(Context context) {
        this.context = context;
        reSearchDataSource();
    }

    public void reSearchDataSource() {
        this.list.clear();
        String path = "/storage/emulated/0/hikam_record/";
        Log.e("tag", "path :" + path);
        File[] recordList = new File(path).listFiles(new C06521());
        if (recordList == null) {
            recordList = new File[0];
        }
        for (File path2 : recordList) {
            RecordVideo item = new RecordVideo();
            String uriStr = path2.getPath().toString();
            item.setPath(uriStr);
            item.setVideopath(uriStr);
            item.setName(uriStr);
            this.list.add(item);
        }
    }

    public int getCount() {
        return this.list.size();
    }

    public Object getItem(int position) {
        return this.list.get(position);
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(this.context).inflate(C0291R.layout.item_record, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv.setText(((RecordVideo) this.list.get(position)).getName());
        return convertView;
    }

    public RecordVideo getRecordVideoByPosition(int position) {
        return (RecordVideo) this.list.get(position);
    }
}
