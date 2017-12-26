package com.jwkj.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.hikam.C0291R;
import com.jwkj.activity.AccountInfoActivity;
import com.jwkj.activity.MainActivity;
import com.jwkj.activity.SettingSystemActivity;
import com.jwkj.activity.SysMsgActivity;
import com.jwkj.data.DataManager;
import com.jwkj.data.SysMessage;
import com.jwkj.entity.Account;
import com.jwkj.global.AccountPersist;
import com.jwkj.global.Constants;
import com.jwkj.global.Constants.Action;
import com.jwkj.global.NpcCommon;
import com.jwkj.global.NpcCommon.NETWORK_TYPE;
import com.jwkj.widget.NormalDialog;

public class SettingFrag extends BaseFragment implements OnClickListener {
    private RelativeLayout account_set;
    private RelativeLayout center_about;
    private RelativeLayout center_support;
    private NormalDialog dialog;
    private Handler handler;
    private boolean isCancelCheck = false;
    boolean isRegFilter = false;
    private RelativeLayout mCheckUpdateTextView = null;
    private Context mContext;
    private RelativeLayout mExit;
    private RelativeLayout mLogOut;
    private TextView mName;
    BroadcastReceiver mReceiver = new C05491();
    ImageView network_type;
    ImageView sysMsg_notify_img;
    private RelativeLayout sys_msg;
    private RelativeLayout sys_set;

