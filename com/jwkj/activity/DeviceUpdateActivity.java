package com.jwkj.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.com.streamax.miotp.p2p.jni.GWellUserInfo;
import cn.com.streamax.miotp.p2p.jni.P2pJni;
import com.google.firebase.analytics.FirebaseAnalytics.Param;
import com.hikam.C0291R;
import com.jwkj.data.Contact;
import com.jwkj.data.ContactDB;
import com.jwkj.global.Constants.P2P;
import com.jwkj.global.Constants.P2P_SET.ACK_RESULT;
import com.jwkj.global.FList;
import com.jwkj.utils.C0568T;
import com.jwkj.widget.NormalDialog;
import com.jwkj.widget.NormalDialog.OnButtonCancelListener;
import com.p2p.core.P2PHandler;
import com.p2p.core.P2PValue;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;

public class DeviceUpdateActivity extends BaseActivity {
    private static final String HIKAM_VERSION_CONTROL_SERVER_ADDRESS = "52.28.45.243";
    private static final int HIKAM_VERSION_CONTROL_SERVER_PORT = 17373;
    TextView button1_text;
    TextView button2_text;
    ProgressBar content_progress;
    TextView content_text;
    String cur_version;
    boolean isDownloading = false;
    boolean isRegFilter;
    int jiashuju = 51;
    RelativeLayout layout_button1;
    RelativeLayout layout_button2;
    LinearLayout layout_main;
    Contact mContact;
    Context mContext;
    public Handler mHandler = new C03694();
    private BroadcastReceiver mReceiver = new C03805();
    private NormalDialog normalDialog;
    String upg_version;

