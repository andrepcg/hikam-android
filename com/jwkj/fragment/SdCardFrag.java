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
import com.jwkj.utils.C0568T;
import com.jwkj.widget.NormalDialog;
import com.jwkj.widget.NormalDialog.OnButtonCancelListener;
import com.jwkj.widget.NormalDialog.OnButtonOkListener;
import com.p2p.core.P2PHandler;
import com.p2p.core.P2PValue;
import java.lang.reflect.Field;
import org.apache.commons.compress.archivers.tar.TarConstants;

public class SdCardFrag extends Fragment implements OnClickListener {
    int SDcardId;
    BroadcastReceiver br = new C05471();
    String command;
    private Contact contact;
    int count = 0;
    ImageView format_icon;
    boolean isRegFilter = false;
    boolean isSDCard;
    private Context mContext;
    ProgressBar progress_format;
    int sdId;
    RelativeLayout sd_format;
    TextView tv_sd_remainning_capacity;
    TextView tv_total_capacity;
    TextView tv_usb_remainning_capacity;
    TextView tv_usb_total_capacity;
    int usbId;
    RelativeLayout usb_capacity;
    RelativeLayout usb_remainning_capacity;

    class C05471 extends BroadcastReceiver {
        C05471() {
        }

        public void onReceive(Context arg0, Intent intent) {
            int result;
            Intent i;
            if (intent.getAction().equals(P2P.ACK_GET_SD_CARD_CAPACITY)) {
                result = intent.getIntExtra("result", -1);
                if (result == 9999) {
                    i = new Intent();
                    i.setAction(Action.CONTROL_SETTING_PWD_ERROR);
                    SdCardFrag.this.mContext.sendBroadcast(i);
                } else if (result == 9998) {
                    Log.e("my", "net error resend:get npc time");
                    P2PHandler.getInstance().getSdCardCapacity(SdCardFrag.this.contact.contactModel, SdCardFrag.this.contact.contactId, SdCardFrag.this.contact.contactPassword, SdCardFrag.this.command);
                }
            } else if (intent.getAction().equals(P2P.RET_GET_SD_CARD_CAPACITY)) {
                total_capacity = intent.getIntExtra("total_capacity", -1);
                remain_capacity = intent.getIntExtra("remain_capacity", -1);
                state = intent.getIntExtra("state", -1);
                SdCardFrag.this.SDcardId = intent.getIntExtra("SDcardID", -1);
                id = Integer.toBinaryString(SdCardFrag.this.SDcardId);
                Log.e("id", "msga" + id);
                while (id.length() < 8) {
                    id = "0" + id;
                }
                index = id.charAt(3);
                Log.e("id", "msgb" + id);
                Log.e("id", "msgc" + index);
                if (state != 1) {
                    back = new Intent();
                    back.setAction(Action.REPLACE_MAIN_CONTROL);
                    SdCardFrag.this.mContext.sendBroadcast(back);
                    C0568T.showShort(SdCardFrag.this.mContext, (int) C0291R.string.sd_no_exist);
                } else if (index == '1') {
                    SdCardFrag.this.sdId = SdCardFrag.this.SDcardId;
                    SdCardFrag.this.tv_total_capacity.setText(String.valueOf(total_capacity) + "M");
                    SdCardFrag.this.tv_sd_remainning_capacity.setText(String.valueOf(remain_capacity) + "M");
                    SdCardFrag.this.showSDImg();
                } else if (index == '0') {
                    SdCardFrag.this.usbId = SdCardFrag.this.SDcardId;
                    SdCardFrag.this.tv_usb_total_capacity.setText(String.valueOf(total_capacity) + "M");
                    SdCardFrag.this.tv_usb_remainning_capacity.setText(String.valueOf(remain_capacity) + "M");
                }
            } else if (intent.getAction().equals(P2P.ACK_GET_SD_CARD_FORMAT)) {
                result = intent.getIntExtra("result", -1);
                if (result == 9999) {
                    i = new Intent();
                    i.setAction(Action.CONTROL_SETTING_PWD_ERROR);
                    SdCardFrag.this.mContext.sendBroadcast(i);
                } else if (result == 9998) {
                    Log.e("my", "net error resend:get npc time");
                    P2PHandler.getInstance().setSdFormat(SdCardFrag.this.contact.contactModel, SdCardFrag.this.contact.contactId, SdCardFrag.this.contact.contactPassword, SdCardFrag.this.sdId);
                }
            } else if (intent.getAction().equals(P2P.RET_GET_SD_CARD_FORMAT)) {
                result = intent.getIntExtra("result", -1);
                if (result == 80) {
                    C0568T.showShort(SdCardFrag.this.mContext, (int) C0291R.string.sd_format_success);
                } else if (result == 81) {
                    C0568T.showShort(SdCardFrag.this.mContext, (int) C0291R.string.sd_format_fail);
                } else if (result == 82) {
                    C0568T.showShort(SdCardFrag.this.mContext, (int) C0291R.string.sd_no_exist);
                }
                SdCardFrag.this.showSDImg();
            } else if (intent.getAction().equals(P2P.RET_GET_USB_CAPACITY)) {
                total_capacity = intent.getIntExtra("total_capacity", -1);
                remain_capacity = intent.getIntExtra("remain_capacity", -1);
                state = intent.getIntExtra("state", -1);
                SdCardFrag.this.SDcardId = intent.getIntExtra("SDcardID", -1);
                id = Integer.toBinaryString(SdCardFrag.this.SDcardId);
                Log.e("id", "msga" + id);
                while (id.length() < 8) {
                    id = "0" + id;
                }
                index = id.charAt(3);
                Log.e("id", "msgb" + id);
                Log.e("id", "msgc" + index);
                if (state != 1) {
                    SdCardFrag sdCardFrag = SdCardFrag.this;
                    sdCardFrag.count++;
                    if (SdCardFrag.this.contact.contactType == 7) {
                        if (SdCardFrag.this.count == 1) {
                            back = new Intent();
                            back.setAction(Action.REPLACE_MAIN_CONTROL);
                            SdCardFrag.this.mContext.sendBroadcast(back);
                            C0568T.showShort(SdCardFrag.this.mContext, (int) C0291R.string.sd_no_exist);
                        }
                    } else if (SdCardFrag.this.count == 2) {
                        back = new Intent();
                        back.setAction(Action.REPLACE_MAIN_CONTROL);
                        SdCardFrag.this.mContext.sendBroadcast(back);
                        C0568T.showShort(SdCardFrag.this.mContext, (int) C0291R.string.sd_no_exist);
                    }
                } else if (index == '1') {
                    SdCardFrag.this.sdId = SdCardFrag.this.SDcardId;
                    SdCardFrag.this.tv_total_capacity.setText(String.valueOf(total_capacity) + "M");
                    SdCardFrag.this.tv_sd_remainning_capacity.setText(String.valueOf(remain_capacity) + "M");
                    SdCardFrag.this.showSDImg();
                } else if (index == '0') {
                    SdCardFrag.this.usbId = SdCardFrag.this.SDcardId;
                    SdCardFrag.this.tv_usb_total_capacity.setText(String.valueOf(total_capacity) + "M");
                    SdCardFrag.this.tv_usb_remainning_capacity.setText(String.valueOf(remain_capacity) + "M");
                }
            } else if (intent.getAction().equals(P2P.RET_DEVICE_NOT_SUPPORT)) {
                back = new Intent();
                back.setAction(Action.REPLACE_MAIN_CONTROL);
                SdCardFrag.this.mContext.sendBroadcast(back);
                C0568T.showShort(SdCardFrag.this.mContext, (int) C0291R.string.not_support);
            }
        }
    }

