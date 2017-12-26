package com.jwkj.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.hikam.C0291R;
import com.jwkj.CallActivity;
import com.jwkj.adapter.TellDetailAdapter;
import com.jwkj.data.Contact;
import com.jwkj.data.ContactDB;
import com.jwkj.data.DataManager;
import com.jwkj.data.NearlyTell;
import com.jwkj.global.Constants.Action;
import com.jwkj.global.FList;
import com.jwkj.global.NpcCommon;
import com.jwkj.utils.C0568T;
import com.jwkj.widget.HeaderView;
import com.jwkj.widget.MyInputDialog;
import com.jwkj.widget.MyInputDialog.OnButtonOkListener;
import java.util.Collections;
import java.util.List;

public class TellDetailActivity extends BaseActivity implements OnClickListener {
    RelativeLayout add_contact;
    ImageView back_btn;
    RelativeLayout call;
    MyInputDialog dialog_input;
    RelativeLayout dialog_input_mask;
    HeaderView header_img;
    boolean isFriend = false;
    boolean isRegFilter = false;
    ListView list_tell;
    TellDetailAdapter mAdapter;
    Context mContext;
    private AlertDialog mInputPasswordDialog;
    BroadcastReceiver mReceiver = new C04551();
    RelativeLayout message;
    RelativeLayout monitor;
    NearlyTell nearlyTell;
    ImageView sep_one;
    ImageView sep_two;
    TextView userName;

