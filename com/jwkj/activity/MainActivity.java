package com.jwkj.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import cn.com.streamax.miotp.p2p.jni.GWellUserInfo;
import cn.com.streamax.miotp.p2p.jni.P2pClientID;
import cn.com.streamax.miotp.p2p.jni.P2pJni;
import com.hikam.C0291R;
import com.jwkj.P2PListener;
import com.jwkj.SettingListener;
import com.jwkj.data.DataManager;
import com.jwkj.data.SharedPreferencesManager;
import com.jwkj.entity.Account;
import com.jwkj.fragment.ContactFrag;
import com.jwkj.fragment.KeyboardFrag;
import com.jwkj.fragment.NearlyTellFrag;
import com.jwkj.fragment.SettingFrag;
import com.jwkj.fragment.UtilsFrag;
import com.jwkj.global.AccountPersist;
import com.jwkj.global.Constants.Action;
import com.jwkj.global.Constants.Update;
import com.jwkj.global.FList;
import com.jwkj.global.HikamService;
import com.jwkj.global.MyApp;
import com.jwkj.global.NpcCommon;
import com.jwkj.global.NpcCommon.NETWORK_TYPE;
import com.jwkj.utils.C0568T;
import com.jwkj.utils.Utils;
import com.jwkj.widget.NormalDialog;
import com.jwkj.widget.NormalDialog.OnButtonOkListener;
import com.p2p.core.P2PHandler;
import com.p2p.core.network.GetAccountInfoResult;
import com.p2p.core.network.NetManager;
import com.p2p.core.utils.MD5;
import java.io.File;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import org.json.JSONObject;

public class MainActivity extends BaseActivity implements OnClickListener {
    public static final int HANDLE_P2PCS_INIT = 0;
    public static boolean isInited = false;
    public static Context mContext;
    private final int REQUEST_CODE = 0;
    private String confPath = "/storage/emulated/0/Android/data/com.hikam/files";
    private RelativeLayout contact;
    private ContactFrag contactFrag;
    private ImageView contact_img;
    private int currFrag = 0;
    private RelativeLayout discover;
    private ImageView discover_img;
    private ProgressBar downApkBar;
    private String[] fragTags = new String[]{"contactFrag", "keyboardFrag", "nearlyTellFrag", "utilsFrag", "settingFrag"};
    Handler handler = new C04092();
    boolean isRegFilter = false;
    private KeyboardFrag keyboardFrag;
    private BroadcastReceiver mReceiver = new C04103();
    private NearlyTellFrag nearlyTellFrag;
    private RelativeLayout recent;
    private ImageView recent_img;
    private SettingFrag settingFrag;
    private RelativeLayout settings;
    private ImageView settings_img;
    private UtilsFrag utilsFrag;

    class C04071 implements Runnable {
        C04071() {
        }

        public void run() {
            new GetAccountInfoTask().execute(new Object[0]);
        }
    }

    class C04092 extends Handler {
        long last_time;

        class C04081 extends Thread {
            C04081() {
            }

            public void run() {
                Log.i("Register", "P2pJni.P2pClientSdkRegister start");
                FList.getInstance().hikam_sdk_register_state = 1;
                int regResult = P2pJni.P2pClientSdkRegister(1, new GWellUserInfo());
                Log.i("Register", "P2pJni.P2pClientSdkRegister finish, result = " + regResult);
                if (regResult == 0) {
                    FList.getInstance().hikam_sdk_register_state = 0;
                } else {
                    FList.getInstance().hikam_sdk_register_state = -1;
                }
            }
        }

        C04092() {
        }