    class C11182 implements OnButtonOkListener {
        C11182() {
        }

        public void onClick() {
            P2PHandler.getInstance().setSdFormat(SdCardFrag.this.contact.contactModel, SdCardFrag.this.contact.contactId, SdCardFrag.this.contact.contactPassword, SdCardFrag.this.sdId);
            Log.e("SDcardId", "SDcardId" + SdCardFrag.this.SDcardId);
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mContext = MainControlActivity.mContext;
        this.contact = (Contact) getArguments().getSerializable(ContactDB.TABLE_NAME);
        View view = inflater.inflate(C0291R.layout.fragment_sd_card, container, false);
        initComponent(view);
        showSDProgress();
        regFilter();
        this.command = createCommand("80", "0", TarConstants.VERSION_POSIX);
        Log.e("sdcapacity", this.command);
        P2PHandler.getInstance().getSdCardCapacity(this.contact.contactModel, this.contact.contactId, this.contact.contactPassword, this.command);
        return view;
    }

    public void initComponent(View view) {
        this.tv_total_capacity = (TextView) view.findViewById(C0291R.id.tv_sd_capacity);
        this.tv_sd_remainning_capacity = (TextView) view.findViewById(C0291R.id.tv_sd_remainning_capacity);
        this.sd_format = (RelativeLayout) view.findViewById(C0291R.id.sd_format);
        this.format_icon = (ImageView) view.findViewById(C0291R.id.format_icon);
        this.progress_format = (ProgressBar) view.findViewById(C0291R.id.progress_format);
        this.usb_capacity = (RelativeLayout) view.findViewById(C0291R.id.usb_capacity);
        this.usb_remainning_capacity = (RelativeLayout) view.findViewById(C0291R.id.usb_remainning_capacity);
        this.tv_usb_total_capacity = (TextView) view.findViewById(C0291R.id.tv_usb_capacity);
        this.tv_usb_remainning_capacity = (TextView) view.findViewById(C0291R.id.tv_usb_remainning_capacity);
        this.sd_format.setOnClickListener(this);
        if (this.contact.contactType == 2) {
            this.sd_format.setVisibility(8);
            this.usb_capacity.setVisibility(0);
            this.usb_remainning_capacity.setVisibility(0);
        }
        if (!P2PValue.HikamDeviceModelList.contains(this.contact.contactModel)) {
        }
    }

