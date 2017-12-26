package com.jwkj.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.hikam.C0291R;
import com.jwkj.data.Contact;
import com.jwkj.data.ContactDB;
import com.jwkj.fragment.AlarmControlFrag;
import com.jwkj.fragment.DefenceAreaControlFrag;
import com.jwkj.fragment.DeviceControlFrag;
import com.jwkj.fragment.MainControlFrag;
import com.jwkj.fragment.NetControlFrag;
import com.jwkj.fragment.RecordControlFrag;
import com.jwkj.fragment.RemoteControlFrag;
import com.jwkj.fragment.SdCardFrag;
import com.jwkj.fragment.SecurityControlFrag;
import com.jwkj.fragment.TimeControlFrag;
import com.jwkj.fragment.VideoControlFrag;
import com.jwkj.global.Constants.Action;
import com.jwkj.global.Constants.P2P;
import com.jwkj.utils.C0568T;
import com.jwkj.utils.Utils;
import com.jwkj.widget.HeaderView;
import com.jwkj.widget.NormalDialog;
import com.p2p.core.P2PHandler;
import com.p2p.core.P2PValue;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import org.jboss.netty.handler.codec.rtsp.RtspHeaders.Values;

public class MainControlActivity extends BaseActivity implements OnClickListener {
    public static final int FRAG_ALARM_CONTROL = 5;
    public static final int FRAG_DEFENCE_AREA_CONTROL = 10;
    public static final int FRAG_DEVICE_CONTROL = 12;
    public static final int FRAG_MAIN = 0;
    public static final int FRAG_NET_CONTROL = 9;
    public static final int FRAG_RECORD_CONTROL = 7;
    public static final int FRAG_REMOTE_CONTROL = 2;
    public static final int FRAG_SD_CARD_CONTROL = 11;
    public static final int FRAG_SECURITY_CONTROL = 8;
    public static final int FRAG_TIME_CONTROL = 1;
    public static final int FRAG_VIDEO_CONTROL = 6;
    public static boolean isCancelCheck = false;
    public static Context mContext;
    AlarmControlFrag alarmFrag;
    private ImageView back;
    private Contact contact;
    private TextView contactName;
    public int current_frag = -1;
    DefenceAreaControlFrag defenceAreaFrag;
    DeviceControlFrag deviceControlFrag;
    int device_timezone_index = -1;
    private int device_type;
    private NormalDialog dialog;
    private String[] fragTags = new String[]{"mainFrag", "timeFrag", "remoteFrag", "loadFrag", "faultFrag", "alarmFrag", "videoFrag", "recordFrag", "securityFrag", "netFrag", "defenceAreaFrag", "sdCardFrag", "deviceFrag"};
    HeaderView header_img;
    boolean isCancelDoUpdate = false;
    boolean isRegFilter = false;
    private BroadcastReceiver mReceiver = new C04122();
    MainControlFrag mainFrag;
    NetControlFrag netFrag;
    boolean pwdWrong = false;
    RecordControlFrag recordFrag;
    RemoteControlFrag remoteFrag;
    SdCardFrag sdCardFrag;
    private SimpleDateFormat sdf_device_time = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    SecurityControlFrag securityFrag;
    TextView tests;
    TimeControlFrag timeFrag;
    TextView tv_setting;
    VideoControlFrag videoFrag;
    Button viewDeviceVersionBtn;

    class C04111 implements OnClickListener {
        C04111() {
        }

        public void onClick(View v) {
            P2PHandler.getInstance().GetValidDataHumanDetect(MainControlActivity.this.contact.contactModel, MainControlActivity.this.contact.contactId, MainControlActivity.this.contact.contactPassword);
        }
    }

    class C04122 extends BroadcastReceiver {
        C04122() {
        }

