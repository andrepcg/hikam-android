package com.jwkj.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.hikam.C0291R;
import com.jwkj.activity.TellDetailActivity;
import com.jwkj.data.Contact;
import com.jwkj.data.DataManager;
import com.jwkj.data.NearlyTell;
import com.jwkj.global.Constants.Action;
import com.jwkj.global.FList;
import com.jwkj.utils.Utils;
import com.jwkj.widget.HeaderView;
import com.jwkj.widget.NormalDialog;
import com.jwkj.widget.NormalDialog.OnButtonOkListener;
import java.util.List;

public class NearlyTellAdapter extends BaseAdapter {
    Context mContext;
    private List<NearlyTell> mData;
    private final LayoutInflater mInflater;

    static final class ViewHolder {
        public ImageView mCallTypeView;
        public TextView mCount;
        public TextView mDate;
        public HeaderView mImgIcon;
        public TextView mNameTextView;

        ViewHolder() {
        }

        public int hashCode() {
            return (this.mImgIcon.hashCode() + this.mNameTextView.hashCode()) + this.mCallTypeView.hashCode();
        }
    }

    public NearlyTellAdapter(Context context, List<NearlyTell> list) {
        this.mInflater = (LayoutInflater) context.getSystemService("layout_inflater");
        this.mContext = context;
        this.mData = list;
    }

    public int getCount() {
        return this.mData.size();
    }

    public NearlyTell getItem(int position) {
        return (NearlyTell) this.mData.get(position);
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHoler;
        if (convertView == null) {
            viewHoler = new ViewHolder();
            convertView = this.mInflater.inflate(C0291R.layout.list_recent_item, parent, false);
            viewHoler.mNameTextView = (TextView) convertView.findViewById(C0291R.id.user_name);
            viewHoler.mImgIcon = (HeaderView) convertView.findViewById(C0291R.id.header_img);
            viewHoler.mDate = (TextView) convertView.findViewById(C0291R.id.date);
            viewHoler.mCallTypeView = (ImageView) convertView.findViewById(C0291R.id.calllogo);
            viewHoler.mCount = (TextView) convertView.findViewById(C0291R.id.count);
            convertView.setTag(viewHoler);
        } else {
            viewHoler = (ViewHolder) convertView.getTag();
        }
        if (this.mData.get(position) instanceof NearlyTell) {
            NearlyTell nearlyTell = (NearlyTell) this.mData.get(position);
            Contact contact = FList.getInstance().isContact(nearlyTell.tellId);
            if (contact != null) {
                viewHoler.mNameTextView.setText(contact.contactName);
            } else {
                viewHoler.mNameTextView.setText(nearlyTell.tellId);
            }
            viewHoler.mImgIcon.updateImage(nearlyTell.tellId, false);
            viewHoler.mDate.setText(Utils.getFormatTellDate(this.mContext, nearlyTell.tellTime));
            if (nearlyTell.count > 1) {
                viewHoler.mCount.setText("(" + nearlyTell.count + ")");
            } else {
                viewHoler.mCount.setText("");
            }
            switch (nearlyTell.tellState) {
                case 0:
                    viewHoler.mCallTypeView.setImageResource(C0291R.drawable.call_in_reject);
                    break;
                case 1:
                    viewHoler.mCallTypeView.setImageResource(C0291R.drawable.call_in_accept);
                    break;
                case 2:
                    viewHoler.mCallTypeView.setImageResource(C0291R.drawable.call_out_reject);
                    break;
                case 3:
                    viewHoler.mCallTypeView.setImageResource(C0291R.drawable.call_out_accept);
                    break;
            }
        }
        convertView.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                Intent i = new Intent(NearlyTellAdapter.this.mContext, TellDetailActivity.class);
                i.putExtra("nearlyTell", (NearlyTell) NearlyTellAdapter.this.mData.get(position));
                NearlyTellAdapter.this.mContext.startActivity(i);
            }
        });
        convertView.setOnLongClickListener(new OnLongClickListener() {
            public boolean onLongClick(View arg0) {
                final String tellId = ((NearlyTell) NearlyTellAdapter.this.mData.get(position)).tellId;
                NormalDialog dialog = new NormalDialog(NearlyTellAdapter.this.mContext, NearlyTellAdapter.this.mContext.getResources().getString(C0291R.string.delete_call_records), NearlyTellAdapter.this.mContext.getResources().getString(C0291R.string.are_you_sure_delete) + " " + tellId + "?", NearlyTellAdapter.this.mContext.getResources().getString(C0291R.string.delete), NearlyTellAdapter.this.mContext.getResources().getString(C0291R.string.cancel));
                dialog.setOnButtonOkListener(new OnButtonOkListener() {
                    public void onClick() {
                        DataManager.deleteNearlyTellByTellId(NearlyTellAdapter.this.mContext, tellId);
                        Intent refreshContans = new Intent();
                        refreshContans.setAction(Action.ACTION_REFRESH_NEARLY_TELL);
                        NearlyTellAdapter.this.mContext.sendBroadcast(refreshContans);
                    }
                });
                dialog.showDialog();
                return true;
            }
        });
        return convertView;
    }

    public void updateData(List<NearlyTell> mData) {
        this.mData = mData;
    }
}
