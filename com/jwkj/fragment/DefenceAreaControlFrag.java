package com.jwkj.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.com.streamax.miotp.p2p.jni.P2pJni;
import com.hikam.C0291R;
import com.jwkj.activity.MainControlActivity;
import com.jwkj.data.Contact;
import com.jwkj.data.ContactDB;
import com.jwkj.global.Constants.Action;
import com.jwkj.global.Constants.P2P;
import com.jwkj.utils.C0568T;
import com.jwkj.utils.Utils;
import com.jwkj.widget.NormalDialog;
import com.jwkj.widget.NormalDialog.OnButtonOkListener;
import com.p2p.core.P2PHandler;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class DefenceAreaControlFrag extends BaseFragment implements OnClickListener {
    private static final int END_EXPAND_OR_SHRINK = 18;
    private static final int EXPAND_OR_SHRINK = 17;
    private Button bt_send;
    RelativeLayout change_defence_area1;
    RelativeLayout change_defence_area2;
    RelativeLayout change_defence_area3;
    RelativeLayout change_defence_area4;
    RelativeLayout change_defence_area5;
    RelativeLayout change_defence_area6;
    RelativeLayout change_defence_area7;
    RelativeLayout change_defence_area8;
    RelativeLayout change_defence_area9;
    private Contact contact;
    int current_group;
    int current_item;
    int current_type;
    LinearLayout defence_area_content1;
    LinearLayout defence_area_content2;
    LinearLayout defence_area_content3;
    LinearLayout defence_area_content4;
    LinearLayout defence_area_content5;
    LinearLayout defence_area_content6;
    LinearLayout defence_area_content7;
    LinearLayout defence_area_content8;
    LinearLayout defence_area_content9;
    NormalDialog dialog_loading;
    TextView eight1;
    TextView eight2;
    TextView eight3;
    TextView eight4;
    TextView eight5;
    TextView eight6;
    TextView eight7;
    TextView eight8;
    private EditText et_send;
    TextView five1;
    TextView five2;
    TextView five3;
    TextView five4;
    TextView five5;
    TextView five6;
    TextView five7;
    TextView five8;
    TextView four1;
    TextView four2;
    TextView four3;
    TextView four4;
    TextView four5;
    TextView four6;
    TextView four7;
    TextView four8;
    private boolean isRegFilter = false;
    boolean is_eight_active = false;
    boolean is_five_active = false;
    boolean is_four_active = false;
    boolean is_nine_active = false;
    boolean is_one_active = false;
    boolean is_seven_active = false;
    boolean is_six_active = false;
    boolean is_three_active = false;
    boolean is_two_active = false;
    private Context mContext;
    public Handler mHandler = new Handler(new C05195());
    private BroadcastReceiver mReceiver = new C05162();
    TextView nine1;
    TextView nine2;
    TextView nine3;
    TextView nine4;
    TextView nine5;
    TextView nine6;
    TextView nine7;
    TextView nine8;
    TextView one1;
    TextView one2;
    TextView one3;
    TextView one4;
    TextView one5;
    TextView one6;
    TextView one7;
    TextView one8;
    ProgressBar progressBar_defence_area1;
    ProgressBar progressBar_defence_area2;
    ProgressBar progressBar_defence_area3;
    ProgressBar progressBar_defence_area4;
    ProgressBar progressBar_defence_area5;
    ProgressBar progressBar_defence_area6;
    ProgressBar progressBar_defence_area7;
    ProgressBar progressBar_defence_area8;
    ProgressBar progressBar_defence_area9;
    TextView seven1;
    TextView seven2;
    TextView seven3;
    TextView seven4;
    TextView seven5;
    TextView seven6;
    TextView seven7;
    TextView seven8;
    TextView six1;
    TextView six2;
    TextView six3;
    TextView six4;
    TextView six5;
    TextView six6;
    TextView six7;
    TextView six8;
    TextView three1;
    TextView three2;
    TextView three3;
    TextView three4;
    TextView three5;
    TextView three6;
    TextView three7;
    TextView three8;
    TextView two1;
    TextView two2;
    TextView two3;
    TextView two4;
    TextView two5;
    TextView two6;
    TextView two7;
    TextView two8;

    class C05151 implements OnClickListener {
        C05151() {
        }

        public void onClick(View v) {
            P2pJni.P2PClientSdkGetAlarmCodeStatus(DefenceAreaControlFrag.this.contact.contactId, DefenceAreaControlFrag.this.contact.contactPassword, Integer.valueOf(DefenceAreaControlFrag.this.et_send.getText().toString().trim()).intValue());
        }
    }

    class C05162 extends BroadcastReceiver {
        C05162() {
        }

        public void onReceive(Context arg0, Intent intent) {
            if (intent.getAction().equals(P2P.RET_GET_DEFENCE_AREA)) {
                DefenceAreaControlFrag.this.initData((ArrayList) intent.getSerializableExtra("data"));
                DefenceAreaControlFrag.this.showDefence_area1();
            } else if (intent.getAction().equals(P2P.RET_SET_DEFENCE_AREA)) {
                if (DefenceAreaControlFrag.this.dialog_loading != null) {
                    DefenceAreaControlFrag.this.dialog_loading.dismiss();
                    DefenceAreaControlFrag.this.dialog_loading = null;
                }
                result = intent.getIntExtra("result", -1);
                if (result == 0) {
                    if (DefenceAreaControlFrag.this.current_type == 1) {
                        DefenceAreaControlFrag.this.grayButton(DefenceAreaControlFrag.this.current_group, DefenceAreaControlFrag.this.current_item);
                        C0568T.showShort(DefenceAreaControlFrag.this.mContext, (int) C0291R.string.clear_success);
                        return;
                    }
                    DefenceAreaControlFrag.this.lightButton(DefenceAreaControlFrag.this.current_group, DefenceAreaControlFrag.this.current_item);
                    C0568T.showShort(DefenceAreaControlFrag.this.mContext, (int) C0291R.string.learning_success);
                } else if (result == 30) {
                    DefenceAreaControlFrag.this.grayButton(DefenceAreaControlFrag.this.current_group, DefenceAreaControlFrag.this.current_item);
                    C0568T.showShort(DefenceAreaControlFrag.this.mContext, (int) C0291R.string.clear_success);
                } else if (result == 32) {
                    int group = intent.getIntExtra("group", -1);
                    int item = intent.getIntExtra("item", -1);
                    Log.e("my", "group:" + group + " item:" + item);
                    C0568T.showShort(DefenceAreaControlFrag.this.mContext, Utils.getDefenceAreaByGroup(DefenceAreaControlFrag.this.mContext, group) + ":" + (item + 1) + " " + DefenceAreaControlFrag.this.mContext.getResources().getString(C0291R.string.channel) + " " + DefenceAreaControlFrag.this.mContext.getResources().getString(C0291R.string.has_been_learning));
                } else if (result == 41) {
                    Intent back = new Intent();
                    back.setAction(Action.REPLACE_MAIN_CONTROL);
                    DefenceAreaControlFrag.this.mContext.sendBroadcast(back);
                    C0568T.showShort(DefenceAreaControlFrag.this.mContext, (int) C0291R.string.device_unsupport_defence_area);
                } else {
                    C0568T.showShort(DefenceAreaControlFrag.this.mContext, (int) C0291R.string.operator_error);
                }
            } else if (intent.getAction().equals(P2P.RET_CLEAR_DEFENCE_AREA)) {
                if (DefenceAreaControlFrag.this.dialog_loading != null) {
                    DefenceAreaControlFrag.this.dialog_loading.dismiss();
                    DefenceAreaControlFrag.this.dialog_loading = null;
                }
                if (intent.getIntExtra("result", -1) == 0) {
                    DefenceAreaControlFrag.this.grayButton(DefenceAreaControlFrag.this.current_group, 0);
                    DefenceAreaControlFrag.this.grayButton(DefenceAreaControlFrag.this.current_group, 1);
                    DefenceAreaControlFrag.this.grayButton(DefenceAreaControlFrag.this.current_group, 2);
                    DefenceAreaControlFrag.this.grayButton(DefenceAreaControlFrag.this.current_group, 3);
                    DefenceAreaControlFrag.this.grayButton(DefenceAreaControlFrag.this.current_group, 4);
                    DefenceAreaControlFrag.this.grayButton(DefenceAreaControlFrag.this.current_group, 5);
                    DefenceAreaControlFrag.this.grayButton(DefenceAreaControlFrag.this.current_group, 6);
                    DefenceAreaControlFrag.this.grayButton(DefenceAreaControlFrag.this.current_group, 7);
                    C0568T.showShort(DefenceAreaControlFrag.this.mContext, (int) C0291R.string.clear_success);
                    return;
                }
                C0568T.showShort(DefenceAreaControlFrag.this.mContext, (int) C0291R.string.operator_error);
            } else if (intent.getAction().equals(P2P.RET_DEVICE_NOT_SUPPORT)) {
                if (DefenceAreaControlFrag.this.dialog_loading != null) {
                    DefenceAreaControlFrag.this.dialog_loading.dismiss();
                    DefenceAreaControlFrag.this.dialog_loading = null;
                }
                C0568T.showShort(DefenceAreaControlFrag.this.mContext, (int) C0291R.string.not_support);
            } else if (intent.getAction().equals(P2P.ACK_RET_GET_DEFENCE_AREA)) {
                result = intent.getIntExtra("result", -1);
                if (result == 9999) {
                    i = new Intent();
                    i.setAction(Action.CONTROL_SETTING_PWD_ERROR);
                    DefenceAreaControlFrag.this.mContext.sendBroadcast(i);
                } else if (result == 9998) {
                    Log.e("my", "net error resend:get defence area");
                    P2PHandler.getInstance().getDefenceArea(DefenceAreaControlFrag.this.contact.contactModel, DefenceAreaControlFrag.this.contact.contactId, DefenceAreaControlFrag.this.contact.contactPassword);
                }
            } else if (intent.getAction().equals(P2P.ACK_RET_SET_DEFENCE_AREA)) {
                result = intent.getIntExtra("result", -1);
                if (result == 9999) {
                    i = new Intent();
                    i.setAction(Action.CONTROL_SETTING_PWD_ERROR);
                    DefenceAreaControlFrag.this.mContext.sendBroadcast(i);
                } else if (result == 9998) {
                    Log.e("my", "net error resend:set defence area");
                    P2PHandler.getInstance().setDefenceAreaState(DefenceAreaControlFrag.this.contact.contactModel, DefenceAreaControlFrag.this.contact.contactId, DefenceAreaControlFrag.this.contact.contactPassword, DefenceAreaControlFrag.this.current_group, DefenceAreaControlFrag.this.current_item, DefenceAreaControlFrag.this.current_type);
                }
            } else if (intent.getAction().equals(P2P.ACK_RET_CLEAR_DEFENCE_AREA)) {
                result = intent.getIntExtra("result", -1);
                if (result == 9999) {
                    i = new Intent();
                    i.setAction(Action.CONTROL_SETTING_PWD_ERROR);
                    DefenceAreaControlFrag.this.mContext.sendBroadcast(i);
                } else if (result == 9998) {
                    Log.e("my", "net error resend:clear defence area");
                    P2PHandler.getInstance().clearDefenceAreaState(DefenceAreaControlFrag.this.contact.contactModel, DefenceAreaControlFrag.this.contact.contactId, DefenceAreaControlFrag.this.contact.contactPassword, DefenceAreaControlFrag.this.current_group);
                }
            }
        }
    }

    class C05195 implements Callback {
        C05195() {
        }

        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 17:
                    int group1 = msg.arg1;
                    int length = msg.arg2;
                    LinearLayout item = DefenceAreaControlFrag.this.getContent(group1);
                    LayoutParams params = (LayoutParams) item.getLayoutParams();
                    params.height = length;
                    item.setLayoutParams(params);
                    break;
                case 18:
                    int group2 = msg.arg1;
                    if (group2 == 8) {
                        DefenceAreaControlFrag.this.getBar(group2).setBackgroundResource(C0291R.drawable.tiao_bg_bottom);
                        break;
                    }
                    break;
            }
            return false;
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mContext = MainControlActivity.mContext;
        this.contact = (Contact) getArguments().getSerializable(ContactDB.TABLE_NAME);
        View view = inflater.inflate(C0291R.layout.fragment_defence_area_control, container, false);
        initComponent(view);
        regFilter();
        this.et_send = (EditText) view.findViewById(C0291R.id.et);
        this.bt_send = (Button) view.findViewById(C0291R.id.btn_send);
        this.bt_send.setOnClickListener(new C05151());
        P2PHandler.getInstance().getDefenceArea(this.contact.contactModel, this.contact.contactId, this.contact.contactPassword);
        return view;
    }

    public void initComponent(View view) {
        this.change_defence_area1 = (RelativeLayout) view.findViewById(C0291R.id.change_defence_area1);
        this.defence_area_content1 = (LinearLayout) view.findViewById(C0291R.id.defence_area_content1);
        this.progressBar_defence_area1 = (ProgressBar) view.findViewById(C0291R.id.progressBar_defence_area1);
        this.change_defence_area2 = (RelativeLayout) view.findViewById(C0291R.id.change_defence_area2);
        this.defence_area_content2 = (LinearLayout) view.findViewById(C0291R.id.defence_area_content2);
        this.progressBar_defence_area2 = (ProgressBar) view.findViewById(C0291R.id.progressBar_defence_area2);
        this.change_defence_area3 = (RelativeLayout) view.findViewById(C0291R.id.change_defence_area3);
        this.defence_area_content3 = (LinearLayout) view.findViewById(C0291R.id.defence_area_content3);
        this.progressBar_defence_area3 = (ProgressBar) view.findViewById(C0291R.id.progressBar_defence_area3);
        this.change_defence_area4 = (RelativeLayout) view.findViewById(C0291R.id.change_defence_area4);
        this.defence_area_content4 = (LinearLayout) view.findViewById(C0291R.id.defence_area_content4);
        this.progressBar_defence_area4 = (ProgressBar) view.findViewById(C0291R.id.progressBar_defence_area4);
        this.change_defence_area5 = (RelativeLayout) view.findViewById(C0291R.id.change_defence_area5);
        this.defence_area_content5 = (LinearLayout) view.findViewById(C0291R.id.defence_area_content5);
        this.progressBar_defence_area5 = (ProgressBar) view.findViewById(C0291R.id.progressBar_defence_area5);
        this.change_defence_area6 = (RelativeLayout) view.findViewById(C0291R.id.change_defence_area6);
        this.defence_area_content6 = (LinearLayout) view.findViewById(C0291R.id.defence_area_content6);
        this.progressBar_defence_area6 = (ProgressBar) view.findViewById(C0291R.id.progressBar_defence_area6);
        this.change_defence_area7 = (RelativeLayout) view.findViewById(C0291R.id.change_defence_area7);
        this.defence_area_content7 = (LinearLayout) view.findViewById(C0291R.id.defence_area_content7);
        this.progressBar_defence_area7 = (ProgressBar) view.findViewById(C0291R.id.progressBar_defence_area7);
        this.change_defence_area8 = (RelativeLayout) view.findViewById(C0291R.id.change_defence_area8);
        this.defence_area_content8 = (LinearLayout) view.findViewById(C0291R.id.defence_area_content8);
        this.progressBar_defence_area8 = (ProgressBar) view.findViewById(C0291R.id.progressBar_defence_area8);
        this.change_defence_area9 = (RelativeLayout) view.findViewById(C0291R.id.change_defence_area9);
        this.defence_area_content9 = (LinearLayout) view.findViewById(C0291R.id.defence_area_content9);
        this.progressBar_defence_area9 = (ProgressBar) view.findViewById(C0291R.id.progressBar_defence_area9);
        this.one1 = (TextView) view.findViewById(C0291R.id.one1);
        this.one2 = (TextView) view.findViewById(C0291R.id.one2);
        this.one3 = (TextView) view.findViewById(C0291R.id.one3);
        this.one4 = (TextView) view.findViewById(C0291R.id.one4);
        this.one5 = (TextView) view.findViewById(C0291R.id.one5);
        this.one6 = (TextView) view.findViewById(C0291R.id.one6);
        this.one7 = (TextView) view.findViewById(C0291R.id.one7);
        this.one8 = (TextView) view.findViewById(C0291R.id.one8);
        this.two1 = (TextView) view.findViewById(C0291R.id.two1);
        this.two2 = (TextView) view.findViewById(C0291R.id.two2);
        this.two3 = (TextView) view.findViewById(C0291R.id.two3);
        this.two4 = (TextView) view.findViewById(C0291R.id.two4);
        this.two5 = (TextView) view.findViewById(C0291R.id.two5);
        this.two6 = (TextView) view.findViewById(C0291R.id.two6);
        this.two7 = (TextView) view.findViewById(C0291R.id.two7);
        this.two8 = (TextView) view.findViewById(C0291R.id.two8);
        this.three1 = (TextView) view.findViewById(C0291R.id.three1);
        this.three2 = (TextView) view.findViewById(C0291R.id.three2);
        this.three3 = (TextView) view.findViewById(C0291R.id.three3);
        this.three4 = (TextView) view.findViewById(C0291R.id.three4);
        this.three5 = (TextView) view.findViewById(C0291R.id.three5);
        this.three6 = (TextView) view.findViewById(C0291R.id.three6);
        this.three7 = (TextView) view.findViewById(C0291R.id.three7);
        this.three8 = (TextView) view.findViewById(C0291R.id.three8);
        this.four1 = (TextView) view.findViewById(C0291R.id.four1);
        this.four2 = (TextView) view.findViewById(C0291R.id.four2);
        this.four3 = (TextView) view.findViewById(C0291R.id.four3);
        this.four4 = (TextView) view.findViewById(C0291R.id.four4);
        this.four5 = (TextView) view.findViewById(C0291R.id.four5);
        this.four6 = (TextView) view.findViewById(C0291R.id.four6);
        this.four7 = (TextView) view.findViewById(C0291R.id.four7);
        this.four8 = (TextView) view.findViewById(C0291R.id.four8);
        this.five1 = (TextView) view.findViewById(C0291R.id.five1);
        this.five2 = (TextView) view.findViewById(C0291R.id.five2);
        this.five3 = (TextView) view.findViewById(C0291R.id.five3);
        this.five4 = (TextView) view.findViewById(C0291R.id.five4);
        this.five5 = (TextView) view.findViewById(C0291R.id.five5);
        this.five6 = (TextView) view.findViewById(C0291R.id.five6);
        this.five7 = (TextView) view.findViewById(C0291R.id.five7);
        this.five8 = (TextView) view.findViewById(C0291R.id.five8);
        this.six1 = (TextView) view.findViewById(C0291R.id.six1);
        this.six2 = (TextView) view.findViewById(C0291R.id.six2);
        this.six3 = (TextView) view.findViewById(C0291R.id.six3);
        this.six4 = (TextView) view.findViewById(C0291R.id.six4);
        this.six5 = (TextView) view.findViewById(C0291R.id.six5);
        this.six6 = (TextView) view.findViewById(C0291R.id.six6);
        this.six7 = (TextView) view.findViewById(C0291R.id.six7);
        this.six8 = (TextView) view.findViewById(C0291R.id.six8);
        this.seven1 = (TextView) view.findViewById(C0291R.id.seven1);
        this.seven2 = (TextView) view.findViewById(C0291R.id.seven2);
        this.seven3 = (TextView) view.findViewById(C0291R.id.seven3);
        this.seven4 = (TextView) view.findViewById(C0291R.id.seven4);
        this.seven5 = (TextView) view.findViewById(C0291R.id.seven5);
        this.seven6 = (TextView) view.findViewById(C0291R.id.seven6);
        this.seven7 = (TextView) view.findViewById(C0291R.id.seven7);
        this.seven8 = (TextView) view.findViewById(C0291R.id.seven8);
        this.eight1 = (TextView) view.findViewById(C0291R.id.eight1);
        this.eight2 = (TextView) view.findViewById(C0291R.id.eight2);
        this.eight3 = (TextView) view.findViewById(C0291R.id.eight3);
        this.eight4 = (TextView) view.findViewById(C0291R.id.eight4);
        this.eight5 = (TextView) view.findViewById(C0291R.id.eight5);
        this.eight6 = (TextView) view.findViewById(C0291R.id.eight6);
        this.eight7 = (TextView) view.findViewById(C0291R.id.eight7);
        this.eight8 = (TextView) view.findViewById(C0291R.id.eight8);
        this.nine1 = (TextView) view.findViewById(C0291R.id.nine1);
        this.nine2 = (TextView) view.findViewById(C0291R.id.nine2);
        this.nine3 = (TextView) view.findViewById(C0291R.id.nine3);
        this.nine4 = (TextView) view.findViewById(C0291R.id.nine4);
        this.nine5 = (TextView) view.findViewById(C0291R.id.nine5);
        this.nine6 = (TextView) view.findViewById(C0291R.id.nine6);
        this.nine7 = (TextView) view.findViewById(C0291R.id.nine7);
        this.nine8 = (TextView) view.findViewById(C0291R.id.nine8);
    }

    public void regFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(P2P.ACK_RET_SET_DEFENCE_AREA);
        filter.addAction(P2P.ACK_RET_GET_DEFENCE_AREA);
        filter.addAction(P2P.ACK_RET_CLEAR_DEFENCE_AREA);
        filter.addAction(P2P.RET_CLEAR_DEFENCE_AREA);
        filter.addAction(P2P.RET_SET_DEFENCE_AREA);
        filter.addAction(P2P.RET_GET_DEFENCE_AREA);
        filter.addAction(P2P.RET_DEVICE_NOT_SUPPORT);
        this.mContext.registerReceiver(this.mReceiver, filter);
        this.isRegFilter = true;
    }

    public void initData(ArrayList<int[]> data) {
        for (int i = 0; i < data.size(); i++) {
            int[] status = (int[]) data.get(i);
            for (int j = 0; j < status.length; j++) {
                if (status[j] == 1) {
                    grayButton(i, j);
                } else {
                    lightButton(i, j);
                }
            }
        }
    }

    public void lightButton(final int i, final int j) {
        TextView item = getKeyBoard(i, j);
        if (item != null) {
            item.setOnClickListener(new OnClickListener() {
                public void onClick(View arg0) {
                    DefenceAreaControlFrag.this.clear(i, j);
                }
            });
            item.setBackgroundResource(C0291R.drawable.button_bg_dialog_ok);
            item.setTextColor(this.mContext.getResources().getColor(C0291R.color.text_color_black));
        }
    }

    public void grayButton(final int i, final int j) {
        TextView item = getKeyBoard(i, j);
        if (item != null) {
            item.setOnClickListener(new OnClickListener() {
                public void onClick(View arg0) {
                    DefenceAreaControlFrag.this.study(i, j);
                }
            });
            item.setBackgroundResource(C0291R.drawable.button_bg_dialog_cancel);
            item.setTextColor(this.mContext.getResources().getColor(C0291R.color.text_color_gray));
        }
    }

    public void shrinkItem(final int i) {
        if (!getIsActive(i)) {
            setActive(i, true);
            new Thread() {

                class C05201 implements OnClickListener {
                    C05201() {
                    }

                    public void onClick(View arg0) {
                        DefenceAreaControlFrag.this.expandItem(i);
                    }
                }

                public void run() {
                    int length = (int) DefenceAreaControlFrag.this.mContext.getResources().getDimension(C0291R.dimen.defen_area_expand_view_height);
                    while (length > 0) {
                        length -= 10;
                        Message msg = new Message();
                        msg.what = 17;
                        msg.arg1 = i;
                        msg.arg2 = length;
                        DefenceAreaControlFrag.this.mHandler.sendMessage(msg);
                        Utils.sleepThread(20);
                    }
                    Message end = new Message();
                    end.what = 18;
                    end.arg1 = i;
                    DefenceAreaControlFrag.this.mHandler.sendMessage(end);
                    DefenceAreaControlFrag.this.setActive(i, false);
                    DefenceAreaControlFrag.this.getBar(i).setOnClickListener(new C05201());
                }
            }.start();
        }
    }

    public void expandItem(final int i) {
        if (!getIsActive(i)) {
            setActive(i, true);
            final RelativeLayout item = getBar(i);
            if (i == 8) {
                item.setBackgroundResource(C0291R.drawable.tiao_bg_center);
            }
            new Thread() {

                class C05221 implements OnClickListener {
                    C05221() {
                    }

                    public void onClick(View arg0) {
                        DefenceAreaControlFrag.this.shrinkItem(i);
                    }
                }

                public void run() {
                    int length = 0;
                    int total = (int) DefenceAreaControlFrag.this.mContext.getResources().getDimension(C0291R.dimen.defen_area_expand_view_height);
                    while (length < total) {
                        length += 10;
                        Message msg = new Message();
                        msg.what = 17;
                        msg.arg1 = i;
                        msg.arg2 = length;
                        DefenceAreaControlFrag.this.mHandler.sendMessage(msg);
                        Utils.sleepThread(20);
                    }
                    DefenceAreaControlFrag.this.setActive(i, false);
                    item.setOnClickListener(new C05221());
                }
            }.start();
        }
    }

    public void showDefence_area1() {
        this.progressBar_defence_area1.setVisibility(8);
        this.progressBar_defence_area2.setVisibility(8);
        this.progressBar_defence_area3.setVisibility(8);
        this.progressBar_defence_area4.setVisibility(8);
        this.progressBar_defence_area5.setVisibility(8);
        this.progressBar_defence_area6.setVisibility(8);
        this.progressBar_defence_area7.setVisibility(8);
        this.progressBar_defence_area8.setVisibility(8);
        this.progressBar_defence_area9.setVisibility(8);
        for (int i = 0; i < 9; i++) {
            RelativeLayout item = getBar(i);
            final int group = i;
            item.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    DefenceAreaControlFrag.this.expandItem(group);
                }
            });
            item.setOnLongClickListener(new OnLongClickListener() {

                class C11091 implements OnButtonOkListener {
                    C11091() {
                    }

                    public void onClick() {
                        if (DefenceAreaControlFrag.this.dialog_loading == null) {
                            DefenceAreaControlFrag.this.dialog_loading = new NormalDialog(DefenceAreaControlFrag.this.mContext, DefenceAreaControlFrag.this.mContext.getResources().getString(C0291R.string.clearing), "", "", "");
                            DefenceAreaControlFrag.this.dialog_loading.setStyle(2);
                        }
                        DefenceAreaControlFrag.this.dialog_loading.showDialog();
                        DefenceAreaControlFrag.this.current_group = group;
                        DefenceAreaControlFrag.this.current_type = 2;
                        P2PHandler.getInstance().clearDefenceAreaState(DefenceAreaControlFrag.this.contact.contactModel, DefenceAreaControlFrag.this.contact.contactId, DefenceAreaControlFrag.this.contact.contactPassword, group);
                    }
                }

                public boolean onLongClick(View v) {
                    NormalDialog dialog = new NormalDialog(DefenceAreaControlFrag.this.mContext, DefenceAreaControlFrag.this.mContext.getResources().getString(C0291R.string.clear_code), DefenceAreaControlFrag.this.mContext.getResources().getString(C0291R.string.clear_code_prompt), DefenceAreaControlFrag.this.mContext.getResources().getString(C0291R.string.ensure), DefenceAreaControlFrag.this.mContext.getResources().getString(C0291R.string.cancel));
                    dialog.setOnButtonOkListener(new C11091());
                    dialog.showNormalDialog();
                    dialog.setCanceledOnTouchOutside(false);
                    return false;
                }
            });
        }
    }

    public void onClick(View v) {
        v.getId();
    }

    public void study(final int group, final int item) {
        NormalDialog dialog = new NormalDialog(this.mContext, this.mContext.getResources().getString(C0291R.string.learing_code), this.mContext.getResources().getString(C0291R.string.learing_code_prompt), this.mContext.getResources().getString(C0291R.string.ensure), this.mContext.getResources().getString(C0291R.string.cancel));
        dialog.setOnButtonOkListener(new OnButtonOkListener() {
            public void onClick() {
                if (DefenceAreaControlFrag.this.dialog_loading == null) {
                    DefenceAreaControlFrag.this.dialog_loading = new NormalDialog(DefenceAreaControlFrag.this.mContext, DefenceAreaControlFrag.this.mContext.getResources().getString(C0291R.string.studying), "", "", "");
                    DefenceAreaControlFrag.this.dialog_loading.setStyle(2);
                }
                DefenceAreaControlFrag.this.dialog_loading.showDialog();
                DefenceAreaControlFrag.this.current_type = 0;
                DefenceAreaControlFrag.this.current_group = group;
                DefenceAreaControlFrag.this.current_item = item;
                P2PHandler.getInstance().setDefenceAreaState(DefenceAreaControlFrag.this.contact.contactModel, DefenceAreaControlFrag.this.contact.contactId, DefenceAreaControlFrag.this.contact.contactPassword, group, item, 0);
            }
        });
        dialog.showNormalDialog();
        dialog.setCanceledOnTouchOutside(false);
    }

    public void clear(final int group, final int item) {
        NormalDialog dialog = new NormalDialog(this.mContext, this.mContext.getResources().getString(C0291R.string.clear_code), this.mContext.getResources().getString(C0291R.string.clear_code_prompt), this.mContext.getResources().getString(C0291R.string.ensure), this.mContext.getResources().getString(C0291R.string.cancel));
        dialog.setOnButtonOkListener(new OnButtonOkListener() {
            public void onClick() {
                if (DefenceAreaControlFrag.this.dialog_loading == null) {
                    DefenceAreaControlFrag.this.dialog_loading = new NormalDialog(DefenceAreaControlFrag.this.mContext, DefenceAreaControlFrag.this.mContext.getResources().getString(C0291R.string.clearing), "", "", "");
                    DefenceAreaControlFrag.this.dialog_loading.setStyle(2);
                }
                DefenceAreaControlFrag.this.dialog_loading.showDialog();
                DefenceAreaControlFrag.this.current_type = 1;
                DefenceAreaControlFrag.this.current_group = group;
                DefenceAreaControlFrag.this.current_item = item;
                P2PHandler.getInstance().setDefenceAreaState(DefenceAreaControlFrag.this.contact.contactModel, DefenceAreaControlFrag.this.contact.contactId, DefenceAreaControlFrag.this.contact.contactPassword, group, item, 1);
            }
        });
        dialog.showNormalDialog();
        dialog.setCanceledOnTouchOutside(false);
    }

    public void onDestroyView() {
        super.onDestroyView();
        if (this.isRegFilter) {
            this.mContext.unregisterReceiver(this.mReceiver);
            this.isRegFilter = false;
        }
    }

    public RelativeLayout getBar(int group) {
        switch (group) {
            case 0:
                return this.change_defence_area1;
            case 1:
                return this.change_defence_area2;
            case 2:
                return this.change_defence_area3;
            case 3:
                return this.change_defence_area4;
            case 4:
                return this.change_defence_area5;
            case 5:
                return this.change_defence_area6;
            case 6:
                return this.change_defence_area7;
            case 7:
                return this.change_defence_area8;
            case 8:
                return this.change_defence_area9;
            default:
                return null;
        }
    }

    public LinearLayout getContent(int group) {
        switch (group) {
            case 0:
                return this.defence_area_content1;
            case 1:
                return this.defence_area_content2;
            case 2:
                return this.defence_area_content3;
            case 3:
                return this.defence_area_content4;
            case 4:
                return this.defence_area_content5;
            case 5:
                return this.defence_area_content6;
            case 6:
                return this.defence_area_content7;
            case 7:
                return this.defence_area_content8;
            case 8:
                return this.defence_area_content9;
            default:
                return null;
        }
    }

    public TextView getKeyBoard(int group, int item) {
        switch (group) {
            case 0:
                if (item == 0) {
                    return this.one1;
                }
                if (item == 1) {
                    return this.one2;
                }
                if (item == 2) {
                    return this.one3;
                }
                if (item == 3) {
                    return this.one4;
                }
                if (item == 4) {
                    return this.one5;
                }
                if (item == 5) {
                    return this.one6;
                }
                if (item == 6) {
                    return this.one7;
                }
                if (item == 7) {
                    return this.one8;
                }
                break;
            case 1:
                if (item == 0) {
                    return this.two1;
                }
                if (item == 1) {
                    return this.two2;
                }
                if (item == 2) {
                    return this.two3;
                }
                if (item == 3) {
                    return this.two4;
                }
                if (item == 4) {
                    return this.two5;
                }
                if (item == 5) {
                    return this.two6;
                }
                if (item == 6) {
                    return this.two7;
                }
                if (item == 7) {
                    return this.two8;
                }
                break;
            case 2:
                if (item == 0) {
                    return this.three1;
                }
                if (item == 1) {
                    return this.three2;
                }
                if (item == 2) {
                    return this.three3;
                }
                if (item == 3) {
                    return this.three4;
                }
                if (item == 4) {
                    return this.three5;
                }
                if (item == 5) {
                    return this.three6;
                }
                if (item == 6) {
                    return this.three7;
                }
                if (item == 7) {
                    return this.three8;
                }
                break;
            case 3:
                if (item == 0) {
                    return this.four1;
                }
                if (item == 1) {
                    return this.four2;
                }
                if (item == 2) {
                    return this.four3;
                }
                if (item == 3) {
                    return this.four4;
                }
                if (item == 4) {
                    return this.four5;
                }
                if (item == 5) {
                    return this.four6;
                }
                if (item == 6) {
                    return this.four7;
                }
                if (item == 7) {
                    return this.four8;
                }
                break;
            case 4:
                if (item == 0) {
                    return this.five1;
                }
                if (item == 1) {
                    return this.five2;
                }
                if (item == 2) {
                    return this.five3;
                }
                if (item == 3) {
                    return this.five4;
                }
                if (item == 4) {
                    return this.five5;
                }
                if (item == 5) {
                    return this.five6;
                }
                if (item == 6) {
                    return this.five7;
                }
                if (item == 7) {
                    return this.five8;
                }
                break;
            case 5:
                if (item == 0) {
                    return this.six1;
                }
                if (item == 1) {
                    return this.six2;
                }
                if (item == 2) {
                    return this.six3;
                }
                if (item == 3) {
                    return this.six4;
                }
                if (item == 4) {
                    return this.six5;
                }
                if (item == 5) {
                    return this.six6;
                }
                if (item == 6) {
                    return this.six7;
                }
                if (item == 7) {
                    return this.six8;
                }
                break;
            case 6:
                if (item == 0) {
                    return this.seven1;
                }
                if (item == 1) {
                    return this.seven2;
                }
                if (item == 2) {
                    return this.seven3;
                }
                if (item == 3) {
                    return this.seven4;
                }
                if (item == 4) {
                    return this.seven5;
                }
                if (item == 5) {
                    return this.seven6;
                }
                if (item == 6) {
                    return this.seven7;
                }
                if (item == 7) {
                    return this.seven8;
                }
                break;
            case 7:
                if (item == 0) {
                    return this.eight1;
                }
                if (item == 1) {
                    return this.eight2;
                }
                if (item == 2) {
                    return this.eight3;
                }
                if (item == 3) {
                    return this.eight4;
                }
                if (item == 4) {
                    return this.eight5;
                }
                if (item == 5) {
                    return this.eight6;
                }
                if (item == 6) {
                    return this.eight7;
                }
                if (item == 7) {
                    return this.eight8;
                }
                break;
            case 8:
                if (item == 0) {
                    return this.nine1;
                }
                if (item == 1) {
                    return this.nine2;
                }
                if (item == 2) {
                    return this.nine3;
                }
                if (item == 3) {
                    return this.nine4;
                }
                if (item == 4) {
                    return this.nine5;
                }
                if (item == 5) {
                    return this.nine6;
                }
                if (item == 6) {
                    return this.nine7;
                }
                if (item == 7) {
                    return this.nine8;
                }
                break;
        }
        return null;
    }

    public boolean getIsActive(int group) {
        switch (group) {
            case 0:
                return this.is_one_active;
            case 1:
                return this.is_two_active;
            case 2:
                return this.is_three_active;
            case 3:
                return this.is_four_active;
            case 4:
                return this.is_five_active;
            case 5:
                return this.is_six_active;
            case 6:
                return this.is_seven_active;
            case 7:
                return this.is_eight_active;
            case 8:
                return this.is_nine_active;
            default:
                return true;
        }
    }

    public void setActive(int group, boolean bool) {
        switch (group) {
            case 0:
                this.is_one_active = bool;
                return;
            case 1:
                this.is_two_active = bool;
                return;
            case 2:
                this.is_three_active = bool;
                return;
            case 3:
                this.is_four_active = bool;
                return;
            case 4:
                this.is_five_active = bool;
                return;
            case 5:
                this.is_six_active = bool;
                return;
            case 6:
                this.is_seven_active = bool;
                return;
            case 7:
                this.is_eight_active = bool;
                return;
            case 8:
                this.is_nine_active = bool;
                return;
            default:
                return;
        }
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