        public void onReceive(Context arg0, Intent intent) {
            boolean isEnforce = intent.getBooleanExtra("isEnforce", false);
            if (intent.getAction().equals(P2P.RET_GET_HUMAN_DETECT_DATA)) {
                MainControlActivity.this.tests.setText("result :" + intent.getIntExtra("result", -1) + "days:" + intent.getIntExtra("days", -1) + " time:" + intent.getStringExtra(Values.TIME));
            } else if (intent.getAction().equals(Action.CONTROL_SETTING_PWD_ERROR)) {
                C0568T.showShort(MainControlActivity.mContext, MainControlActivity.this.getString(C0291R.string.password_error));
                MainControlActivity.this.finish();
            } else if (intent.getAction().equals(Action.REFRESH_CONTANTS)) {
                Contact c = (Contact) intent.getSerializableExtra(ContactDB.TABLE_NAME);
                if (c != null) {
                    MainControlActivity.this.contact = c;
                    MainControlActivity.this.contactName.setText(MainControlActivity.this.contact.contactName);
                }
            } else if (intent.getAction().equals(Action.REPLACE_MAIN_CONTROL)) {
                MainControlActivity.this.replaceFragment(0, true, true);
            } else if (intent.getAction().equals(Action.REPLACE_SETTING_NAME)) {
                Intent modify = new Intent();
                modify.setClass(MainControlActivity.mContext, ModifyContactActivity.class);
                modify.putExtra(ContactDB.TABLE_NAME, MainControlActivity.this.contact);
                MainControlActivity.mContext.startActivity(modify);
            } else if (intent.getAction().equals(Action.REPLACE_SETTING_TIME)) {
                MainControlActivity.this.tv_setting.setText(C0291R.string.time_set);
                MainControlActivity.this.replaceFragment(1, true, isEnforce);
            } else if (intent.getAction().equals(Action.REPLACE_DEFENCE_AREA_CONTROL)) {
                MainControlActivity.this.tv_setting.setText(C0291R.string.defense_zone_set);
                MainControlActivity.this.replaceFragment(10, true, isEnforce);
            } else if (intent.getAction().equals(Action.REPLACE_NET_CONTROL)) {
                MainControlActivity.this.tv_setting.setText(C0291R.string.network_set);
                MainControlActivity.this.replaceFragment(9, true, isEnforce);
            } else if (intent.getAction().equals(Action.REPLACE_ALARM_CONTROL)) {
                MainControlActivity.this.tv_setting.setText(C0291R.string.alarm_set);
                MainControlActivity.this.replaceFragment(5, true, isEnforce);
            } else if (intent.getAction().equals(Action.REPLACE_VIDEO_CONTROL)) {
                MainControlActivity.this.tv_setting.setText(C0291R.string.media_set);
                MainControlActivity.this.replaceFragment(6, true, isEnforce);
            } else if (intent.getAction().equals(Action.REPLACE_RECORD_CONTROL)) {
                MainControlActivity.this.tv_setting.setText(C0291R.string.video_set);
                MainControlActivity.this.replaceFragment(7, true, isEnforce);
            } else if (intent.getAction().equals(Action.REPLACE_DEVICE_CONTROL)) {
                MainControlActivity.this.tv_setting.setText(C0291R.string.device_management);
                MainControlActivity.this.replaceFragment(12, true, isEnforce);
            } else if (intent.getAction().equals(Action.REPLACE_SECURITY_CONTROL)) {
                Intent intent2 = new Intent(MainControlActivity.mContext, ModifyNpcPasswordActivity.class);
                intent2.putExtra(ContactDB.TABLE_NAME, MainControlActivity.this.contact);
                MainControlActivity.mContext.startActivity(intent2);
            } else if (intent.getAction().equals(Action.REPLACE_REMOTE_CONTROL)) {
                MainControlActivity.this.replaceFragment(2, true, isEnforce);
            } else if (intent.getAction().equals(Action.REPLACE_SD_CARD_CONTROL)) {
                MainControlActivity.this.tv_setting.setText(C0291R.string.sd_card_set);
                MainControlActivity.this.replaceFragment(11, true, isEnforce);
            } else if (intent.getAction().equals(P2P.ACK_RET_GET_DEVICE_INFO)) {
                result = intent.getIntExtra("result", -1);
                if (result == 9999) {
                    if (MainControlActivity.this.dialog != null) {
                        MainControlActivity.this.dialog.dismiss();
                        MainControlActivity.this.dialog = null;
                    }
                    C0568T.showLong(MainControlActivity.mContext, (int) C0291R.string.device_password_error);
                } else if (result == 9998) {
                    Log.e("my", "net error resend:get device info");
                    P2PHandler.getInstance().getDeviceVersion(MainControlActivity.this.contact.contactModel, MainControlActivity.this.contact.contactId, MainControlActivity.this.contact.contactPassword);
                }
            } else if (intent.getAction().equals(P2P.RET_GET_DEVICE_INFO)) {
                if (!MainControlActivity.isCancelCheck) {
                    result = intent.getIntExtra("result", -1);
                    cur_version = intent.getStringExtra("cur_version");
                    iUbootVersion = intent.getIntExtra("iUbootVersion", 0);
                    iKernelVersion = intent.getIntExtra("iKernelVersion", 0);
                    iRootfsVersion = intent.getIntExtra("iRootfsVersion", 0);
                    if (MainControlActivity.this.dialog != null) {
                        MainControlActivity.this.dialog.dismiss();
                        MainControlActivity.this.dialog = null;
                    }
                    new NormalDialog(MainControlActivity.mContext).showDeviceInfoDialog(Utils.showShortDevID(MainControlActivity.this.contact.contactId), cur_version, String.valueOf(iUbootVersion), String.valueOf(iKernelVersion), String.valueOf(iRootfsVersion), null);
                }
            } else if (intent.getAction().equals(P2P.RET_GET_DEVICE_INFO2)) {
                if (!MainControlActivity.isCancelCheck) {
                    result = intent.getIntExtra("result", -1);
                    cur_version = intent.getStringExtra("cur_version");
                    iUbootVersion = intent.getIntExtra("iUbootVersion", 0);
                    iKernelVersion = intent.getIntExtra("iKernelVersion", 0);
                    iRootfsVersion = intent.getIntExtra("iRootfsVersion", 0);
                    String device_ip = intent.getStringExtra("device_ip");
                    if (MainControlActivity.this.dialog != null) {
                        MainControlActivity.this.dialog.dismiss();
                        MainControlActivity.this.dialog = null;
                    }
                    new NormalDialog(MainControlActivity.mContext).showDeviceInfoDialog(Utils.showShortDevID(MainControlActivity.this.contact.contactId), cur_version, String.valueOf(iUbootVersion), String.valueOf(iKernelVersion), String.valueOf(iRootfsVersion), device_ip);
                }
            } else if (intent.getAction().equals(P2P.RET_GET_TIME_ZONE)) {
                if (!MainControlActivity.isCancelCheck) {
                    MainControlActivity.this.device_timezone_index = intent.getIntExtra("state", -1);
                    Log.e("alex", "-------------------------device timezone index = " + MainControlActivity.this.device_timezone_index);
                    if (!P2PValue.HikamDeviceModelList.contains(MainControlActivity.this.contact.contactModel)) {
                        P2PHandler.getInstance().getDeviceTime(MainControlActivity.this.contact.contactModel, MainControlActivity.this.contact.contactId, MainControlActivity.this.contact.contactPassword);
                    }
                }
            } else if (intent.getAction().equals(P2P.RET_GET_TIME)) {
                if (!MainControlActivity.isCancelCheck) {
                    String device_time = intent.getStringExtra(Values.TIME);
                    Calendar system_calendar = Calendar.getInstance();
                    Log.e("alex", "-------------------------device time = " + device_time);
                    Calendar device_calendar = Calendar.getInstance();
                    Date device_date = null;
                    try {
                        device_date = MainControlActivity.this.sdf_device_time.parse(device_time);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    device_calendar.setTime(device_date);
                    int device_hour = device_calendar.get(11);
                    int device_minute = device_calendar.get(12);
                    int system_hour = system_calendar.get(11);
                    if (Math.abs(device_minute - system_calendar.get(12)) >= 1 && MainControlActivity.this.device_timezone_index >= 0) {
                        int[] iArr = new int[24];
                        iArr = new int[]{-11, -10, -9, -8, -7, -6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
                        int device_timezone_offset = 0;
                        if (MainControlActivity.this.device_timezone_index > -1 && iArr.length > 0) {
                            device_timezone_offset = ((iArr[MainControlActivity.this.device_timezone_index] * 1000) * 60) * 60;
                        }
                        long newLongTime = system_calendar.getTimeInMillis();
                        if (TimeZone.getDefault().inDaylightTime(new Date())) {
                            newLongTime = system_calendar.getTimeInMillis() - ((long) ((TimeZone.getDefault().getRawOffset() + TimeZone.getDefault().getDSTSavings()) - device_timezone_offset));
                        } else {
                            newLongTime = system_calendar.getTimeInMillis() - ((long) (TimeZone.getDefault().getRawOffset() - device_timezone_offset));
                        }
                        device_calendar.setTimeInMillis(newLongTime);
                        String new_device_time = MainControlActivity.this.sdf_device_time.format(device_calendar.getTime());
                        Log.e("alex", "-------------------------new device time = " + new_device_time);
                        P2PHandler.getInstance().setDeviceTime(MainControlActivity.this.contact.contactModel, MainControlActivity.this.contact.contactId, MainControlActivity.this.contact.contactPassword, new_device_time);
                    }
                }
            } else if (intent.getAction().equals(Action.CONTROL_BACK)) {
                MainControlActivity.this.tv_setting.setText(C0291R.string.device_set);
            }
        }
    }

    class C04133 implements OnCancelListener {
        C04133() {
        }

        public void onCancel(DialogInterface arg0) {
            MainControlActivity.isCancelCheck = true;
        }
    }

    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(C0291R.layout.activity_control_main);
        this.contact = (Contact) getIntent().getSerializableExtra(ContactDB.TABLE_NAME);
        this.device_type = getIntent().getIntExtra("type", -1);
        this.pwdWrong = getIntent().getBooleanExtra("wrongPwd", false);
        mContext = this;
        initComponent();
        regFilter();
        P2PHandler.getInstance().GetValidDataHumanDetect(this.contact.contactModel, this.contact.contactId, this.contact.contactPassword);
        P2PHandler.getInstance().getNpcSettings(this.contact.contactModel, this.contact.contactId, this.contact.contactPassword);
        ((Button) Button.class.cast(findViewById(C0291R.id.test))).setOnClickListener(new C04111());
        this.tests = (TextView) findViewById(C0291R.id.test_tv);
        this.tests.setMovementMethod(new ScrollingMovementMethod());
        replaceFragment(0, false, true);
    }

