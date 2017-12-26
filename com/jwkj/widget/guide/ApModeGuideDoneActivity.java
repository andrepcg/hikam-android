package com.jwkj.widget.guide;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.view.InputDeviceCompat;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.hikam.C0291R;
import com.jwkj.activity.BaseActivity;
import com.jwkj.adapter.WifiAdapter;
import com.jwkj.data.Contact;
import com.jwkj.data.ContactDB;
import com.jwkj.data.SharedPreferencesManager;
import com.jwkj.global.Constants.Action;
import com.jwkj.global.Constants.P2P;
import com.jwkj.global.FList;
import com.jwkj.global.MyApp;
import com.jwkj.utils.C0568T;
import com.jwkj.widget.BounceScrollView;
import com.jwkj.widget.MyInputDialog;
import com.jwkj.widget.MyInputDialog.OnButtonOkListener;
import com.jwkj.widget.MyListView;
import com.jwkj.widget.NormalDialog;
import com.jwkj.widget.NormalDialog.OnButtonCancelListener;
import com.p2p.core.P2PHandler;

public class ApModeGuideDoneActivity extends BaseActivity implements OnClickListener {
    Animation anim;
    private Contact contact;
    private CountDownTimer countDownTimer;
    NormalDialog dialog_info;
    MyInputDialog dialog_input;
    RelativeLayout dialog_input_mask;
    NormalDialog dialog_loading;
    private GetWiFiListThread getWiFiListThread = null;
    private Handler handler = new Handler(new C06391());
    private ImageView img_back;
    private ImageView img_refresh;
    private boolean isOne = true;
    private boolean isRegFilter = false;
    String last_modify_wifi_name;
    String last_modify_wifi_password;
    int last_modify_wifi_type;
    private MyListView list;
    private WifiAdapter mAdapter;
    private Context mContext;
    private BroadcastReceiver mReceiver = new C06413();
    private int progress = 0;
    private ProgressBar progressBar_list_wifi;
    private RelativeLayout rl_back;
    private RelativeLayout rl_continue;
    private RelativeLayout rl_finish;
    private BounceScrollView rl_one;
    private RelativeLayout rl_refresh;
    private RelativeLayout rl_two;
    private RelativeLayout rl_zero;
    private TextView tv;
    private TextView tv_title;

    class C06391 implements Callback {
        C06391() {
        }

        public boolean handleMessage(Message msg) {
            ApModeGuideDoneActivity.this.dialog_loading.showProgressDialog(1, msg.getData().getInt("progress"));
            return true;
        }
    }

    class C06413 extends BroadcastReceiver {
        C06413() {
        }

