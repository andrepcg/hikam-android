package com.jwkj.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.hikam.C0291R;
import com.jwkj.adapter.BellChoiceAdapter;
import com.jwkj.data.SharedPreferencesManager;
import com.jwkj.data.SystemDataManager;
import com.jwkj.utils.C0568T;
import java.io.IOException;
import java.util.HashMap;
import org.apache.http.cookie.ClientCookie;

public class SettingBellRingActivity extends BaseActivity implements OnClickListener {
    BellChoiceAdapter adapter;
    ImageView back_btn;
    int bellType;
    int checkedId;
    Context context;
    ListView list_sys_bell;
    boolean myreceiverIsReg = false;
    MediaPlayer player;
    MyReceiver receiver;
    Button save_btn;
    TextView selectBell;
    int selectPos;
    RelativeLayout set_bellRing_btn;
    RelativeLayout set_sd_bell_btn;
    int settingType;
    int vibrateState;
    Vibrator vibrator;

    class C04391 implements OnItemClickListener {
        C04391() {
        }

        public void onItemClick(AdapterView<?> adapterView, View arg1, int arg2, long arg3) {
            int id = Integer.parseInt((String) ((HashMap) SettingBellRingActivity.this.adapter.getItem(arg2)).get("bellId"));
            SettingBellRingActivity.this.checkedId = id;
            SettingBellRingActivity.this.selectPos = arg2;
            SettingBellRingActivity.this.adapter.setCheckedId(id);
            SettingBellRingActivity.this.adapter.notifyDataSetChanged();
            SettingBellRingActivity.this.playMusic(SettingBellRingActivity.this.checkedId);
        }
    }

    public class MyReceiver extends BroadcastReceiver {
        public void onReceive(Context arg0, Intent intent) {
            if (intent.getAction().equals(SettingSystemActivity.ACTION_CHANGEBELL)) {
                SettingBellRingActivity.this.initSelectMusicName();
                SettingBellRingActivity.this.initSelectState();
                SettingBellRingActivity.this.list_sys_bell.setSelection(SettingBellRingActivity.this.selectPos);
                SettingBellRingActivity.this.adapter.setCheckedId(SettingBellRingActivity.this.checkedId);
                SettingBellRingActivity.this.adapter.notifyDataSetChanged();
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0291R.layout.set_bell_ring);
        this.context = this;
        this.settingType = getIntent().getIntExtra("type", 0);
        initCompent();
        registerMonitor();
        initSelectMusicName();
    }

    public void initCompent() {
        this.player = new MediaPlayer();
        this.back_btn = (ImageView) findViewById(C0291R.id.back_btn);
        this.save_btn = (Button) findViewById(C0291R.id.save);
        this.set_sd_bell_btn = (RelativeLayout) findViewById(C0291R.id.set_sd_bell_btn);
        this.selectBell = (TextView) findViewById(C0291R.id.selectBell);
        this.list_sys_bell = (ListView) findViewById(C0291R.id.list_sys_bell);
        initSelectState();
        this.adapter = new BellChoiceAdapter(this, SystemDataManager.getInstance().getSysBells(this));
        this.adapter.setCheckedId(this.checkedId);
        this.list_sys_bell.setAdapter(this.adapter);
        this.list_sys_bell.setSelection(this.selectPos);
        this.list_sys_bell.setOnItemClickListener(new C04391());
        this.set_sd_bell_btn.setOnClickListener(this);
        this.save_btn.setOnClickListener(this);
        this.back_btn.setOnClickListener(this);
    }

    public void initSelectState() {
        HashMap<String, String> data;
        if (this.settingType == 0) {
            this.selectPos = SharedPreferencesManager.getInstance().getCBellSelectPos(this);
            this.bellType = SharedPreferencesManager.getInstance().getCBellType(this);
            if (this.bellType == 0) {
                this.checkedId = SharedPreferencesManager.getInstance().getCSystemBellId(this);
                this.selectBell.setText("");
                return;
            }
            this.checkedId = SharedPreferencesManager.getInstance().getCSdBellId(this);
            data = SystemDataManager.getInstance().findSdBellById(this.context, this.checkedId);
            if (data != null) {
                this.selectBell.setText((CharSequence) data.get("bellName"));
            }
            this.checkedId = -1;
            this.selectPos = 1;
        } else if (this.settingType == 1) {
            this.selectPos = SharedPreferencesManager.getInstance().getABellSelectPos(this);
            this.bellType = SharedPreferencesManager.getInstance().getABellType(this);
            if (this.bellType == 0) {
                this.checkedId = SharedPreferencesManager.getInstance().getASystemBellId(this);
                this.selectBell.setText("");
                return;
            }
            this.checkedId = SharedPreferencesManager.getInstance().getASdBellId(this);
            data = SystemDataManager.getInstance().findSdBellById(this.context, this.checkedId);
            if (data != null) {
                this.selectBell.setText((CharSequence) data.get("bellName"));
            }
            this.checkedId = -1;
            this.selectPos = 1;
        }
    }

