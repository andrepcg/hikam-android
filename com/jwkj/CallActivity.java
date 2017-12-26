package com.jwkj;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.com.streamax.miotp.p2p.jni.P2pJni;
import com.hikam.C0291R;
import com.jwkj.activity.MainActivity;
import com.jwkj.data.Contact;
import com.jwkj.data.ContactDB;
import com.jwkj.data.DataManager;
import com.jwkj.data.NearlyTell;
import com.jwkj.global.Constants.P2P;
import com.jwkj.global.FList;
import com.jwkj.global.MyApp;
import com.jwkj.global.NpcCommon;
import com.jwkj.utils.C0568T;
import com.jwkj.utils.PhoneWatcher;
import com.jwkj.utils.PhoneWatcher.OnCommingCallListener;
import com.jwkj.utils.Utils;
import com.jwkj.widget.HeaderView;
import com.p2p.core.BaseCallActivity;
import com.p2p.core.MediaPlayer;
import com.p2p.core.P2PHandler;
import com.p2p.core.P2PValue;

public class CallActivity extends BaseCallActivity implements OnClickListener {
    RelativeLayout accept;
    String callId;
    String callModel;
    ImageView call_anim;
    String contactName;
    HeaderView header_img;
    String ipFlag;
    boolean isAccept = false;
    String isAlarmTrigger;
    boolean isOutCall;
    boolean isRegFilter = false;
    boolean isReject = false;
    RelativeLayout layout_accept;
    Context mContext;
    PhoneWatcher mPhoneWatcher;
    private BroadcastReceiver mReceiver = new C02943();
    String password;
    RelativeLayout reject;
    TextView reject_text;
    TextView title_text;
    TextView top_text;
    int type;

    class C02943 extends BroadcastReceiver {
        C02943() {
        }

        public void onReceive(Context arg0, Intent intent) {
            if (intent.getAction().equals(P2P.P2P_ACCEPT)) {
                P2PHandler.getInstance().openAudioAndStartPlaying(CallActivity.this.type);
            } else if (intent.getAction().equals(P2P.P2P_READY)) {
                Intent intentCall = new Intent();
                if (CallActivity.this.type == 0) {
                    intentCall.setClass(CallActivity.this.mContext, VideoActivity.class);
                } else if (CallActivity.this.type == 1) {
                    intentCall.setClass(CallActivity.this.mContext, MonitorActivity.class);
                }
                intentCall.putExtra("type", CallActivity.this.type);
                intentCall.putExtra("callId", CallActivity.this.callId);
                intentCall.putExtra("password", CallActivity.this.password);
                intentCall.putExtra("callModel", CallActivity.this.callModel);
                intentCall.putExtra(ContactDB.COLUMN_CONTACT_NAME, CallActivity.this.contactName);
                intentCall.putExtra("isAlarmTrigger", CallActivity.this.isAlarmTrigger);
                intentCall.setFlags(268435456);
                CallActivity.this.mContext.startActivity(intentCall);
                CallActivity.this.finish();
            } else if (intent.getAction().equals(P2P.P2P_REJECT)) {
                CallActivity.this.reject();
            } else if (intent.getAction().equals("android.intent.action.SCREEN_OFF")) {
                CallActivity.this.reject();
            }
        }
    }

    class C10641 implements OnCommingCallListener {
        C10641() {
        }

        public void onCommingCall() {
            CallActivity.this.reject();
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window win = getWindow();
        win.addFlags(4718592);
        win.addFlags(2097280);
        this.mContext = this;
        setContentView(C0291R.layout.activity_call);
        this.isOutCall = getIntent().getBooleanExtra("isOutCall", false);
        this.callModel = getIntent().getStringExtra("callModel");
        this.isAlarmTrigger = getIntent().getStringExtra("isAlarmTrigger");
        this.callId = getIntent().getStringExtra("callId");
        this.contactName = getIntent().getStringExtra(ContactDB.COLUMN_CONTACT_NAME);
        this.ipFlag = getIntent().getStringExtra("ipFlag");
        this.type = getIntent().getIntExtra("type", -1);
        this.password = getIntent().getStringExtra("password");
        Log.e("oaosj", "call: " + this.callModel + " " + this.callId + " " + this.contactName + " " + this.password + " " + this.isOutCall + " " + this.type);
        if (Utils.hasDigit(this.callId)) {
            P2PConnect.setCurrent_state(1);
            P2PConnect.setCurrent_call_id(this.callId);
            initComponent();
            regFilter();
            startWatcher();
            String push_mesg = NpcCommon.mThreeNum + ":" + this.mContext.getResources().getString(C0291R.string.p2p_call_push_mesg);
            System.out.println("CallActivity callModel = " + this.callModel);
            P2PHandler.getInstance().call(NpcCommon.mThreeNum, this.password, this.isOutCall, this.type, this.callModel, this.callId, this.ipFlag, push_mesg);
        } else {
            if (this.type == 1) {
                C0568T.showShort(this.mContext, (int) C0291R.string.monitor_id_must_include_digit);
            } else {
                C0568T.showShort(this.mContext, (int) C0291R.string.call_id_must_include_digit);
            }
            finish();
        }
        if (P2pJni.P2PMediaGetMute() == 0) {
            P2pJni.P2PMediaSetMute(1);
        }
    }