    public void initComponent() {
        this.tv_setting = (TextView) findViewById(C0291R.id.tv_setting);
        this.viewDeviceVersionBtn = (Button) findViewById(C0291R.id.viewDeviceVersionBtn);
        this.contactName = (TextView) findViewById(C0291R.id.contactName);
        this.header_img = (HeaderView) findViewById(C0291R.id.header_img);
        this.header_img.updateImage(this.contact.contactId, false);
        this.back = (ImageView) findViewById(C0291R.id.back_btn);
        this.back.setOnClickListener(this);
        this.viewDeviceVersionBtn.setOnClickListener(this);
        this.contactName.setText(this.contact.contactName);
        if (this.contact.contactType == 2) {
            this.viewDeviceVersionBtn.setVisibility(8);
        } else {
            this.viewDeviceVersionBtn.setVisibility(0);
        }
    }

    public void regFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(P2P.RET_GET_HUMAN_DETECT_DATA);
        filter.addAction(Action.REPLACE_SETTING_TIME);
        filter.addAction(Action.REPLACE_SETTING_NAME);
        filter.addAction(Action.REPLACE_ALARM_CONTROL);
        filter.addAction(Action.REPLACE_REMOTE_CONTROL);
        filter.addAction(Action.REFRESH_CONTANTS);
        filter.addAction(Action.REPLACE_VIDEO_CONTROL);
        filter.addAction(Action.REPLACE_RECORD_CONTROL);
        filter.addAction(Action.REPLACE_SECURITY_CONTROL);
        filter.addAction(Action.REPLACE_NET_CONTROL);
        filter.addAction(Action.REPLACE_DEFENCE_AREA_CONTROL);
        filter.addAction(Action.REPLACE_SD_CARD_CONTROL);
        filter.addAction(Action.REPLACE_MAIN_CONTROL);
        filter.addAction(Action.REPLACE_DEVICE_CONTROL);
        filter.addAction(Action.CONTROL_SETTING_PWD_ERROR);
        filter.addAction(P2P.ACK_RET_GET_DEVICE_INFO);
        filter.addAction(P2P.RET_GET_DEVICE_INFO);
        filter.addAction(P2P.RET_GET_DEVICE_INFO2);
        filter.addAction(P2P.RET_GET_TIME);
        filter.addAction(P2P.RET_GET_TIME_ZONE);
        filter.addAction(Action.CONTROL_BACK);
        registerReceiver(this.mReceiver, filter);
        this.isRegFilter = true;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case C0291R.id.back_btn:
                back();
                return;
            case C0291R.id.viewDeviceVersionBtn:
                if (this.dialog == null || !this.dialog.isShowing()) {
                    this.dialog = new NormalDialog(mContext);
                    this.dialog.setOnCancelListener(new C04133());
                    this.dialog.setTitle(mContext.getResources().getString(C0291R.string.device_info));
                    this.dialog.showLoadingDialog();
                    this.dialog.setCanceledOnTouchOutside(false);
                    isCancelCheck = false;
                    P2PHandler.getInstance().getDeviceVersion(this.contact.contactModel, this.contact.contactId, this.contact.contactPassword);
                    return;
                }
                Log.e("my", "isShowing");
                return;
            default:
                return;
        }
    }

    public void back() {
        if (this.current_frag != 0) {
            replaceFragment(0, true, true);
        } else {
            finish();
        }
    }

    public boolean isReplace(int type, boolean isEnforce) {
        if (isEnforce || this.current_frag != 0) {
            return true;
        }
        return false;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void replaceFragment(int r8, boolean r9, boolean r10) {
        /*
        r7 = this;
        r6 = -1;
        r4 = r7.current_frag;
        if (r8 != r4) goto L_0x0006;
    L_0x0005:
        return;
    L_0x0006:
        r4 = r7.isReplace(r8, r10);
        if (r4 == 0) goto L_0x0005;
    L_0x000c:
        r1 = r7.newFragInstance(r8);
        r2 = r7.getSupportFragmentManager();	 Catch:{ Exception -> 0x003a }
        r3 = r2.beginTransaction();	 Catch:{ Exception -> 0x003a }
        if (r9 == 0) goto L_0x0025;
    L_0x001a:
        r4 = 17432576; // 0x10a0000 float:2.5346597E-38 double:8.612837E-317;
        r5 = 17432577; // 0x10a0001 float:2.53466E-38 double:8.6128374E-317;
        r3.setCustomAnimations(r4, r5);	 Catch:{ Exception -> 0x003a }
        switch(r8) {
            case 0: goto L_0x0058;
            case 1: goto L_0x0099;
            case 2: goto L_0x0046;
            case 3: goto L_0x0025;
            case 4: goto L_0x0025;
            case 5: goto L_0x00ac;
            case 6: goto L_0x00bf;
            case 7: goto L_0x00d0;
            case 8: goto L_0x00e3;
            case 9: goto L_0x00f4;
            case 10: goto L_0x0107;
            case 11: goto L_0x011a;
            case 12: goto L_0x012d;
            default: goto L_0x0025;
        };	 Catch:{ Exception -> 0x003a }
    L_0x0025:
        r7.current_frag = r8;	 Catch:{ Exception -> 0x003a }
        r4 = 2131165501; // 0x7f07013d float:1.794522E38 double:1.0529356596E-314;
        r5 = r7.fragTags;	 Catch:{ Exception -> 0x003a }
        r6 = r7.current_frag;	 Catch:{ Exception -> 0x003a }
        r5 = r5[r6];	 Catch:{ Exception -> 0x003a }
        r3.replace(r4, r1, r5);	 Catch:{ Exception -> 0x003a }
        r3.commit();	 Catch:{ Exception -> 0x003a }
        r2.executePendingTransactions();	 Catch:{ Exception -> 0x003a }
        goto L_0x0005;
    L_0x003a:
        r0 = move-exception;
        r0.printStackTrace();
        r4 = "my";
        r5 = "replaceFrag error--main";
        android.util.Log.e(r4, r5);
        goto L_0x0005;
    L_0x0046:
        r4 = r7.current_frag;	 Catch:{ Exception -> 0x003a }
        if (r4 == 0) goto L_0x004e;
    L_0x004a:
        r4 = r7.current_frag;	 Catch:{ Exception -> 0x003a }
        if (r4 != r6) goto L_0x0025;
    L_0x004e:
        r4 = 2130772014; // 0x7f01002e float:1.7147134E38 double:1.052741251E-314;
        r5 = 2130772018; // 0x7f010032 float:1.7147143E38 double:1.052741253E-314;
        r3.setCustomAnimations(r4, r5);	 Catch:{ Exception -> 0x003a }
        goto L_0x0025;
    L_0x0058:
        r4 = r7.current_frag;	 Catch:{ Exception -> 0x003a }
        r5 = 2;
        if (r4 == r5) goto L_0x008f;
    L_0x005d:
        r4 = r7.current_frag;	 Catch:{ Exception -> 0x003a }
        r5 = 1;
        if (r4 == r5) goto L_0x008f;
    L_0x0062:
        r4 = r7.current_frag;	 Catch:{ Exception -> 0x003a }
        r5 = 5;
        if (r4 == r5) goto L_0x008f;
    L_0x0067:
        r4 = r7.current_frag;	 Catch:{ Exception -> 0x003a }
        r5 = 6;
        if (r4 == r5) goto L_0x008f;
    L_0x006c:
        r4 = r7.current_frag;	 Catch:{ Exception -> 0x003a }
        r5 = 7;
        if (r4 == r5) goto L_0x008f;
    L_0x0071:
        r4 = r7.current_frag;	 Catch:{ Exception -> 0x003a }
        r5 = 8;
        if (r4 == r5) goto L_0x008f;
    L_0x0077:
        r4 = r7.current_frag;	 Catch:{ Exception -> 0x003a }
        r5 = 9;
        if (r4 == r5) goto L_0x008f;
    L_0x007d:
        r4 = r7.current_frag;	 Catch:{ Exception -> 0x003a }
        r5 = 10;
        if (r4 == r5) goto L_0x008f;
    L_0x0083:
        r4 = r7.current_frag;	 Catch:{ Exception -> 0x003a }
        r5 = 11;
        if (r4 == r5) goto L_0x008f;
    L_0x0089:
        r4 = r7.current_frag;	 Catch:{ Exception -> 0x003a }
        r5 = 12;
        if (r4 != r5) goto L_0x0025;
    L_0x008f:
        r4 = 2130772012; // 0x7f01002c float:1.714713E38 double:1.0527412502E-314;
        r5 = 2130772020; // 0x7f010034 float:1.7147147E38 double:1.052741254E-314;
        r3.setCustomAnimations(r4, r5);	 Catch:{ Exception -> 0x003a }
        goto L_0x0025;
    L_0x0099:
        r4 = r7.current_frag;	 Catch:{ Exception -> 0x003a }
        if (r4 == 0) goto L_0x00a1;
    L_0x009d:
        r4 = r7.current_frag;	 Catch:{ Exception -> 0x003a }
        if (r4 != r6) goto L_0x0025;
    L_0x00a1:
        r4 = 2130772014; // 0x7f01002e float:1.7147134E38 double:1.052741251E-314;
        r5 = 2130772018; // 0x7f010032 float:1.7147143E38 double:1.052741253E-314;
        r3.setCustomAnimations(r4, r5);	 Catch:{ Exception -> 0x003a }
        goto L_0x0025;
    L_0x00ac:
        r4 = r7.current_frag;	 Catch:{ Exception -> 0x003a }
        if (r4 == 0) goto L_0x00b4;
    L_0x00b0:
        r4 = r7.current_frag;	 Catch:{ Exception -> 0x003a }
        if (r4 != r6) goto L_0x0025;
    L_0x00b4:
        r4 = 2130772014; // 0x7f01002e float:1.7147134E38 double:1.052741251E-314;
        r5 = 2130772018; // 0x7f010032 float:1.7147143E38 double:1.052741253E-314;
        r3.setCustomAnimations(r4, r5);	 Catch:{ Exception -> 0x003a }
        goto L_0x0025;
    L_0x00bf:
        r4 = r7.current_frag;	 Catch:{ Exception -> 0x003a }
        if (r4 == 0) goto L_0x00c7;
    L_0x00c3:
        r4 = r7.current_frag;	 Catch:{ Exception -> 0x003a }
        if (r4 != r6) goto L_0x00d0;
    L_0x00c7:
        r4 = 2130772014; // 0x7f01002e float:1.7147134E38 double:1.052741251E-314;
        r5 = 2130772018; // 0x7f010032 float:1.7147143E38 double:1.052741253E-314;
        r3.setCustomAnimations(r4, r5);	 Catch:{ Exception -> 0x003a }
    L_0x00d0:
        r4 = r7.current_frag;	 Catch:{ Exception -> 0x003a }
        if (r4 == 0) goto L_0x00d8;
    L_0x00d4:
        r4 = r7.current_frag;	 Catch:{ Exception -> 0x003a }
        if (r4 != r6) goto L_0x0025;
    L_0x00d8:
        r4 = 2130772014; // 0x7f01002e float:1.7147134E38 double:1.052741251E-314;
        r5 = 2130772018; // 0x7f010032 float:1.7147143E38 double:1.052741253E-314;
        r3.setCustomAnimations(r4, r5);	 Catch:{ Exception -> 0x003a }
        goto L_0x0025;
    L_0x00e3:
        r4 = r7.current_frag;	 Catch:{ Exception -> 0x003a }
        if (r4 == 0) goto L_0x00eb;
    L_0x00e7:
        r4 = r7.current_frag;	 Catch:{ Exception -> 0x003a }
        if (r4 != r6) goto L_0x00f4;
    L_0x00eb:
        r4 = 2130772014; // 0x7f01002e float:1.7147134E38 double:1.052741251E-314;
        r5 = 2130772018; // 0x7f010032 float:1.7147143E38 double:1.052741253E-314;
        r3.setCustomAnimations(r4, r5);	 Catch:{ Exception -> 0x003a }
    L_0x00f4:
        r4 = r7.current_frag;	 Catch:{ Exception -> 0x003a }
        if (r4 == 0) goto L_0x00fc;
    L_0x00f8:
        r4 = r7.current_frag;	 Catch:{ Exception -> 0x003a }
        if (r4 != r6) goto L_0x0025;
    L_0x00fc:
        r4 = 2130772014; // 0x7f01002e float:1.7147134E38 double:1.052741251E-314;
        r5 = 2130772018; // 0x7f010032 float:1.7147143E38 double:1.052741253E-314;
        r3.setCustomAnimations(r4, r5);	 Catch:{ Exception -> 0x003a }
        goto L_0x0025;
    L_0x0107:
        r4 = r7.current_frag;	 Catch:{ Exception -> 0x003a }
        if (r4 == 0) goto L_0x010f;
    L_0x010b:
        r4 = r7.current_frag;	 Catch:{ Exception -> 0x003a }
        if (r4 != r6) goto L_0x0025;
    L_0x010f:
        r4 = 2130772014; // 0x7f01002e float:1.7147134E38 double:1.052741251E-314;
        r5 = 2130772018; // 0x7f010032 float:1.7147143E38 double:1.052741253E-314;
        r3.setCustomAnimations(r4, r5);	 Catch:{ Exception -> 0x003a }
        goto L_0x0025;
    L_0x011a:
        r4 = r7.current_frag;	 Catch:{ Exception -> 0x003a }
        if (r4 == 0) goto L_0x0122;
    L_0x011e:
        r4 = r7.current_frag;	 Catch:{ Exception -> 0x003a }
        if (r4 != r6) goto L_0x0025;
    L_0x0122:
        r4 = 2130772014; // 0x7f01002e float:1.7147134E38 double:1.052741251E-314;
        r5 = 2130772018; // 0x7f010032 float:1.7147143E38 double:1.052741253E-314;
        r3.setCustomAnimations(r4, r5);	 Catch:{ Exception -> 0x003a }
        goto L_0x0025;
    L_0x012d:
        r4 = r7.current_frag;	 Catch:{ Exception -> 0x003a }
        if (r4 == 0) goto L_0x0135;
    L_0x0131:
        r4 = r7.current_frag;	 Catch:{ Exception -> 0x003a }
        if (r4 != r6) goto L_0x0025;
    L_0x0135:
        r4 = 2130772014; // 0x7f01002e float:1.7147134E38 double:1.052741251E-314;
        r5 = 2130772018; // 0x7f010032 float:1.7147143E38 double:1.052741253E-314;
        r3.setCustomAnimations(r4, r5);	 Catch:{ Exception -> 0x003a }
        goto L_0x0025;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.jwkj.activity.MainControlActivity.replaceFragment(int, boolean, boolean):void");
    }

    public Fragment newFragInstance(int type) {
        Bundle args = new Bundle();
        args.putSerializable(ContactDB.TABLE_NAME, this.contact);
        args.putInt("type", this.device_type);
        if (this.pwdWrong) {
            args.putBoolean("wrongPwd", true);
        }
        switch (type) {
            case 0:
                if (this.mainFrag == null) {
                    this.mainFrag = new MainControlFrag();
                    this.mainFrag.setArguments(args);
                }
                return this.mainFrag;
            case 1:
                this.timeFrag = new TimeControlFrag();
                this.timeFrag.setArguments(args);
                return this.timeFrag;
            case 2:
                if (this.remoteFrag == null) {
                    this.remoteFrag = new RemoteControlFrag();
                    this.remoteFrag.setArguments(args);
                }
                return this.remoteFrag;
            case 5:
                this.alarmFrag = new AlarmControlFrag();
                this.alarmFrag.setArguments(args);
                return this.alarmFrag;
            case 6:
                this.videoFrag = new VideoControlFrag();
                this.videoFrag.setArguments(args);
                return this.videoFrag;
            case 7:
                this.recordFrag = new RecordControlFrag();
                this.recordFrag.setArguments(args);
                return this.recordFrag;
            case 8:
                this.securityFrag = new SecurityControlFrag();
                this.securityFrag.setArguments(args);
                return this.securityFrag;
            case 9:
                this.netFrag = new NetControlFrag();
                this.netFrag.setArguments(args);
                return this.netFrag;
            case 10:
                this.defenceAreaFrag = new DefenceAreaControlFrag();
                this.defenceAreaFrag.setArguments(args);
                return this.defenceAreaFrag;
            case 11:
                this.sdCardFrag = new SdCardFrag();
                this.sdCardFrag.setArguments(args);
                return this.sdCardFrag;
            case 12:
                this.deviceControlFrag = new DeviceControlFrag();
                this.deviceControlFrag.setArguments(args);
                return this.deviceControlFrag;
            default:
                return null;
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        if (this.isRegFilter) {
            this.isRegFilter = false;
            unregisterReceiver(this.mReceiver);
        }
    }

    public void onBackPressed() {
        if (this.netFrag != null && this.current_frag == 9 && this.netFrag.IsInputDialogShowing()) {
            Intent close_input_dialog = new Intent();
            close_input_dialog.setAction(Action.CLOSE_INPUT_DIALOG);
            mContext.sendBroadcast(close_input_dialog);
        }
        if (this.current_frag != 0) {
            replaceFragment(0, true, true);
        } else {
            super.onBackPressed();
        }
    }

    public int getActivityInfo() {
        return 12;
    }
}
