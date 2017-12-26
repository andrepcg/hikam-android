package com.jwkj.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import cn.com.streamax.miotp.p2p.jni.P2pJni;
import com.google.firebase.analytics.FirebaseAnalytics.Param;
import com.hikam.C0291R;
import com.jwkj.activity.MainControlActivity;
import com.jwkj.data.Contact;
import com.jwkj.data.ContactDB;
import com.jwkj.global.Constants.Action;
import com.jwkj.global.Constants.P2P;
import com.jwkj.thread.DelayThread;
import com.jwkj.thread.DelayThread.OnRunListener;
import com.jwkj.utils.C0568T;
import com.p2p.core.P2PHandler;
import com.p2p.core.P2PValue;
import com.p2p.core.P2PValue.HikamDeviceModel;
import java.lang.reflect.Field;

public class VideoControlFrag extends BaseFragment implements OnClickListener {
    RelativeLayout change_image_reverse;
    RelativeLayout change_ldc;
    RelativeLayout change_video_format;
    RelativeLayout change_volume;
    private Contact contact;
    int cur_modify_video_format;
    int cur_modify_video_volume;
    ImageView img_image_ldc;
    ImageView img_image_reverse;
    boolean isLdcOpen = false;
    boolean isOpenModify;
    boolean isOpenReverse = true;
    private boolean isRegFilter = false;
    RelativeLayout layout_reverse;
    private Context mContext;
    private BroadcastReceiver mReceiver = new C05552();
    ProgressBar progressBar_pir;
    ProgressBar progressBar_video_format;
    ProgressBar progressBar_volume;
    ProgressBar progressbar_image_ldc;
    ProgressBar progressbar_image_reverse;
    RadioButton radio_one;
    RadioButton radio_two;
    RadioButton rb_auto;
    RadioButton rb_off;
    RadioButton rb_on;
    RelativeLayout rl_pir;
    LinearLayout rl_pir2;
    SeekBar seek_volume;
    TextView tv_ldc;
    LinearLayout video_format_radio;
    RelativeLayout video_voleme_seek;

    class C05541 implements OnSeekBarChangeListener {
        C05541() {
        }

        public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
        }

        public void onStartTrackingTouch(SeekBar arg0) {
        }