    public void onHomePressed() {
        super.onHomePressed();
        reject();
    }

    private void startWatcher() {
        this.mPhoneWatcher = new PhoneWatcher(this.mContext);
        this.mPhoneWatcher.setOnCommingCallListener(new C10641());
        this.mPhoneWatcher.startWatcher();
    }

    public void initComponent() {
        this.top_text = (TextView) findViewById(C0291R.id.top_text);
        this.accept = (RelativeLayout) findViewById(C0291R.id.accept);
        this.layout_accept = (RelativeLayout) findViewById(C0291R.id.layout_accept);
        this.reject = (RelativeLayout) findViewById(C0291R.id.reject);
        this.reject_text = (TextView) findViewById(C0291R.id.reject_text);
        this.title_text = (TextView) findViewById(C0291R.id.title_text);
        this.call_anim = (ImageView) findViewById(C0291R.id.call_anim);
        this.header_img = (HeaderView) findViewById(C0291R.id.header_img);
        this.header_img.updateImage(this.callId, false);
        if (this.isOutCall) {
            this.reject_text.setText(C0291R.string.hungup);
            this.layout_accept.setVisibility(8);
            if (this.type == 1) {
                this.top_text.setText(this.mContext.getResources().getString(C0291R.string.connecting_to) + "......");
                if (this.contactName == null || this.contactName.equals("")) {
                    String strId = "";
                    if (P2PValue.HikamDeviceModelList.contains(this.callModel)) {
                        strId = Utils.showShortDevID(String.valueOf(this.callId));
                    } else {
                        strId = this.callId;
                    }
                    this.title_text.setText(strId);
                } else {
                    this.title_text.setText(this.contactName);
                }
                this.call_anim.setImageResource(C0291R.anim.monitor);
            } else {
                if (this.contactName == null || this.contactName.equals("")) {
                    this.title_text.setText(this.callId);
                } else {
                    this.title_text.setText(this.contactName);
                }
                this.call_anim.setImageResource(C0291R.anim.call_out);
                this.top_text.setText(this.mContext.getResources().getString(C0291R.string.calling_to) + "......");
            }
        } else {
            this.call_anim.setImageResource(C0291R.anim.call_in);
            this.reject_text.setText(C0291R.string.reject);
            this.layout_accept.setVisibility(0);
            Contact contact = FList.getInstance().isContact(this.callId);
            if (contact == null) {
                this.title_text.setText(this.callId);
            } else {
                this.title_text.setText(contact.contactName);
            }
        }
        final AnimationDrawable anim = (AnimationDrawable) this.call_anim.getDrawable();
        this.call_anim.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
            public boolean onPreDraw() {
                anim.start();
                return true;
            }
        });
        this.accept.setOnClickListener(this);
        this.reject.setOnClickListener(this);
    }

    public void regFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(P2P.P2P_ACCEPT);
        filter.addAction(P2P.P2P_READY);
        filter.addAction(P2P.P2P_REJECT);
        filter.addAction("android.intent.action.SCREEN_OFF");
        this.mContext.registerReceiver(this.mReceiver, filter);
        this.isRegFilter = true;
    }

    public void onBackPressed() {
        reject();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case C0291R.id.accept:
                if (!this.isAccept) {
                    this.isAccept = true;
                    P2PHandler.getInstance().accept();
                    return;
                }
                return;
            case C0291R.id.reject:
                reject();
                return;
            default:
                return;
        }
    }

    public void reject() {
        if (!this.isReject) {
            this.isReject = true;
            if (P2PValue.HikamDeviceModelList.contains(this.callModel)) {
                MediaPlayer.getInstance().p2p_close_stream();
            }
            P2PHandler.getInstance().reject();
            if (!activity_stack.containsKey(Integer.valueOf(1))) {
                startActivity(new Intent(this, MainActivity.class));
            }
            finish();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (this.isRegFilter) {
            this.mContext.unregisterReceiver(this.mReceiver);
            this.isRegFilter = false;
        }
        if (this.mPhoneWatcher != null) {
            this.mPhoneWatcher.stopWatcher();
        }
        insertNearly();
    }

    public void insertNearly() {
        NearlyTell nearlyTell = new NearlyTell();
        nearlyTell.activeUser = NpcCommon.mThreeNum;
        nearlyTell.tellId = this.callId;
        nearlyTell.tellTime = String.valueOf(System.currentTimeMillis());
        nearlyTell.tellState = this.type;
        if (this.isOutCall && this.isReject) {
            nearlyTell.tellState = 2;
        } else if (this.isOutCall && !this.isReject) {
            nearlyTell.tellState = 3;
        } else if (this.isOutCall || !this.isReject) {
            nearlyTell.tellState = 1;
        } else {
            nearlyTell.tellState = 0;
        }
        DataManager.insertNearlyTell(this.mContext, nearlyTell);
    }

    public int getActivityInfo() {
        return 36;
    }

    protected void onGoBack() {
        MyApp.app.showNotification();
    }

    protected void onGoFront() {
        MyApp.app.hideNotification();
    }

    protected void onExit() {
        MyApp.app.hideNotification();
    }
}