    class C03631 extends Handler {
        C03631() {
        }

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                C0568T.showShort(DeviceUpdateActivity.this.mContext, (int) C0291R.string.net_error_operator_fault);
                DeviceUpdateActivity.this.finish();
            }
        }
    }

    class C03653 implements OnClickListener {
        C03653() {
        }

        public void onClick(View v) {
            DeviceUpdateActivity.this.finish();
        }
    }

    class C03694 extends Handler {

        class C03661 implements OnClickListener {
            C03661() {
            }

            public void onClick(View arg0) {
                DeviceUpdateActivity.this.finish();
            }
        }

        class C03682 implements OnClickListener {

            class C03671 implements OnClickListener {
                C03671() {
                }

                public void onClick(View v) {
                    DeviceUpdateActivity.this.finish();
                }
            }

            C03682() {
            }

            public void onClick(View v) {
                DeviceUpdateActivity.this.layout_button1.setVisibility(0);
                DeviceUpdateActivity.this.layout_button2.setVisibility(8);
                DeviceUpdateActivity.this.content_progress.setVisibility(0);
                DeviceUpdateActivity.this.content_text.setVisibility(8);
                DeviceUpdateActivity.this.button1_text.setText(C0291R.string.cancel);
                DeviceUpdateActivity.this.content_text.setText(DeviceUpdateActivity.this.mContext.getResources().getString(C0291R.string.down_device_update) + "0%");
                DeviceUpdateActivity.this.button1_text.setOnClickListener(new C03671());
                P2PHandler.getInstance().doDeviceUpdate(DeviceUpdateActivity.this.mContact.contactModel, DeviceUpdateActivity.this.mContact.contactId, DeviceUpdateActivity.this.mContact.contactPassword, "");
            }
        }

        C03694() {
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    DeviceUpdateActivity.this.content_text.setText(DeviceUpdateActivity.this.mContext.getResources().getString(C0291R.string.current_version_is) + DeviceUpdateActivity.this.cur_version + "," + DeviceUpdateActivity.this.mContext.getResources().getString(C0291R.string.can_update_to) + DeviceUpdateActivity.this.upg_version);
                    DeviceUpdateActivity.this.layout_button1.setVisibility(0);
                    DeviceUpdateActivity.this.layout_button2.setVisibility(0);
                    DeviceUpdateActivity.this.button1_text.setText(C0291R.string.update_now);
                    DeviceUpdateActivity.this.button2_text.setText(C0291R.string.next_time);
                    DeviceUpdateActivity.this.button2_text.setOnClickListener(new C03661());
                    DeviceUpdateActivity.this.button1_text.setOnClickListener(new C03682());
                    break;
                case 2:
                    C0568T.showShort(DeviceUpdateActivity.this.mContext, DeviceUpdateActivity.this.mContext.getResources().getString(C0291R.string.device_is_latest_version) + ":" + DeviceUpdateActivity.this.cur_version);
                    DeviceUpdateActivity.this.finish();
                    break;
            }
            super.handleMessage(msg);
        }
    }

    class C03805 extends BroadcastReceiver {

        class C03701 implements OnClickListener {
            C03701() {
            }

            public void onClick(View arg0) {
                DeviceUpdateActivity.this.finish();
            }
        }

        class C03722 implements OnClickListener {

            class C03711 implements OnClickListener {
                C03711() {
                }

                public void onClick(View v) {
                    DeviceUpdateActivity.this.finish();
                }
            }

            C03722() {
            }

            public void onClick(View v) {
                DeviceUpdateActivity.this.layout_button1.setVisibility(0);
                DeviceUpdateActivity.this.layout_button2.setVisibility(8);
                DeviceUpdateActivity.this.content_progress.setVisibility(0);
                DeviceUpdateActivity.this.content_text.setVisibility(8);
                DeviceUpdateActivity.this.button1_text.setText(C0291R.string.cancel);
                DeviceUpdateActivity.this.content_text.setText(DeviceUpdateActivity.this.mContext.getResources().getString(C0291R.string.down_device_update) + "0%");
                DeviceUpdateActivity.this.button1_text.setOnClickListener(new C03711());
                P2PHandler.getInstance().doDeviceUpdate(DeviceUpdateActivity.this.mContact.contactModel, DeviceUpdateActivity.this.mContact.contactId, DeviceUpdateActivity.this.mContact.contactPassword, DeviceUpdateActivity.this.upg_version);
            }
        }

        class C03733 implements OnClickListener {
            C03733() {
            }

            public void onClick(View arg0) {
                DeviceUpdateActivity.this.finish();
            }
        }

        class C03754 implements OnClickListener {

            class C03741 implements OnClickListener {
                C03741() {
                }

                public void onClick(View v) {
                    DeviceUpdateActivity.this.finish();
                }
            }

            C03754() {
            }

            public void onClick(View v) {
                DeviceUpdateActivity.this.layout_button1.setVisibility(0);
                DeviceUpdateActivity.this.layout_button2.setVisibility(8);
                DeviceUpdateActivity.this.content_progress.setVisibility(0);
                DeviceUpdateActivity.this.content_text.setVisibility(8);
                DeviceUpdateActivity.this.button1_text.setText(C0291R.string.cancel);
                DeviceUpdateActivity.this.content_text.setText(DeviceUpdateActivity.this.mContext.getResources().getString(C0291R.string.down_device_update) + "0%");
                DeviceUpdateActivity.this.button1_text.setOnClickListener(new C03741());
                P2PHandler.getInstance().doDeviceUpdate(DeviceUpdateActivity.this.mContact.contactModel, DeviceUpdateActivity.this.mContact.contactId, DeviceUpdateActivity.this.mContact.contactPassword, DeviceUpdateActivity.this.upg_version);
            }
        }

        class C03765 extends Thread {

            class C10801 implements ChannelPipelineFactory {
                C10801() {
                }

                public ChannelPipeline getPipeline() throws Exception {
                    return Channels.pipeline(new StringDecoder(), new StringEncoder(), new NettyClientHandler());
                }
            }

            C03765() {
            }

            public void run() {
                ClientBootstrap bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));
                bootstrap.setPipelineFactory(new C10801());
                bootstrap.connect(new InetSocketAddress(DeviceUpdateActivity.HIKAM_VERSION_CONTROL_SERVER_ADDRESS, DeviceUpdateActivity.HIKAM_VERSION_CONTROL_SERVER_PORT)).getChannel().getCloseFuture().awaitUninterruptibly();
                bootstrap.releaseExternalResources();
            }
        }

        class C03776 implements OnClickListener {
            C03776() {
            }

            public void onClick(View arg0) {
                DeviceUpdateActivity.this.finish();
            }
        }

        class C03797 implements OnClickListener {

            class C03781 implements OnClickListener {
                C03781() {
                }

                public void onClick(View v) {
                    DeviceUpdateActivity.this.finish();
                }
            }

            C03797() {
            }

            public void onClick(View v) {
                DeviceUpdateActivity.this.layout_button1.setVisibility(0);
                DeviceUpdateActivity.this.layout_button2.setVisibility(8);
                DeviceUpdateActivity.this.content_progress.setVisibility(0);
                DeviceUpdateActivity.this.content_text.setVisibility(8);
                DeviceUpdateActivity.this.button1_text.setText(C0291R.string.cancel);
                DeviceUpdateActivity.this.button1_text.setOnClickListener(new C03781());
                P2PHandler.getInstance().doDeviceUpdate(DeviceUpdateActivity.this.mContact.contactModel, DeviceUpdateActivity.this.mContact.contactId, DeviceUpdateActivity.this.mContact.contactPassword, "");
            }
        }

        class C10818 implements OnButtonCancelListener {
            C10818() {
            }

            public void onClick() {
                DeviceUpdateActivity.this.normalDialog.dismiss();
                DeviceUpdateActivity.this.normalDialog = null;
                DeviceUpdateActivity.this.finish();
            }
        }

        class C10829 implements OnButtonCancelListener {
            C10829() {
            }

            public void onClick() {
                DeviceUpdateActivity.this.normalDialog.dismiss();
                DeviceUpdateActivity.this.normalDialog = null;
                DeviceUpdateActivity.this.finish();
            }
        }

        C03805() {
        }

        public void onReceive(Context arg0, Intent intent) {
            int result;
            if (intent.getAction().equals(P2P.ACK_RET_GET_DEVICE_INFO)) {
                result = intent.getIntExtra("result", -1);
                if (result == 9999) {
                    C0568T.showShort(DeviceUpdateActivity.this.mContext, (int) C0291R.string.password_error);
                    DeviceUpdateActivity.this.finish();
                } else if (result == 9998) {
                    Log.e("my", "net error resend:get device info");
                    P2PHandler.getInstance().getDeviceVersion(DeviceUpdateActivity.this.mContact.contactModel, DeviceUpdateActivity.this.mContact.contactId, DeviceUpdateActivity.this.mContact.contactPassword);
                }
            } else if (intent.getAction().equals(P2P.RET_GET_DEVICE_INFO) || intent.getAction().equals(P2P.RET_GET_DEVICE_INFO2)) {
                DeviceUpdateActivity.this.cur_version = intent.getStringExtra("cur_version");
                MainControlActivity.isCancelCheck = false;
                P2PHandler.getInstance().checkDeviceUpdate(DeviceUpdateActivity.this.mContact.contactModel, DeviceUpdateActivity.this.mContact.contactId, DeviceUpdateActivity.this.mContact.contactPassword, DeviceUpdateActivity.this.cur_version);
            } else if (intent.getAction().equals(P2P.ACK_RET_CHECK_DEVICE_UPDATE)) {
                result = intent.getIntExtra("result", -1);
                if (result == 9999) {
                    C0568T.showShort(DeviceUpdateActivity.this.mContext, (int) C0291R.string.password_error);
                    DeviceUpdateActivity.this.finish();
                } else if (result == 9998) {
                    Log.e("my", "net error resend:check device update");
                    P2PHandler.getInstance().checkDeviceUpdate(DeviceUpdateActivity.this.mContact.contactModel, DeviceUpdateActivity.this.mContact.contactId, DeviceUpdateActivity.this.mContact.contactPassword, DeviceUpdateActivity.this.cur_version);
                } else if (result == ACK_RESULT.ACK_SDK_NOT_REG) {
                    Log.e("my", "net error resend:check device update");
                    if (P2PValue.HikamDeviceModelList.contains(DeviceUpdateActivity.this.mContact.contactModel) && FList.getInstance().hikam_sdk_register_state == -1) {
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
                    P2PHandler.getInstance().checkDeviceUpdate(DeviceUpdateActivity.this.mContact.contactModel, DeviceUpdateActivity.this.mContact.contactId, DeviceUpdateActivity.this.mContact.contactPassword, DeviceUpdateActivity.this.cur_version);
                }
            } else if (intent.getAction().equals(P2P.RET_CHECK_DEVICE_UPDATE)) {
                result = intent.getIntExtra("result", -1);
                DeviceUpdateActivity.this.cur_version = intent.getStringExtra("cur_version");
                DeviceUpdateActivity.this.upg_version = intent.getStringExtra("upg_version");
                DeviceUpdateActivity.this.content_progress.setVisibility(8);
                DeviceUpdateActivity.this.content_text.setVisibility(0);
                if (result == 1) {
                    if (P2PValue.HikamDeviceModelList.contains(DeviceUpdateActivity.this.mContact.contactModel)) {
                        DeviceUpdateActivity.this.content_text.setText(DeviceUpdateActivity.this.mContext.getResources().getString(C0291R.string.current_version_is) + DeviceUpdateActivity.this.cur_version + "," + DeviceUpdateActivity.this.mContext.getResources().getString(C0291R.string.can_update_to) + DeviceUpdateActivity.this.upg_version);
                        DeviceUpdateActivity.this.layout_button1.setVisibility(0);
                        DeviceUpdateActivity.this.layout_button2.setVisibility(0);
                        DeviceUpdateActivity.this.button1_text.setText(C0291R.string.update_now);
                        DeviceUpdateActivity.this.button2_text.setText(C0291R.string.next_time);
                        DeviceUpdateActivity.this.button2_text.setOnClickListener(new C03701());
                        DeviceUpdateActivity.this.button1_text.setOnClickListener(new C03722());
                    } else if ("13.0.0.90".equals(DeviceUpdateActivity.this.upg_version)) {
                        DeviceUpdateActivity.this.content_text.setText(DeviceUpdateActivity.this.mContext.getResources().getString(C0291R.string.current_version_is) + DeviceUpdateActivity.this.cur_version + "," + DeviceUpdateActivity.this.mContext.getResources().getString(C0291R.string.can_update_to) + DeviceUpdateActivity.this.upg_version);
                        DeviceUpdateActivity.this.layout_button1.setVisibility(0);
                        DeviceUpdateActivity.this.layout_button2.setVisibility(0);
                        DeviceUpdateActivity.this.button1_text.setText(C0291R.string.update_now);
                        DeviceUpdateActivity.this.button2_text.setText(C0291R.string.next_time);
                        DeviceUpdateActivity.this.button2_text.setOnClickListener(new C03733());
                        DeviceUpdateActivity.this.button1_text.setOnClickListener(new C03754());
                    } else {
                        new C03765().start();
                    }
                } else if (result == 72) {
                    DeviceUpdateActivity.this.content_progress.setVisibility(8);
                    DeviceUpdateActivity.this.content_text.setText(DeviceUpdateActivity.this.mContext.getResources().getString(C0291R.string.current_version_is) + DeviceUpdateActivity.this.cur_version + "," + DeviceUpdateActivity.this.mContext.getResources().getString(C0291R.string.can_update_in_sd));
                    DeviceUpdateActivity.this.layout_button1.setVisibility(0);
                    DeviceUpdateActivity.this.layout_button2.setVisibility(0);
                    DeviceUpdateActivity.this.button1_text.setText(C0291R.string.update_now);
                    DeviceUpdateActivity.this.button2_text.setText(C0291R.string.next_time);
                    DeviceUpdateActivity.this.button2_text.setOnClickListener(new C03776());
                    DeviceUpdateActivity.this.button1_text.setOnClickListener(new C03797());
                } else if (result == 54) {
                    C0568T.showShort(DeviceUpdateActivity.this.mContext, DeviceUpdateActivity.this.mContext.getResources().getString(C0291R.string.device_is_latest_version) + ":" + DeviceUpdateActivity.this.cur_version);
                    DeviceUpdateActivity.this.finish();
                } else if (result == 58) {
                    C0568T.showShort(DeviceUpdateActivity.this.mContext, (int) C0291R.string.other_was_checking);
                    DeviceUpdateActivity.this.finish();
                } else {
                    C0568T.showShort(DeviceUpdateActivity.this.mContext, (int) C0291R.string.operator_error);
                    DeviceUpdateActivity.this.finish();
                }
            } else if (intent.getAction().equals(P2P.ACK_RET_DO_DEVICE_UPDATE)) {
                result = intent.getIntExtra("result", -1);
                if (result == 9999) {
                    C0568T.showShort(DeviceUpdateActivity.this.mContext, (int) C0291R.string.password_error);
                    DeviceUpdateActivity.this.finish();
                } else if (result == 9998) {
                    Log.e("my", "net error resend:do device update");
                    P2PHandler.getInstance().doDeviceUpdate(DeviceUpdateActivity.this.mContact.contactModel, DeviceUpdateActivity.this.mContact.contactId, DeviceUpdateActivity.this.mContact.contactPassword, "");
                }
            } else if (intent.getAction().equals(P2P.RET_DO_DEVICE_UPDATE)) {
                result = intent.getIntExtra("result", -1);
                int value = intent.getIntExtra(Param.VALUE, -1);
                Log.e("my", result + ":" + value);
                if (result == 1) {
                    DeviceUpdateActivity.this.content_progress.setVisibility(8);
                    DeviceUpdateActivity.this.content_text.setVisibility(0);
                    DeviceUpdateActivity.this.layout_button1.setVisibility(8);
                    if (value == 100) {
                        DeviceUpdateActivity.this.content_text.setText(DeviceUpdateActivity.this.mContext.getResources().getString(C0291R.string.update) + DeviceUpdateActivity.this.jiashuju + ".0% \n" + DeviceUpdateActivity.this.mContext.getResources().getString(C0291R.string.updating));
                        DeviceUpdateActivity deviceUpdateActivity = DeviceUpdateActivity.this;
                        deviceUpdateActivity.jiashuju++;
                        if (DeviceUpdateActivity.this.jiashuju >= 99) {
                            DeviceUpdateActivity.this.jiashuju = 99;
                            return;
                        }
                        return;
                    }
                    DeviceUpdateActivity.this.content_text.setText(DeviceUpdateActivity.this.mContext.getResources().getString(C0291R.string.update) + (((float) value) / 2.0f) + "% \n" + DeviceUpdateActivity.this.mContext.getResources().getString(C0291R.string.downloading));
                } else if (result == 65) {
                    DeviceUpdateActivity.this.content_text.setText(DeviceUpdateActivity.this.mContext.getResources().getString(C0291R.string.update) + 100 + "% \n" + DeviceUpdateActivity.this.mContext.getResources().getString(C0291R.string.downloading));
                    DeviceUpdateActivity.this.jiashuju = 51;
                    if (DeviceUpdateActivity.this.normalDialog == null || !DeviceUpdateActivity.this.normalDialog.isShowing()) {
                        DeviceUpdateActivity.this.normalDialog = new NormalDialog(DeviceUpdateActivity.this.mContext, DeviceUpdateActivity.this.mContext.getResources().getString(C0291R.string.prompt), DeviceUpdateActivity.this.mContext.getResources().getString(C0291R.string.start_update), null, null);
                        DeviceUpdateActivity.this.normalDialog.setOnButtonCancelListener(new C10818());
                        DeviceUpdateActivity.this.normalDialog.showPromptDialog();
                        return;
                    }
                    Log.e("my", "isShowing");
                } else if (DeviceUpdateActivity.this.normalDialog == null || !DeviceUpdateActivity.this.normalDialog.isShowing()) {
                    DeviceUpdateActivity.this.normalDialog = new NormalDialog(DeviceUpdateActivity.this.mContext, DeviceUpdateActivity.this.mContext.getResources().getString(C0291R.string.prompt), DeviceUpdateActivity.this.mContext.getResources().getString(C0291R.string.update_failed), null, null);
                    DeviceUpdateActivity.this.normalDialog.setOnButtonCancelListener(new C10829());
                    DeviceUpdateActivity.this.normalDialog.showPromptDialog();
                } else {
                    Log.e("my", "isShowing");
                }
            }
        }
    }

    class C03816 implements AnimationListener {
        C03816() {
        }

        public void onAnimationEnd(Animation arg0) {
            super.finish();
        }

        public void onAnimationRepeat(Animation arg0) {
        }

        public void onAnimationStart(Animation arg0) {
        }
    }

    private class NettyClientHandler extends SimpleChannelHandler {
        private NettyClientHandler() {
        }

        public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
            super.messageReceived(ctx, e);
            String hikam_latest_version = e.getMessage().toString();
            e.getChannel().close();
            if (hikam_latest_version.equals(DeviceUpdateActivity.this.upg_version)) {
                Message message = new Message();
                message.what = 1;
                DeviceUpdateActivity.this.mHandler.sendMessage(message);
                return;
            }
            message = new Message();
            message.what = 2;
            DeviceUpdateActivity.this.mHandler.sendMessage(message);
        }

        public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
            super.channelConnected(ctx, e);
            Log.e("alex", "channelConnected");
        }

        public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
            e.getChannel().close();
            Log.e("alex", "exceptionCaught");
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0291R.layout.activity_device_update);
        this.mContext = this;
        this.mContact = (Contact) getIntent().getSerializableExtra(ContactDB.TABLE_NAME);
        initCompoment();
        regFilter();
        MainControlActivity.isCancelCheck = true;
        final Handler handler = new C03631();
        new Thread() {
            private boolean pingBing() {
                try {
                    if (Runtime.getRuntime().exec("ping -c 1 -w 100 " + "www.bing.com").waitFor() == 0) {
                        return true;
                    }
                } catch (Exception e) {
                }
                return false;
            }

            public void run() {
                if (pingBing()) {
                    P2PHandler.getInstance().getDeviceVersion(DeviceUpdateActivity.this.mContact.contactModel, DeviceUpdateActivity.this.mContact.contactId, DeviceUpdateActivity.this.mContact.contactPassword);
                } else {
                    handler.sendEmptyMessage(1);
                }
            }
        }.start();
    }

    public void initCompoment() {
        this.layout_main = (LinearLayout) findViewById(C0291R.id.layout_main);
        this.layout_button1 = (RelativeLayout) findViewById(C0291R.id.layout_button1);
        this.layout_button2 = (RelativeLayout) findViewById(C0291R.id.layout_button2);
        this.content_text = (TextView) findViewById(C0291R.id.content_text);
        this.button1_text = (TextView) findViewById(C0291R.id.button1_text);
        this.button2_text = (TextView) findViewById(C0291R.id.button2_text);
        this.content_progress = (ProgressBar) findViewById(C0291R.id.content_progress);
        this.content_progress.setVisibility(0);
        this.layout_button1.setVisibility(0);
        this.layout_button2.setVisibility(8);
        this.button1_text.setText(C0291R.string.cancel);
        this.button1_text.setOnClickListener(new C03653());
        this.layout_main.startAnimation(AnimationUtils.loadAnimation(this.mContext, C0291R.anim.scale_in));
    }

    public void regFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(P2P.ACK_RET_CHECK_DEVICE_UPDATE);
        filter.addAction(P2P.RET_CHECK_DEVICE_UPDATE);
        filter.addAction(P2P.ACK_RET_DO_DEVICE_UPDATE);
        filter.addAction(P2P.RET_DO_DEVICE_UPDATE);
        filter.addAction(P2P.ACK_RET_CANCEL_DEVICE_UPDATE);
        filter.addAction(P2P.RET_CANCEL_DEVICE_UPDATE);
        filter.addAction(P2P.ACK_RET_GET_DEVICE_INFO);
        filter.addAction(P2P.RET_GET_DEVICE_INFO);
        filter.addAction(P2P.RET_GET_DEVICE_INFO2);
        filter.addAction(P2P.RET_DEVICE_NOT_SUPPORT);
        this.mContext.registerReceiver(this.mReceiver, filter);
        this.isRegFilter = true;
    }

    public void finish() {
        Animation anim = AnimationUtils.loadAnimation(this.mContext, C0291R.anim.scale_out);
        anim.setAnimationListener(new C03816());
        this.layout_main.startAnimation(anim);
    }

    protected void onDestroy() {
        super.onDestroy();
        if (this.isRegFilter) {
            this.isRegFilter = false;
            unregisterReceiver(this.mReceiver);
        }
        P2PHandler.getInstance().cancelDeviceUpdate(this.mContact.contactModel, this.mContact.contactId, this.mContact.contactPassword);
    }

    public int getActivityInfo() {
        return 40;
    }
}
