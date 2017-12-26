package com.jwkj.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import com.hikam.C0291R;
import com.jwkj.activity.MainControlActivity;
import com.jwkj.adapter.WifiAdapter;
import com.jwkj.data.Contact;
import com.jwkj.data.ContactDB;
import com.jwkj.data.SharedPreferencesManager;
import com.jwkj.global.Constants.Action;
import com.jwkj.global.Constants.P2P;
import com.jwkj.global.FList;
import com.jwkj.global.MyApp;
import com.jwkj.thread.DelayThread;
import com.jwkj.thread.DelayThread.OnRunListener;
import com.jwkj.utils.C0568T;
import com.jwkj.widget.MyInputDialog;
import com.jwkj.widget.MyListView;
import com.jwkj.widget.NormalDialog;
import com.jwkj.widget.NormalDialog.OnButtonCancelListener;
import com.jwkj.widget.NormalDialog.OnButtonOkListener;
import com.p2p.core.P2PHandler;
import java.lang.reflect.Field;

public class NetControlFrag extends BaseFragment implements OnClickListener {
    private Contact contact;
    private CountDownTimer countDownTimer;
    NormalDialog dialog_info;
    MyInputDialog dialog_input;
    RelativeLayout dialog_input_mask;
    NormalDialog dialog_loading;
    private GetWiFiListThread getWiFiListThread = null;
    private Handler handler = new Handler(new C05361());
    private boolean isRegFilter = false;
    int last_modify_net_type;
    String last_modify_wifi_name;
    String last_modify_wifi_password;
    int last_modify_wifi_type;
    int last_net_type;
    MyListView list;
    RelativeLayout list_wifi_bar;
    LinearLayout list_wifi_content;
    WifiAdapter mAdapter;
    private Context mContext;
    private BroadcastReceiver mReceiver = new C05383();
    RelativeLayout net_type_bar;
    LinearLayout net_type_radio;
    ProgressBar progressBar_list_wifi;
    ProgressBar progressBar_net_type;
    RadioButton radio_one;
    RadioButton radio_two;

    class C05361 implements Callback {
        C05361() {
        }

        public boolean handleMessage(Message msg) {
            NetControlFrag.this.dialog_loading.showProgressDialog(1, msg.getData().getInt("progress"));
            return true;
        }
    }

    class C05383 extends BroadcastReceiver {
        C05383() {
        }