        public void onReceive(Context arg0, Intent intent) {
            if (!intent.getAction().equals(Action.CLOSE_INPUT_DIALOG)) {
                if (intent.getAction().equals(P2P.RET_GET_WIFI)) {
                    if (ApModeGuideDoneActivity.this.anim != null) {
                        ApModeGuideDoneActivity.this.anim.cancel();
                        ApModeGuideDoneActivity.this.anim = null;
                    }
                    int iCurrentId = intent.getIntExtra("iCurrentId", 0);
                    int iCount = intent.getIntExtra("iCount", 0);
                    int[] iType = intent.getIntArrayExtra("iType");
                    int[] iStrength = intent.getIntArrayExtra("iStrength");
                    String[] names = intent.getStringArrayExtra(SharedPreferencesManager.KEY_NAMES);
                    if (iCount > 0) {
                        if (iCount > iType.length) {
                            iCount = iType.length;
                        }
                        if (iCount > iStrength.length) {
                            iCount = iStrength.length;
                        }
                        if (iCount > names.length) {
                            iCount = names.length;
                        }
                        ApModeGuideDoneActivity.this.mAdapter.updateData(iCurrentId, iCount, iType, iStrength, names);
                        ApModeGuideDoneActivity.this.list.setSelection(iCurrentId);
                    }
                    ApModeGuideDoneActivity.this.closeProgress();
                    if (ApModeGuideDoneActivity.this.getWiFiListThread != null) {
                        ApModeGuideDoneActivity.this.getWiFiListThread.terminate();
                        ApModeGuideDoneActivity.this.getWiFiListThread = null;
                    }
                } else if (intent.getAction().equals(P2P.RET_SET_WIFI)) {
                    String tips;
                    result = intent.getIntExtra("result", -1);
                    boolean isFinish = false;
                    ApModeGuideDoneActivity.this.countDownTimer.cancel();
                    if (result == 0 || result == 1) {
                        tips = ApModeGuideDoneActivity.this.getString(C0291R.string.set_wifi_normal_tip);
                        FList.getInstance().setState(ApModeGuideDoneActivity.this.contact.contactId, 0);
                        Intent friends = new Intent();
                        friends.setAction(Action.GET_FRIENDS_STATE);
                        MyApp.app.sendBroadcast(friends);
                        isFinish = true;
                    } else if (result == -1 || result == 2 || result == 20) {
                        tips = ApModeGuideDoneActivity.this.getString(C0291R.string.wifi_password_error);
                    } else if (result == -2) {
                        tips = ApModeGuideDoneActivity.this.getString(C0291R.string.set_wifi_ap_tip);
                    } else if (result == -3) {
                        tips = ApModeGuideDoneActivity.this.getString(C0291R.string.ssid_not_exist);
                    } else if (result == -4) {
                        tips = ApModeGuideDoneActivity.this.getString(C0291R.string.wifi_signal_weak);
                    } else if (result == -5) {
                        tips = ApModeGuideDoneActivity.this.getString(C0291R.string.wifi_dhcp_failed);
                    } else {
                        tips = ApModeGuideDoneActivity.this.getString(C0291R.string.operator_error);
                    }
                    ApModeGuideDoneActivity.this.showSetWifiDialog(tips, isFinish);
                } else if (!intent.getAction().equals(P2P.ACK_RET_GET_NPC_SETTINGS) && !intent.getAction().equals(P2P.ACK_RET_GET_WIFI) && intent.getAction().equals(P2P.ACK_RET_SET_WIFI)) {
                    result = intent.getIntExtra("result", -1);
                    if (result == 9999) {
                        Intent i = new Intent();
                        i.setAction(Action.CONTROL_SETTING_PWD_ERROR);
                        ApModeGuideDoneActivity.this.mContext.sendBroadcast(i);
                    } else if (result == 9998) {
                        Log.e("my", "net error resend:set wifi");
                        if (ApModeGuideDoneActivity.this.dialog_loading != null && ApModeGuideDoneActivity.this.dialog_loading.isShowing()) {
                            P2PHandler.getInstance().setWifi(ApModeGuideDoneActivity.this.contact.contactModel, ApModeGuideDoneActivity.this.contact.contactId, ApModeGuideDoneActivity.this.contact.contactPassword, ApModeGuideDoneActivity.this.last_modify_wifi_type, ApModeGuideDoneActivity.this.last_modify_wifi_name, ApModeGuideDoneActivity.this.last_modify_wifi_password);
                        }
                    } else if (result == 9997) {
                        if (ApModeGuideDoneActivity.this.dialog_loading != null) {
                            ApModeGuideDoneActivity.this.dialog_loading.dismiss();
                        }
                        ApModeGuideDoneActivity.this.hideWiFiList();
                        P2PHandler.getInstance().getNpcSettings(ApModeGuideDoneActivity.this.contact.contactModel, ApModeGuideDoneActivity.this.contact.contactId, ApModeGuideDoneActivity.this.contact.contactPassword);
                    }
                }
            }
        }
    }

    private class GetWiFiListThread extends Thread {
        private boolean running;

