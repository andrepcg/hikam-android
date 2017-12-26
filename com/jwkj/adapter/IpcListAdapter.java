package com.jwkj.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.hikam.C0291R;
import com.jwkj.CallActivity;
import com.jwkj.activity.AddContactNextActivity;
import com.jwkj.data.Contact;
import com.jwkj.data.ContactDB;
import com.jwkj.global.FList;
import com.jwkj.global.NpcCommon;
import com.jwkj.widget.HeaderView;
import com.jwkj.widget.NormalDialog;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;

public class IpcListAdapter extends BaseAdapter {
    private HashMap<String, InetAddress> addresses = new HashMap();
    private Context context;
    private ArrayList<String> data;
    private NormalDialog dialog;
    private HashMap<String, Integer> flags = new HashMap();
    boolean isShowAnim = true;
    private HashMap<String, String> names = new HashMap();
    private HashMap<String, Integer> types = new HashMap();

    class C04711 implements OnTouchListener {
        C04711() {
        }

        public boolean onTouch(View arg0, MotionEvent arg1) {
            return true;
        }
    }

    static final class ViewHolder {
        public TextView addressText;
        public HeaderView headerImg;
        public RelativeLayout main;
        public TextView nameText;
        public ImageView operatorImg;
        public ImageView typeImg;

        ViewHolder() {
        }
    }

    public IpcListAdapter(Context context, ArrayList<String> data) {
        this.context = context;
        this.data = data;
    }

    public int getCount() {
        return this.data.size();
    }

    public Object getItem(int arg0) {
        return null;
    }

    public long getItemId(int arg0) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup arg2) {
        ViewHolder viewHoler;
        if (convertView == null) {
            viewHoler = new ViewHolder();
            convertView = LayoutInflater.from(this.context).inflate(C0291R.layout.list_ipc_item, null);
            viewHoler.headerImg = (HeaderView) convertView.findViewById(C0291R.id.header_img);
            viewHoler.typeImg = (ImageView) convertView.findViewById(C0291R.id.type_img);
            viewHoler.nameText = (TextView) convertView.findViewById(C0291R.id.ipc_name);
            viewHoler.operatorImg = (ImageView) convertView.findViewById(C0291R.id.operator_img);
            viewHoler.main = (RelativeLayout) convertView.findViewById(C0291R.id.main);
            viewHoler.addressText = (TextView) convertView.findViewById(C0291R.id.ipc_address);
            convertView.setTag(viewHoler);
        } else {
            viewHoler = (ViewHolder) convertView.getTag();
        }
        convertView.setOnTouchListener(new C04711());
        if (position == this.data.size() - 1 && this.isShowAnim) {
            convertView.startAnimation(AnimationUtils.loadAnimation(this.context, C0291R.anim.ipc_item_down));
        }
        TextView nameText = viewHoler.nameText;
        TextView addressText = viewHoler.addressText;
        HeaderView headerImg = viewHoler.headerImg;
        ImageView typeImg = viewHoler.typeImg;
        RelativeLayout main = viewHoler.main;
        ImageView operatorImg = viewHoler.operatorImg;
        final String threeNum = (String) this.data.get(position);
        final InetAddress address = (InetAddress) this.addresses.get(threeNum);
        headerImg.updateImage(threeNum, false);
        final Contact contact = FList.getInstance().isContact(threeNum);
        final int deviceType = ((Integer) this.types.get(threeNum)).intValue();
        final int flag = ((Integer) this.flags.get(threeNum)).intValue();
        Log.e("shake", "id:" + threeNum + " type:" + deviceType + " flag:" + flag + " ip:" + address.getHostAddress());
        addressText.setText(address.getHostAddress());
        switch (deviceType) {
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
        if (contact != null) {
            nameText.setText(contact.contactName);
            if (flag == 1) {
                operatorImg.setImageResource(C0291R.drawable.ic_shake_monitor);
            } else if (flag == 0) {
                operatorImg.setImageResource(C0291R.drawable.add);
            }
        } else {
            nameText.setText(threeNum);
            if (flag == 1) {
                operatorImg.setImageResource(C0291R.drawable.add);
            } else if (flag == 0) {
                operatorImg.setImageResource(C0291R.drawable.add);
            }
        }
        String mark = address.getHostAddress();
        final String ipFlag = mark.substring(mark.lastIndexOf(".") + 1, mark.length());
        main.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                if (contact != null) {
                    if (flag == 1) {
                        Intent monitor = new Intent();
                        monitor.setClass(IpcListAdapter.this.context, CallActivity.class);
                        String mark = address.getHostAddress();
                        monitor.putExtra("callId", contact.contactId);
                        monitor.putExtra(ContactDB.COLUMN_CONTACT_NAME, contact.contactName);
                        monitor.putExtra("ipFlag", mark.substring(mark.lastIndexOf(".") + 1, mark.length()));
                        monitor.putExtra("password", contact.contactPassword);
                        monitor.putExtra("isOutCall", true);
                        monitor.putExtra("type", 1);
                        IpcListAdapter.this.context.startActivity(monitor);
                    } else if (flag == 0) {
                        IpcListAdapter.this.addContact(threeNum, deviceType, true, ipFlag);
                    }
                } else if (flag == 1) {
                    IpcListAdapter.this.addContact(threeNum, deviceType, false, "");
                } else if (flag == 0) {
                    IpcListAdapter.this.addContact(threeNum, deviceType, true, "");
                }
            }
        });
        return convertView;
    }

    public void addContact(String threeNum, int deviceType, boolean isCreatePassword, String ipFlag) {
        Contact saveContact = new Contact();
        saveContact.contactId = threeNum;
        saveContact.contactType = deviceType;
        saveContact.messageCount = 0;
        saveContact.activeUser = NpcCommon.mThreeNum;
        Intent modify = new Intent();
        modify.setClass(this.context, AddContactNextActivity.class);
        modify.putExtra("isCreatePassword", isCreatePassword);
        modify.putExtra(ContactDB.TABLE_NAME, saveContact);
        modify.putExtra("ipFlag", ipFlag);
        this.context.startActivity(modify);
    }

    public void updateData(String id, InetAddress address, String name, int flag, int type) {
        if (!this.data.contains(id)) {
            this.data.add(id);
            this.addresses.put(id, address);
            this.names.put(id, name);
            this.flags.put(id, Integer.valueOf(flag));
            this.types.put(id, Integer.valueOf(type));
            notifyDataSetChanged();
        }
    }

    public void updateFlag(String threeNum, boolean isInit) {
        if (isInit) {
            this.flags.put(threeNum, Integer.valueOf(1));
        } else {
            this.flags.put(threeNum, Integer.valueOf(0));
        }
        notifyDataSetChanged();
    }

    public void closeAnim() {
        this.isShowAnim = false;
    }

    public void clear() {
        this.isShowAnim = true;
        this.data.clear();
        notifyDataSetChanged();
    }
}
