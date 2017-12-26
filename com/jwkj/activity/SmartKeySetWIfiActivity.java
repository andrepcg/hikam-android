package com.jwkj.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.hikam.C0291R;
import com.jwkj.global.Constants.Action;
import com.jwkj.utils.C0568T;
import com.jwkj.utils.UDPHelper;
import com.jwkj.widget.NormalDialog;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SmartKeySetWIfiActivity extends BaseActivity implements OnClickListener {
    int SinIndex = 0;
    ImageView anim_load;
    BroadcastReceiver br = new C04514();
    Button choose_wifi;
    int current = 0;
    String devicePwd;
    EditText et_input_device_password;
    EditText et_input_wifi_password;
    int iDevPassword;
    ImageView img_back;
    Button img_insert_infrared;
    boolean isFinish = false;
    boolean isReceive = false;
    boolean isRegFilter = false;
    boolean isRunning = false;
    RelativeLayout layout_total;
    NormalDialog listen;
    private Context mContext;
    private Handler mHandler = new Handler();
    UDPHelper mHelper;
    public Runnable mRunnable = new C04492();
    private Handler myhandler = new Handler();
    private Runnable myrunable = new C04525();
    Button next;
    NormalDialog remind_insert;
    RelativeLayout remind_insert_infrared;
    RelativeLayout reming_listener;
    Handler setwifi = new Handler();
    String ssid;
    AudioTrack track;
    TextView tv_wifi_name;
    int type;
    NormalDialog waitdialog;
    String wifiPwd;
    WifiThread wifithread;

    class C04481 extends Handler {
        C04481() {
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Log.e("my", "HANDLER_MESSAGE_BIND_ERROR");
                    C0568T.showShort(SmartKeySetWIfiActivity.this.mContext, (int) C0291R.string.port_is_occupied);
                    return;
                case 2:
                    SmartKeySetWIfiActivity.this.isReceive = true;
                    Log.e("my", "HANDLER_MESSAGE_RECEIVE_MSG");
                    SmartKeySetWIfiActivity.this.waitdialog.dismiss();
                    C0568T.showShort(SmartKeySetWIfiActivity.this.mContext, (int) C0291R.string.set_wifi_success);
                    SmartKeySetWIfiActivity.this.mHelper.StopListen();
                    SmartKeySetWIfiActivity.this.finish();
                    Intent it = new Intent();
                    it.setAction(Action.SETTING_WIFI_SUCCESS);
                    SmartKeySetWIfiActivity.this.mContext.sendBroadcast(it);
                    String input_password = SmartKeySetWIfiActivity.this.et_input_device_password.getText().toString();
                    if (!input_password.equals("") && input_password != null) {
                        SmartKeySetWIfiActivity.this.startActivity(new Intent(SmartKeySetWIfiActivity.this.mContext, LocalDeviceListActivity.class));
                        return;
                    }
                    return;
                default:
                    return;
            }
        }
    }

    class C04492 implements Runnable {
        C04492() {
        }

        public void run() {
            if (((AudioManager) SmartKeySetWIfiActivity.this.getSystemService("audio")).isWiredHeadsetOn()) {
                SmartKeySetWIfiActivity.this.remind_insert.dismiss();
            } else if (!SmartKeySetWIfiActivity.this.remind_insert.isShowing()) {
                SmartKeySetWIfiActivity.this.remind_insert.showRemindDiaglog(true);
            }
            if (!(SmartKeySetWIfiActivity.this.listen == null || SmartKeySetWIfiActivity.this.listen.isShowing())) {
                SmartKeySetWIfiActivity.this.isRunning = false;
                SmartKeySetWIfiActivity.this.wifithread = null;
            }
            SmartKeySetWIfiActivity.this.mHandler.postDelayed(this, 500);
        }
    }

    class C04503 implements OnTouchListener {
        C04503() {
        }

        public boolean onTouch(View arg0, MotionEvent event) {
            if (event.getAction() != 0) {
                return false;
            }
            SmartKeySetWIfiActivity.this.remind_insert_infrared.setVisibility(8);
            return true;
        }
    }

    class C04514 extends BroadcastReceiver {
        C04514() {
        }

        public void onReceive(Context arg0, Intent intent) {
            if (intent.getAction().equals(Action.CURRENT_WIFI_NAME)) {
                SmartKeySetWIfiActivity.this.ssid = intent.getStringExtra("ssid");
                SmartKeySetWIfiActivity.this.type = intent.getIntExtra("type", 0);
                SmartKeySetWIfiActivity.this.tv_wifi_name.setText(SmartKeySetWIfiActivity.this.ssid);
            } else if (intent.getAction().equals(Action.INSERT_INFRARED_BACK)) {
                SmartKeySetWIfiActivity.this.finish();
            } else if (intent.getAction().equals(Action.HEARED)) {
                SmartKeySetWIfiActivity.this.listen.dismiss();
                SmartKeySetWIfiActivity.this.waitdialog = new NormalDialog(SmartKeySetWIfiActivity.this.mContext);
                SmartKeySetWIfiActivity.this.waitdialog.showWaitConnectionDialog();
                SmartKeySetWIfiActivity.this.mHelper.StartListen();
                SmartKeySetWIfiActivity.this.myhandler.postDelayed(SmartKeySetWIfiActivity.this.myrunable, 60000);
            }
        }
    }

    class C04525 implements Runnable {
        C04525() {
        }

        public void run() {
            if (!SmartKeySetWIfiActivity.this.isReceive) {
                SmartKeySetWIfiActivity.this.waitdialog.dismiss();
                new NormalDialog(SmartKeySetWIfiActivity.this.mContext).faildDialog();
            }
        }
    }

    class WifiThread extends Thread {
        WifiThread() {
        }

        public void run() {
            while (SmartKeySetWIfiActivity.this.isRunning) {
                try {
                    SmartKeySetWIfiActivity.this.vSetWiFiByIR(SmartKeySetWIfiActivity.this.iDevPassword, SmartKeySetWIfiActivity.this.type, SmartKeySetWIfiActivity.this.ssid, SmartKeySetWIfiActivity.this.wifiPwd, SmartKeySetWIfiActivity.this.track);
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    int iCrc32(byte[] buf, int len) {
        int dwCrcValue = -1437217639;
        int i = 0;
        int[] crc_table = new int[]{0, 1996959894, -301047508, -1727442502, 124634137, 1886057615, -379345611, -1637575261, 249268274, 2044508324, -522852066, -1747789432, 162941995, 2125561021, -407360249, -1866523247, 498536548, 1789927666, -205950648, -2067906082, 450548861, 1843258603, -187386543, -2083289657, 325883990, 1684777152, -43845254, -1973040660, 335633487, 1661365465, -99664541, -1928851979, 997073096, 1281953886, -715111964, -1570279054, 1006888145, 1258607687, -770865667, -1526024853, 901097722, 1119000684, -608450090, -1396901568, 853044451, 1172266101, -589951537, -1412350631, 651767980, 1373503546, -925412992, -1076862698, 565507253, 1454621731, -809855591, -1195530993, 671266974, 1594198024, -972236366, -1324619484, 795835527, 1483230225, -1050600021, -1234817731, 1994146192, 31158534, -1731059524, -271249366, 1907459465, 112637215, -1614814043, -390540237, 2013776290, 251722036, -1777751922, -519137256, 2137656763, 141376813, -1855689577, -429695999, 1802195444, 476864866, -2056965928, -228458418, 1812370925, 453092731, -2113342271, -183516073, 1706088902, 314042704, -1950435094, -54949764, 1658658271, 366619977, -1932296973, -69972891, 1303535960, 984961486, -1547960204, -725929758, 1256170817, 1037604311, -1529756563, -740887301, 1131014506, 879679996, -1385723834, -631195440, 1141124467, 855842277, -1442165665, -586318647, 1342533948, 654459306, -1106571248, -921952122, 1466479909, 544179635, -1184443383, -832445281, 1591671054, 702138776, -1328506846, -942167884, 1504918807, 783551873, -1212326853, -1061524307, -306674912, -1698712650, 62317068, 1957810842, -355121351, -1647151185, 81470997, 1943803523, -480048366, -1805370492, 225274430, 2053790376, -468791541, -1828061283, 167816743, 2097651377, -267414716, -2029476910, 503444072, 1762050814, -144550051, -2140837941, 426522225, 1852507879, -19653770, -1982649376, 282753626, 1742555852, -105259153, -1900089351, 397917763, 1622183637, -690576408, -1580100738, 953729732, 1340076626, -776247311, -1497606297, 1068828381, 1219638859, -670225446, -1358292148, 906185462, 1090812512, -547295293, -1469587627, 829329135, 1181335161, -882789492, -1134132454, 628085408, 1382605366, -871598187, -1156888829, 570562233, 1426400815, -977650754, -1296233688, 733239954, 1555261956, -1026031705, -1244606671, 752459403, 1541320221, -1687895376, -328994266, 1969922972, 40735498, -1677130071, -351390145, 1913087877, 83908371, -1782625662, -491226604, 2075208622, 213261112, -1831694693, -438977011, 2094854071, 198958881, -2032938284, -237706686, 1759359992, 534414190, -2118248755, -155638181, 1873836001, 414664567, -2012718362, -15766928, 1711684554, 285281116, -1889165569, -127750551, 1634467795, 376229701, -1609899400, -686959890, 1308918612, 956543938, -1486412191, -799009033, 1231636301, 1047427035, -1362007478, -640263460, 1088359270, 936918000, -1447252397, -558129467, 1202900863, 817233897, -1111625188, -893730166, 1404277552, 615818150, -1160759803, -841546093, 1423857449, 601450431, -1285129682, -1000256840, 1567103746, 711928724, -1274298825, -1022587231, 1510334235, 755167117};
        if (len != 0) {
            do {
                dwCrcValue = crc_table[(buf[i] ^ dwCrcValue) & 255] ^ (dwCrcValue >> 8);
                i++;
                len--;
            } while (len != 0);
        }
        return dwCrcValue;
    }

    protected short SinValue(int frequence) {
        this.SinIndex++;
        return (short) ((int) (32767.0d * Math.sin(((6.28d * ((double) this.SinIndex)) * ((double) frequence)) / 44100.0d)));
    }

    protected int iCreate5KHzHeader(short[] wBuffer) {
        for (int i = 0; i < 1102; i++) {
            wBuffer[i * 2] = (short) ((int) (Math.sin(((((double) i) * 6.28d) * 5000.0d) / 44100.0d) * 32767.0d));
            wBuffer[(i * 2) + 1] = (short) ((int) (Math.sin(((((double) i) * 6.28d) * 5000.0d) / 44100.0d) * 32767.0d));
        }
        return 7054;
    }

    protected int iFillSinData(short[] wBuffer, int offset, int pointer, double time_begin_pos, double time_end_pos) {
        this.SinIndex = 0;
        while (true) {
            double t1 = (((double) pointer) * 1000.0d) / 44100.0d;
            if (t1 > time_end_pos) {
                return pointer;
            }
            if (t1 < time_begin_pos || t1 > time_end_pos) {
                wBuffer[(pointer * 2) + offset] = (short) 0;
            } else {
                wBuffer[(pointer * 2) + offset] = SinValue(12600);
            }
            wBuffer[((pointer * 2) + offset) + 1] = wBuffer[(pointer * 2) + offset];
            pointer++;
        }
    }

    protected int iCreateAudioDataSeq(byte[] TransData, int iDataLen, short[] wBuffer, int offset) {
        double time_begin = 9.0d + 4.5d;
        double time_end = time_begin + 0.56d;
        int pos = iFillSinData(wBuffer, offset, iFillSinData(wBuffer, offset, 0, 0.0d, 9.0d), time_begin, time_end);
        for (int i = 0; i < iDataLen; i++) {
            byte data = TransData[i];
            for (int j = 0; j < 8; j++) {
                if ((data & 1) != 0) {
                    time_begin = time_end + 1.69d;
                    time_end = time_begin + 0.56d;
                    pos = iFillSinData(wBuffer, offset, pos, time_begin, time_end);
                } else {
                    time_begin = time_end + 0.56d;
                    time_end = time_begin + 0.56d;
                    pos = iFillSinData(wBuffer, offset, pos, time_begin, time_end);
                }
                data = (byte) (data >> 1);
            }
        }
        time_begin = time_end + 40.0d;
        time_end = time_begin + 9.0d;
        pos = iFillSinData(wBuffer, offset, pos, time_begin, time_end);
        time_begin = time_end + 2.25d;
        return iFillSinData(wBuffer, offset, pos, time_begin, time_begin + 0.55d) * 2;
    }

    protected int iCreateSetWiFiCmdRawData(int DevicePassword, int iType, String SSID, String WiFiPassword, byte[] bCmdBuf) {
        int i;
        bCmdBuf[0] = (byte) 1;
        bCmdBuf[1] = (byte) (((byte) (DevicePassword >> 24)) & -1);
        bCmdBuf[2] = (byte) (((byte) (DevicePassword >> 16)) & -1);
        bCmdBuf[3] = (byte) (((byte) (DevicePassword >> 8)) & -1);
        bCmdBuf[4] = (byte) (((byte) (DevicePassword >> 0)) & -1);
        bCmdBuf[5] = (byte) (((byte) iType) & 3);
        int k = 6;
        for (i = 0; i < SSID.length(); i++) {
            bCmdBuf[k] = (byte) SSID.charAt(i);
            k++;
        }
        bCmdBuf[k] = (byte) 0;
        k++;
        for (i = 0; i < WiFiPassword.length(); i++) {
            bCmdBuf[k] = (byte) WiFiPassword.charAt(i);
            k++;
        }
        bCmdBuf[k] = (byte) 0;
        return k + 1;
    }

    protected int SetWiFiCmd(int DevicePassword, int iType, String SSID, String WiFiPassword, short[] PlayBuf) {
        byte bTmp;
        byte[] bCmdData = new byte[256];
        Arrays.fill(bCmdData, (byte) 0);
        int ilen = iCreateSetWiFiCmdRawData(DevicePassword, iType, SSID, WiFiPassword, bCmdData);
        Arrays.fill(PlayBuf, (short) 0);
        int AudioDataLen = iCreate5KHzHeader(PlayBuf);
        byte[] bPlayData = new byte[6];
        int iSeqCnt = (ilen / 5) + 1;
        if (ilen % 5 != 0) {
            iSeqCnt++;
        }
        int idx = 0;
        for (int i = 0; i < iSeqCnt; i++) {
            bPlayData[1] = bCmdData[idx];
            idx++;
            bPlayData[2] = bCmdData[idx];
            idx++;
            bPlayData[3] = bCmdData[idx];
            idx++;
            bPlayData[4] = bCmdData[idx];
            idx++;
            bPlayData[5] = bCmdData[idx];
            idx++;
            bPlayData[0] = (byte) (((byte) i) & 15);
            bTmp = (byte) (((((bPlayData[0] ^ bPlayData[1]) ^ bPlayData[2]) ^ bPlayData[3]) ^ bPlayData[4]) ^ bPlayData[5]);
            bPlayData[0] = (byte) (((byte) ((bTmp & 240) ^ (bTmp << 4))) | (bPlayData[0] & 15));
            AudioDataLen = (AudioDataLen + iCreateAudioDataSeq(bPlayData, 6, PlayBuf, AudioDataLen)) + 1760;
            if (i == 0) {
                AudioDataLen = (AudioDataLen + iCreateAudioDataSeq(bPlayData, 6, PlayBuf, AudioDataLen)) + 1760;
            }
        }
        bCrcData = new byte[6];
        int iCrcValue = iCrc32(bCmdData, ilen);
        bCrcData[0] = (byte) 15;
        bCrcData[1] = (byte) ilen;
        bCrcData[2] = (byte) (((byte) (iCrcValue >> 24)) & 255);
        bCrcData[3] = (byte) (((byte) (iCrcValue >> 16)) & 255);
        bCrcData[4] = (byte) (((byte) (iCrcValue >> 8)) & 255);
        bCrcData[5] = (byte) (((byte) (iCrcValue >> 0)) & 255);
        bTmp = (byte) (((((bCrcData[0] ^ bCrcData[1]) ^ bCrcData[2]) ^ bCrcData[3]) ^ bCrcData[4]) ^ bCrcData[5]);
        bCrcData[0] = (byte) (((byte) ((bTmp & 240) ^ (bTmp << 4))) | (bCrcData[0] & 15));
        Log.e("e", "iCrcValue=" + iCrcValue + "  ilen=" + ilen);
        return (AudioDataLen + iCreateAudioDataSeq(bCrcData, 6, PlayBuf, AudioDataLen)) + 4400;
    }

    protected int iSetPTZCmd(byte bDirection, short[] PlayBuf) {
        Arrays.fill(new byte[256], (byte) 0);
        Arrays.fill(PlayBuf, (short) 0);
        int AudioDataLen = iCreate5KHzHeader(PlayBuf);
        byte[] bData = new byte[]{(byte) 15, (byte) 4, (byte) 2, bDirection, (byte) 0, (byte) 0};
        byte bTmp = (byte) (((((bData[0] ^ bData[1]) ^ bData[2]) ^ bData[3]) ^ bData[4]) ^ bData[5]);
        bData[0] = (byte) (((byte) ((bTmp & 240) ^ (bTmp << 4))) | (bData[0] & 15));
        AudioDataLen = (AudioDataLen + iCreateAudioDataSeq(bData, 6, PlayBuf, AudioDataLen)) + 1760;
        AudioDataLen = (AudioDataLen + iCreateAudioDataSeq(bData, 6, PlayBuf, AudioDataLen)) + 1760;
        return (AudioDataLen + iCreateAudioDataSeq(bData, 6, PlayBuf, AudioDataLen)) + 1760;
    }

    protected void vSendDataToTrack(short[] play_buffer, int iLen, AudioTrack track, int iRetryCnt) {
        int AudioBufferSize = AudioTrack.getMinBufferSize(44100, 12, 2);
        while (iRetryCnt > 0) {
            if (iLen <= AudioBufferSize) {
                track.write(play_buffer, 0, iLen);
            } else {
                int offset = 0;
                for (int i = 0; i < (iLen / AudioBufferSize) + 1; i++) {
                    track.write(play_buffer, offset, AudioBufferSize);
                    offset += AudioBufferSize;
                    track.flush();
                }
            }
            iRetryCnt--;
        }
        Log.e("e", "vSendDataToTrack");
    }

    protected void vSetWiFiByIR(int DevicePassword, int iType, String SSID, String WiFiPassword, AudioTrack track) {
        short[] play_buffer = new short[441000];
        vSendDataToTrack(play_buffer, SetWiFiCmd(DevicePassword, iType, SSID, WiFiPassword, play_buffer), track, 1);
        flushTrack(track);
    }

    protected void vPTZCtrlByIR(byte bDirection, AudioTrack track) {
        short[] play_buffer = new short[441000];
        vSendDataToTrack(play_buffer, iSetPTZCmd(bDirection, play_buffer), track, 1);
        flushTrack(track);
        Log.e("my", "send:" + bDirection);
    }

    protected void flushTrack(AudioTrack track) {
        vSendDataToTrack(new short[17640], 17640, track, 1);
    }

    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        Window win = getWindow();
        win.addFlags(4718592);
        win.addFlags(2097280);
        setContentView(C0291R.layout.activity_smart_key);
        this.mContext = this;
        initComponent();
        regFilter();
        maxVoice();
        currentWifi();
        AudioManager localAudioManager = (AudioManager) getSystemService("audio");
        this.remind_insert = new NormalDialog(this.mContext);
        if (localAudioManager.isWiredHeadsetOn()) {
            this.remind_insert.showRemindDiaglog(false);
        } else {
            this.remind_insert.showRemindDiaglog(true);
        }
        this.track = new AudioTrack(3, 44100, 12, 2, AudioTrack.getMinBufferSize(44100, 12, 2) * 2, 1);
        this.track.play();
        this.mHandler.postDelayed(this.mRunnable, 500);
        this.mHelper = new UDPHelper(9988);
        this.mHelper.setCallBack(new C04481());
    }

    public void initComponent() {
        this.tv_wifi_name = (TextView) findViewById(C0291R.id.tv_wifi_name);
        this.et_input_wifi_password = (EditText) findViewById(C0291R.id.input_wifi_password);
        this.et_input_device_password = (EditText) findViewById(C0291R.id.input_device_password);
        this.choose_wifi = (Button) findViewById(C0291R.id.choose_wifi);
        this.img_back = (ImageView) findViewById(C0291R.id.img_back);
        this.next = (Button) findViewById(C0291R.id.next);
        this.choose_wifi.setOnClickListener(this);
        this.next.setOnClickListener(this);
        this.img_back.setOnClickListener(this);
        this.reming_listener = (RelativeLayout) findViewById(C0291R.id.remind_listener);
        this.anim_load = (ImageView) findViewById(C0291R.id.anim_load);
        this.layout_total = (RelativeLayout) findViewById(C0291R.id.layout_total);
        this.remind_insert_infrared = (RelativeLayout) findViewById(C0291R.id.remind_insert_infrared);
        this.remind_insert_infrared.setOnTouchListener(new C04503());
    }

    public void regFilter() {
        this.isRegFilter = true;
        IntentFilter filter = new IntentFilter();
        filter.addAction(Action.CURRENT_WIFI_NAME);
        filter.addAction(Action.HEARED);
        filter.addAction(Action.INSERT_INFRARED_BACK);
        this.mContext.registerReceiver(this.br, filter);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case C0291R.id.choose_wifi:
                startActivity(new Intent(this, WifiListActivity.class));
                return;
            case C0291R.id.img_back:
                finish();
                return;
            case C0291R.id.next:
                this.wifiPwd = this.et_input_wifi_password.getText().toString();
                this.devicePwd = this.et_input_device_password.getText().toString();
                if (this.ssid.equals("<unknown ssid>")) {
                    C0568T.showShort(this.mContext, (int) C0291R.string.please_choose_wireless);
                    return;
                } else if (this.wifiPwd == null || (this.wifiPwd.length() <= 0 && (this.type == 1 || this.type == 2))) {
                    C0568T.showShort(this.mContext, (int) C0291R.string.please_input_wifi_password);
                    return;
                } else {
                    this.iDevPassword = 0;
                    if (this.devicePwd == null || this.devicePwd.length() <= 0) {
                        this.iDevPassword = 0;
                    } else {
                        try {
                            this.iDevPassword = Integer.parseInt(this.devicePwd);
                        } catch (Exception e) {
                            C0568T.showShort(this.mContext, (int) C0291R.string.device_pwd_must_be);
                            return;
                        }
                    }
                    if (this.ssid == null) {
                        C0568T.showShort(this.mContext, (int) C0291R.string.please_choose_wireless);
                        return;
                    }
                    this.listen = new NormalDialog(this.mContext);
                    this.listen.showListenDialog();
                    this.isRunning = true;
                    this.wifithread = new WifiThread();
                    this.wifithread.start();
                    return;
                }
            default:
                return;
        }
    }

    public void maxVoice() {
        AudioManager audioManager = (AudioManager) getSystemService("audio");
        audioManager.setStreamVolume(3, audioManager.getStreamMaxVolume(3), 0);
    }

    public void currentWifi() {
        WifiManager manager = (WifiManager) getSystemService("wifi");
        if (manager.isWifiEnabled()) {
            this.ssid = manager.getConnectionInfo().getSSID();
            Log.e("ssid", this.ssid);
            List<ScanResult> datas = new ArrayList();
            WifiManager mWifiManager = (WifiManager) this.mContext.getSystemService("wifi");
            if (mWifiManager.isWifiEnabled()) {
                WifiInfo mWifiInfo = mWifiManager.getConnectionInfo();
                mWifiManager.startScan();
                datas = mWifiManager.getScanResults();
                if (this.ssid != null) {
                    if (this.ssid.charAt(0) == 34) {
                        this.ssid = this.ssid.substring(1, this.ssid.length() - 1);
                    }
                    if (!(this.ssid.equals("<unknown ssid>") || this.ssid.equals("0x"))) {
                        this.tv_wifi_name.setText(this.ssid);
                    }
                    int i = 0;
                    while (i < datas.size()) {
                        ScanResult result = (ScanResult) datas.get(i);
                        if (!((ScanResult) datas.get(i)).SSID.equals(this.ssid)) {
                            i++;
                        } else if (result.capabilities.indexOf("WPA") > 0) {
                            this.type = 2;
                            return;
                        } else if (result.capabilities.indexOf("WEP") > 0) {
                            this.type = 1;
                            return;
                        } else {
                            this.type = 0;
                            return;
                        }
                    }
                }
            }
        }
    }

    protected void onPause() {
        super.onPause();
    }

    protected void onDestroy() {
        super.onDestroy();
        if (this.isRegFilter) {
            this.isRegFilter = false;
            this.mContext.unregisterReceiver(this.br);
        }
        if (this.track != null) {
            this.track.stop();
            this.track = null;
        }
        this.isFinish = true;
        this.mHandler.removeCallbacks(this.mRunnable);
        this.myhandler.removeCallbacks(this.myrunable);
        this.mHelper.StopListen();
    }

    protected void onResume() {
        super.onResume();
    }

    public int getActivityInfo() {
        return 49;
    }
}
