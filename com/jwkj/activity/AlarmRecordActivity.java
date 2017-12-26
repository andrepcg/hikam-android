package com.jwkj.activity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.media.TransportMediator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.com.streamax.miotp.p2p.jni.P2pJni;
import com.hikam.C0291R;
import com.jwkj.NativePlayerActivity;
import com.jwkj.adapter.AlarmRecordAdapter;
import com.jwkj.adapter.AlarmRecordAdapter.OnItemClick;
import com.jwkj.data.AlarmRecord;
import com.jwkj.data.Contact;
import com.jwkj.data.ContactDB;
import com.jwkj.data.DataManager;
import com.jwkj.data.SharedPreferencesManager;
import com.jwkj.global.Constants.Action;
import com.jwkj.global.Constants.P2P;
import com.jwkj.global.FList;
import com.jwkj.global.MyApp;
import com.jwkj.global.NpcCommon;
import com.jwkj.utils.C0568T;
import com.jwkj.utils.Utils;
import com.jwkj.widget.NormalDialog;
import com.jwkj.widget.NormalDialog.OnButtonOkListener;
import com.lib.pullToRefresh.PullToRefreshListView;
import com.p2p.core.P2PValue;
import com.p2p.core.P2PValue.HikamDeviceModel;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.http.cookie.ClientCookie;
import org.jboss.netty.handler.codec.rtsp.RtspHeaders.Values;

