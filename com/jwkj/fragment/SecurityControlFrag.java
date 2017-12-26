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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import com.hikam.C0291R;
import com.jwkj.activity.MainControlActivity;
import com.jwkj.activity.ModifyNpcPasswordActivity;
import com.jwkj.activity.ModifyNpcVisitorPasswordActivity;
import com.jwkj.data.Contact;
import com.jwkj.data.ContactDB;
import com.jwkj.global.Constants.Action;
import com.jwkj.global.Constants.P2P;
import com.p2p.core.P2PHandler;
import java.lang.reflect.Field;

public class SecurityControlFrag extends BaseFragment implements OnClickListener {
    RelativeLayout automatic_upgrade;
    RelativeLayout change_password;
    RelativeLayout change_super_password;
    private Contact contact;
    ImageView img_automatic_upgrade;
    boolean isOpenAutomaticUpgrade;
    private boolean isRegFilter = false;
    private Context mContext;
    private BroadcastReceiver mReceiver = new C05481();
    ProgressBar progressBar_automatic_upgrade;

    class C05481 extends BroadcastReceiver {
        C05481() {
        }

        public void onReceive(Context arg0, Intent intent) {
            if (intent.getAction().equals(Action.REFRESH_CONTANTS)) {
                SecurityControlFrag.this.contact = (Contact) intent.getSerializableExtra(ContactDB.TABLE_NAME);
            } else if (intent.getAction().equals(P2P.RET_GET_AUTOMATIC_UPGRAD)) {
                state = intent.getIntExtra("state", -1);
                if (state == 1) {
                    SecurityControlFrag.this.automatic_upgrade.setVisibility(0);
                    SecurityControlFrag.this.isOpenAutomaticUpgrade = false;
                    SecurityControlFrag.this.img_automatic_upgrade.setBackgroundResource(C0291R.drawable.ic_checkbox_on);
                } else if (state == 0) {
                    SecurityControlFrag.this.automatic_upgrade.setVisibility(0);
                    SecurityControlFrag.this.isOpenAutomaticUpgrade = true;
                    SecurityControlFrag.this.img_automatic_upgrade.setBackgroundResource(C0291R.drawable.ic_checkbox_off);
                }
                SecurityControlFrag.this.showImg_automatic_upgrade();
            } else if (intent.getAction().equals(P2P.ACK_RET_SET_AUTOMATIC_UPGRADE)) {
                state = intent.getIntExtra("state", -1);
                if (state == 9998) {
                    if (SecurityControlFrag.this.isOpenAutomaticUpgrade) {
                        P2PHandler.getInstance().setAutomaticUpgrade(SecurityControlFrag.this.contact.contactId, SecurityControlFrag.this.contact.contactPassword, 1);
                    } else {
                        P2PHandler.getInstance().setAutomaticUpgrade(SecurityControlFrag.this.contact.contactId, SecurityControlFrag.this.contact.contactPassword, 0);
                    }
                    SecurityControlFrag.this.showImg_automatic_upgrade();
                } else if (state == 9997) {
                    if (SecurityControlFrag.this.isOpenAutomaticUpgrade) {
                        SecurityControlFrag.this.isOpenAutomaticUpgrade = false;
                        SecurityControlFrag.this.img_automatic_upgrade.setBackgroundResource(C0291R.drawable.ic_checkbox_on);
                    } else {
                        SecurityControlFrag.this.isOpenAutomaticUpgrade = true;
                        SecurityControlFrag.this.img_automatic_upgrade.setBackgroundResource(C0291R.drawable.ic_checkbox_off);
                    }
                    SecurityControlFrag.this.showImg_automatic_upgrade();
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
        View view = inflater.inflate(C0291R.layout.fragment_security_control, container, false);
        initComponent(view);
        regFilter();
        P2PHandler.getInstance().getNpcSettings(this.contact.contactModel, this.contact.contactId, this.contact.contactPassword);
        return view;
    }

    public void initComponent(View view) {
        this.change_password = (RelativeLayout) view.findViewById(C0291R.id.change_password);
        this.change_super_password = (RelativeLayout) view.findViewById(C0291R.id.change_super_password);
        this.automatic_upgrade = (RelativeLayout) view.findViewById(C0291R.id.automatic_upgrade);
        this.img_automatic_upgrade = (ImageView) view.findViewById(C0291R.id.img_automatic_upgrade);
        this.progressBar_automatic_upgrade = (ProgressBar) view.findViewById(C0291R.id.progressBar_automatic_upgrade);
        this.change_password.setOnClickListener(this);
        this.change_super_password.setOnClickListener(this);
        this.automatic_upgrade.setOnClickListener(this);
        if (this.contact.contactType == 7) {
            this.change_super_password.setVisibility(0);
        }
        if (Integer.parseInt(this.contact.contactId) < 256) {
            this.change_super_password.setVisibility(0);
        }
    }

    public void regFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Action.REFRESH_CONTANTS);
        filter.addAction(P2P.ACK_RET_SET_AUTOMATIC_UPGRADE);
        this.mContext.registerReceiver(this.mReceiver, filter);
        this.isRegFilter = true;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case C0291R.id.automatic_upgrade:
                if (this.isOpenAutomaticUpgrade) {
                    P2PHandler.getInstance().setAutomaticUpgrade(this.contact.contactId, this.contact.contactPassword, 1);
                    return;
                } else {
                    P2PHandler.getInstance().setAutomaticUpgrade(this.contact.contactId, this.contact.contactPassword, 0);
                    return;
                }
            case C0291R.id.change_password:
                Intent modify_password = new Intent(this.mContext, ModifyNpcPasswordActivity.class);
                modify_password.putExtra(ContactDB.TABLE_NAME, this.contact);
                this.mContext.startActivity(modify_password);
                return;
            case C0291R.id.change_super_password:
                Intent modify_visitor_password = new Intent(this.mContext, ModifyNpcVisitorPasswordActivity.class);
                modify_visitor_password.putExtra(ContactDB.TABLE_NAME, this.contact);
                this.mContext.startActivity(modify_visitor_password);
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

    public void onDestroy() {
        super.onDestroy();
        Intent it = new Intent();
        it.setAction(Action.CONTROL_BACK);
        this.mContext.sendBroadcast(it);
    }

    public void showProgress_automatic_upgrade() {
        this.progressBar_automatic_upgrade.setVisibility(0);
        this.img_automatic_upgrade.setVisibility(8);
    }

    public void showImg_automatic_upgrade() {
        this.progressBar_automatic_upgrade.setVisibility(8);
        this.img_automatic_upgrade.setVisibility(0);
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