        public final void run() {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Incorrect nodes count for selectOther: B:11:0x0027 in [B:10:0x0023, B:11:0x0027, B:15:0x0015, B:16:0x0015]
	at jadx.core.utils.BlockUtils.selectOther(BlockUtils.java:53)
	at jadx.core.dex.instructions.IfNode.initBlocks(IfNode.java:64)
	at jadx.core.dex.visitors.blocksmaker.BlockFinish.initBlocksInIfNodes(BlockFinish.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockFinish.visit(BlockFinish.java:33)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:306)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:199)
*/
            /*
            r2 = this;
            r0 = 1;
            r2.running = r0;
        L_0x0003:
            r0 = java.lang.Thread.interrupted();	 Catch:{ InterruptedException -> 0x000d, all -> 0x001e }
            if (r0 != 0) goto L_0x0016;	 Catch:{ InterruptedException -> 0x000d, all -> 0x001e }
        L_0x0009:
            r2.taskBody();	 Catch:{ InterruptedException -> 0x000d, all -> 0x001e }
            goto L_0x0003;
        L_0x000d:
            r0 = move-exception;
            r0 = r2.running;
            if (r0 != 0) goto L_0x0015;
        L_0x0012:
            r2.cleanUp();
        L_0x0015:
            return;
        L_0x0016:
            r0 = r2.running;
            if (r0 != 0) goto L_0x0015;
        L_0x001a:
            r2.cleanUp();
            goto L_0x0015;
        L_0x001e:
            r0 = move-exception;
            r1 = r2.running;
            if (r1 != 0) goto L_0x0027;
        L_0x0023:
            r2.cleanUp();
            goto L_0x0015;
        L_0x0027:
            throw r0;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.jwkj.widget.guide.ApModeGuideDoneActivity.GetWiFiListThread.run():void");
        }

        private GetWiFiListThread() {
            this.running = false;
        }

        private void cleanUp() {
        }

        private void taskBody() throws InterruptedException {
            Thread.sleep(3000);
            P2PHandler.getInstance().getWifiList(ApModeGuideDoneActivity.this.contact.contactModel, ApModeGuideDoneActivity.this.contact.contactId, ApModeGuideDoneActivity.this.contact.contactPassword);
        }

        public void terminate() {
            this.running = false;
            interrupt();
            try {
                join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0291R.layout.activity_apmode_guide_done);
        this.contact = (Contact) getIntent().getSerializableExtra(ContactDB.TABLE_NAME);
        this.mContext = this;
        initComponent();
        regFilter();
        P2PHandler.getInstance().getWifiList(this.contact.contactModel, this.contact.contactId, this.contact.contactPassword);
        this.getWiFiListThread = new GetWiFiListThread();
        this.getWiFiListThread.start();
    }

    private void showProgress() {
        this.progressBar_list_wifi.setVisibility(0);
    }

    private void closeProgress() {
        this.progressBar_list_wifi.setVisibility(8);
    }

    private void regFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Action.CLOSE_INPUT_DIALOG);
        filter.addAction(P2P.ACK_RET_GET_NPC_SETTINGS);
        filter.addAction(P2P.ACK_RET_SET_WIFI);
        filter.addAction(P2P.ACK_RET_GET_WIFI);
        filter.addAction(P2P.RET_SET_WIFI);
        filter.addAction(P2P.RET_GET_WIFI);
        this.mContext.registerReceiver(this.mReceiver, filter);
        this.isRegFilter = true;
    }

    private void initComponent() {
        this.countDownTimer = new CountDownTimer(100000, 1000) {
            public void onTick(long millisUntilFinished) {
                Message msg = new Message();
                msg.what = 0;
                Bundle bundle = new Bundle();
                bundle.putInt("progress", (int) (100 - (millisUntilFinished / 1000)));
                msg.setData(bundle);
                ApModeGuideDoneActivity.this.handler.sendMessage(msg);
                Log.e("few", "counting :" + millisUntilFinished);
            }

            public void onFinish() {
                ApModeGuideDoneActivity.this.showSetWifiDialog(ApModeGuideDoneActivity.this.getString(C0291R.string.wifi_time_out), true);
            }
        };
        this.tv_title = (TextView) findViewById(C0291R.id.title);
        this.img_refresh = (ImageView) findViewById(C0291R.id.img_refresh);
        this.dialog_input_mask = (RelativeLayout) findViewById(C0291R.id.dialog_input_mask);
        this.tv = (TextView) findViewById(C0291R.id.tv10);
        this.tv.setText(Html.fromHtml(getResources().getString(C0291R.string.colorful)));
        this.tv.setOnClickListener(this);
        this.img_back = (ImageView) findViewById(C0291R.id.img_back);
        this.img_back.setOnClickListener(this);
        this.rl_finish = (RelativeLayout) findViewById(C0291R.id.rl_finish);
        this.rl_finish.setOnClickListener(this);
        this.rl_back = (RelativeLayout) findViewById(C0291R.id.rl_back);
        this.rl_back.setOnClickListener(this);
        this.rl_one = (BounceScrollView) findViewById(C0291R.id.rl_one);
        this.rl_two = (RelativeLayout) findViewById(C0291R.id.rl_two);
        this.rl_zero = (RelativeLayout) findViewById(C0291R.id.rl_zero);
        this.list = (MyListView) findViewById(C0291R.id.list_wifi);
        this.rl_refresh = (RelativeLayout) findViewById(C0291R.id.rl_refresh);
        this.rl_refresh.setOnClickListener(this);
        this.mAdapter = new WifiAdapter(this.mContext, 1, this);
        this.list.setAdapter(this.mAdapter);
        this.progressBar_list_wifi = (ProgressBar) findViewById(C0291R.id.progressBar_list_wifi);
        this.rl_continue = (RelativeLayout) findViewById(C0291R.id.rl_continue);
        this.rl_continue.setOnClickListener(this);
        goToOne();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case C0291R.id.img_back:
                goToPre();
                return;
            case C0291R.id.rl_back:
                goToPre();
                return;
            case C0291R.id.rl_continue:
                goToOne();
                return;
            case C0291R.id.rl_finish:
                finish();
                return;
            case C0291R.id.rl_refresh:
                P2PHandler.getInstance().getWifiList(this.contact.contactModel, this.contact.contactId, this.contact.contactPassword);
                this.getWiFiListThread = new GetWiFiListThread();
                this.getWiFiListThread.start();
                this.anim = new RotateAnimation(0.0f, 360.0f, 1, 0.5f, 1, 0.5f);
                this.anim.setFillAfter(true);
                this.anim.setDuration(500);
                this.anim.setRepeatCount(500);
                this.anim.setInterpolator(new AccelerateInterpolator());
                this.img_refresh.startAnimation(this.anim);
                return;
            default:
                return;
        }
    }

    private void hideWiFiList() {
    }

    public void onBackPressed() {
        goToPre();
    }

    private void showSetWifiDialog(String tips, final boolean isFinish) {
        if (this.dialog_loading != null) {
            this.dialog_loading.dismiss();
        }
        if (this.dialog_info != null) {
            this.dialog_info.dismiss();
        }
        this.dialog_info = new NormalDialog(this, getString(C0291R.string.prompt), tips, "1", getString(C0291R.string.okay));
        this.dialog_info.showPromptDialog();
        this.dialog_info.setOnButtonCancelListener(new OnButtonCancelListener() {
            public void onClick() {
                ApModeGuideDoneActivity.this.dialog_info.dismiss();
                InputMethodManager imm = (InputMethodManager) ApModeGuideDoneActivity.this.getSystemService("input_method");
                if (imm != null) {
                    imm.hideSoftInputFromWindow(ApModeGuideDoneActivity.this.getWindow().getDecorView().getWindowToken(), 0);
                }
                if (isFinish) {
                    ApModeGuideDoneActivity.this.finish();
                }
            }
        });
    }

    private void goToPre() {
        if (this.progress != 0) {
            if (this.progress == 1) {
                finish();
            } else if (this.progress == 2) {
                goToOne();
            }
        }
    }

    private void goToZero() {
        this.progress = 0;
        this.tv_title.setText(C0291R.string.con_wifi_router);
        this.rl_zero.setVisibility(0);
        this.rl_one.setVisibility(8);
        this.rl_two.setVisibility(8);
    }

    private void goToOne() {
        this.tv_title.setText(C0291R.string.con_wifi_router);
        this.progress = 1;
        P2PHandler.getInstance().getWifiList(this.contact.contactModel, this.contact.contactId, this.contact.contactPassword);
        this.getWiFiListThread = new GetWiFiListThread();
        this.getWiFiListThread.start();
        this.rl_zero.setVisibility(8);
        this.rl_one.setVisibility(0);
        this.rl_two.setVisibility(8);
    }

    private void goToTwo() {
        this.progress = 2;
        this.tv_title.setText(C0291R.string.ap_mode);
        this.rl_zero.setVisibility(8);
        this.rl_one.setVisibility(8);
        this.rl_two.setVisibility(0);
    }

    public int getActivityInfo() {
        return InputDeviceCompat.SOURCE_GAMEPAD;
    }

    public void showModfyWifi(final int type, final String name) {
        Log.e("wifiname", "wifiname" + name + "  " + name.length());
        this.dialog_input = new MyInputDialog(this.mContext);
        this.dialog_input.setTitle(this.mContext.getResources().getString(C0291R.string.change_wifi) + "(" + name + ")");
        this.dialog_input.setBtn1_str(this.mContext.getResources().getString(C0291R.string.ensure));
        this.dialog_input.setBtn2_str(this.mContext.getResources().getString(C0291R.string.cancel));
        this.dialog_input.setOnButtonOkListener(new OnButtonOkListener() {
            public void onClick() {
                String password = ApModeGuideDoneActivity.this.dialog_input.getInput1Text().trim();
                if (type == 0 || !"".equals(password)) {
                    ApModeGuideDoneActivity.this.dialog_input.hide(ApModeGuideDoneActivity.this.dialog_input_mask);
                    if (ApModeGuideDoneActivity.this.dialog_loading == null) {
                        ApModeGuideDoneActivity.this.dialog_loading = new NormalDialog(ApModeGuideDoneActivity.this.mContext, ApModeGuideDoneActivity.this.mContext.getResources().getString(C0291R.string.verification), "", "", "");
                        ApModeGuideDoneActivity.this.dialog_loading.setStyle(2);
                    }
                    ApModeGuideDoneActivity.this.dialog_loading.showProgressDialog(0, 0);
                    ApModeGuideDoneActivity.this.last_modify_wifi_type = type;
                    ApModeGuideDoneActivity.this.last_modify_wifi_name = name;
                    ApModeGuideDoneActivity.this.last_modify_wifi_password = password;
                    if (type == 0) {
                        P2PHandler.getInstance().setWifi(ApModeGuideDoneActivity.this.contact.contactModel, ApModeGuideDoneActivity.this.contact.contactId, ApModeGuideDoneActivity.this.contact.contactPassword, type, name, "0");
                    } else {
                        P2PHandler.getInstance().setWifi(ApModeGuideDoneActivity.this.contact.contactModel, ApModeGuideDoneActivity.this.contact.contactId, ApModeGuideDoneActivity.this.contact.contactPassword, type, name, password);
                    }
                    FList.getInstance().setState(ApModeGuideDoneActivity.this.contact.contactId, 0);
                    Intent friends = new Intent();
                    friends.setAction(Action.GET_FRIENDS_STATE);
                    MyApp.app.sendBroadcast(friends);
                    ApModeGuideDoneActivity.this.countDownTimer.start();
                    return;
                }
                C0568T.showShort(ApModeGuideDoneActivity.this.mContext, (int) C0291R.string.input_wifi_pwd);
            }
        });
        this.dialog_input.show(this.dialog_input_mask);
        this.dialog_input.setInput1HintText((int) C0291R.string.input_wifi_pwd);
    }

    protected void onDestroy() {
        super.onDestroy();
        if (this.countDownTimer != null) {
            this.countDownTimer.cancel();
        }
        if (this.dialog_info != null) {
            this.dialog_info.dismiss();
        }
    }
}