    class C04551 extends BroadcastReceiver {
        C04551() {
        }

        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Action.ACTION_REFRESH_NEARLY_TELL)) {
                if (TellDetailActivity.this.mAdapter != null) {
                    List<NearlyTell> list = DataManager.findNearlyTellByActiveUserAndTellId(TellDetailActivity.this.mContext, NpcCommon.mThreeNum, TellDetailActivity.this.nearlyTell.tellId);
                    Collections.sort(list);
                    TellDetailActivity.this.mAdapter.updateData(list);
                }
            } else if (intent.getAction().equals(Action.ADD_CONTACT_SUCCESS)) {
                TellDetailActivity.this.changeData();
            }
        }
    }

    class C10922 implements OnButtonOkListener {
        C10922() {
        }

        public void onClick() {
            String password = TellDetailActivity.this.dialog_input.getInput1Text();
            if ("".equals(password.trim())) {
                C0568T.showShort(TellDetailActivity.this.mContext, (int) C0291R.string.input_monitor_pwd);
            } else if (password.length() > 9) {
                C0568T.showShort(TellDetailActivity.this.mContext, (int) C0291R.string.password_length_error);
            } else {
                TellDetailActivity.this.dialog_input.hide(TellDetailActivity.this.dialog_input_mask);
                Intent monitor = new Intent();
                monitor.setClass(TellDetailActivity.this.mContext, CallActivity.class);
                monitor.putExtra("callId", TellDetailActivity.this.nearlyTell.tellId);
                monitor.putExtra("password", password);
                monitor.putExtra("isOutCall", true);
                monitor.putExtra("type", 1);
                TellDetailActivity.this.startActivity(monitor);
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0291R.layout.activity_tell_detail);
        this.mContext = this;
        this.nearlyTell = (NearlyTell) getIntent().getSerializableExtra("nearlyTell");
        initComponent();
        regFilter();
    }

    public void initComponent() {
        this.dialog_input_mask = (RelativeLayout) findViewById(C0291R.id.dialog_input_mask);
        this.header_img = (HeaderView) findViewById(C0291R.id.header_img);
        this.header_img.updateImage(this.nearlyTell.tellId, false);
        this.userName = (TextView) findViewById(C0291R.id.userName);
        this.back_btn = (ImageView) findViewById(C0291R.id.back_btn);
        this.list_tell = (ListView) findViewById(C0291R.id.list_tell_detial);
        this.call = (RelativeLayout) findViewById(C0291R.id.call);
        this.monitor = (RelativeLayout) findViewById(C0291R.id.monitor);
        this.message = (RelativeLayout) findViewById(C0291R.id.message);
        this.add_contact = (RelativeLayout) findViewById(C0291R.id.add_contact);
        this.sep_one = (ImageView) findViewById(C0291R.id.separator_one);
        this.sep_two = (ImageView) findViewById(C0291R.id.separator_two);
        this.back_btn.setOnClickListener(this);
        this.call.setOnClickListener(this);
        this.monitor.setOnClickListener(this);
        this.message.setOnClickListener(this);
        this.add_contact.setOnClickListener(this);
        List<NearlyTell> list = DataManager.findNearlyTellByActiveUserAndTellId(this.mContext, NpcCommon.mThreeNum, this.nearlyTell.tellId);
        Collections.sort(list);
        this.mAdapter = new TellDetailAdapter(this, list);
        this.list_tell.setAdapter(this.mAdapter);
        changeData();
    }

    private void changeData() {
        Contact contact = FList.getInstance().isContact(this.nearlyTell.tellId);
        if (contact != null) {
            this.isFriend = true;
        }
        if (this.isFriend) {
            if (this.nearlyTell.tellId.charAt(0) == '0') {
                this.monitor.setVisibility(8);
                this.message.setVisibility(0);
            } else {
                this.monitor.setVisibility(0);
                this.message.setVisibility(8);
            }
            this.add_contact.setVisibility(8);
            this.userName.setText(contact.contactName);
            this.sep_two.setVisibility(8);
            return;
        }
        if (this.nearlyTell.tellId.charAt(0) == '0') {
            this.monitor.setVisibility(8);
            this.sep_two.setVisibility(8);
        } else {
            this.monitor.setVisibility(0);
        }
        this.add_contact.setVisibility(0);
        this.message.setVisibility(8);
        this.userName.setText(this.nearlyTell.tellId);
        if (this.nearlyTell.tellId.contains("+")) {
            this.monitor.setVisibility(8);
            this.add_contact.setVisibility(8);
            this.sep_one.setVisibility(8);
            this.sep_two.setVisibility(8);
        }
    }

    public void regFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Action.ACTION_REFRESH_NEARLY_TELL);
        filter.addAction(Action.ADD_CONTACT_SUCCESS);
        this.mContext.registerReceiver(this.mReceiver, filter);
        this.isRegFilter = true;
    }

    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case C0291R.id.add_contact:
                Intent add_contact = new Intent(this.mContext, AddContactNextActivity.class);
                Contact contact = new Contact();
                contact.contactId = this.nearlyTell.tellId;
                add_contact.putExtra(ContactDB.TABLE_NAME, contact);
                this.mContext.startActivity(add_contact);
                return;
            case C0291R.id.back_btn:
                finish();
                return;
            case C0291R.id.call:
                Intent call = new Intent();
                call.setClass(this.mContext, CallActivity.class);
                call.putExtra("callId", this.nearlyTell.tellId);
                call.putExtra("isOutCall", true);
                call.putExtra("type", 0);
                startActivity(call);
                finish();
                return;
            case C0291R.id.message:
                if (this.isFriend) {
                    Intent goMessage = new Intent(this, MessageActivity.class);
                    goMessage.putExtra("user", FList.getInstance().isContact(this.nearlyTell.tellId));
                    startActivity(goMessage);
                    finish();
                    return;
                }
                return;
            case C0291R.id.monitor:
                if (this.isFriend) {
                    Intent monitor = new Intent();
                    monitor.setClass(this.mContext, CallActivity.class);
                    monitor.putExtra("callId", this.nearlyTell.tellId);
                    monitor.putExtra("password", FList.getInstance().isContact(this.nearlyTell.tellId).contactPassword);
                    monitor.putExtra("isOutCall", true);
                    monitor.putExtra("type", 1);
                    startActivity(monitor);
                    finish();
                    return;
                }
                showInputPwd();
                return;
            default:
                return;
        }
    }

    public void showInputPwd() {
        this.dialog_input = new MyInputDialog(this.mContext);
        this.dialog_input.setTitle(this.mContext.getResources().getString(C0291R.string.monitor));
        this.dialog_input.setBtn1_str(this.mContext.getResources().getString(C0291R.string.ensure));
        this.dialog_input.setBtn2_str(this.mContext.getResources().getString(C0291R.string.cancel));
        this.dialog_input.setOnButtonOkListener(new C10922());
        this.dialog_input.show(this.dialog_input_mask);
        this.dialog_input.setInput1Type_number();
        this.dialog_input.setInput1HintText((int) C0291R.string.input_monitor_pwd);
    }

    public void onBackPressed() {
        if (this.dialog_input == null || !this.dialog_input.isShowing()) {
            finish();
        } else {
            this.dialog_input.hide(this.dialog_input_mask);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (this.isRegFilter) {
            this.isRegFilter = false;
            this.mContext.unregisterReceiver(this.mReceiver);
        }
    }

    public int getActivityInfo() {
        return 29;
    }
}