        public void handleMessage(Message msg) {
            int value = msg.arg1;
            switch (msg.what) {
                case 0:
                    Log.e("few", "init  .....");
                    if (!MainActivity.isInited) {
                        MainActivity.isInited = true;
                        String recentName = SharedPreferencesManager.getInstance().getData(MainActivity.mContext, SharedPreferencesManager.SP_FILE_GWELL, SharedPreferencesManager.KEY_RECENTNAME_EMAIL);
                        String recentPwd = SharedPreferencesManager.getInstance().getData(MainActivity.mContext, SharedPreferencesManager.SP_FILE_GWELL, SharedPreferencesManager.KEY_RECENTPASS_EMAIL);
                        P2pClientID client = new P2pClientID();
                        client.account = recentName;
                        MD5 md5 = new MD5();
                        client.passwd = Utils.MD5(recentPwd);
                        String localip = MainActivity.this.getLocalIP();
                        MainActivity.this.confPath = MainActivity.mContext.getExternalFilesDir(null).getPath();
                        Log.i("alex", "P2pJni.P2pClientSdkInit start");
                        FList.getInstance().hikam_sdk_register_state = -1;
                        int initResult = P2pJni.P2pClientSdkInit(client, localip, MainActivity.this.confPath);
                        Log.i("ward", "P2pJni.P2pClientSdkInit client: " + client + "localip: " + localip + "confPath" + MainActivity.this.confPath);
                        Log.i("alex", "P2pJni.P2pClientSdkInit finish, result = " + initResult);
                        if (FList.getInstance().hikam_sdk_register_state == -1) {
                            new C04081().start();
                            return;
                        }
                        return;
                    }
                    return;
                case 17:
                    if (System.currentTimeMillis() - this.last_time > 1000) {
                        MyApp.app.showDownNotification(17, value);
                        this.last_time = System.currentTimeMillis();
                        return;
                    }
                    return;
                case 18:
                    MyApp.app.hideDownNotification();
                    Intent intent = new Intent("android.intent.action.VIEW");
                    File file = new File(Environment.getExternalStorageDirectory() + "/" + Update.SAVE_PATH + "/" + Update.FILE_NAME);
                    if (file.exists()) {
                        intent.setDataAndType(Uri.fromFile(file), Update.INSTALL_APK);
                        MainActivity.mContext.startActivity(intent);
                        return;
                    }
                    return;
                case 19:
                    MyApp.app.showDownNotification(19, value);
                    C0568T.showShort(MainActivity.mContext, (int) C0291R.string.down_fault);
                    return;
                default:
                    return;
            }
        }
    }

    class C04103 extends BroadcastReceiver {

        class C10831 implements OnButtonOkListener {
            C10831() {
            }

            public void onClick() {
                Intent i = new Intent(MyApp.MAIN_SERVICE_START);
                i.setPackage(MainActivity.mContext.getPackageName());
                MainActivity.this.stopService(i);
                MainActivity.this.isGoExit(true);
                MainActivity.this.finish();
            }
        }

        C04103() {
        }

