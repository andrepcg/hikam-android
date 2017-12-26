package com.jwkj.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import com.hikam.C0291R;
import com.jwkj.ShortAVActivity;
import com.jwkj.activity.AlarmSetActivity;
import com.jwkj.activity.MainActivity;
import com.jwkj.entity.CmdAlarmFeedback;
import com.jwkj.entity.CmdAlarmFeedback.CmdParamBean;
import com.jwkj.entity.CmdAlarmFeedback.CmdParamBean.AlarmInfoBean;
import com.jwkj.entity.CmdAlarmFeedback.CmdParamBean.AppInfoBean;
import com.jwkj.entity.CmdAlarmFeedback.CmdParamBean.FeedbackInfoBean;
import com.jwkj.global.Constants.P2P;
import com.jwkj.global.NpcCommon;
import com.jwkj.net.CMD;
import com.jwkj.net.HKHttpClient;
import com.jwkj.net.HKHttpClient.HKCallback;
import com.jwkj.widget.album.AlbumManagerActivity;
import com.p2p.core.P2PHandler;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Response;

public class UtilsFrag extends BaseFragment implements OnClickListener {
    private RelativeLayout alarm_set_btn;
    private RelativeLayout layout_infrared_remote;
    private RelativeLayout layout_qrcode;
    private RelativeLayout layout_shake;
    private Context mContext;
    BroadcastReceiver receiver = new C05533();
    private RelativeLayout screenshot;
    private RelativeLayout videorecord;

    class C05521 implements OnClickListener {

        class C05511 implements Runnable {
            C05511() {
            }

            public void run() {
            }
        }

        C05521() {
        }

        public void onClick(View v) {
            new Thread(new C05511()).start();
        }
    }

    class C05533 extends BroadcastReceiver {
        C05533() {
        }

        public void onReceive(Context arg0, Intent intent) {
            if (intent.getAction().equals(P2P.P2P_ACCEPT)) {
                P2PHandler.getInstance().openAudioAndStartPlaying(2);
            } else if (intent.getAction().equals(P2P.P2P_READY)) {
                P2PHandler.getInstance().openAudioAndStartPlaying(2);
                Intent intentCall = new Intent();
                intentCall.setClass(UtilsFrag.this.getActivity(), ShortAVActivity.class);
                intentCall.putExtra("type", 2);
                intentCall.setFlags(268435456);
                UtilsFrag.this.startActivity(intentCall);
            }
        }
    }

    class C11222 implements HKCallback {
        C11222() {
        }

        public void onFailure(Call call, IOException e) {
        }

        public void onResponse(Call call, Response response) {
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(C0291R.layout.fragment_utils, container, false);
        this.mContext = MainActivity.mContext;
        initComponent(view);
        regFilter();
        return view;
    }

    public void regFilter() {
    }

    public void initComponent(View view) {
        ((Button) Button.class.cast(view.findViewById(C0291R.id.btn_test))).setOnClickListener(new C05521());
        this.videorecord = (RelativeLayout) view.findViewById(C0291R.id.videorecord);
        this.screenshot = (RelativeLayout) view.findViewById(C0291R.id.screenshot);
        this.alarm_set_btn = (RelativeLayout) view.findViewById(C0291R.id.alarm_set_btn);
        this.alarm_set_btn.setOnClickListener(this);
        this.screenshot.setOnClickListener(this);
        this.videorecord.setOnClickListener(this);
        if (NpcCommon.mThreeNum.equals("517400")) {
            this.alarm_set_btn.setVisibility(8);
            this.screenshot.setBackgroundResource(C0291R.drawable.tiao_bg_single);
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case C0291R.id.alarm_set_btn:
                startActivity(new Intent(this.mContext, AlarmSetActivity.class));
                return;
            case C0291R.id.screenshot:
                startActivity(new Intent(this.mContext, AlbumManagerActivity.class));
                return;
            case C0291R.id.videorecord:
                CmdAlarmFeedback cmdAlarmFeedback = new CmdAlarmFeedback();
                cmdAlarmFeedback.setMagic_number(CMD.MAGIC_NUMBER);
                cmdAlarmFeedback.setMessage_id((int) (System.currentTimeMillis() / 1000));
                cmdAlarmFeedback.setDate_time(CMD.getDateTime("yyyy-MM-dd HH:mm:ss"));
                cmdAlarmFeedback.setMessage_cmd(CMD.CMD_ALARM_FEEDBACK);
                CmdParamBean bean = new CmdParamBean();
                AlarmInfoBean alarmInfoBean = new AlarmInfoBean();
                alarmInfoBean.setAlarm_type("Motion Detection");
                alarmInfoBean.setAlarm_uuid("00e63f8a-1dd2-11b2-b507-a2930bdde3dd");
                alarmInfoBean.setCamera_id("A003515");
                alarmInfoBean.setCamera_name("haha");
                FeedbackInfoBean feedbackInfoBean = new FeedbackInfoBean();
                feedbackInfoBean.setFeedback_content("content hahhahahahahahahahahahahahahhaha");
                feedbackInfoBean.setFeedback_result("result shm shi resul fjewiofewpo");
                AppInfoBean appInfoBean = new AppInfoBean();
                appInfoBean.setDevice_token(CMD.getToken());
                appInfoBean.setUser_name("oaosj");
                bean.setAlarm_info(alarmInfoBean);
                bean.setApp_info(appInfoBean);
                bean.setFeedback_info(feedbackInfoBean);
                cmdAlarmFeedback.setCmd_param(bean);
                HKHttpClient.getInstance().asyncPost(cmdAlarmFeedback, CMD.URL2, new C11222());
                return;
            default:
                return;
        }
    }
}