    public void registerMonitor() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(SettingSystemActivity.ACTION_CHANGEBELL);
        this.receiver = new MyReceiver();
        registerReceiver(this.receiver, filter);
        this.myreceiverIsReg = true;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case C0291R.id.back_btn:
                finish();
                return;
            case C0291R.id.save:
                if (this.checkedId == -1) {
                    C0568T.showShort((Context) this, (int) C0291R.string.savebell_error);
                    return;
                }
                Intent i;
                if (this.settingType == 0) {
                    SharedPreferencesManager.getInstance().putCSystemBellId(this.checkedId, this);
                    SharedPreferencesManager.getInstance().putCBellSelectPos(this.selectPos, this);
                    SharedPreferencesManager.getInstance().putCBellType(0, this);
                    i = new Intent();
                    i.setAction(SettingSystemActivity.ACTION_CHANGEBELL);
                    sendBroadcast(i);
                } else if (this.settingType == 1) {
                    SharedPreferencesManager.getInstance().putASystemBellId(this.checkedId, this);
                    SharedPreferencesManager.getInstance().putABellSelectPos(this.selectPos, this);
                    SharedPreferencesManager.getInstance().putABellType(0, this);
                    i = new Intent();
                    i.setAction(SettingSystemActivity.ACTION_CHANGEBELL);
                    sendBroadcast(i);
                }
                finish();
                return;
            case C0291R.id.set_sd_bell_btn:
                Intent go_set_sd_bell = new Intent(this, SettingSdBellActivity.class);
                go_set_sd_bell.putExtra("type", this.settingType);
                startActivity(go_set_sd_bell);
                return;
            default:
                return;
        }
    }

    public void initSelectMusicName() {
        HashMap<String, String> data;
        if (this.settingType == 0) {
            if (SharedPreferencesManager.getInstance().getCBellType(this) == 0) {
                if (SystemDataManager.getInstance().findSystemBellById(this, SharedPreferencesManager.getInstance().getCSystemBellId(this)) != null) {
                    this.selectBell.setText("");
                    return;
                }
                return;
            }
            data = SystemDataManager.getInstance().findSdBellById(this, SharedPreferencesManager.getInstance().getCSdBellId(this));
            if (data != null) {
                this.selectBell.setText((CharSequence) data.get("bellName"));
            }
        } else if (this.settingType != 1) {
        } else {
            if (SharedPreferencesManager.getInstance().getABellType(this) == 0) {
                if (SystemDataManager.getInstance().findSystemBellById(this, SharedPreferencesManager.getInstance().getASystemBellId(this)) != null) {
                    this.selectBell.setText("");
                    return;
                }
                return;
            }
            data = SystemDataManager.getInstance().findSdBellById(this, SharedPreferencesManager.getInstance().getASdBellId(this));
            if (data != null) {
                this.selectBell.setText((CharSequence) data.get("bellName"));
            }
        }
    }

    public void playMusic(int bellId) {
        try {
            this.player.reset();
            String path = (String) SystemDataManager.getInstance().findSystemBellById(this.context, bellId).get(ClientCookie.PATH_ATTR);
            if (path != null && !"".equals(path)) {
                this.player.setDataSource(path);
                this.player.prepare();
                this.player.start();
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e2) {
            e2.printStackTrace();
        } catch (IOException e3) {
            e3.printStackTrace();
        }
    }

    public void onStop() {
        super.onStop();
        this.player.stop();
    }

    protected void onDestroy() {
        super.onDestroy();
        if (this.myreceiverIsReg) {
            unregisterReceiver(this.receiver);
        }
        this.player.stop();
        this.player.release();
    }

    public int getActivityInfo() {
        return 24;
    }
}