        public void onReceive(Context arg0, Intent intent) {
            if (intent.getAction().equals(Action.ACTION_NETWORK_CHANGE)) {
                boolean isNetConnect = false;
                NetworkInfo activeNetInfo = ((ConnectivityManager) MainActivity.mContext.getSystemService("connectivity")).getActiveNetworkInfo();
                if (activeNetInfo != null) {
                    if (activeNetInfo.isConnected()) {
                        isNetConnect = true;
                        C0568T.showShort(MainActivity.mContext, MainActivity.this.getString(C0291R.string.message_net_connect) + activeNetInfo.getTypeName());
                    } else {
                        C0568T.showShort(MainActivity.mContext, MainActivity.this.getString(C0291R.string.network_error) + " " + activeNetInfo.getTypeName());
                    }
                    if (activeNetInfo.getType() == 1) {
                        NpcCommon.mNetWorkType = NETWORK_TYPE.NETWORK_WIFI;
                    } else {
                        NpcCommon.mNetWorkType = NETWORK_TYPE.NETWORK_2GOR3G;
                    }
                } else {
                    Toast.makeText(MainActivity.mContext, MainActivity.this.getString(C0291R.string.network_error), 0).show();
                }
                NpcCommon.setNetWorkState(isNetConnect);
                Intent intentNew = new Intent();
                intentNew.setAction(Action.NET_WORK_TYPE_CHANGE);
                MainActivity.mContext.sendBroadcast(intentNew);
            } else if (intent.getAction().equals(Action.ACTION_SWITCH_USER)) {
                new ExitTask(AccountPersist.getInstance().getActiveAccountInfo(MainActivity.mContext)).execute(new Object[0]);
                AccountPersist.getInstance().setActiveAccount(MainActivity.mContext, new Account());
                NpcCommon.mThreeNum = "";
                NpcCommon.mThreeNum2 = "000000";
                i = new Intent(MyApp.MAIN_SERVICE_START);
                i.setPackage(MainActivity.mContext.getPackageName());
                MainActivity.this.stopService(i);
                MainActivity.this.startActivity(new Intent(MainActivity.mContext, LoginActivity.class));
                MainActivity.this.finish();
            } else if (intent.getAction().equals(Action.SESSION_ID_ERROR)) {
                new ExitTask(AccountPersist.getInstance().getActiveAccountInfo(MainActivity.mContext)).execute(new Object[0]);
                AccountPersist.getInstance().setActiveAccount(MainActivity.mContext, new Account());
                i = new Intent(MyApp.MAIN_SERVICE_START);
                i.setPackage(MainActivity.mContext.getPackageName());
                MainActivity.this.stopService(i);
                MainActivity.this.startActivity(new Intent(MainActivity.mContext, LoginActivity.class));
                C0568T.showShort(MainActivity.mContext, (int) C0291R.string.session_id_error);
                MainActivity.this.finish();
            } else if (intent.getAction().equals(Action.ACTION_EXIT)) {
                NormalDialog dialog = new NormalDialog(MainActivity.mContext, MainActivity.mContext.getResources().getString(C0291R.string.exit), MainActivity.mContext.getResources().getString(C0291R.string.confirm_exit), MainActivity.mContext.getResources().getString(C0291R.string.exit), MainActivity.mContext.getResources().getString(C0291R.string.cancel));
                dialog.setOnButtonOkListener(new C10831());
                dialog.showNormalDialog();
            } else if (!intent.getAction().equals("android.intent.action.LOCALE_CHANGED")) {
                if (intent.getAction().equals(Action.RECEIVE_MSG)) {
                    int result = intent.getIntExtra("result", -1);
                    String msgFlag = intent.getStringExtra("msgFlag");
                    Intent refresh;
                    if (result == 9997) {
                        DataManager.updateMessageStateByFlag(MainActivity.mContext, msgFlag, 0);
                        refresh = new Intent();
                        refresh.setAction(MessageActivity.REFRESH_MESSAGE);
                        MainActivity.this.sendBroadcast(refresh);
                        return;
                    }
                    DataManager.updateMessageStateByFlag(MainActivity.mContext, msgFlag, 2);
                    refresh = new Intent();
                    refresh.setAction(MessageActivity.REFRESH_MESSAGE);
                    MainActivity.this.sendBroadcast(refresh);
                } else if (!intent.getAction().equals(Action.RECEIVE_SYS_MSG) && intent.getAction().equals(Action.SETTING_WIFI_SUCCESS)) {
                    MainActivity.this.currFrag = 0;
                    if (MainActivity.this.contactFrag == null) {
                        MainActivity.this.contactFrag = new ContactFrag();
                    }
                    MainActivity.this.replaceFragment(C0291R.id.fragContainer, MainActivity.this.contactFrag, MainActivity.this.fragTags[0]);
                    MainActivity.this.changeIconShow();
                }
            }
        }
    }

    class ExitTask extends AsyncTask {
        Account account;

        public ExitTask(Account account) {
            this.account = account;
        }

        protected Object doInBackground(Object... params) {
            if (this.account.three_number2 == null || "".equals(this.account.three_number2)) {
                return Integer.valueOf(0);
            }
            return Integer.valueOf(NetManager.getInstance(MainActivity.mContext).exit_application(this.account.three_number2, this.account.sessionId));
        }

        protected void onPostExecute(Object object) {
            switch (((Integer) object).intValue()) {
                case NetManager.CONNECT_CHANGE /*998*/:
                    new ExitTask(this.account).execute(new Object[0]);
                    return;
                default:
                    return;
            }
        }
    }

