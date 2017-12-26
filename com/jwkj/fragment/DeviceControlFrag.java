package com.jwkj.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import cn.com.streamax.miotp.p2p.jni.P2pJni;
import com.hikam.C0291R;
import com.jwkj.activity.MainControlActivity;
import com.jwkj.data.Contact;
import com.jwkj.data.ContactDB;
import com.jwkj.global.Constants.Action;
import com.jwkj.global.Constants.P2P;
import com.jwkj.utils.C0568T;
import com.p2p.core.P2PHandler;
import java.lang.reflect.Field;

public class DeviceControlFrag extends BaseFragment implements OnClickListener {
    private Button btn_upload;
    private Contact contact;
    private ImageView img_lamp;
    private ImageView img_rtsp;
    private boolean isRegFilter = false;
    private boolean lampStatus = false;
    private Context mContext;
    private BroadcastReceiver mReceiver = new C05261();
    private ProgressBar progressBar;
    private ProgressBar progressBar_lamp;
    private ProgressBar progressBar_rtsp;
    private RelativeLayout rl_lamp;
    private RelativeLayout rl_rtsp;
    private boolean rtspStatus = false;

    class C05261 extends BroadcastReceiver {
        C05261() {
        }

        public void onReceive(Context arg0, Intent intent) {
            if (intent.getAction().equals(P2P.ACK_RET_SET_UPLOAD_TO_SVR)) {
                DeviceControlFrag.this.progressBar.setVisibility(8);
                DeviceControlFrag.this.btn_upload.setVisibility(0);
                if (intent.getIntExtra("result", -1) == 0) {
                    C0568T.showShort(DeviceControlFrag.this.getActivity(), (int) C0291R.string.upload_success);
                } else {
                    C0568T.showShort(DeviceControlFrag.this.getActivity(), (int) C0291R.string.upload_fail);
                }
            } else if (intent.getAction().equals(P2P.ACK_RET_GET_LAMP_SWITCH)) {
                r1 = intent.getIntExtra("result", -1);
                if (intent.getIntExtra("onoff", -1) == 0) {
                    DeviceControlFrag.this.showLampOpen();
                } else {
                    DeviceControlFrag.this.showLampClose();
                }
            } else if (intent.getAction().equals(P2P.ACK_RET_SET_LAMP_SWITCH)) {
                if (intent.getIntExtra("result", -1) == 0) {
                    if (DeviceControlFrag.this.lampStatus) {
                        C0568T.showShort(DeviceControlFrag.this.mContext, (int) C0291R.string.lamp_close_success);
                        DeviceControlFrag.this.showLampClose();
                        return;
                    }
                    C0568T.showShort(DeviceControlFrag.this.mContext, (int) C0291R.string.lamp_open_success);
                    DeviceControlFrag.this.showLampOpen();
                } else if (DeviceControlFrag.this.lampStatus) {
                    C0568T.showShort(DeviceControlFrag.this.mContext, (int) C0291R.string.lamp_close_failed);
                    DeviceControlFrag.this.showLampOpen();
                } else {
                    C0568T.showShort(DeviceControlFrag.this.mContext, (int) C0291R.string.lamp_open_failed);
                    DeviceControlFrag.this.showLampClose();
                }
            } else if (P2P.RET_GET_RTSP_SWITCH.equals(intent.getAction())) {
                r1 = intent.getIntExtra("result", -1);
                int state = intent.getIntExtra("state", 0);
                if (r1 == 0 && state == 1) {
                    DeviceControlFrag.this.showRtspOpen();
                } else {
                    DeviceControlFrag.this.showRtspClose();
                }
            } else if (!P2P.RET_SET_RTSP_SWITCH.equals(intent.getAction())) {
            } else {
                if (intent.getIntExtra("result", -1) == 0) {
                    P2pJni.P2PClientSdkGetRtspSwitch(DeviceControlFrag.this.contact.contactId, DeviceControlFrag.this.contact.contactPassword, 15016);
                } else {
                    DeviceControlFrag.this.showRtspClose();
                }
            }
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mContext = MainControlActivity.mContext;
        this.contact = (Contact) getArguments().getSerializable(ContactDB.TABLE_NAME);
        View view = inflater.inflate(C0291R.layout.fragment_device_control, container, false);
        initComponent(view);
        regFilter();
        P2PHandler.getInstance().getLampSwitch(this.contact.contactModel, this.contact.contactId, this.contact.contactPassword);
        P2pJni.P2PClientSdkGetRtspSwitch(this.contact.contactId, this.contact.contactPassword, 15016);
        showSwitching();
        showRtspSwitching();
        return view;
    }

