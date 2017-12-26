package com.jwkj.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.hikam.C0291R;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RecordAdapter extends BaseAdapter {
    public static Date startTime;
    Context context;
    public List<String> list = new ArrayList();
    public List<Integer> rateList = new ArrayList();

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

    public RecordAdapter(Context context, List<String> list, List<Integer> rateList) {
        this.context = context;
        this.list = list;
        this.rateList = rateList;
    }

    public int getCount() {
        return this.list.size();
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
            view = LayoutInflater.from(this.context).inflate(C0291R.layout.list_record_item, null);
            holder = new ViewHolder();
            holder.setRecord_name((TextView) view.findViewById(C0291R.id.rName));
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        if (((String) this.list.get(arg0)).startsWith("disc1/")) {
            holder.getRecord_name().setText(((String) this.list.get(arg0)).substring("disc1/".length()));
        } else if (((String) this.list.get(arg0)).startsWith("/tmp/mmc/")) {
            String fileFullName = ((String) this.list.get(arg0)).substring("/tmp/mmc/".length());
            holder.getRecord_name().setText(fileFullName.substring(0, fileFullName.length() - ".avx".length()).replaceAll("\\.", ":") + fileFullName.substring(fileFullName.length() - ".avx".length()));
        } else {
            holder.getRecord_name().setText((CharSequence) this.list.get(arg0));
        }
        return view;
    }

    public String getLastItem() {
        if (this.list.size() <= 0) {
            return "";
        }
        if (((String) this.list.get(this.list.size() - 1)).startsWith("disc1/")) {
            return ((String) this.list.get(this.list.size() - 1)).substring(6, 22).replace("_", " ");
        }
        if (!((String) this.list.get(this.list.size() - 1)).startsWith("/tmp/mmc/")) {
            return "";
        }
        String lastTime = ((String) this.list.get(this.list.size() - 1)).substring("/tmp/mmc/".length(), 21).replace("_", " ").replace(".", ":");
        StringBuilder lastTimeResult = new StringBuilder();
        lastTimeResult.append("20");
        lastTimeResult.append(lastTime.substring(0, 2));
        lastTimeResult.append("-");
        lastTimeResult.append(lastTime.substring(2, 4));
        lastTimeResult.append("-");
        lastTimeResult.append(lastTime.substring(4, lastTime.length()));
        return lastTimeResult.toString();
    }

    public static void setStartTime(Date startTime) {
        startTime = startTime;
    }

    public void setList(List<String> list, List<Integer> rateList) {
        this.list = list;
        this.rateList = rateList;
    }

    public void upLoadData(List<String> loadData) {
        Log.e("listsize", "old_list_size" + this.list.size());
        Log.e("loaddate", "loaddata_size" + loadData.size());
        if (loadData.size() > 0) {
            List<String> removeList = new ArrayList();
            List<String> addList = new ArrayList();
            for (String str : loadData) {
                for (String s : this.list) {
                    if (str.equals(s)) {
                        removeList.add(str);
                    }
                    Log.e("adddate", s + "--");
                }
                addList.add(str);
                Log.e("adddate", str);
            }
            Log.e("removelist", "removelist" + removeList.size());
            addList.removeAll(removeList);
            Log.e("removelist", "removelist" + addList.size());
            this.list.addAll(addList);
            Log.e("listsize", "list_size--" + this.list.size());
            for (String st : this.list) {
                Log.e("datas", "data" + st);
            }
            notifyDataSetChanged();
        }
    }

    public List<String> getList() {
        return this.list;
    }

    public List<Integer> getRateList() {
        return this.rateList;
    }

    public void loadData() {
        notifyDataSetChanged();
    }
}