    class GetAccountInfoTask extends AsyncTask {
        protected Object doInBackground(Object... params) {
            Utils.sleepThread(1000);
            Account account = AccountPersist.getInstance().getActiveAccountInfo(MainActivity.mContext);
            if (account.three_number2 == null || account.sessionId == null) {
                return Integer.valueOf(-1);
            }
            Log.e("few", "main threenumm2 " + account.three_number2 + " sessionid " + account.sessionId);
            return NetManager.getInstance(MainActivity.mContext).getAccountInfo(account.three_number2, account.sessionId);
        }

        protected void onPostExecute(Object object) {
            GetAccountInfoResult result = NetManager.createGetAccountInfoResult((JSONObject) object);
            switch (Integer.parseInt(result.error_code)) {
                case 0:
                    try {
                        String email = result.email;
                        String phone = result.phone;
                        Account account = AccountPersist.getInstance().getActiveAccountInfo(MainActivity.mContext);
                        if (account == null) {
                            account = new Account();
                        }
                        account.email = email;
                        account.phone = phone;
                        AccountPersist.getInstance().setActiveAccount(MainActivity.mContext, account);
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                case 23:
                    Intent i = new Intent();
                    i.setAction(Action.SESSION_ID_ERROR);
                    MyApp.app.sendBroadcast(i);
                    return;
                case NetManager.CONNECT_CHANGE /*998*/:
                    new GetAccountInfoTask().execute(new Object[0]);
                    return;
                default:
                    return;
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        DataManager.findAlarmMaskByActiveUser(mContext, "");
        NpcCommon.verifyNetwork(mContext);
        regFilter();
        if (verifyLogin()) {
            initComponent();
            FList fList = new FList();
            P2PHandler.getInstance().p2pInit(this, new P2PListener(), new SettingListener());
            connect();
            if (this.contactFrag == null) {
                this.contactFrag = new ContactFrag();
            }
            this.currFrag = 0;
            if (this.contactFrag == null) {
                this.contactFrag = new ContactFrag();
            }
            replaceFragment(C0291R.id.fragContainer, this.contactFrag, this.fragTags[0]);
            changeIconShow();
            changeIconShow();
            if (!NpcCommon.mThreeNum.equals("517400")) {
                new Handler().postDelayed(new C04071(), 500);
                return;
            }
            return;
        }
        startActivity(new Intent(mContext, LoginActivity.class));
        finish();
    }

    public void initComponent() {
        setContentView(C0291R.layout.activity_main);
        this.contact = (RelativeLayout) findViewById(C0291R.id.icon_contact);
        this.contact_img = (ImageView) findViewById(C0291R.id.icon_contact_img);
        this.recent = (RelativeLayout) findViewById(C0291R.id.icon_nearlytell);
        this.recent_img = (ImageView) findViewById(C0291R.id.icon_nearlytell_img);
        this.settings = (RelativeLayout) findViewById(C0291R.id.icon_setting);
        this.settings_img = (ImageView) findViewById(C0291R.id.icon_setting_img);
        this.discover = (RelativeLayout) findViewById(C0291R.id.icon_discover);
        this.discover_img = (ImageView) findViewById(C0291R.id.icon_discover_img);
        this.contact.setOnClickListener(this);
        this.recent.setOnClickListener(this);
        this.settings.setOnClickListener(this);
        this.discover.setOnClickListener(this);
    }

    public void regFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Action.ACTION_NETWORK_CHANGE);
        filter.addAction(Action.ACTION_SWITCH_USER);
        filter.addAction(Action.ACTION_EXIT);
        filter.addAction(Action.RECEIVE_MSG);
        filter.addAction(Action.RECEIVE_SYS_MSG);
        filter.addAction("android.intent.action.LOCALE_CHANGED");
        filter.addAction(Action.ACTION_UPDATE);
        registerReceiver(this.mReceiver, filter);
        this.isRegFilter = true;
    }

    private void connect() {
        Intent service = new Intent(MyApp.MAIN_SERVICE_START);
        service.setPackage(mContext.getPackageName());
        startService(service);
    }

    private boolean isWifiConnected() {
        NetworkInfo mWiFiNetworkInfo = ((ConnectivityManager) getSystemService("connectivity")).getNetworkInfo(1);
        if (mWiFiNetworkInfo != null) {
            return mWiFiNetworkInfo.isConnected();
        }
        return false;
    }