        public void onReceive(Context arg0, Intent intent) {
            if (intent.getAction().equals(Action.CLOSE_INPUT_DIALOG)) {
                if (NetControlFrag.this.dialog_input != null) {
                    NetControlFrag.this.dialog_input.hide(NetControlFrag.this.dialog_input_mask);
                }
            } else if (intent.getAction().equals(P2P.RET_GET_NET_TYPE)) {
                int type = intent.getIntExtra("type", -1);
                if (type == 0) {
                    NetControlFrag.this.last_net_type = 0;
                    NetControlFrag.this.radio_one.setChecked(true);
                    if (NetControlFrag.this.contact.contactType == 5 || NetControlFrag.this.contact.contactType == 7) {
                        NetControlFrag.this.showProgressWiFiList();
                        P2PHandler.getInstance().getWifiList(NetControlFrag.this.contact.contactModel, NetControlFrag.this.contact.contactId, NetControlFrag.this.contact.contactPassword);
                    } else {
                        NetControlFrag.this.hideWiFiList();
                    }
                } else if (type == 1) {
                    NetControlFrag.this.last_net_type = 1;
                    NetControlFrag.this.radio_two.setChecked(true);
                    NetControlFrag.this.showProgressWiFiList();
                    P2PHandler.getInstance().getWifiList(NetControlFrag.this.contact.contactModel, NetControlFrag.this.contact.contactId, NetControlFrag.this.contact.contactPassword);
                    if (NetControlFrag.this.getWiFiListThread != null) {
                        NetControlFrag.this.getWiFiListThread = new GetWiFiListThread();
                        NetControlFrag.this.getWiFiListThread.start();
                    }
                }
                NetControlFrag.this.showNetType();
                NetControlFrag.this.setRadioEnable(true);
            } else if (intent.getAction().equals(P2P.RET_SET_NET_TYPE)) {
                if (intent.getIntExtra("result", -1) == 0) {
                    NetControlFrag.this.last_net_type = NetControlFrag.this.last_modify_net_type;
                    if (NetControlFrag.this.last_modify_net_type == 1) {
                        NetControlFrag.this.showProgressWiFiList();
                        P2PHandler.getInstance().getWifiList(NetControlFrag.this.contact.contactModel, NetControlFrag.this.contact.contactId, NetControlFrag.this.contact.contactPassword);
                        NetControlFrag.this.radio_two.setChecked(true);
                    } else {
                        NetControlFrag.this.hideWiFiList();
                        NetControlFrag.this.radio_one.setChecked(true);
                    }
                    C0568T.showShort(NetControlFrag.this.mContext, (int) C0291R.string.modify_success);
                } else {
                    if (NetControlFrag.this.last_net_type == 1) {
                        NetControlFrag.this.showProgressWiFiList();
                        NetControlFrag.this.radio_two.setChecked(true);
                    } else {
                        NetControlFrag.this.hideWiFiList();
                        NetControlFrag.this.radio_one.setChecked(true);
                    }
                    C0568T.showShort(NetControlFrag.this.mContext, (int) C0291R.string.operator_error);
                }
                NetControlFrag.this.showNetType();
                NetControlFrag.this.setRadioEnable(true);
            } else if (intent.getAction().equals(P2P.RET_GET_WIFI)) {
                int iCurrentId = intent.getIntExtra("iCurrentId", 0);
                int iCount = intent.getIntExtra("iCount", 0);
                int[] iType = intent.getIntArrayExtra("iType");
                int[] iStrength = intent.getIntArrayExtra("iStrength");
                String[] names = intent.getStringArrayExtra(SharedPreferencesManager.KEY_NAMES);
                if (iType != null && iStrength != null && names != null) {
                    if (iCount > iType.length) {
                        iCount = iType.length;
                    }
                    if (iCount > iStrength.length) {
                        iCount = iStrength.length;
                    }
                    if (iCount > names.length) {
                        iCount = names.length;
                    }
                    NetControlFrag.this.mAdapter.updateData(iCurrentId, iCount, iType, iStrength, names);
                    NetControlFrag.this.showWiFiList();
                    NetControlFrag.this.list.setSelection(iCurrentId);
                    if (NetControlFrag.this.getWiFiListThread != null) {
                        NetControlFrag.this.getWiFiListThread.terminate();
                        NetControlFrag.this.getWiFiListThread = null;
                    }
                } else if (NetControlFrag.this.getWiFiListThread != null) {
                    NetControlFrag.this.getWiFiListThread.terminate();
                    NetControlFrag.this.getWiFiListThread = null;
                }
            } else if (intent.getAction().equals(P2P.RET_SET_WIFI)) {
                String tips;
                if (NetControlFrag.this.dialog_loading != null) {
                    NetControlFrag.this.dialog_loading.dismiss();
                }
                result = intent.getIntExtra("result", -1);
                System.out.println("RET_SET_WIFI " + result);
                boolean isFinish = false;
                NetControlFrag.this.countDownTimer.cancel();
                if (result == 0 || result == 1) {
                    tips = NetControlFrag.this.getString(C0291R.string.set_wifi_normal_tip);
                    FList.getInstance().setState(NetControlFrag.this.contact.contactId, 0);
                    Intent friends = new Intent();
                    friends.setAction(Action.GET_FRIENDS_STATE);
                    MyApp.app.sendBroadcast(friends);
                    isFinish = true;
                } else if (result == -1 || result == 2 || result == 20) {
                    tips = NetControlFrag.this.getString(C0291R.string.wifi_password_error);
                } else if (result == -2) {
                    tips = NetControlFrag.this.getString(C0291R.string.set_wifi_ap_tip);
                } else if (result == -3) {
                    tips = NetControlFrag.this.getString(C0291R.string.ssid_not_exist);
                } else if (result == -4) {
                    tips = NetControlFrag.this.getString(C0291R.string.wifi_signal_weak);
                } else if (result == -5) {
                    tips = NetControlFrag.this.getString(C0291R.string.wifi_dhcp_failed);
                } else {
                    tips = NetControlFrag.this.getString(C0291R.string.operator_error);
                }
                NetControlFrag.this.showSetWifiDialog(tips, isFinish);
            } else if (intent.getAction().equals(P2P.ACK_RET_GET_NPC_SETTINGS)) {
                result = intent.getIntExtra("result", -1);
                if (result == 9999) {
                    i = new Intent();
                    i.setAction(Action.CONTROL_SETTING_PWD_ERROR);
                    NetControlFrag.this.mContext.sendBroadcast(i);
                } else if (result == 9998) {
                    Log.e("my", "net error resend:get npc settings");
                    P2PHandler.getInstance().getNpcSettings(NetControlFrag.this.contact.contactModel, NetControlFrag.this.contact.contactId, NetControlFrag.this.contact.contactPassword);
                }
            } else if (intent.getAction().equals(P2P.ACK_RET_SET_NET_TYPE)) {
                result = intent.getIntExtra("result", -1);
                if (result == 9999) {
                    i = new Intent();
                    i.setAction(Action.CONTROL_SETTING_PWD_ERROR);
                    NetControlFrag.this.mContext.sendBroadcast(i);
                } else if (result == 9998) {
                    Log.e("my", "net error resend:set npc settings net type");
                    if (NetControlFrag.this.dialog_loading != null && NetControlFrag.this.dialog_loading.isShowing()) {
                        P2PHandler.getInstance().setNetType(NetControlFrag.this.contact.contactModel, NetControlFrag.this.contact.contactId, NetControlFrag.this.contact.contactPassword, NetControlFrag.this.last_modify_net_type);
                    }
                } else if (result == 9997) {
                    if (NetControlFrag.this.dialog_loading != null) {
                        NetControlFrag.this.dialog_loading.dismiss();
                    }
                    NetControlFrag.this.hideWiFiList();
                    NetControlFrag.this.showProgress_net_type();
                    P2PHandler.getInstance().getNpcSettings(NetControlFrag.this.contact.contactModel, NetControlFrag.this.contact.contactId, NetControlFrag.this.contact.contactPassword);
                    NetControlFrag.this.setRadioEnable(true);
                }
            } else if (intent.getAction().equals(P2P.ACK_RET_GET_WIFI)) {
                result = intent.getIntExtra("result", -1);
                if (result == 9999) {
                    i = new Intent();
                    i.setAction(Action.CONTROL_SETTING_PWD_ERROR);
                    NetControlFrag.this.mContext.sendBroadcast(i);
                } else if (result == 9998) {
                    Log.e("my", "net error resend:get wifi list");
                    P2PHandler.getInstance().getWifiList(NetControlFrag.this.contact.contactModel, NetControlFrag.this.contact.contactId, NetControlFrag.this.contact.contactPassword);
                }
            } else if (intent.getAction().equals(P2P.ACK_RET_SET_WIFI)) {
                System.out.println("ACK_RET_SET_WIFI");
                result = intent.getIntExtra("result", -1);
                if (result == 9999) {
                    i = new Intent();
                    i.setAction(Action.CONTROL_SETTING_PWD_ERROR);
                    NetControlFrag.this.mContext.sendBroadcast(i);
                } else if (result == 9998) {
                    Log.e("my", "net error resend:set wifi");
                    if (NetControlFrag.this.dialog_loading != null && NetControlFrag.this.dialog_loading.isShowing()) {
                        P2PHandler.getInstance().setWifi(NetControlFrag.this.contact.contactModel, NetControlFrag.this.contact.contactId, NetControlFrag.this.contact.contactPassword, NetControlFrag.this.last_modify_wifi_type, NetControlFrag.this.last_modify_wifi_name, NetControlFrag.this.last_modify_wifi_password);
                    }
                } else if (result == 9997) {
                    if (NetControlFrag.this.dialog_loading != null) {
                        NetControlFrag.this.dialog_loading.dismiss();
                    }
                    NetControlFrag.this.hideWiFiList();
                    NetControlFrag.this.showProgress_net_type();
                    P2PHandler.getInstance().getNpcSettings(NetControlFrag.this.contact.contactModel, NetControlFrag.this.contact.contactId, NetControlFrag.this.contact.contactPassword);
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
            throw new UnsupportedOperationException("Method not decompiled: com.jwkj.fragment.NetControlFrag.GetWiFiListThread.run():void");
        }

        private GetWiFiListThread() {
            this.running = false;
        }

        private void cleanUp() {
        }

        private void taskBody() throws InterruptedException {
            Thread.sleep(3000);
            P2PHandler.getInstance().getWifiList(NetControlFrag.this.contact.contactModel, NetControlFrag.this.contact.contactId, NetControlFrag.this.contact.contactPassword);
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

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mContext = MainControlActivity.mContext;
        this.contact = (Contact) getArguments().getSerializable(ContactDB.TABLE_NAME);
        View view = inflater.inflate(C0291R.layout.fragment_net_control, container, false);
        initComponent(view);
        regFilter();
        showProgress_net_type();
        P2PHandler.getInstance().getNpcSettings(this.contact.contactModel, this.contact.contactId, this.contact.contactPassword);
        return view;
    }

    public void initComponent(View view) {
        this.countDownTimer = new CountDownTimer(100000, 1000) {
            public void onTick(long millisUntilFinished) {
                Message msg = new Message();
                msg.what = 0;
                Bundle bundle = new Bundle();
                bundle.putInt("progress", (int) (100 - (millisUntilFinished / 1000)));
                msg.setData(bundle);
                NetControlFrag.this.handler.sendMessage(msg);
                Log.e("few", "counting :" + millisUntilFinished);
            }

            public void onFinish() {
                NetControlFrag.this.showSetWifiDialog(NetControlFrag.this.getString(C0291R.string.wifi_time_out), true);
            }
        };
        this.dialog_input_mask = (RelativeLayout) view.findViewById(C0291R.id.dialog_input_mask);
        this.net_type_bar = (RelativeLayout) view.findViewById(C0291R.id.net_type_bar);
        this.list_wifi_bar = (RelativeLayout) view.findViewById(C0291R.id.list_wifi_bar);
        this.net_type_radio = (LinearLayout) view.findViewById(C0291R.id.net_type_radio);
        this.list_wifi_content = (LinearLayout) view.findViewById(C0291R.id.list_wifi_content);
        this.progressBar_net_type = (ProgressBar) view.findViewById(C0291R.id.progressBar_net_type);
        this.progressBar_list_wifi = (ProgressBar) view.findViewById(C0291R.id.progressBar_list_wifi);
        this.list = (MyListView) view.findViewById(C0291R.id.list_wifi);
        this.mAdapter = new WifiAdapter(this.mContext, this);
        this.list.setAdapter(this.mAdapter);
        this.radio_one = (RadioButton) view.findViewById(C0291R.id.radio_one);
        this.radio_two = (RadioButton) view.findViewById(C0291R.id.radio_two);
        this.radio_one.setOnClickListener(this);
        this.radio_two.setOnClickListener(this);
    }

    public void regFilter() {
        MainControlActivity.isCancelCheck = true;
        IntentFilter filter = new IntentFilter();
        filter.addAction(Action.CLOSE_INPUT_DIALOG);
        filter.addAction(P2P.ACK_RET_GET_NPC_SETTINGS);
        filter.addAction(P2P.ACK_RET_SET_NET_TYPE);
        filter.addAction(P2P.RET_SET_NET_TYPE);
        filter.addAction(P2P.RET_GET_NET_TYPE);
        filter.addAction(P2P.ACK_RET_SET_WIFI);
        filter.addAction(P2P.ACK_RET_GET_WIFI);
        filter.addAction(P2P.RET_SET_WIFI);
        filter.addAction(P2P.RET_GET_WIFI);
        this.mContext.registerReceiver(this.mReceiver, filter);
        this.isRegFilter = true;
    }

    private void showSetWifiDialog(String tips, final boolean isFinish) {
        if (this.dialog_loading != null) {
            this.dialog_loading.dismiss();
        }
        if (this.dialog_info != null) {
            this.dialog_info.dismiss();
        }
        this.dialog_info = new NormalDialog(getActivity(), getString(C0291R.string.prompt), tips, "1", getString(C0291R.string.okay));
        this.dialog_info.showPromptDialog();
        this.dialog_info.setOnButtonCancelListener(new OnButtonCancelListener() {
            public void onClick() {
                NetControlFrag.this.dialog_info.dismiss();
                InputMethodManager imm = (InputMethodManager) NetControlFrag.this.getActivity().getSystemService("input_method");
                if (imm != null) {
                    imm.hideSoftInputFromWindow(NetControlFrag.this.getActivity().getWindow().getDecorView().getWindowToken(), 0);
                }
                if (isFinish) {
                    NetControlFrag.this.getActivity().onBackPressed();
                }
            }
        });
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case C0291R.id.radio_one:
                changeNetType(0);
                return;
            case C0291R.id.radio_two:
                changeNetType(1);
                return;
            default:
                return;
        }
    }

    public void changeNetType(final int type) {
        final NormalDialog dialog = new NormalDialog(this.mContext, this.mContext.getResources().getString(C0291R.string.warning), this.mContext.getResources().getString(C0291R.string.modify_net_warning), this.mContext.getResources().getString(C0291R.string.change), this.mContext.getResources().getString(C0291R.string.cancel));
        switch (this.last_net_type) {
            case 0:
                dialog.setOnButtonCancelListener(new OnButtonCancelListener() {
                    public void onClick() {
                        NetControlFrag.this.radio_one.setChecked(true);
                        dialog.dismiss();
                    }
                });
                break;
            case 1:
                dialog.setOnButtonCancelListener(new OnButtonCancelListener() {
                    public void onClick() {
                        NetControlFrag.this.radio_two.setChecked(true);
                        dialog.dismiss();
                    }
                });
                break;
        }
        dialog.setOnButtonOkListener(new OnButtonOkListener() {

            class C11151 implements OnRunListener {
                C11151() {
                }

                public void run() {
                    NetControlFrag.this.last_modify_net_type = type;
                    P2PHandler.getInstance().setNetType(NetControlFrag.this.contact.contactModel, NetControlFrag.this.contact.contactId, NetControlFrag.this.contact.contactPassword, type);
                }
            }

            public void onClick() {
                new DelayThread(0, new C11151()).start();
                NetControlFrag.this.setRadioEnable(false);
                FList.getInstance().setState(NetControlFrag.this.contact.contactId, 0);
                Intent friends = new Intent();
                friends.setAction(Action.GET_FRIENDS_STATE);
                MyApp.app.sendBroadcast(friends);
                NetControlFrag.this.showSetWifiDialog(NetControlFrag.this.getString(C0291R.string.set_wifi_normal_tip), true);
            }
        });
        dialog.showNormalDialog();
        dialog.setCanceledOnTouchOutside(false);
    }

    public void onDestroyView() {
        super.onDestroyView();
        if (this.isRegFilter) {
            this.mContext.unregisterReceiver(this.mReceiver);
            this.isRegFilter = false;
        }
        MainControlActivity.isCancelCheck = false;
    }

    public void setRadioEnable(boolean bool) {
        if (bool) {
            this.radio_one.setEnabled(true);
            this.radio_two.setEnabled(true);
            return;
        }
        this.radio_one.setEnabled(false);
        this.radio_two.setEnabled(false);
    }

    public void showProgress_net_type() {
        this.net_type_bar.setBackgroundResource(C0291R.drawable.tiao_bg_single);
        this.progressBar_net_type.setVisibility(0);
        this.net_type_radio.setVisibility(8);
    }

    public void showNetType() {
        this.net_type_bar.setBackgroundResource(C0291R.drawable.tiao_bg_up);
        this.progressBar_net_type.setVisibility(8);
        this.net_type_radio.setVisibility(0);
    }

    public void hideWiFiList() {
        this.list_wifi_bar.setVisibility(8);
        this.list_wifi_content.setVisibility(8);
    }

    public void showProgressWiFiList() {
        this.list_wifi_bar.setBackgroundResource(C0291R.drawable.tiao_bg_single);
        this.list_wifi_bar.setVisibility(0);
        this.progressBar_list_wifi.setVisibility(0);
        this.list_wifi_content.setVisibility(8);
    }

    public void showWiFiList() {
        this.list_wifi_bar.setBackgroundResource(C0291R.drawable.tiao_bg_up);
        this.list_wifi_bar.setVisibility(0);
        this.progressBar_list_wifi.setVisibility(8);
        this.list_wifi_content.setVisibility(0);
    }

    public void showModfyWifi(final int type, final String name, final boolean nowNotify) {
        Log.e("wifiname", "wifiname" + name + "  " + name.length());
        this.dialog_input = new MyInputDialog(this.mContext);
        this.dialog_input.setTitle(this.mContext.getResources().getString(C0291R.string.change_wifi) + "(" + name + ")");
        this.dialog_input.setBtn1_str(this.mContext.getResources().getString(C0291R.string.ensure));
        this.dialog_input.setBtn2_str(this.mContext.getResources().getString(C0291R.string.cancel));
        this.dialog_input.setOnButtonOkListener(new MyInputDialog.OnButtonOkListener() {
            public void onClick() {
                String password = NetControlFrag.this.dialog_input.getInput1Text().trim();
                if (type == 0 || !"".equals(password)) {
                    NetControlFrag.this.dialog_input.hide(NetControlFrag.this.dialog_input_mask);
                    Intent friends;
                    if (NetControlFrag.this.last_net_type == 1 && nowNotify) {
                        if (type == 0) {
                            P2PHandler.getInstance().setWifi(NetControlFrag.this.contact.contactModel, NetControlFrag.this.contact.contactId, NetControlFrag.this.contact.contactPassword, type, name, "0");
                        } else {
                            P2PHandler.getInstance().setWifi(NetControlFrag.this.contact.contactModel, NetControlFrag.this.contact.contactId, NetControlFrag.this.contact.contactPassword, type, name, password);
                        }
                        FList.getInstance().setState(NetControlFrag.this.contact.contactId, 0);
                        friends = new Intent();
                        friends.setAction(Action.GET_FRIENDS_STATE);
                        MyApp.app.sendBroadcast(friends);
                        NetControlFrag.this.showSetWifiDialog(NetControlFrag.this.getString(C0291R.string.set_wifi_normal_tip), true);
                        return;
                    }
                    if (NetControlFrag.this.dialog_loading == null) {
                        NetControlFrag.this.dialog_loading = new NormalDialog(NetControlFrag.this.mContext, NetControlFrag.this.mContext.getResources().getString(C0291R.string.verification), "", "", "");
                        NetControlFrag.this.dialog_loading.setStyle(2);
                    }
                    NetControlFrag.this.dialog_loading.showProgressDialog(0, 0);
                    NetControlFrag.this.last_modify_wifi_type = type;
                    NetControlFrag.this.last_modify_wifi_name = name;
                    NetControlFrag.this.last_modify_wifi_password = password;
                    if (type == 0) {
                        P2PHandler.getInstance().setWifi(NetControlFrag.this.contact.contactModel, NetControlFrag.this.contact.contactId, NetControlFrag.this.contact.contactPassword, type, name, "0");
                    } else {
                        P2PHandler.getInstance().setWifi(NetControlFrag.this.contact.contactModel, NetControlFrag.this.contact.contactId, NetControlFrag.this.contact.contactPassword, type, name, password);
                    }
                    FList.getInstance().setState(NetControlFrag.this.contact.contactId, 0);
                    friends = new Intent();
                    friends.setAction(Action.GET_FRIENDS_STATE);
                    MyApp.app.sendBroadcast(friends);
                    NetControlFrag.this.countDownTimer.start();
                    return;
                }
                C0568T.showShort(NetControlFrag.this.mContext, (int) C0291R.string.input_wifi_pwd);
            }
        });
        this.dialog_input.show(this.dialog_input_mask);
        this.dialog_input.setInput1HintText((int) C0291R.string.input_wifi_pwd);
    }

    public boolean IsInputDialogShowing() {
        if (this.dialog_input != null) {
            return this.dialog_input.isShowing();
        }
        return false;
    }

    public void onDestroy() {
        super.onDestroy();
        if (this.countDownTimer != null) {
            this.countDownTimer.cancel();
        }
        if (this.dialog_info != null) {
            this.dialog_info.dismiss();
        }
        Intent it = new Intent();
        it.setAction(Action.CONTROL_BACK);
        this.mContext.sendBroadcast(it);
    }

    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e2) {
            throw new RuntimeException(e2);
        }
    }
}
