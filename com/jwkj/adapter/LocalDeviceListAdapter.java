package com.jwkj.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.hikam.C0291R;
import com.jwkj.activity.AddContactNextActivity;
import com.jwkj.data.Contact;
import com.jwkj.data.ContactDB;
import com.jwkj.entity.LocalDevice;
import com.jwkj.global.FList;
import com.jwkj.global.NpcCommon;
import com.jwkj.utils.Utils;
import java.util.List;

public class LocalDeviceListAdapter extends BaseAdapter {
    List<LocalDevice> datas = FList.getInstance().getSetPasswordLocalDevices();
    Context mContext;

    public LocalDeviceListAdapter(Context context) {
        this.mContext = context;
    }

    public int getCount() {
        return this.datas.size();
    }

    public Object getItem(int arg0) {
        return null;
    }

    public long getItemId(int arg0) {
        return 0;
    }

    public View getView(int position, View arg1, ViewGroup arg2) {
        View view = arg1;
        if (view == null) {
            view = LayoutInflater.from(this.mContext).inflate(C0291R.layout.list_item_local_device, null);
        }
        ImageView typeImg = (ImageView) view.findViewById(C0291R.id.img_type);
        final LocalDevice localDevice = (LocalDevice) this.datas.get(position);
        ((TextView) view.findViewById(C0291R.id.text_name)).setText(Utils.showShortDevID(localDevice.getContactId()));
        switch (localDevice.getType()) {
            case 0:
                typeImg.setImageResource(C0291R.drawable.ic_device_type_unknown);
                break;
            case 2:
                typeImg.setImageResource(C0291R.drawable.ic_device_type_npc);
                break;
            case 5:
                typeImg.setImageResource(C0291R.drawable.ic_device_type_door_bell);
                break;
            case 7:
                typeImg.setImageResource(C0291R.drawable.ic_device_type_ipc);
                break;
            default:
                typeImg.setImageResource(C0291R.drawable.ic_device_type_unknown);
                break;
        }
        view.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                Contact saveContact = new Contact();
                saveContact.contactId = localDevice.contactId;
                saveContact.contactType = localDevice.type;
                saveContact.messageCount = 0;
                saveContact.activeUser = NpcCommon.mThreeNum;
                saveContact.contactModel = localDevice.contactModel;
                Intent modify = new Intent();
                modify.setClass(LocalDeviceListAdapter.this.mContext, AddContactNextActivity.class);
                if (localDevice.getFlag() == 1) {
                    modify.putExtra("isCreatePassword", false);
                } else {
                    modify.putExtra("isCreatePassword", true);
                }
                modify.putExtra(ContactDB.TABLE_NAME, saveContact);
                LocalDeviceListAdapter.this.mContext.startActivity(modify);
            }
        });
        return view;
    }

    public void updateData() {
        this.datas = FList.getInstance().getLocalDevices();
        notifyDataSetChanged();
    }
}