    private String getLocalIP() {
        if (isWifiConnected()) {
            int ipAddress = ((WifiManager) mContext.getApplicationContext().getSystemService("wifi")).getConnectionInfo().getIpAddress();
            if (ipAddress == 0) {
                return "0.0.0.0";
            }
            return (ipAddress & 255) + "." + ((ipAddress >> 8) & 255) + "." + ((ipAddress >> 16) & 255) + "." + ((ipAddress >> 24) & 255);
        }
        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements()) {
                Enumeration<InetAddress> enumIpAddr = ((NetworkInterface) en.nextElement()).getInetAddresses();
                while (enumIpAddr.hasMoreElements()) {
                    InetAddress inetAddress = (InetAddress) enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && (inetAddress instanceof Inet4Address)) {
                        String text = inetAddress.getHostAddress().toString();
                        if (ipCheck(text)) {
                            return text;
                        }
                    }
                }
            }
        } catch (SocketException e) {
        }
        return "0.0.0.0";
    }

    private boolean ipCheck(String text) {
        if (text == null || text.isEmpty() || !text.matches("^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$")) {
            return false;
        }
        return true;
    }

    private boolean verifyLogin() {
        Account activeUser = AccountPersist.getInstance().getActiveAccountInfo(mContext);
        if (activeUser == null) {
            return false;
        }
        NpcCommon.mThreeNum = activeUser.three_number;
        NpcCommon.mThreeNum2 = activeUser.three_number2;
        if (ContextCompat.checkSelfPermission(this, "android.permission.READ_EXTERNAL_STORAGE") != 0) {
            requestPermission();
        } else {
            this.handler.sendEmptyMessage(0);
        }
        return true;
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.READ_EXTERNAL_STORAGE")) {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 0);
            return;
        }
        ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 0);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != 0) {
            return;
        }
        if (grantResults[0] == 0) {
            this.confPath = mContext.getExternalFilesDir(null).getPath();
            this.handler.sendEmptyMessage(0);
            return;
        }
        finish();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case C0291R.id.icon_contact:
                this.currFrag = 0;
                if (this.contactFrag == null) {
                    this.contactFrag = new ContactFrag();
                }
                replaceFragment(C0291R.id.fragContainer, this.contactFrag, this.fragTags[0]);
                changeIconShow();
                return;
            case C0291R.id.icon_discover:
                this.currFrag = 4;
                if (this.utilsFrag == null) {
                    this.utilsFrag = new UtilsFrag();
                }
                replaceFragment(C0291R.id.fragContainer, this.utilsFrag, this.fragTags[4]);
                changeIconShow();
                return;
            case C0291R.id.icon_nearlytell:
                this.currFrag = 2;
                if (this.nearlyTellFrag == null) {
                    this.nearlyTellFrag = new NearlyTellFrag();
                }
                replaceFragment(C0291R.id.fragContainer, this.nearlyTellFrag, this.fragTags[2]);
                changeIconShow();
                return;
            case C0291R.id.icon_setting:
                this.currFrag = 3;
                if (this.settingFrag == null) {
                    this.settingFrag = new SettingFrag();
                }
                replaceFragment(C0291R.id.fragContainer, this.settingFrag, this.fragTags[3]);
                changeIconShow();
                return;
            default:
                return;
        }
    }

    public void changeIconShow() {
        switch (this.currFrag) {
            case 0:
                this.contact_img.setImageResource(C0291R.drawable.contact_p);
                this.recent_img.setImageResource(C0291R.drawable.recent);
                this.settings_img.setImageResource(C0291R.drawable.setting);
                this.discover_img.setImageResource(C0291R.drawable.toolbox);
                this.contact.setSelected(true);
                this.recent.setSelected(false);
                this.settings.setSelected(false);
                this.discover.setSelected(false);
                return;
            case 2:
                this.contact_img.setImageResource(C0291R.drawable.contact);
                this.recent_img.setImageResource(C0291R.drawable.recent_p);
                this.settings_img.setImageResource(C0291R.drawable.setting);
                this.discover_img.setImageResource(C0291R.drawable.toolbox);
                this.contact.setSelected(false);
                this.recent.setSelected(true);
                this.settings.setSelected(false);
                this.discover.setSelected(false);
                return;
            case 3:
                this.contact_img.setImageResource(C0291R.drawable.contact);
                this.recent_img.setImageResource(C0291R.drawable.recent);
                this.settings_img.setImageResource(C0291R.drawable.setting_p);
                this.discover_img.setImageResource(C0291R.drawable.toolbox);
                this.contact.setSelected(false);
                this.recent.setSelected(false);
                this.settings.setSelected(true);
                this.discover.setSelected(false);
                return;
            case 4:
                this.contact_img.setImageResource(C0291R.drawable.contact);
                this.recent_img.setImageResource(C0291R.drawable.recent);
                this.settings_img.setImageResource(C0291R.drawable.setting);
                this.discover_img.setImageResource(C0291R.drawable.toolbox_p);
                this.contact.setSelected(false);
                this.recent.setSelected(false);
                this.settings.setSelected(false);
                this.discover.setSelected(true);
                return;
            default:
                return;
        }
    }

    public void replaceFragment(int container, Fragment fragment, String tag) {
        try {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(C0291R.id.fragContainer, fragment, tag);
            transaction.commit();
            manager.executePendingTransactions();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("my", "replaceFrag error--main");
        }
    }

    public void onPause() {
        Log.e("life", "MainActivity->>onPause");
        super.onPause();
    }

    public void onResume() {
        Log.e("life", "MainActivity->>onResume");
        super.onResume();
        Intent intent = new Intent(mContext, HikamService.class);
        intent.setAction(HikamService.ACTION_SCAN_SHORTAV_NUM);
        startService(intent);
        File tempHead_nomedia = new File("/storage/emulated/0/screenshot/tempHead/.nomedia");
        if (tempHead_nomedia.exists()) {
            Log.e("oaosj", "not need create temphead nomedia");
        } else {
            try {
                Log.e("oaosj", "create temphead nomedia");
                tempHead_nomedia.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        File shortav_nomedia = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "hikam_shortav" + File.separator + ".nomedia");
        if (!shortav_nomedia.exists()) {
            try {
                Log.e("oaosj", "create ico nomedia");
                shortav_nomedia.createNewFile();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        File ico_nomedia = new File("/storage/emulated/0/screenshot/ico/.nomedia");
        File ico_dir = new File("/storage/emulated/0/screenshot/ico");
        if (ico_nomedia.exists()) {
            Log.e("oaosj", "not need create ico nomedia");
            return;
        }
        try {
            Log.e("oaosj", "create ico nomedia");
            ico_nomedia.createNewFile();
        } catch (Exception e22) {
            e22.printStackTrace();
        }
    }

    public void onStart() {
        Log.e("life", "MainActivity->>onStart");
        super.onStart();
    }

    public void onStop() {
        Log.e("life", "MainActivity->>onStop");
        super.onStop();
    }

    public void onDestroy() {
        Log.e("life", "MainActivity->>onDestroy");
        super.onDestroy();
        if (this.isRegFilter) {
            this.isRegFilter = false;
            unregisterReceiver(this.mReceiver);
        }
        this.handler.removeCallbacksAndMessages(null);
    }

    public void onBackPressed() {
        Log.e("my", "onBackPressed");
        if (this.keyboardFrag != null && this.currFrag == 1 && this.keyboardFrag.IsInputDialogShowing()) {
            Intent close_input_dialog = new Intent();
            close_input_dialog.setAction(Action.CLOSE_INPUT_DIALOG);
            mContext.sendBroadcast(close_input_dialog);
            return;
        }
        Intent mHomeIntent = new Intent("android.intent.action.MAIN");
        mHomeIntent.addCategory("android.intent.category.HOME");
        mHomeIntent.addFlags(270532608);
        mContext.startActivity(mHomeIntent);
    }

    public int getActivityInfo() {
        return 1;
    }

    protected void finalize() throws Throwable {
        super.finalize();
        Log.e("trace stack", getClass().getSimpleName() + "gc staring");
    }
}