    public void initComponent(View view) {
        this.progressBar = (ProgressBar) view.findViewById(C0291R.id.progressBar);
        this.btn_upload = (Button) view.findViewById(C0291R.id.bt_upload);
        this.btn_upload.setOnClickListener(this);
        this.progressBar_lamp = (ProgressBar) view.findViewById(C0291R.id.progressBar_lamp);
        this.img_lamp = (ImageView) view.findViewById(C0291R.id.img_receive_lamp);
        this.rl_lamp = (RelativeLayout) view.findViewById(C0291R.id.layout_lamp_status);
        this.rl_lamp.setOnClickListener(this);
        this.progressBar_rtsp = (ProgressBar) view.findViewById(C0291R.id.progressBar_rtsp);
        this.img_rtsp = (ImageView) view.findViewById(C0291R.id.img_rtsp);
        this.rl_rtsp = (RelativeLayout) view.findViewById(C0291R.id.rl_rtsp);
        this.rl_rtsp.setOnClickListener(this);
    }

    public void regFilter() {
        MainControlActivity.isCancelCheck = true;
        IntentFilter filter = new IntentFilter();
        filter.addAction(P2P.ACK_RET_SET_UPLOAD_TO_SVR);
        filter.addAction(P2P.ACK_RET_SET_LAMP_SWITCH);
        filter.addAction(P2P.ACK_RET_GET_LAMP_SWITCH);
        filter.addAction(P2P.RET_SET_RTSP_SWITCH);
        filter.addAction(P2P.RET_GET_RTSP_SWITCH);
        this.mContext.registerReceiver(this.mReceiver, filter);
        this.isRegFilter = true;
    }

    public void showRtspOpen() {
        this.rl_rtsp.setClickable(true);
        this.rtspStatus = true;
        this.img_rtsp.setVisibility(0);
        this.img_rtsp.setBackgroundResource(C0291R.drawable.ic_checkbox_on);
        this.progressBar_rtsp.setVisibility(8);
    }

    public void showRtspClose() {
        this.rl_rtsp.setClickable(true);
        this.rtspStatus = false;
        this.img_rtsp.setVisibility(0);
        this.img_rtsp.setBackgroundResource(C0291R.drawable.ic_checkbox_off);
        this.progressBar_rtsp.setVisibility(8);
    }

    public void showRtspSwitching() {
        this.rl_rtsp.setClickable(false);
        this.img_rtsp.setVisibility(8);
        this.progressBar_rtsp.setVisibility(0);
    }

    public void showLampOpen() {
        this.lampStatus = true;
        this.img_lamp.setVisibility(0);
        this.img_lamp.setBackgroundResource(C0291R.drawable.ic_checkbox_on);
        this.progressBar_lamp.setVisibility(8);
    }

    public void showLampClose() {
        this.lampStatus = false;
        this.img_lamp.setVisibility(0);
        this.img_lamp.setBackgroundResource(C0291R.drawable.ic_checkbox_off);
        this.progressBar_lamp.setVisibility(8);
    }

    public void showSwitching() {
        this.img_lamp.setVisibility(8);
        this.progressBar_lamp.setVisibility(0);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case C0291R.id.bt_upload:
                P2PHandler.getInstance().setUploadToSvr(this.contact.contactModel, this.contact.contactId, this.contact.contactPassword);
                this.progressBar.setVisibility(0);
                this.btn_upload.setVisibility(8);
                return;
            case C0291R.id.layout_lamp_status:
                showSwitching();
                if (this.lampStatus) {
                    P2PHandler.getInstance().setLampSwitch(this.contact.contactModel, this.contact.contactId, this.contact.contactPassword, 1);
                    return;
                } else {
                    P2PHandler.getInstance().setLampSwitch(this.contact.contactModel, this.contact.contactId, this.contact.contactPassword, 0);
                    return;
                }
            case C0291R.id.rl_rtsp:
                if (this.rtspStatus) {
                    P2pJni.P2PClientSdkSetRtspSwitch(this.contact.contactId, this.contact.contactPassword, 0, 15016112);
                    showRtspSwitching();
                    return;
                }
                P2pJni.P2PClientSdkSetRtspSwitch(this.contact.contactId, this.contact.contactPassword, 1, 15016112);
                showRtspSwitching();
                return;
            default:
                return;
        }
    }

    public void onDestroyView() {
        super.onDestroyView();
        if (this.isRegFilter) {
            this.mContext.unregisterReceiver(this.mReceiver);
            this.isRegFilter = false;
        }
        MainControlActivity.isCancelCheck = false;
    }

    public void onDestroy() {
        super.onDestroy();
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
