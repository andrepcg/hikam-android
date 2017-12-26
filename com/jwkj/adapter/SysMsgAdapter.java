package com.jwkj.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.hikam.C0291R;
import com.jwkj.activity.SysMsgActivity;
import com.jwkj.data.DataManager;
import com.jwkj.data.SysMessage;
import com.jwkj.global.Constants.Action;
import com.jwkj.global.NpcCommon;
import com.jwkj.utils.Utils;
import com.jwkj.widget.NormalDialog;
import com.jwkj.widget.NormalDialog.OnButtonOkListener;
import java.util.HashMap;
import java.util.List;

public class SysMsgAdapter extends BaseAdapter {
    HashMap<Integer, ImageView> cacheArrow = new HashMap();
    HashMap<Integer, WebView> cacheContent = new HashMap();
    HashMap<Integer, TextView> cacheText = new HashMap();
    Context context;
    int lastExpandId = -1;
    List<SysMessage> list;
    private AlertDialog mDeleteDialog;

    public SysMsgAdapter(Context context, List<SysMessage> list) {
        this.list = list;
        this.context = context;
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

    public View getView(int position, View arg1, ViewGroup arg2) {
        LinearLayout view = (LinearLayout) arg1;
        if (view == null) {
            view = (LinearLayout) LayoutInflater.from(this.context).inflate(C0291R.layout.list_sysmsg_item, null);
        }
        final SysMessage msg = (SysMessage) this.list.get(position);
        RelativeLayout toggle = (RelativeLayout) view.findViewById(C0291R.id.expandable_toggle_button);
        toggle.setOnLongClickListener(new OnLongClickListener() {

            class C11021 implements OnButtonOkListener {
                C11021() {
                }

                public void onClick() {
                    DataManager.deleteSysMessage(SysMsgAdapter.this.context, msg.id);
                    Intent i = new Intent();
                    i.setAction(SysMsgActivity.DELETE_REFESH);
                    SysMsgAdapter.this.context.sendBroadcast(i);
                    Intent k = new Intent();
                    k.setAction(Action.RECEIVE_SYS_MSG);
                    SysMsgAdapter.this.context.sendBroadcast(k);
                }
            }

            public boolean onLongClick(View arg0) {
                Log.e("my", "long click");
                NormalDialog dialog = new NormalDialog(SysMsgAdapter.this.context, SysMsgAdapter.this.context.getResources().getString(C0291R.string.delete_sys_messages), SysMsgAdapter.this.context.getResources().getString(C0291R.string.confirm_delete), SysMsgAdapter.this.context.getResources().getString(C0291R.string.delete), SysMsgAdapter.this.context.getResources().getString(C0291R.string.cancel));
                dialog.setOnButtonOkListener(new C11021());
                dialog.showDialog();
                return true;
            }
        });
        TextView text = (TextView) view.findViewById(C0291R.id.title);
        TextView time = (TextView) view.findViewById(C0291R.id.date);
        ImageView arrow = (ImageView) toggle.findViewById(C0291R.id.arrow);
        WebView content = (WebView) view.findViewById(C0291R.id.content);
        if (msg.id == this.lastExpandId) {
            Animation rotate = new RotateAnimation(0.0f, 180.0f, 1, 0.5f, 1, 0.5f);
            rotate.setFillAfter(true);
            rotate.setDuration(380);
            arrow.startAnimation(rotate);
        } else {
            arrow.setImageResource(C0291R.drawable.arrow2);
        }
        text.setText(Utils.getMsgInfo(msg, this.context)[0]);
        time.setText(Utils.ConvertTimeByLong(Long.parseLong(msg.msg_time)));
        if (msg.msgState == 1) {
            text.setTextColor(this.context.getResources().getColor(C0291R.color.text_color_light));
        } else {
            text.setTextColor(this.context.getResources().getColor(C0291R.color.black));
        }
        this.cacheText.put(Integer.valueOf(msg.id), text);
        this.cacheArrow.put(Integer.valueOf(msg.id), arrow);
        this.cacheContent.put(Integer.valueOf(msg.id), content);
        return view;
    }

    public void upDateSysMsg(int position, int type) {
        SysMessage msg = (SysMessage) this.list.get(position);
        ImageView arrow = (ImageView) this.cacheArrow.get(Integer.valueOf(msg.id));
        if (type == 1) {
            ((WebView) this.cacheContent.get(Integer.valueOf(msg.id))).loadDataWithBaseURL(null, Utils.getMsgInfo(msg, this.context)[1], "text/html", "utf-8", null);
        }
        Animation rotate;
        if (this.lastExpandId == -1) {
            rotate = new RotateAnimation(0.0f, 180.0f, 1, 0.5f, 1, 0.5f);
            rotate.setFillAfter(true);
            rotate.setDuration(380);
            arrow.startAnimation(rotate);
            this.lastExpandId = msg.id;
        } else if (this.lastExpandId == msg.id) {
            rotate = new RotateAnimation(180.0f, 0.0f, 1, 0.5f, 1, 0.5f);
            rotate.setFillAfter(true);
            rotate.setDuration(380);
            arrow.startAnimation(rotate);
            this.lastExpandId = -1;
        } else {
            ImageView last_arrow = (ImageView) this.cacheArrow.get(Integer.valueOf(this.lastExpandId));
            Animation last_rotate = new RotateAnimation(180.0f, 0.0f, 1, 0.5f, 1, 0.5f);
            last_rotate.setFillAfter(true);
            last_rotate.setDuration(380);
            last_arrow.startAnimation(last_rotate);
            Animation rotateAnimation = new RotateAnimation(0.0f, 180.0f, 1, 0.5f, 1, 0.5f);
            rotateAnimation.setFillAfter(true);
            rotateAnimation.setDuration(380);
            arrow.startAnimation(rotateAnimation);
            this.lastExpandId = msg.id;
        }
        if (msg.msgState == 0) {
            DataManager.updateSysMessageState(this.context, msg.id, 1);
            ((TextView) this.cacheText.get(Integer.valueOf(msg.id))).setTextColor(this.context.getResources().getColor(C0291R.color.text_color_light));
            Intent i = new Intent();
            i.setAction(Action.RECEIVE_SYS_MSG);
            this.context.sendBroadcast(i);
            msg.msgState = 1;
        }
        Log.e("my", "lastExpandId:" + this.lastExpandId);
    }

    public void refresh() {
        this.list = DataManager.findSysMessageByActiveUser(this.context, NpcCommon.mThreeNum);
        notifyDataSetChanged();
    }
}