public class AlarmRecordActivity extends BaseActivity implements OnClickListener {
    public static final int FILLING = 2;
    public static final int SCROLL = 1;
    public static final int STOP = 0;
    private String SHORT_AVPIC_DIRECTORY = (this.SHORT_AV_DIRECTORY + "tmp/mmc/clips/images/");
    private String SHORT_AV_DIRECTORY = (Environment.getExternalStorageDirectory().getPath() + File.separator + "hikam_shortav" + File.separator);
    AlarmRecordAdapter adapter;
    int[] addition;
    private int alarm_tip_num = 0;
    ImageView back_btn;
    ImageView clear_btn;
    Contact contact;
    Context context;
    int count;
    private CountDownTimer countDownTimer;
    private int current_scroll_state = 0;
    String[] data;
    private String deviceId = null;
    private String devicePwd = null;
    NormalDialog dialog_loading;
    private int endIndex = 0;
    private long endTime = 0;
    private Handler handler = new Handler(new C03501());
    private boolean isFirstIn = true;
    private boolean isRegFilter = false;
    private boolean isSingleDevice = false;
    private boolean isSyncing = false;
    RelativeLayout layout_loading;
    List<AlarmRecord> list;
    ListView list_record;
    private AlertDialog load_record;
    View load_view;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context arg0, Intent intent) {
            if (intent.getAction().equals(P2P.RET_FRESH_PIC)) {
                AlarmRecordActivity.this.adapter.notifyDataSetChanged();
            } else if (intent.getAction().equals(Action.REFRESH_ALARM_RECORD)) {
                String operate = intent.getStringExtra("operate");
                Object obj = -1;
                switch (operate.hashCode()) {
                    case -934873754:
                        if (operate.equals("reduce")) {
                            obj = 1;
                            break;
                        }
                        break;
                    case 96417:
                        if (operate.equals("add")) {
                            obj = null;
                            break;
                        }
                        break;
                }
                switch (obj) {
                    case null:
                        if (AlarmRecordActivity.this.isSingleDevice) {
                            if (AlarmRecordActivity.this.deviceId.equals(intent.getStringExtra("id"))) {
                                AlarmRecordActivity.this.tv_alarm_tip.setVisibility(0);
                                AlarmRecordActivity.this.alarm_tip_num = AlarmRecordActivity.this.alarm_tip_num + 1;
                                AlarmRecordActivity.this.tv_alarm_tip.setText(AlarmRecordActivity.this.alarm_tip_num + " " + AlarmRecordActivity.this.getString(C0291R.string.alarm_num_tip));
                                return;
                            }
                            return;
                        }
                        AlarmRecordActivity.this.tv_alarm_tip.setVisibility(0);
                        AlarmRecordActivity.this.alarm_tip_num = AlarmRecordActivity.this.alarm_tip_num + 1;
                        AlarmRecordActivity.this.tv_alarm_tip.setText(AlarmRecordActivity.this.alarm_tip_num + " " + AlarmRecordActivity.this.getString(C0291R.string.alarm_num_tip));
                        return;
                    case 1:
                        if (AlarmRecordActivity.this.isSingleDevice) {
                            AlarmRecordActivity.this.adapter.updateData(AlarmRecordActivity.this.deviceId, AlarmRecordActivity.this.contact.contactName);
                        } else {
                            AlarmRecordActivity.this.adapter.updateData();
                        }
                        AlarmRecordActivity.this.adapter.notifyDataSetChanged();
                        return;
                    default:
                        return;
                }
            } else if (intent.getAction().equals(P2P.RET_ALARM_LOG_SYNC_STATUS)) {
                status = intent.getIntExtra("status", 3);
                if (status == 1) {
                    AlarmRecordActivity.this.showSync();
                } else if (status == 2) {
                    AlarmRecordActivity.this.closeSync();
                } else if (status == 3) {
                    AlarmRecordActivity.this.closeSync();
                } else if (status == 4) {
                    AlarmRecordActivity.this.closeSync();
                }
            } else if (intent.getAction().equals(P2P.RET_ALARM_LOG_SYNC_DATA)) {
                AlarmRecordActivity.this.count = intent.getIntExtra("count", 0);
                AlarmRecordActivity.this.type = intent.getIntArrayExtra("type");
                AlarmRecordActivity.this.time = intent.getIntArrayExtra(Values.TIME);
                AlarmRecordActivity.this.addition = intent.getIntArrayExtra("addition");
                AlarmRecordActivity.this.data = intent.getStringArrayExtra("data");
                if (AlarmRecordActivity.this.count > 0) {
                    AlarmRecordActivity.this.handler.sendEmptyMessage(0);
                }
            } else if (intent.getAction().equals(P2P.RET_DOWNLOAD_SHORT_AV)) {
                status = intent.getIntExtra("status", 2);
                if (status == 2 || status == 3) {
                    AlarmRecordActivity.this.closeDialog();
                }
            } else if (intent.getAction().equals(P2P.RET_PLAY_SHORT_AV)) {
                AlarmRecordActivity.this.closeDialog();
                status = intent.getIntExtra("state", 2);
                String path = intent.getStringExtra(ClientCookie.PATH_ATTR);
                int resolution = 1;
                if (AlarmRecordActivity.this.isSingleDevice) {
                    Log.e("few", "RET_PLAY_SHORT_AV path : " + path + " state:" + status + " model: " + AlarmRecordActivity.this.contact.contactModel);
                    if (HikamDeviceModel.Q5.equals(AlarmRecordActivity.this.contact.contactModel)) {
                        resolution = 2;
                    }
                } else if (AlarmRecordActivity.this.total_contact != null && HikamDeviceModel.Q5.equals(AlarmRecordActivity.this.total_contact.contactModel)) {
                    resolution = 2;
                }
                if (status != 1 && status != 0) {
                    C0568T.showShort(AlarmRecordActivity.this.context, (int) C0291R.string.short_av_unconnect);
                } else if (new File(path).isFile()) {
                    Intent intents = new Intent(AlarmRecordActivity.this, NativePlayerActivity.class);
                    intents.putExtra(ClientCookie.PATH_ATTR, path);
                    intents.putExtra("resolution", resolution);
                    AlarmRecordActivity.this.startActivity(intents);
                } else {
                    Log.e("few", "no file");
                    C0568T.showShort(AlarmRecordActivity.this.context, (int) C0291R.string.short_av_unconnect);
                }
            }
        }
    };
    PullToRefreshListView mpull_refresh_list;
    private String path = "no_data";
    private ProgressBar pb;
    RelativeLayout rl_sync;
    private int startIndex = 0;
    private long startTime = 0;
    int[] time;
    private int totalNum = 0;
    private Contact total_contact;
    public TextView tv_alarm_tip;
    int[] type;
    private int visibleNum = 0;

    class C03501 implements Callback {

        class C03471 implements Runnable {
            C03471() {
            }

            public void run() {
                int addItem = 0;
                for (int i = 0; i < AlarmRecordActivity.this.count; i++) {
                    AlarmRecord item = new AlarmRecord();
                    item.setDeviceId(AlarmRecordActivity.this.deviceId);
                    item.setAlarmType(AlarmRecordActivity.this.type[i]);
                    item.setAlarmTime(String.valueOf(AlarmRecordActivity.this.time[i]));
                    item.setGroup(AlarmRecordActivity.this.addition[i] / 8);
                    item.setItem(AlarmRecordActivity.this.addition[i] % 8);
                    item.setUuid(AlarmRecordActivity.this.data[i]);
                    item.setDeviceName(AlarmRecordActivity.this.contact.contactName);
                    item.setActiveUser(NpcCommon.mThreeNum);
                    if (AlarmRecordActivity.this.syncList(item)) {
                        addItem++;
                    }
                }
                AlarmRecordActivity.this.list.clear();
                if (AlarmRecordActivity.this.isSingleDevice) {
                    AlarmRecordActivity.this.list.addAll(DataManager.findAlarmRecordByActiveUserAndDeviceId2(AlarmRecordActivity.this.context, NpcCommon.mThreeNum, AlarmRecordActivity.this.deviceId, AlarmRecordActivity.this.contact.contactName));
                    SharedPreferencesManager.getInstance().putAsyncTimeByDevice(AlarmRecordActivity.this.context, AlarmRecordActivity.this.deviceId, AlarmRecordActivity.this.endTime);
                } else {
                    AlarmRecordActivity.this.list.addAll(DataManager.findAlarmRecordByActiveUser(AlarmRecordActivity.this.context, NpcCommon.mThreeNum));
                }
                AlarmRecordActivity.this.handler.sendEmptyMessage(1);
                if (addItem > 7) {
                    addItem = 7;
                }
                AlarmRecordActivity.this.reqAVPic(0, addItem);
            }
        }

        C03501() {
        }

        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    new Thread(new C03471()).start();
                    break;
                case 1:
                    AlarmRecordActivity.this.adapter.notifyDataSetChanged();
                    break;
            }
            return true;
        }
    }

    class C03524 implements OnScrollListener {
        C03524() {
        }

        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (AlarmRecordActivity.this.isSingleDevice) {
                AlarmRecordActivity.this.current_scroll_state = scrollState;
                switch (AlarmRecordActivity.this.current_scroll_state) {
                    case 0:
                        AlarmRecordActivity.this.reqAVPic(AlarmRecordActivity.this.startIndex, AlarmRecordActivity.this.endIndex);
                        return;
                    default:
                        return;
                }
            }
        }

        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            AlarmRecordActivity.this.startIndex = firstVisibleItem;
            AlarmRecordActivity.this.totalNum = totalItemCount;
            AlarmRecordActivity.this.visibleNum = visibleItemCount;
            AlarmRecordActivity.this.endIndex = AlarmRecordActivity.this.startIndex + AlarmRecordActivity.this.visibleNum;
            if (AlarmRecordActivity.this.isFirstIn && visibleItemCount != 0) {
                AlarmRecordActivity.this.reqAVPic(AlarmRecordActivity.this.startIndex, AlarmRecordActivity.this.endIndex);
                AlarmRecordActivity.this.isFirstIn = false;
            }
        }
    }

    class C03535 implements Runnable {
        C03535() {
        }

        public void run() {
            P2pJni.P2PClientSdkAlarmLogSyncStart(AlarmRecordActivity.this.deviceId, AlarmRecordActivity.this.devicePwd, AlarmRecordActivity.this.startTime, AlarmRecordActivity.this.endTime, 60000);
        }
    }

    class C03546 implements OnKeyListener {
        C03546() {
        }

        public boolean onKey(DialogInterface arg0, int arg1, KeyEvent event) {
            if (event.getAction() != 0 || event.getKeyCode() != 4) {
                return false;
            }
            if (AlarmRecordActivity.this.load_record.isShowing()) {
                AlarmRecordActivity.this.load_record.dismiss();
            }
            return true;
        }
    }

    class C03557 implements OnDismissListener {
        C03557() {
        }

        public void onDismiss(DialogInterface dialogInterface) {
            if (AlarmRecordActivity.this.countDownTimer != null) {
                AlarmRecordActivity.this.countDownTimer.cancel();
            }
        }
    }

    class C10743 implements OnItemClick {
        C10743() {
        }

        public void onPlay(int position) {
            if (!AlarmRecordActivity.this.isSingleDevice || AlarmRecordActivity.this.isSyncing) {
                Contact contact = FList.getInstance().isContact(((AlarmRecord) AlarmRecordActivity.this.list.get(position)).getDeviceId());
                AlarmRecordActivity.this.total_contact = contact;
                if (contact == null) {
                    C0568T.showShort(AlarmRecordActivity.this.context, (int) C0291R.string.short_av_denied);
                } else if (P2PValue.HikamDeviceModelList.contains(contact.contactModel)) {
                    AlarmRecordActivity.this.startAV(position, contact.contactId, contact.contactPassword);
                }
            } else if (P2PValue.HikamDeviceModelList.contains(AlarmRecordActivity.this.contact.contactModel)) {
                AlarmRecordActivity.this.startAV(position, AlarmRecordActivity.this.deviceId, AlarmRecordActivity.this.devicePwd);
            }
        }
    }

    public boolean syncList(AlarmRecord alarmRecord) {
        boolean isNeedInsert = true;
        for (int i = 0; i < this.list.size(); i++) {
            AlarmRecord item = (AlarmRecord) this.list.get(i);
            if (alarmRecord.getUuid().equals(item.getUuid()) && alarmRecord.getAlarmType() == item.getAlarmType()) {
                if (item.getAlarmType() != 1 && item.getAlarmType() != 6) {
                    isNeedInsert = false;
                } else if (item.getGroup() == alarmRecord.getGroup() && item.getItem() == alarmRecord.getItem()) {
                    isNeedInsert = false;
                }
            }
        }
        if (isNeedInsert) {
            DataManager.insertAlarmRecord(this, alarmRecord);
        }
        return isNeedInsert;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0291R.layout.activity_alarm_record);
        this.context = this;
        ((NotificationManager) getSystemService("notification")).cancelAll();
        MyApp.app.hideDownNotification();
        MyApp.app.hideNotification();
        this.contact = (Contact) getIntent().getSerializableExtra(ContactDB.TABLE_NAME);
        if (this.contact != null) {
            this.deviceId = this.contact.contactId;
            this.devicePwd = this.contact.contactPassword;
            this.isSingleDevice = true;
            this.list = DataManager.findAlarmRecordByActiveUserAndDeviceId2(this.context, NpcCommon.mThreeNum, this.deviceId, this.contact.contactName);
        } else {
            this.isSingleDevice = false;
            this.list = DataManager.findAlarmRecordByActiveUser(this.context, NpcCommon.mThreeNum);
        }
        initComponent();
        regFilter();
        Log.e("few", ClientCookie.PATH_ATTR + (Environment.getExternalStorageDirectory().getPath() + File.separator + "shortAV"));
    }

    public void initComponent() {
        this.countDownTimer = new CountDownTimer(40000, 1000) {
            public void onTick(long millisUntilFinished) {
                Log.e("few", "few" + millisUntilFinished);
            }

            public void onFinish() {
                C0568T.showShort(AlarmRecordActivity.this.context, (int) C0291R.string.timeout);
                AlarmRecordActivity.this.closeDialog();
            }
        };
        this.pb = (ProgressBar) findViewById(C0291R.id.pb);
        this.tv_alarm_tip = (TextView) findViewById(C0291R.id.tv_alarm_tip);
        this.tv_alarm_tip.setOnClickListener(this);
        this.back_btn = (ImageView) findViewById(C0291R.id.back_btn);
        this.clear_btn = (ImageView) findViewById(C0291R.id.clear);
        this.list_record = (ListView) findViewById(C0291R.id.list_allarm);
        this.mpull_refresh_list = (PullToRefreshListView) findViewById(C0291R.id.pull_refresh_list);
        this.layout_loading = (RelativeLayout) findViewById(C0291R.id.layout_loading);
        this.clear_btn.setOnClickListener(this);
        this.back_btn.setOnClickListener(this);
        this.adapter = new AlarmRecordAdapter(this, this.list, this.deviceId, this.devicePwd);
        this.list_record.setAdapter(this.adapter);
        this.adapter.setOnItemClick(new C10743());
        this.list_record.setOnScrollListener(new C03524());
        if (this.isSingleDevice && !this.isSyncing && P2PValue.HikamDeviceModelList.contains(this.contact.contactModel)) {
            this.endTime = new Date().getTime() / 1000;
            this.startTime = SharedPreferencesManager.getInstance().getAsyncTimeByDevice(this.context, this.deviceId);
            Log.e("few", this.startTime + " -- starttime");
            if (this.startTime == 0) {
                this.startTime = this.endTime - 86400;
            }
            this.pb.setVisibility(0);
            new Thread(new C03535()).start();
            Log.e("few", this.startTime + " -- " + this.endTime);
        }
    }

    public void showDialog() {
        this.load_view = LayoutInflater.from(this.context).inflate(C0291R.layout.dialog_load_record, null);
        this.load_record = new Builder(this.context, C0291R.style.hikamDialog).create();
        this.load_record.show();
        this.load_record.setContentView(this.load_view);
        this.load_record.setOnKeyListener(new C03546());
        this.load_record.setOnDismissListener(new C03557());
        this.load_view.setLayoutParams(new LayoutParams(Utils.dip2px(this.context, 222), Utils.dip2px(this.context, TransportMediator.KEYCODE_MEDIA_RECORD)));
        ImageView img = (ImageView) this.load_view.findViewById(C0291R.id.load_record_img);
        final AnimationDrawable anim = (AnimationDrawable) img.getDrawable();
        img.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
            public boolean onPreDraw() {
                anim.start();
                return true;
            }
        });
    }

    public void closeDialog() {
        if (this.load_record != null) {
            if (this.load_record.isShowing()) {
                this.load_record.dismiss();
            }
            if (this.countDownTimer != null) {
                this.countDownTimer.cancel();
            }
        }
    }

    public void reqAVPic(int startItem, int endItem) {
        Log.e("few", "reqAVPic" + startItem + " -- " + endItem);
        if (this.isSingleDevice && P2PValue.HikamDeviceModelList.contains(this.contact.contactModel)) {
            int i;
            ArrayList<String> uuid_list = new ArrayList();
            StringBuilder sb = new StringBuilder();
            for (i = startItem; i < endItem; i++) {
                boolean isInsert = true;
                String uuid = ((AlarmRecord) this.list.get(i)).getUuid();
                for (int j = 0; j < uuid_list.size(); j++) {
                    if (uuid.equals(uuid_list.get(j))) {
                        isInsert = false;
                    }
                }
                if (isInsert) {
                    sb.delete(0, sb.length());
                    sb.append("/storage/emulated/0/hikam_shortav/tmp/mmc/clips/images/");
                    sb.append(uuid);
                    sb.append(".jpg");
                    if (new File(sb.toString()).isFile()) {
                        isInsert = false;
                    }
                }
                if (isInsert) {
                    uuid_list.add(uuid);
                }
            }
            int size = uuid_list.size();
            if (size != 0) {
                String[] paths = new String[size];
                for (i = 0; i < size; i++) {
                    paths[i] = (String) uuid_list.get(i);
                }
                P2pJni.P2PClientSdkGetShortAVPic(this.contact.contactId, this.contact.contactPassword, paths, size);
            }
        }
    }

    public void startAV(int position, final String deviceId, final String devicePwd) {
        final String uuid = ((AlarmRecord) this.adapter.getList().get(position)).getUuid();
        StringBuilder sb = new StringBuilder();
        sb.append(this.SHORT_AV_DIRECTORY);
        sb.append(uuid);
        sb.append(".avx");
        this.path = sb.toString();
        if (Utils.isNetworkConnected(this) || new File(this.path).exists()) {
            showDialog();
            new Thread(new Runnable() {
                public void run() {
                    P2pJni.P2PClientSdkStartShortAV(deviceId, devicePwd, uuid);
                }
            }).start();
            this.countDownTimer.start();
            return;
        }
        C0568T.showShort(this.context, (int) C0291R.string.net_error_tip);
    }

    protected void onPause() {
        super.onPause();
        closeDialog();
        if (this.isSingleDevice && this.isSyncing) {
            P2pJni.P2PClientSdkAlarmLogSyncStop(this.deviceId, this.devicePwd, 61000);
        }
    }

    public void showSync() {
        this.isSyncing = true;
    }

    public void closeSync() {
        this.isSyncing = false;
        this.pb.setVisibility(8);
    }

    public void regFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Action.REFRESH_ALARM_RECORD);
        filter.addAction(P2P.RET_ALARM_LOG_SYNC_STATUS);
        filter.addAction(P2P.RET_ALARM_LOG_SYNC_DATA);
        filter.addAction(P2P.RET_DOWNLOAD_SHORT_AV);
        filter.addAction(P2P.RET_PLAY_SHORT_AV);
        filter.addAction(P2P.RET_FRESH_PIC);
        this.context.registerReceiver(this.mReceiver, filter);
        this.isRegFilter = true;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case C0291R.id.back_btn:
                finish();
                return;
            case C0291R.id.clear:
                if (this.dialog_loading == null) {
                    this.dialog_loading = new NormalDialog(this.context);
                }
                NormalDialog dialog = new NormalDialog(this.context, this.context.getResources().getString(C0291R.string.delete_alarm_records), this.context.getResources().getString(C0291R.string.confirm_clear), this.context.getResources().getString(C0291R.string.clear), this.context.getResources().getString(C0291R.string.cancel));
                dialog.setOnButtonOkListener(new OnButtonOkListener() {

                    class C03492 implements Runnable {
                        C03492() {
                        }

                        public void run() {
                            DataManager.clearAlarmRecord(AlarmRecordActivity.this.context, NpcCommon.mThreeNum);
                        }
                    }

                    public void onClick() {
                        AlarmRecordActivity.this.dialog_loading.showLoadingDialog();
                        if (AlarmRecordActivity.this.isSingleDevice) {
                            final ArrayList<AlarmRecord> copyList = new ArrayList();
                            copyList.addAll(AlarmRecordActivity.this.list);
                            AlarmRecordActivity.this.list.clear();
                            AlarmRecordActivity.this.adapter.notifyDataSetChanged();
                            new Thread(new Runnable() {
                                public void run() {
                                    Iterator it = copyList.iterator();
                                    while (it.hasNext()) {
                                        DataManager.deleteAlarmRecordById(AlarmRecordActivity.this.context, ((AlarmRecord) it.next()).id);
                                        try {
                                            Thread.sleep(50);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }).start();
                        } else {
                            AlarmRecordActivity.this.list.clear();
                            AlarmRecordActivity.this.adapter.notifyDataSetChanged();
                            new Thread(new C03492()).start();
                        }
                        AlarmRecordActivity.this.dialog_loading.cancel();
                    }
                });
                dialog.showDialog();
                return;
            case C0291R.id.tv_alarm_tip:
                this.alarm_tip_num = 0;
                this.tv_alarm_tip.setVisibility(8);
                this.list.clear();
                if (this.isSingleDevice) {
                    this.list.addAll(DataManager.findAlarmRecordByActiveUserAndDeviceId2(this.context, NpcCommon.mThreeNum, this.deviceId, this.contact.contactName));
                } else {
                    this.list.addAll(DataManager.findAlarmRecordByActiveUser(this.context, NpcCommon.mThreeNum));
                }
                this.adapter.notifyDataSetChanged();
                this.list_record.postDelayed(new Runnable() {
                    public void run() {
                        AlarmRecordActivity.this.list_record.setSelection(0);
                    }
                }, 1000);
                return;
            default:
                return;
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (this.isRegFilter) {
            this.context.unregisterReceiver(this.mReceiver);
            this.isRegFilter = false;
        }
        if (this.countDownTimer != null) {
            this.countDownTimer.cancel();
        }
    }

    public int getActivityInfo() {
        return 9;
    }
}
