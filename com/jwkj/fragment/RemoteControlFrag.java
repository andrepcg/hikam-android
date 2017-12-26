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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.hikam.C0291R;
import com.jwkj.activity.MainControlActivity;
import com.jwkj.data.Contact;
import com.jwkj.data.ContactDB;
import com.jwkj.global.Constants.Action;
import com.jwkj.global.Constants.P2P;
import com.p2p.core.P2PHandler;
import java.lang.reflect.Field;

public class RemoteControlFrag extends BaseFragment implements OnClickListener {
    RelativeLayout change_defence;
    RelativeLayout change_record;
    private Contact contact;
    int defenceState;
    ImageView defence_img;
    TextView defence_text;
    private boolean isRegFilter = false;
    int last_defence;
    int last_modify_defence;
    int last_modify_record;
    int last_record;
    private Context mContext;
    private BroadcastReceiver mReceiver = new C05461();
    ProgressBar progressBar_defence;
    ProgressBar progressBar_record;
    int recordState;
    ImageView record_img;
    TextView record_text;

    class C05461 extends BroadcastReceiver {
        C05461() {
        }

        public void onReceive(Context arg0, Intent intent) {
            int state;
            if (intent.getAction().equals(P2P.RET_GET_REMOTE_DEFENCE)) {
                state = intent.getIntExtra("state", -1);
                RemoteControlFrag.this.progressBar_defence.setVisibility(8);
                RemoteControlFrag.this.defence_img.setVisibility(0);
                RemoteControlFrag.this.updateDefence(state);
            } else if (intent.getAction().equals(P2P.RET_SET_REMOTE_DEFENCE)) {
                state = intent.getIntExtra("state", -1);
                P2PHandler.getInstance().getNpcSettings(RemoteControlFrag.this.contact.contactModel, RemoteControlFrag.this.contact.contactId, RemoteControlFrag.this.contact.contactPassword);
            } else if (intent.getAction().equals(P2P.RET_GET_REMOTE_RECORD)) {
                state = intent.getIntExtra("state", -1);
                RemoteControlFrag.this.progressBar_record.setVisibility(8);
                RemoteControlFrag.this.record_img.setVisibility(0);
                RemoteControlFrag.this.updateRecord(state);
            } else if (intent.getAction().equals(P2P.RET_SET_REMOTE_RECORD)) {
                state = intent.getIntExtra("state", -1);
                P2PHandler.getInstance().getNpcSettings(RemoteControlFrag.this.contact.contactModel, RemoteControlFrag.this.contact.contactId, RemoteControlFrag.this.contact.contactPassword);
            } else if (intent.getAction().equals(P2P.ACK_RET_GET_NPC_SETTINGS)) {
                result = intent.getIntExtra("result", -1);
                if (result == 9999) {
                    i = new Intent();
                    i.setAction(Action.CONTROL_SETTING_PWD_ERROR);
                    RemoteControlFrag.this.mContext.sendBroadcast(i);
                } else if (result == 9998) {
                    Log.e("my", "net error resend:get npc settings");
                    P2PHandler.getInstance().getNpcSettings(RemoteControlFrag.this.contact.contactModel, RemoteControlFrag.this.contact.contactId, RemoteControlFrag.this.contact.contactPassword);
                }
            } else if (intent.getAction().equals(P2P.ACK_RET_SET_REMOTE_DEFENCE)) {
                result = intent.getIntExtra("result", -1);
                if (result == 9999) {
                    i = new Intent();
                    i.setAction(Action.CONTROL_SETTING_PWD_ERROR);
                    RemoteControlFrag.this.mContext.sendBroadcast(i);
                } else if (result == 9998) {
                    Log.e("my", "net error resend:set remote defence");
                    P2PHandler.getInstance().setRemoteDefence(RemoteControlFrag.this.contact.contactModel, RemoteControlFrag.this.contact.contactId, RemoteControlFrag.this.contact.contactPassword, RemoteControlFrag.this.last_modify_defence);
                }
            } else if (intent.getAction().equals(P2P.ACK_RET_SET_REMOTE_RECORD)) {
                result = intent.getIntExtra("result", -1);
                if (result == 9999) {
                    i = new Intent();
                    i.setAction(Action.CONTROL_SETTING_PWD_ERROR);
                    RemoteControlFrag.this.mContext.sendBroadcast(i);
                } else if (result == 9998) {
                    Log.e("my", "net error resend:set remote record");
                    P2PHandler.getInstance().setRemoteRecord(RemoteControlFrag.this.contact.contactModel, RemoteControlFrag.this.contact.contactId, RemoteControlFrag.this.contact.contactPassword, RemoteControlFrag.this.last_modify_record);
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
        View view = inflater.inflate(C0291R.layout.fragment_remote_control, container, false);
        initComponent(view);
        regFilter();
        P2PHandler.getInstance().getNpcSettings(this.contact.contactModel, this.contact.contactId, this.contact.contactPassword);
        return view;
    }

    public void initComponent(View view) {
        this.change_defence = (RelativeLayout) view.findViewById(C0291R.id.change_defence);
        this.defence_img = (ImageView) view.findViewById(C0291R.id.defence_img);
        this.defence_text = (TextView) view.findViewById(C0291R.id.defence_text);
        this.change_record = (RelativeLayout) view.findViewById(C0291R.id.change_record);
        this.record_img = (ImageView) view.findViewById(C0291R.id.record_img);
        this.record_text = (TextView) view.findViewById(C0291R.id.record_text);
        this.progressBar_defence = (ProgressBar) view.findViewById(C0291R.id.progressBar_defence);
        this.progressBar_record = (ProgressBar) view.findViewById(C0291R.id.progressBar_record);
        this.change_defence.setOnClickListener(this);
        this.change_record.setOnClickListener(this);
    }

    public void regFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(P2P.ACK_RET_GET_NPC_SETTINGS);
        filter.addAction(P2P.ACK_RET_SET_REMOTE_DEFENCE);
        filter.addAction(P2P.RET_SET_REMOTE_DEFENCE);
        filter.addAction(P2P.RET_GET_REMOTE_DEFENCE);
        filter.addAction(P2P.ACK_RET_SET_REMOTE_RECORD);
        filter.addAction(P2P.RET_SET_REMOTE_RECORD);
        filter.addAction(P2P.RET_GET_REMOTE_RECORD);
        this.mContext.registerReceiver(this.mReceiver, filter);
        this.isRegFilter = true;
    }

    public void updateDefence(int state) {
        if (state == 1) {
            this.last_defence = 1;
            this.defence_img.setBackgroundResource(C0291R.drawable.ic_checkbox_on);
            return;
        }
        this.last_defence = 0;
        this.defence_img.setBackgroundResource(C0291R.drawable.ic_checkbox_off);
    }

    public void updateRecord(int state) {
        if (state == 1) {
            this.last_record = 1;
            this.record_img.setBackgroundResource(C0291R.drawable.ic_checkbox_on);
            return;
        }
        this.last_record = 0;
        this.record_img.setBackgroundResource(C0291R.drawable.ic_checkbox_off);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case C0291R.id.change_defence:
                this.progressBar_defence.setVisibility(0);
                this.defence_img.setVisibility(8);
                if (this.last_defence == 1) {
                    this.last_modify_defence = 0;
                    P2PHandler.getInstance().setRemoteDefence(this.contact.contactModel, this.contact.contactId, this.contact.contactPassword, this.last_modify_defence);
                    return;
                }
                this.last_modify_defence = 1;
                P2PHandler.getInstance().setRemoteDefence(this.contact.contactModel, this.contact.contactId, this.contact.contactPassword, this.last_modify_defence);
                return;
            case C0291R.id.change_record:
                this.progressBar_record.setVisibility(0);
                this.record_img.setVisibility(8);
                if (this.last_record == 1) {
                    this.last_modify_record = 0;
                    P2PHandler.getInstance().setRemoteRecord(this.contact.contactModel, this.contact.contactId, this.contact.contactPassword, this.last_modify_record);
                    return;
                }
                this.last_modify_record = 1;
                P2PHandler.getInstance().setRemoteRecord(this.contact.contactModel, this.contact.contactId, this.contact.contactPassword, this.last_modify_record);
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