    public void regFilter() {
        this.isRegFilter = true;
        IntentFilter filter = new IntentFilter();
        filter.addAction(P2P.ACK_GET_SD_CARD_CAPACITY);
        filter.addAction(P2P.RET_GET_SD_CARD_CAPACITY);
        filter.addAction(P2P.ACK_GET_SD_CARD_FORMAT);
        filter.addAction(P2P.RET_GET_SD_CARD_FORMAT);
        filter.addAction(P2P.RET_GET_USB_CAPACITY);
        filter.addAction(P2P.RET_DEVICE_NOT_SUPPORT);
        this.mContext.registerReceiver(this.br, filter);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case C0291R.id.sd_format:
                final NormalDialog dialog = new NormalDialog(this.mContext, this.mContext.getResources().getString(C0291R.string.sd_formatting), this.mContext.getResources().getString(C0291R.string.delete_sd_remind), this.mContext.getResources().getString(C0291R.string.ensure), this.mContext.getResources().getString(C0291R.string.cancel));
                dialog.setOnButtonOkListener(new C11182());
                dialog.setOnButtonCancelListener(new OnButtonCancelListener() {
                    public void onClick() {
                        SdCardFrag.this.showSDImg();
                        dialog.dismiss();
                    }
                });
                dialog.showNormalDialog();
                dialog.setCanceledOnTouchOutside(false);
                showSDProgress();
                return;
            default:
                return;
        }
    }

    public void showSDImg() {
        this.format_icon.setVisibility(0);
        ProgressBar progressBar = this.progress_format;
        ProgressBar progressBar2 = this.progress_format;
        progressBar.setVisibility(8);
        this.sd_format.setClickable(true);
    }

    public void showSDProgress() {
        this.format_icon.setVisibility(8);
        ProgressBar progressBar = this.progress_format;
        ProgressBar progressBar2 = this.progress_format;
        progressBar.setVisibility(0);
        this.sd_format.setClickable(false);
    }

    public String createCommand(String bCommandType, String bOption, String SDCardCounts) {
        return bCommandType + bOption + SDCardCounts;
    }

    public void onDestroy() {
        super.onDestroy();
        if (this.isRegFilter) {
            this.mContext.unregisterReceiver(this.br);
        }
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