    class C05491 extends BroadcastReceiver {
        C05491() {
        }

        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Action.RECEIVE_SYS_MSG)) {
                SettingFrag.this.updateSysMsg();
            } else if (!intent.getAction().equals(Action.NET_WORK_TYPE_CHANGE)) {
            } else {
                if (NpcCommon.mNetWorkType == NETWORK_TYPE.NETWORK_WIFI) {
                    SettingFrag.this.network_type.setImageResource(C0291R.drawable.wifi);
                } else {
                    SettingFrag.this.network_type.setImageResource(C0291R.drawable.net_3g);
                }
            }
        }
    }

    class MyHandler extends Handler {
        MyHandler() {
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 17:
                    Log.e("my", "diss");
                    if (SettingFrag.this.dialog != null) {
                        Log.e("my", "diss ok");
                        SettingFrag.this.dialog.dismiss();
                        SettingFrag.this.dialog = null;
                    }
                    if (!SettingFrag.this.isCancelCheck) {
                        SettingFrag.this.dialog = new NormalDialog(SettingFrag.this.mContext, SettingFrag.this.mContext.getResources().getString(C0291R.string.update_prompt_title), SettingFrag.this.mContext.getResources().getString(C0291R.string.update_check_false), "", "");
                        SettingFrag.this.dialog.setStyle(5);
                        SettingFrag.this.dialog.showDialog();
                        return;
                    }
                    return;
                case 18:
                    if (SettingFrag.this.dialog != null) {
                        SettingFrag.this.dialog.dismiss();
                        SettingFrag.this.dialog = null;
                    }
                    if (!SettingFrag.this.isCancelCheck) {
                        Intent i = new Intent(Action.ACTION_UPDATE);
                        i.putExtra("updateDescription", (String) msg.obj);
                        SettingFrag.this.mContext.sendBroadcast(i);
                        return;
                    }
                    return;
                default:
                    return;
            }
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(C0291R.layout.fragment_setting, container, false);
        Log.e("my", "createSettingFrag");
        this.mContext = MainActivity.mContext;
        initComponent(view);
        regFilter();
        updateSysMsg();
        return view;
    }

    public void initComponent(View view) {
        this.mCheckUpdateTextView = (RelativeLayout) view.findViewById(C0291R.id.center_t);
        this.mName = (TextView) view.findViewById(C0291R.id.mailAdr);
        Account account = AccountPersist.getInstance().getActiveAccountInfo(this.mContext);
        String email = "";
        if (account != null) {
            email = account.email;
        }
        if (email.endsWith(Constants.HIKAM_EMAIL_SUFFIX)) {
            email = email.substring(0, email.indexOf(Constants.HIKAM_EMAIL_SUFFIX));
        }
        if (account.three_number.contains(Constants.HIKAM_EMAIL_SUFFIX)) {
            email = account.three_number;
            email = email.substring(0, email.indexOf(Constants.HIKAM_EMAIL_SUFFIX));
        }
        this.mName.setText(email);
        this.mLogOut = (RelativeLayout) view.findViewById(C0291R.id.logout_layout);
        this.account_set = (RelativeLayout) view.findViewById(C0291R.id.account_set);
        this.sys_set = (RelativeLayout) view.findViewById(C0291R.id.system_set);
        this.mExit = (RelativeLayout) view.findViewById(C0291R.id.exit_layout);
        this.center_support = (RelativeLayout) view.findViewById(C0291R.id.center_support);
        this.center_about = (RelativeLayout) view.findViewById(C0291R.id.center_about);
        this.sys_msg = (RelativeLayout) view.findViewById(C0291R.id.system_message);
        this.sysMsg_notify_img = (ImageView) view.findViewById(C0291R.id.sysMsg_notify_img);
        this.network_type = (ImageView) view.findViewById(C0291R.id.network_type);
        if (NpcCommon.mNetWorkType == NETWORK_TYPE.NETWORK_WIFI) {
            this.network_type.setImageResource(C0291R.drawable.wifi);
        } else {
            this.network_type.setImageResource(C0291R.drawable.net_3g);
        }
        this.mLogOut.setOnClickListener(this);
        this.account_set.setOnClickListener(this);
        this.sys_msg.setOnClickListener(this);
        this.mExit.setOnClickListener(this);
        this.sys_set.setOnClickListener(this);
        this.center_support.setOnClickListener(this);
        this.center_about.setOnClickListener(this);
        this.mCheckUpdateTextView.setOnClickListener(this);
        this.handler = new MyHandler();
        if (NpcCommon.mThreeNum.equals("517400")) {
            this.account_set.setVisibility(8);
            this.sys_set.setBackgroundResource(C0291R.drawable.tiao_bg_up);
        }
    }

    public void regFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Action.RECEIVE_SYS_MSG);
        filter.addAction(Action.NET_WORK_TYPE_CHANGE);
        this.mContext.registerReceiver(this.mReceiver, filter);
        this.isRegFilter = true;
    }

    public void onResume() {
        super.onResume();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case C0291R.id.account_set:
                Intent goAccount_set = new Intent();
                goAccount_set.setClass(this.mContext, AccountInfoActivity.class);
                startActivity(goAccount_set);
                return;
            case C0291R.id.center_about:
                new NormalDialog(this.mContext).showAboutDialog();
                return;
            case C0291R.id.center_support:
                String url = this.mContext.getResources().getString(C0291R.string.support_web);
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.setData(Uri.parse(url));
                startActivity(intent);
                return;
            case C0291R.id.center_t:
                try {
                    moveToGooglePlay();
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            case C0291R.id.exit_layout:
                MainActivity.isInited = false;
                Intent exit = new Intent();
                exit.setAction(Action.ACTION_EXIT);
                this.mContext.sendBroadcast(exit);
                return;
            case C0291R.id.logout_layout:
                MainActivity.isInited = false;
                Intent canel = new Intent();
                canel.setAction(Action.ACTION_SWITCH_USER);
                this.mContext.sendBroadcast(canel);
                return;
            case C0291R.id.system_message:
                startActivity(new Intent(this.mContext, SysMsgActivity.class));
                return;
            case C0291R.id.system_set:
                Intent goSys_set = new Intent();
                goSys_set.setClass(this.mContext, SettingSystemActivity.class);
                startActivity(goSys_set);
                return;
            default:
                return;
        }
    }

    public void moveToGooglePlay() {
        Intent localIntent = new Intent("android.intent.action.VIEW");
        localIntent.setData(Uri.parse("market://details?id=com.hikam"));
        startActivity(localIntent);
    }

    public void updateSysMsg() {
        boolean isNewSysMsg = false;
        for (SysMessage msg : DataManager.findSysMessageByActiveUser(this.mContext, NpcCommon.mThreeNum)) {
            if (msg.msgState == 0) {
                isNewSysMsg = true;
            }
        }
        if (isNewSysMsg) {
            this.sysMsg_notify_img.setVisibility(0);
        } else {
            this.sysMsg_notify_img.setVisibility(8);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (this.isRegFilter) {
            this.isRegFilter = false;
            this.mContext.unregisterReceiver(this.mReceiver);
        }
    }
}
