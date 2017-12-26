package com.jwkj.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import com.hikam.C0291R;
import com.jwkj.data.ContactDB;
import com.jwkj.global.Constants.P2P;
import com.jwkj.utils.C0568T;
import com.jwkj.widget.ScaleRuler;
import com.p2p.core.P2PHandler;
import com.p2p.core.P2PValue;

public class MotionDetectionActivity extends BaseActivity implements OnClickListener {
    private ImageView btn_back;
    private Button btn_save;
    RelativeLayout change_motion;
    String contactId;
    String contactModel;
    String contactPassword;
    int cur_modify_motion_state;
    private boolean isRegFilter = false;
    private boolean isSensitivitySetting = false;
    private LinearLayout ll_sensitivity;
    private Context mContext;
    private BroadcastReceiver mReceiver = new C04272();
    ImageView motion_img;
    int motion_switch;
    ProgressBar progressBar_motion;
    private ScaleRuler scale_ruler;
    int sensitivity = 0;

    class C04261 implements OnSeekBarChangeListener {
        C04261() {
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            int progress = seekBar.getProgress();
            if (progress >= 630) {
                seekBar.setProgress(720);
            } else if (progress >= 450) {
                seekBar.setProgress(540);
            } else if (progress >= 270) {
                seekBar.setProgress(360);
            } else if (progress >= 90) {
                seekBar.setProgress(180);
            } else if (progress >= 0) {
                seekBar.setProgress(0);
            }
            MotionDetectionActivity.this.showProgress_motion();
            if (MotionDetectionActivity.this.cur_modify_motion_state == 1) {
                P2PHandler.getInstance().setMotion(MotionDetectionActivity.this.contactModel, MotionDetectionActivity.this.contactId, MotionDetectionActivity.this.contactPassword, 1, MotionDetectionActivity.this.scale_ruler.getSensitivity());
                return;
            }
            P2PHandler.getInstance().setMotion(MotionDetectionActivity.this.contactModel, MotionDetectionActivity.this.contactId, MotionDetectionActivity.this.contactPassword, 0, MotionDetectionActivity.this.scale_ruler.getSensitivity());
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        }
    }

    class C04272 extends BroadcastReceiver {
        C04272() {
        }

        public void onReceive(Context arg0, Intent intent) {
            if (intent.getAction().equals(P2P.RET_GET_MOTION)) {
                int state = intent.getIntExtra("motionState", -1);
                int sensitivity = intent.getIntExtra("sensitivity", 3) + 1;
                if (state == 1) {
                    MotionDetectionActivity.this.motion_switch = 1;
                    MotionDetectionActivity.this.motion_img.setBackgroundResource(C0291R.drawable.ic_checkbox_on);
                    MotionDetectionActivity.this.scale_ruler.setSensitivity(sensitivity);
                    MotionDetectionActivity.this.scale_ruler.open();
                } else {
                    MotionDetectionActivity.this.motion_switch = 0;
                    MotionDetectionActivity.this.motion_img.setBackgroundResource(C0291R.drawable.ic_checkbox_off);
                    MotionDetectionActivity.this.scale_ruler.setSensitivity(sensitivity);
                    MotionDetectionActivity.this.scale_ruler.close();
                }
                MotionDetectionActivity.this.showMotionState();
                MotionDetectionActivity.this.cur_modify_motion_state = state;
            } else if (intent.getAction().equals(P2P.RET_SET_MOTION)) {
                int result = intent.getIntExtra("result", -1);
                MotionDetectionActivity.this.showMotionState();
                if (result == 0) {
                    C0568T.showShort(MotionDetectionActivity.this.mContext, (int) C0291R.string.modify_success);
                } else {
                    C0568T.showShort(MotionDetectionActivity.this.mContext, (int) C0291R.string.operator_error);
                }
                P2PHandler.getInstance().getNpcSettings(MotionDetectionActivity.this.contactModel, MotionDetectionActivity.this.contactId, MotionDetectionActivity.this.contactPassword);
            }
        }
    }

    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(C0291R.layout.activity_motion_detection);
        this.mContext = this;
        this.contactModel = getIntent().getStringExtra(ContactDB.COLUMN_CONTACT_MODEL);
        this.contactPassword = getIntent().getStringExtra(ContactDB.COLUMN_CONTACT_PASSWORD);
        this.contactId = getIntent().getStringExtra(ContactDB.COLUMN_CONTACT_ID);
        initComponent();
        regFilter();
        P2PHandler.getInstance().getNpcSettings(this.contactModel, this.contactId, this.contactPassword);
    }

    public void showMotionState() {
        this.progressBar_motion.setVisibility(8);
        this.motion_img.setVisibility(0);
        this.change_motion.setEnabled(true);
    }

    public void showProgress_motion() {
        this.progressBar_motion.setVisibility(0);
        this.motion_img.setVisibility(8);
        this.change_motion.setEnabled(false);
    }

    private void regFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(P2P.ACK_RET_SET_MOTION);
        filter.addAction(P2P.RET_SET_MOTION);
        filter.addAction(P2P.RET_GET_MOTION);
        this.mContext.registerReceiver(this.mReceiver, filter);
        this.isRegFilter = true;
    }

    private void initComponent() {
        this.btn_back = (ImageView) findViewById(C0291R.id.back_btn);
        this.btn_save = (Button) findViewById(C0291R.id.save);
        this.btn_back.setOnClickListener(this);
        this.btn_save.setOnClickListener(this);
        this.ll_sensitivity = (LinearLayout) findViewById(C0291R.id.ll_sensitivity);
        if (P2PValue.HikamDeviceModelList.contains(this.contactModel)) {
            this.ll_sensitivity.setVisibility(0);
        }
        this.scale_ruler = (ScaleRuler) findViewById(C0291R.id.scaleRuler);
        this.scale_ruler.close();
        this.scale_ruler.setSensitivity(3);
        this.scale_ruler.setOnSeekBarChangeListener(new C04261());
        this.change_motion = (RelativeLayout) findViewById(C0291R.id.change_motion);
        this.change_motion.setOnClickListener(this);
        this.motion_img = (ImageView) findViewById(C0291R.id.motion_img);
        this.progressBar_motion = (ProgressBar) findViewById(C0291R.id.progressBar_motion);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case C0291R.id.back_btn:
                finish();
                return;
            case C0291R.id.change_motion:
                showProgress_motion();
                if (this.cur_modify_motion_state == 1) {
                    P2PHandler.getInstance().setMotion(this.contactModel, this.contactId, this.contactPassword, 0, this.scale_ruler.getSensitivity());
                    return;
                } else {
                    P2PHandler.getInstance().setMotion(this.contactModel, this.contactId, this.contactPassword, 1, this.scale_ruler.getSensitivity());
                    return;
                }
            case C0291R.id.save:
                showProgress_motion();
                P2PHandler.getInstance().setMotion(this.contactModel, this.contactId, this.contactPassword, this.cur_modify_motion_state, this.scale_ruler.getSensitivity());
                return;
            default:
                return;
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        if (this.isRegFilter) {
            this.mContext.unregisterReceiver(this.mReceiver);
            this.isRegFilter = false;
        }
    }

    public int getActivityInfo() {
        return 0;
    }
}