        public void onStopTrackingTouch(SeekBar arg0) {
            int value = arg0.getProgress();
            VideoControlFrag.this.progressBar_volume.setVisibility(0);
            VideoControlFrag.this.seek_volume.setEnabled(false);
            VideoControlFrag.this.cur_modify_video_volume = value;
            VideoControlFrag.this.switchVideoVolume(value);
        }
    }

    class C05552 extends BroadcastReceiver {
        C05552() {
        }

        public void onReceive(Context arg0, Intent intent) {
            if (intent.getAction().equals(P2P.RET_SET_PIR_LED)) {
                if (intent.getIntExtra("status", 1) == 0) {
                    P2pJni.P2PClientSdkGetPir(VideoControlFrag.this.contact.contactId, VideoControlFrag.this.contact.contactPassword, 720000);
                }
            } else if (intent.getAction().equals(P2P.RET_GET_PIR_LED)) {
                int state = intent.getIntExtra("status", 0);
                if (state == 1) {
                    VideoControlFrag.this.showIrOn();
                } else if (state == 2) {
                    VideoControlFrag.this.showIrAuto();
                } else {
                    VideoControlFrag.this.showIrOff();
                }
            } else if (intent.getAction().equals(P2P.RET_GET_VIDEO_FORMAT)) {
                type = intent.getIntExtra("type", -1);
                if (type == 1) {
                    VideoControlFrag.this.radio_one.setChecked(true);
                } else if (type == 0) {
                    VideoControlFrag.this.radio_two.setChecked(true);
                }
                VideoControlFrag.this.showVideoFormat();
                VideoControlFrag.this.radio_one.setEnabled(true);
                VideoControlFrag.this.radio_two.setEnabled(true);
            } else if (intent.getAction().equals(P2P.RET_SET_VIDEO_FORMAT)) {
                if (intent.getIntExtra("result", -1) == 0) {
                    if (VideoControlFrag.this.cur_modify_video_format == 1) {
                        VideoControlFrag.this.radio_one.setChecked(true);
                    } else if (VideoControlFrag.this.cur_modify_video_format == 0) {
                        VideoControlFrag.this.radio_two.setChecked(true);
                    }
                    VideoControlFrag.this.showVideoFormat();
                    VideoControlFrag.this.radio_one.setEnabled(true);
                    VideoControlFrag.this.radio_two.setEnabled(true);
                    C0568T.showShort(VideoControlFrag.this.mContext, (int) C0291R.string.modify_success);
                    return;
                }
                VideoControlFrag.this.showVideoFormat();
                VideoControlFrag.this.radio_one.setEnabled(true);
                VideoControlFrag.this.radio_two.setEnabled(true);
                C0568T.showShort(VideoControlFrag.this.mContext, (int) C0291R.string.operator_error);
            } else if (intent.getAction().equals(P2P.RET_GET_VIDEO_VOLUME)) {
                VideoControlFrag.this.seek_volume.setProgress(intent.getIntExtra(Param.VALUE, 0));
                VideoControlFrag.this.seek_volume.setEnabled(true);
                VideoControlFrag.this.showVideoVolume();
            } else if (intent.getAction().equals(P2P.RET_SET_VIDEO_VOLUME)) {
                if (intent.getIntExtra("result", -1) == 0) {
                    VideoControlFrag.this.seek_volume.setProgress(VideoControlFrag.this.cur_modify_video_volume);
                    VideoControlFrag.this.seek_volume.setEnabled(true);
                    VideoControlFrag.this.showVideoVolume();
                    C0568T.showShort(VideoControlFrag.this.mContext, (int) C0291R.string.modify_success);
                    return;
                }
                VideoControlFrag.this.seek_volume.setEnabled(true);
                VideoControlFrag.this.showVideoVolume();
                C0568T.showShort(VideoControlFrag.this.mContext, (int) C0291R.string.operator_error);
            } else if (intent.getAction().equals(P2P.ACK_RET_GET_NPC_SETTINGS)) {
                result = intent.getIntExtra("result", -1);
                if (result == 9999) {
                    i = new Intent();
                    i.setAction(Action.CONTROL_SETTING_PWD_ERROR);
                    VideoControlFrag.this.mContext.sendBroadcast(i);
                } else if (result == 9998) {
                    Log.e("my", "net error resend:get npc settings");
                    P2PHandler.getInstance().getNpcSettings(VideoControlFrag.this.contact.contactModel, VideoControlFrag.this.contact.contactId, VideoControlFrag.this.contact.contactPassword);
                }
            } else if (intent.getAction().equals(P2P.ACK_RET_SET_VIDEO_FORMAT)) {
                result = intent.getIntExtra("result", -1);
                if (result == 9999) {
                    i = new Intent();
                    i.setAction(Action.CONTROL_SETTING_PWD_ERROR);
                    VideoControlFrag.this.mContext.sendBroadcast(i);
                } else if (result == 9998) {
                    Log.e("my", "net error resend:set npc settings video format");
                    VideoControlFrag.this.switchVideoFormat(VideoControlFrag.this.cur_modify_video_format);
                }
            } else if (intent.getAction().equals(P2P.ACK_RET_SET_VIDEO_VOLUME)) {
                result = intent.getIntExtra("result", -1);
                if (result == 9999) {
                    i = new Intent();
                    i.setAction(Action.CONTROL_SETTING_PWD_ERROR);
                    VideoControlFrag.this.mContext.sendBroadcast(i);
                } else if (result == 9998) {
                    Log.e("my", "net error resend:set npc settings video volume");
                    VideoControlFrag.this.switchVideoVolume(VideoControlFrag.this.cur_modify_video_volume);
                }
            } else if (intent.getAction().equals(P2P.RET_GET_IMAGE_REVERSE)) {
                type = intent.getIntExtra("type", -1);
                if (type == 0) {
                    VideoControlFrag.this.layout_reverse.setVisibility(0);
                    VideoControlFrag.this.showImageview_image_reverse();
                    VideoControlFrag.this.img_image_reverse.setBackgroundResource(C0291R.drawable.ic_checkbox_off);
                    VideoControlFrag.this.isOpenReverse = true;
                } else if (type == 1) {
                    VideoControlFrag.this.layout_reverse.setVisibility(0);
                    VideoControlFrag.this.showImageview_image_reverse();
                    VideoControlFrag.this.img_image_reverse.setBackgroundResource(C0291R.drawable.ic_checkbox_on);
                    VideoControlFrag.this.isOpenReverse = false;
                }
            } else if (intent.getAction().equals(P2P.ACK_VRET_SET_IMAGEREVERSE)) {
                result = intent.getIntExtra("result", -1);
                if (result == 9998) {
                    VideoControlFrag.this.isOpenReverse = true;
                    P2PHandler.getInstance().setImageReverse(VideoControlFrag.this.contact.contactModel, VideoControlFrag.this.contact.contactId, VideoControlFrag.this.contact.contactPassword, 1);
                } else if (result != 9997) {
                } else {
                    if (VideoControlFrag.this.isOpenReverse) {
                        VideoControlFrag.this.showImageview_image_reverse();
                        VideoControlFrag.this.img_image_reverse.setBackgroundResource(C0291R.drawable.ic_checkbox_on);
                        VideoControlFrag.this.isOpenReverse = false;
                        return;
                    }
                    VideoControlFrag.this.showImageview_image_reverse();
                    VideoControlFrag.this.img_image_reverse.setBackgroundResource(C0291R.drawable.ic_checkbox_off);
                    VideoControlFrag.this.isOpenReverse = true;
                }
            } else if (intent.getAction().equals(P2P.RET_GET_IMG_LDC)) {
                result = intent.getIntExtra("result", -1);
                if (intent.getIntExtra("onoff", -1) == 1) {
                    VideoControlFrag.this.show_ldc_open();
                } else {
                    VideoControlFrag.this.show_ldc_close();
                }
            } else if (intent.getAction().equals(P2P.RET_SET_IMG_LDC) && intent.getIntExtra("result", -1) == 0) {
                P2pJni.P2PClientSdkGetLDC(VideoControlFrag.this.contact.contactId, VideoControlFrag.this.contact.contactPassword, 700000);
            }
        }
    }

    class C05563 implements Runnable {
        C05563() {
        }

        public void run() {
            P2pJni.P2PClientSdkSetPir(VideoControlFrag.this.contact.contactId, VideoControlFrag.this.contact.contactPassword, 2, 780000);
        }
    }

    class C05574 implements Runnable {
        C05574() {
        }

        public void run() {
            P2pJni.P2PClientSdkSetPir(VideoControlFrag.this.contact.contactId, VideoControlFrag.this.contact.contactPassword, 1, 790000);
        }
    }

    class C05585 implements Runnable {
        C05585() {
        }

        public void run() {
            P2pJni.P2PClientSdkSetPir(VideoControlFrag.this.contact.contactId, VideoControlFrag.this.contact.contactPassword, 0, 800000);
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mContext = MainControlActivity.mContext;
        this.contact = (Contact) getArguments().getSerializable(ContactDB.TABLE_NAME);
        View view = inflater.inflate(C0291R.layout.fragment_video_control, container, false);
        initComponent(view);
        regFilter();
        showProgress_video_format();
        showProgress_volume();
        showProgress_image_reverse();
        P2PHandler.getInstance().getNpcSettings(this.contact.contactModel, this.contact.contactId, this.contact.contactPassword);
        if (P2PValue.HikamDeviceModelList.contains(this.contact.contactModel)) {
            P2pJni.P2PClientSdkGetLDC(this.contact.contactId, this.contact.contactPassword, 700000);
        }
        return view;
    }

    public void initComponent(View view) {
        this.rb_auto = (RadioButton) view.findViewById(C0291R.id.rb_auto);
        this.rb_on = (RadioButton) view.findViewById(C0291R.id.rb_on);
        this.rb_off = (RadioButton) view.findViewById(C0291R.id.rb_off);
        this.rl_pir = (RelativeLayout) view.findViewById(C0291R.id.change_pir);
        this.rl_pir2 = (LinearLayout) view.findViewById(C0291R.id.change_pir2);
        this.rb_auto.setOnClickListener(this);
        this.rb_on.setOnClickListener(this);
        this.rb_off.setOnClickListener(this);
        this.progressBar_pir = (ProgressBar) view.findViewById(C0291R.id.progressBar_pir);
        this.change_ldc = (RelativeLayout) view.findViewById(C0291R.id.change_ldc);
        this.img_image_ldc = (ImageView) view.findViewById(C0291R.id.image_ldc_img);
        this.progressbar_image_ldc = (ProgressBar) view.findViewById(C0291R.id.progressBar_ldc);
        this.change_video_format = (RelativeLayout) view.findViewById(C0291R.id.change_video_format);
        this.video_format_radio = (LinearLayout) view.findViewById(C0291R.id.video_format_radio);
        this.progressBar_video_format = (ProgressBar) view.findViewById(C0291R.id.progressBar_video_format);
        this.change_volume = (RelativeLayout) view.findViewById(C0291R.id.change_volume);
        this.video_voleme_seek = (RelativeLayout) view.findViewById(C0291R.id.video_voleme_seek);
        this.progressBar_volume = (ProgressBar) view.findViewById(C0291R.id.progressBar_volume);
        this.seek_volume = (SeekBar) view.findViewById(C0291R.id.seek_volume);
        this.radio_one = (RadioButton) view.findViewById(C0291R.id.radio_one);
        this.radio_two = (RadioButton) view.findViewById(C0291R.id.radio_two);
        this.tv_ldc = (TextView) view.findViewById(C0291R.id.tv_ldc);
        this.change_image_reverse = (RelativeLayout) view.findViewById(C0291R.id.change_image_reverse);
        this.img_image_reverse = (ImageView) view.findViewById(C0291R.id.image_reverse_img);
        this.progressbar_image_reverse = (ProgressBar) view.findViewById(C0291R.id.progressBar_image_reverse);
        this.layout_reverse = (RelativeLayout) view.findViewById(C0291R.id.change_image_reverse);
        this.radio_one.setOnClickListener(this);
        this.radio_two.setOnClickListener(this);
        this.change_image_reverse.setOnClickListener(this);
        this.change_ldc.setOnClickListener(this);
        this.seek_volume.setOnSeekBarChangeListener(new C05541());
        if (P2PValue.HikamDeviceModelList.contains(this.contact.contactModel)) {
            this.change_volume.setVisibility(8);
            this.video_voleme_seek.setVisibility(8);
            this.progressBar_volume.setVisibility(8);
            this.seek_volume.setVisibility(8);
            this.change_ldc.setVisibility(0);
            this.tv_ldc.setVisibility(0);
        }
        if (HikamDeviceModel.Q3.equals(this.contact.contactModel) || HikamDeviceModel.Q5.equals(this.contact.contactModel)) {
            this.rl_pir.setVisibility(0);
            P2pJni.P2PClientSdkGetPir(this.contact.contactId, this.contact.contactPassword, 710000);
        }
    }

    public void regFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(P2P.ACK_RET_GET_NPC_SETTINGS);
        filter.addAction(P2P.ACK_RET_SET_VIDEO_FORMAT);
        filter.addAction(P2P.RET_SET_VIDEO_FORMAT);
        filter.addAction(P2P.RET_GET_VIDEO_FORMAT);
        filter.addAction(P2P.ACK_RET_SET_VIDEO_VOLUME);
        filter.addAction(P2P.RET_SET_VIDEO_VOLUME);
        filter.addAction(P2P.RET_GET_VIDEO_VOLUME);
        filter.addAction(P2P.RET_GET_IMAGE_REVERSE);
        filter.addAction(P2P.ACK_VRET_SET_IMAGEREVERSE);
        filter.addAction(P2P.RET_GET_IMG_LDC);
        filter.addAction(P2P.RET_SET_IMG_LDC);
        filter.addAction(P2P.RET_GET_PIR_LED);
        filter.addAction(P2P.RET_SET_PIR_LED);
        this.mContext.registerReceiver(this.mReceiver, filter);
        this.isRegFilter = true;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case C0291R.id.change_image_reverse:
                showProgress_image_reverse();
                if (this.isOpenReverse) {
                    P2PHandler.getInstance().setImageReverse(this.contact.contactModel, this.contact.contactId, this.contact.contactPassword, 1);
                    return;
                } else {
                    P2PHandler.getInstance().setImageReverse(this.contact.contactModel, this.contact.contactId, this.contact.contactPassword, 0);
                    return;
                }
            case C0291R.id.change_ldc:
                show_ldc_progress();
                if (this.isLdcOpen) {
                    P2pJni.P2PClientSdkSetLDC(this.contact.contactId, this.contact.contactPassword, 0, 710000);
                    return;
                } else {
                    P2pJni.P2PClientSdkSetLDC(this.contact.contactId, this.contact.contactPassword, 1, 720000);
                    return;
                }
            case C0291R.id.radio_one:
                this.progressBar_video_format.setVisibility(0);
                this.radio_one.setEnabled(false);
                this.radio_two.setEnabled(false);
                this.cur_modify_video_format = 1;
                switchVideoFormat(1);
                return;
            case C0291R.id.radio_two:
                this.progressBar_video_format.setVisibility(0);
                this.radio_one.setEnabled(false);
                this.radio_two.setEnabled(false);
                this.cur_modify_video_format = 0;
                switchVideoFormat(0);
                return;
            case C0291R.id.rb_auto:
                showIrProgress();
                new Thread(new C05563()).start();
                return;
            case C0291R.id.rb_off:
                showIrProgress();
                new Thread(new C05585()).start();
                return;
            case C0291R.id.rb_on:
                showIrProgress();
                new Thread(new C05574()).start();
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
    }

    public void showIrProgress() {
        this.progressBar_pir.setVisibility(0);
        this.rb_auto.setEnabled(false);
        this.rb_on.setEnabled(false);
        this.rb_off.setEnabled(false);
        this.rb_auto.setChecked(false);
        this.rb_on.setChecked(false);
        this.rb_off.setChecked(false);
    }

    public void showIrAuto() {
        this.progressBar_pir.setVisibility(8);
        this.rl_pir.setBackgroundDrawable(getResources().getDrawable(C0291R.drawable.tiao_bg_up));
        this.rl_pir2.setVisibility(0);
        this.rb_auto.setEnabled(true);
        this.rb_on.setEnabled(true);
        this.rb_off.setEnabled(true);
        this.rb_auto.setChecked(true);
        this.rb_on.setChecked(false);
        this.rb_off.setChecked(false);
    }

    public void showIrOn() {
        this.progressBar_pir.setVisibility(8);
        this.rl_pir.setBackgroundDrawable(getResources().getDrawable(C0291R.drawable.tiao_bg_up));
        this.rl_pir2.setVisibility(0);
        this.rb_auto.setEnabled(true);
        this.rb_on.setEnabled(true);
        this.rb_off.setEnabled(true);
        this.rb_auto.setChecked(false);
        this.rb_on.setChecked(true);
        this.rb_off.setChecked(false);
    }

    public void showIrOff() {
        this.progressBar_pir.setVisibility(8);
        this.rl_pir.setBackgroundDrawable(getResources().getDrawable(C0291R.drawable.tiao_bg_up));
        this.rl_pir2.setVisibility(0);
        this.rb_auto.setEnabled(true);
        this.rb_on.setEnabled(true);
        this.rb_off.setEnabled(true);
        this.rb_auto.setChecked(false);
        this.rb_on.setChecked(false);
        this.rb_off.setChecked(true);
    }

    public void switchVideoVolume(final int toggle) {
        new DelayThread(0, new OnRunListener() {
            public void run() {
                P2PHandler.getInstance().setVideoVolume(VideoControlFrag.this.contact.contactId, VideoControlFrag.this.contact.contactPassword, toggle);
            }
        }).start();
    }

    public void switchVideoFormat(final int toggle) {
        new DelayThread(0, new OnRunListener() {
            public void run() {
                P2PHandler.getInstance().setVideoFormat(VideoControlFrag.this.contact.contactModel, VideoControlFrag.this.contact.contactId, VideoControlFrag.this.contact.contactPassword, toggle);
            }
        }).start();
    }

    public void show_ldc_progress() {
        this.progressbar_image_ldc.setVisibility(0);
        this.img_image_ldc.setVisibility(8);
    }

    public void show_ldc_open() {
        this.isLdcOpen = true;
        this.progressbar_image_ldc.setVisibility(8);
        this.img_image_ldc.setVisibility(0);
        this.img_image_ldc.setBackgroundResource(C0291R.drawable.ic_checkbox_on);
    }

    public void show_ldc_close() {
        this.isLdcOpen = false;
        this.progressbar_image_ldc.setVisibility(8);
        this.img_image_ldc.setVisibility(0);
        this.img_image_ldc.setBackgroundResource(C0291R.drawable.ic_checkbox_off);
    }

    public void showProgress_image_reverse() {
        this.progressbar_image_reverse.setVisibility(0);
        this.img_image_reverse.setVisibility(8);
    }

    public void showImageview_image_reverse() {
        this.progressbar_image_reverse.setVisibility(8);
        this.img_image_reverse.setVisibility(0);
    }

    public void showVideoFormat() {
        this.change_video_format.setBackgroundResource(C0291R.drawable.tiao_bg_up);
        this.progressBar_video_format.setVisibility(8);
        this.video_format_radio.setVisibility(0);
    }

    public void showProgress_video_format() {
        this.change_video_format.setBackgroundResource(C0291R.drawable.tiao_bg_single);
        this.progressBar_video_format.setVisibility(0);
        this.video_format_radio.setVisibility(8);
    }

    public void showVideoVolume() {
        this.change_volume.setBackgroundResource(C0291R.drawable.tiao_bg_up);
        this.video_voleme_seek.setVisibility(0);
        this.progressBar_volume.setVisibility(8);
        this.seek_volume.setEnabled(true);
    }

    public void showProgress_volume() {
        this.progressBar_volume.setVisibility(0);
        this.seek_volume.setEnabled(false);
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
