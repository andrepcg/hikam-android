package com.jwkj.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.hikam.C0291R;
import com.jwkj.data.Contact;
import com.jwkj.data.Message;
import com.jwkj.global.FList;
import com.jwkj.global.NpcCommon;
import com.jwkj.utils.Utils;
import com.jwkj.widget.HeaderView;
import java.util.List;

public class MessageAdapter extends BaseAdapter {
    private Context context;
    private List<Message> lists;

    class ViewHolder {
        public TextView content;
        public HeaderView head_img;
        public TextView sendTime;
        public TextView userName;

        ViewHolder() {
        }

        public TextView getSendTime() {
            return this.sendTime;
        }

        public void setSendTime(TextView sendTime) {
            this.sendTime = sendTime;
        }

        public TextView getUserName() {
            return this.userName;
        }

        public void setUserName(TextView userName) {
            this.userName = userName;
        }

        public TextView getContent() {
            return this.content;
        }

        public void setContent(TextView content) {
            this.content = content;
        }

        public HeaderView getHead_img() {
            return this.head_img;
        }

        public void setHead_img(HeaderView head_img) {
            this.head_img = head_img;
        }
    }

    public MessageAdapter(Context context, List<Message> lists) {
        this.context = context;
        this.lists = lists;
    }

    public int getCount() {
        return this.lists.size();
    }

    public Object getItem(int arg0) {
        return this.lists.get(arg0);
    }

    public long getItemId(int arg0) {
        return 0;
    }

    public int getItemViewType(int position) {
        if (isComming((Message) this.lists.get(position))) {
            return 0;
        }
        return 1;
    }

    public int getViewTypeCount() {
        return 2;
    }

    public View getView(int arg0, View arg1, ViewGroup arg2) {
        ViewHolder holder;
        View view = arg1;
        Message msg = (Message) this.lists.get(arg0);
        boolean isCommingMsg = isComming(msg);
        if (view == null) {
            if (isCommingMsg) {
                Log.e("my", "inflater left");
                view = LayoutInflater.from(this.context).inflate(C0291R.layout.message_left, null);
            } else {
                Log.e("my", "inflater right");
                view = LayoutInflater.from(this.context).inflate(C0291R.layout.message_right, null);
            }
            holder = new ViewHolder();
            holder.setHead_img((HeaderView) view.findViewById(C0291R.id.iv_userhead));
            holder.setUserName((TextView) view.findViewById(C0291R.id.tv_username));
            holder.setContent((TextView) view.findViewById(C0291R.id.tv_chatcontent));
            holder.setSendTime((TextView) view.findViewById(C0291R.id.tv_sendtime));
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        if (isCommingMsg) {
            Contact contact = FList.getInstance().isContact(msg.fromId);
            holder.getHead_img().updateImage(msg.fromId, false);
            if (contact != null) {
                holder.getUserName().setText(contact.contactName);
            } else {
                holder.getUserName().setText(msg.fromId);
            }
        } else {
            holder.getHead_img().updateImage(NpcCommon.mThreeNum, false);
            holder.getUserName().setText(C0291R.string.me);
        }
        holder.getContent().setText(msg.msg);
        if (Integer.parseInt(msg.msgState) == 1) {
            holder.getSendTime().setText(C0291R.string.sending);
        } else if (Integer.parseInt(msg.msgState) == 2) {
            holder.getSendTime().setText(C0291R.string.send_fault);
        } else {
            holder.getSendTime().setText(Utils.ConvertTimeByLong(Long.parseLong(msg.msgTime)));
        }
        return view;
    }

    public boolean isComming(Message msg) {
        if (msg.fromId.equals(msg.activeUser)) {
            return false;
        }
        return true;
    }

    public void updateData(List<Message> lists) {
        this.lists = lists;
    }
}
