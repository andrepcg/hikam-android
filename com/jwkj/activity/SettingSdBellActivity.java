package com.jwkj.activity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import com.hikam.C0291R;
import com.jwkj.adapter.BellChoiceAdapter;
import com.jwkj.data.SharedPreferencesManager;
import com.jwkj.data.SystemDataManager;
import com.jwkj.utils.C0568T;
import java.io.IOException;
import java.util.HashMap;
import org.apache.http.cookie.ClientCookie;

public class SettingSdBellActivity extends BaseActivity implements OnClickListener {
    ImageView back_btn;
    int bellType;
    int checkedId;
    Context context;
    ListView list_sd_bell;
    MediaPlayer player;
    Button save_btn;
    int selectPos;
    int settingType;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0291R.layout.set_sd_bell);
        this.settingType = getIntent().getIntExtra("type", 0);
        this.context = this;
        initCompent();
    }

    public void initCompent() {
        this.back_btn = (ImageView) findViewById(C0291R.id.back_btn);
        this.save_btn = (Button) findViewById(C0291R.id.save);
        this.list_sd_bell = (ListView) findViewById(C0291R.id.list_sd_bell);
        this.player = new MediaPlayer();
        initSelectState();
        final BellChoiceAdapter adapter = new BellChoiceAdapter(this, SystemDataManager.getInstance().getSdBells(this));
        adapter.setCheckedId(this.checkedId);
        this.list_sd_bell.setAdapter(adapter);
        this.list_sd_bell.setSelection(this.selectPos);
        this.list_sd_bell.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View arg1, int arg2, long arg3) {
                int id = Integer.parseInt((String) ((HashMap) adapter.getItem(arg2)).get("bellId"));
                SettingSdBellActivity.this.checkedId = id;
                SettingSdBellActivity.this.selectPos = arg2;
                adapter.setCheckedId(id);
                adapter.notifyDataSetChanged();
                SettingSdBellActivity.this.playMusic(SettingSdBellActivity.this.checkedId);
            }
        });
        this.back_btn.setOnClickListener(this);
        this.save_btn.setOnClickListener(this);
    }

    public void initSelectState() {
        if (this.settingType == 0) {
            this.selectPos = SharedPreferencesManager.getInstance().getCBellSelectPos(this);
            this.bellType = SharedPreferencesManager.getInstance().getCBellType(this);
            this.checkedId = SharedPreferencesManager.getInstance().getCSdBellId(this);
            if (this.bellType == 0) {
                this.checkedId = -1;
                this.selectPos = 1;
            }
        } else if (this.settingType == 1) {
            this.selectPos = SharedPreferencesManager.getInstance().getABellSelectPos(this);
            this.bellType = SharedPreferencesManager.getInstance().getABellType(this);
            this.checkedId = SharedPreferencesManager.getInstance().getASdBellId(this);
            if (this.bellType == 0) {
                this.checkedId = -1;
                this.selectPos = 1;
            }
        }
    }

    public void playMusic(int bellId) {
        try {
            this.player.reset();
            this.bellType = SharedPreferencesManager.getInstance().getCBellType(this.context);
            String path = (String) SystemDataManager.getInstance().findSdBellById(this.context, bellId).get(ClientCookie.PATH_ATTR);
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
                    SharedPreferencesManager.getInstance().putCSdBellId(this.checkedId, this);
                    SharedPreferencesManager.getInstance().putCBellSelectPos(this.selectPos, this);
                    SharedPreferencesManager.getInstance().putCBellType(1, this);
                    i = new Intent();
                    i.setAction(SettingSystemActivity.ACTION_CHANGEBELL);
                    sendBroadcast(i);
                } else if (this.settingType == 1) {
                    SharedPreferencesManager.getInstance().putASdBellId(this.checkedId, this);
                    SharedPreferencesManager.getInstance().putABellSelectPos(this.selectPos, this);
                    SharedPreferencesManager.getInstance().putABellType(1, this);
                    i = new Intent();
                    i.setAction(SettingSystemActivity.ACTION_CHANGEBELL);
                    sendBroadcast(i);
                }
                finish();
                return;
            default:
                return;
        }
    }

    public void onStop() {
        super.onStop();
        this.player.stop();
    }

    protected void onDestroy() {
        super.onDestroy();
        this.player.stop();
        this.player.release();
    }

    public int getActivityInfo() {
        return 25;
    }
}
