package com.jwkj.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.hikam.C0291R;
import com.jwkj.adapter.MessageAdapter;
import com.jwkj.data.Contact;
import com.jwkj.data.ContactDB;
import com.jwkj.data.DataManager;
import com.jwkj.data.Message;
import com.jwkj.global.Constants.Action;
import com.jwkj.global.FList;
import com.jwkj.global.NpcCommon;
import com.jwkj.utils.C0568T;
import com.jwkj.widget.NormalDialog;
import com.jwkj.widget.NormalDialog.OnButtonOkListener;
import com.p2p.core.P2PHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MessageActivity extends BaseActivity implements OnClickListener {
    public static final String RECEIVER_MSG = "com.jwkj.RECEIVER_MSG";
    public static final String REFRESH_MESSAGE = "com.jwkj.REFRESH_MESSAGE";
    public static final String SEND_MSG = "com.jwkj.SEND_MSG";
    MessageAdapter adapter;
    ImageView back;
    TextView charName_text;
    ImageView clear;
    Contact contact;
    NormalDialog dialog;
    boolean isRegReceiver;
    int lastSelection = 0;
    ListView list_msg;
    Context mContext;
    AlertDialog mExitDialog;
    EditText msg_text;
    public BroadcastReceiver receiver = new C04141();
    Button send;
    String toId;

    class C04141 extends BroadcastReceiver {
        C04141() {
        }

        public void onReceive(Context arg0, Intent intent) {
            if (intent.getAction().equals(MessageActivity.SEND_MSG)) {
                MessageActivity.this.refreshMessage();
            } else if (intent.getAction().equals(MessageActivity.REFRESH_MESSAGE)) {
                MessageActivity.this.refreshMessage();
            } else if (intent.getAction().equals(MessageActivity.RECEIVER_MSG)) {
                MessageActivity.this.refreshMessage();
                MessageActivity.this.clearNoReadMsg();
            }
        }
    }

    class C10842 implements OnButtonOkListener {
        C10842() {
        }

        public void onClick() {
            DataManager.clearMessageByActiveUserAndChatId(MessageActivity.this.mContext, NpcCommon.mThreeNum, MessageActivity.this.toId);
            MessageActivity.this.adapter.updateData(new ArrayList());
            MessageActivity.this.adapter.notifyDataSetChanged();
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0291R.layout.activity_message);
        this.mContext = this;
        this.contact = (Contact) getIntent().getSerializableExtra(ContactDB.TABLE_NAME);
        this.toId = this.contact.contactId;
        initComponent();
        regFilter();
        clearNoReadMsg();
    }

    public void initComponent() {
        this.back = (ImageView) findViewById(C0291R.id.back_btn);
        this.send = (Button) findViewById(C0291R.id.send);
        this.clear = (ImageView) findViewById(C0291R.id.clear);
        this.list_msg = (ListView) findViewById(C0291R.id.list_msg);
        this.msg_text = (EditText) findViewById(C0291R.id.msg_text);
        this.charName_text = (TextView) findViewById(C0291R.id.chat_name);
        if (this.contact != null) {
            this.charName_text.setText(this.contact.contactId);
        }
        List<Message> lists = DataManager.findMessageByActiveUserAndChatId(this.mContext, NpcCommon.mThreeNum, this.toId);
        Collections.sort(lists);
        this.lastSelection = lists.size();
        this.adapter = new MessageAdapter(this, lists);
        this.list_msg.setAdapter(this.adapter);
        this.list_msg.setSelection(this.lastSelection);
        this.back.setOnClickListener(this);
        this.send.setOnClickListener(this);
        this.msg_text.setOnClickListener(this);
        this.clear.setOnClickListener(this);
    }

    public void regFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(SEND_MSG);
        filter.addAction(REFRESH_MESSAGE);
        filter.addAction(RECEIVER_MSG);
        registerReceiver(this.receiver, filter);
        this.isRegReceiver = true;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case C0291R.id.back_btn:
                finish();
                return;
            case C0291R.id.clear:
                if (this.dialog == null || !this.dialog.isShowing()) {
                    this.dialog = new NormalDialog(this.mContext, this.mContext.getResources().getString(C0291R.string.delete_chat_records), this.mContext.getResources().getString(C0291R.string.confirm_clear_chat), this.mContext.getResources().getString(C0291R.string.clear), this.mContext.getResources().getString(C0291R.string.cancel));
                    this.dialog.setOnButtonOkListener(new C10842());
                    this.dialog.showDialog();
                    return;
                }
                Log.e("my", "isShowing");
                return;
            case C0291R.id.msg_text:
                this.list_msg.setSelection(this.lastSelection);
                return;
            case C0291R.id.send:
                String msgStr = this.msg_text.getText().toString();
                if (!msgStr.equals("")) {
                    String fromId = NpcCommon.mThreeNum;
                    if (fromId.equals(this.toId)) {
                        C0568T.showShort(this.mContext, getString(C0291R.string.send_msg_error1));
                        return;
                    }
                    Message msg = new Message();
                    msg.fromId = fromId;
                    msg.toId = this.toId;
                    msg.activeUser = NpcCommon.mThreeNum;
                    msg.msg = msgStr;
                    msg.msgTime = String.valueOf(System.currentTimeMillis());
                    msg.msgState = String.valueOf(1);
                    this.msg_text.setText("");
                    msg.msgFlag = P2PHandler.getInstance().sendMessage(this.toId, msgStr);
                    DataManager.insertMessage(this.mContext, msg);
                    Intent i = new Intent();
                    i.setAction(SEND_MSG);
                    sendBroadcast(i);
                    return;
                }
                return;
            default:
                return;
        }
    }

    public void onDestroy() {
        super.onDestroy();
        Log.e("my", "unreg");
        if (this.isRegReceiver) {
            unregisterReceiver(this.receiver);
        }
    }

    public void clearNoReadMsg() {
        this.contact.messageCount = 0;
        FList.getInstance().update(this.contact);
        Intent refreshContans = new Intent();
        refreshContans.setAction(Action.REFRESH_CONTANTS);
        this.mContext.sendBroadcast(refreshContans);
    }

    public void refreshMessage() {
        List<Message> lists = DataManager.findMessageByActiveUserAndChatId(this.mContext, NpcCommon.mThreeNum, this.toId);
        this.adapter.updateData(lists);
        this.adapter.notifyDataSetChanged();
        this.list_msg.setSelection(lists.size());
        this.lastSelection = lists.size();
    }

    public int getActivityInfo() {
        return 13;
    }
}
