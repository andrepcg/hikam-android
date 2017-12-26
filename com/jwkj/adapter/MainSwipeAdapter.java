package com.jwkj.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.hikam.C0291R;
import com.jwkj.CallActivity;
import com.jwkj.activity.AddContactNextActivity;
import com.jwkj.activity.AlarmRecordActivity;
import com.jwkj.activity.MainControlActivity;
import com.jwkj.activity.SetInitPasswordActivity;
import com.jwkj.data.Contact;
import com.jwkj.data.ContactDB;
import com.jwkj.entity.LocalDevice;
import com.jwkj.fragment.ContactFrag;
import com.jwkj.global.FList;
import com.jwkj.global.NpcCommon;
import com.jwkj.utils.C0568T;
import com.jwkj.utils.Utils;
import com.jwkj.widget.CompatLinearLayout;
import com.jwkj.widget.HKHorizontalSlideView;
import com.jwkj.widget.HeaderView;
import com.jwkj.widget.NormalDialog;
import com.jwkj.widget.NormalDialog.OnButtonCancelListener;
import com.jwkj.widget.NormalDialog.OnButtonOkListener;
import com.jwkj.widget.playback.PlayBackManagerActivity;
import com.p2p.core.P2PHandler;
import com.p2p.core.P2PValue;
import java.io.File;

public class MainSwipeAdapter extends BaseAdapter {
    private ContactFrag cf;
    Context context;
    Handler handler = new Handler(new C04921());
    private NormalDialog normalDialog;
    private OnCheckVersionBeforeOpen onCheckVersionBeforeOpen;

    class C04921 implements Callback {
        C04921() {
        }

        public boolean handleMessage(Message msg) {
            MainSwipeAdapter.this.notifyDataSetChanged();
            return true;
        }
    }

    public interface OnCheckVersionBeforeOpen {
        void onCheckContact(Contact contact);
    }

    class ViewHolder2 {
        public ImageView device_type;
        public TextView name;

        ViewHolder2() {
        }

        public TextView getName() {
            return this.name;
        }

        public void setName(TextView name) {
            this.name = name;
        }

        public ImageView getDevice_type() {
            return this.device_type;
        }

        public void setDevice_type(ImageView device_type) {
            this.device_type = device_type;
        }
    }

    class ViewHolder {
        private LinearLayout alarm;
        private CompatLinearLayout alarm_history;
        private ImageView alarm_msg_icon;
        private HeaderView head;
        private ImageView header_icon_play;
        private ImageView image_defence_state;
        private RelativeLayout layout_defence_btn;
        private TextView name;
        private TextView online_state;
        private LinearLayout playback;
        private ProgressBar progress_defence;
        private LinearLayout setting;

        ViewHolder() {
        }

        public TextView getOnline_state() {
            return this.online_state;
        }

        public void setOnline_state(TextView online_state) {
            this.online_state = online_state;
        }

        public HeaderView getHead() {
            return this.head;
        }

        public void setHead(HeaderView head) {
            this.head = head;
        }

        public TextView getName() {
            return this.name;
        }

        public void setName(TextView name) {
            this.name = name;
        }

        public ImageView getHeader_icon_play() {
            return this.header_icon_play;
        }

        public void setHeader_icon_play(ImageView header_icon_play) {
            this.header_icon_play = header_icon_play;
        }

        public RelativeLayout getLayout_defence_btn() {
            return this.layout_defence_btn;
        }

        public void setLayout_defence_btn(RelativeLayout layout_defence_btn) {
            this.layout_defence_btn = layout_defence_btn;
        }

        public ImageView getImage_defence_state() {
            return this.image_defence_state;
        }

        public void setImage_defence_state(ImageView image_defence_state) {
            this.image_defence_state = image_defence_state;
        }

        public ProgressBar getProgress_defence() {
            return this.progress_defence;
        }

        public void setProgress_defence(ProgressBar progress_defence) {
            this.progress_defence = progress_defence;
        }

        public ImageView getAlarm_msg_icon() {
            return this.alarm_msg_icon;
        }

        public void setAlarm_msg_icon(ImageView alarm_icon) {
            this.alarm_msg_icon = alarm_icon;
        }

        public CompatLinearLayout getAlarm_history() {
            return this.alarm_history;
        }

        public void setAlarm_history(CompatLinearLayout alarm_history) {
            this.alarm_history = alarm_history;
        }

        public LinearLayout getAlarm() {
            return this.alarm;
        }

        public void setAlarm(LinearLayout alarm) {
            this.alarm = alarm;
        }

        public LinearLayout getPlayback() {
            return this.playback;
        }

        public void setPlayback(LinearLayout playback) {
            this.playback = playback;
        }

        public LinearLayout getSetting() {
            return this.setting;
        }

        public void setSetting(LinearLayout setting) {
            this.setting = setting;
        }
    }

    public void setOnCheckVersionBeforeOpen(OnCheckVersionBeforeOpen onCheckVersionBeforeOpen) {
        this.onCheckVersionBeforeOpen = onCheckVersionBeforeOpen;
    }

    public MainSwipeAdapter(Context context, ContactFrag cf) {
        this.context = context;
        this.cf = cf;
    }

    public int getCount() {
        int size = FList.getInstance().getUnsetPasswordLocalDevices().size();
        return FList.getInstance().size();
    }

    public Contact getItem(int position) {
        return FList.getInstance().get(position);
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public int getItemViewType(int position) {
        if (position >= FList.getInstance().size()) {
            return 0;
        }
        return 1;
    }

    public int getViewTypeCount() {
        return 2;
    }

    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        int size1 = FList.getInstance().list().size();
        if (position < size1) {
            convertView = LayoutInflater.from(this.context).inflate(C0291R.layout.list_camera_item, null);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.setHead((HeaderView) convertView.findViewById(C0291R.id.user_icon));
            viewHolder.setName((TextView) convertView.findViewById(C0291R.id.user_name));
            viewHolder.setOnline_state((TextView) convertView.findViewById(C0291R.id.online_state));
            viewHolder.setHeader_icon_play((ImageView) convertView.findViewById(C0291R.id.header_icon_play));
            viewHolder.setLayout_defence_btn((RelativeLayout) convertView.findViewById(C0291R.id.layout_defence_btn));
            viewHolder.setImage_defence_state((ImageView) convertView.findViewById(C0291R.id.image_defence_state));
            viewHolder.setProgress_defence((ProgressBar) convertView.findViewById(C0291R.id.progress_defence));
            viewHolder.setAlarm_msg_icon((ImageView) convertView.findViewById(C0291R.id.alarm_history_dot));
            viewHolder.setAlarm_history((CompatLinearLayout) convertView.findViewById(C0291R.id.alarm_history));
            viewHolder.setAlarm((LinearLayout) convertView.findViewById(C0291R.id.alarm));
            viewHolder.setPlayback((LinearLayout) convertView.findViewById(C0291R.id.play_back));
            viewHolder.setSetting((LinearLayout) convertView.findViewById(C0291R.id.setting));
            convertView.setTag(viewHolder);
        } else {
            View view = LayoutInflater.from(this.context).inflate(C0291R.layout.list_contact_item2, null);
            ViewHolder2 viewHolder2 = new ViewHolder2();
            viewHolder2.setName((TextView) view.findViewById(C0291R.id.user_name));
            viewHolder2.setDevice_type((ImageView) view.findViewById(C0291R.id.login_type));
            view.setTag(viewHolder2);
        }
        if (position < size1) {
            ViewHolder holder = (ViewHolder) convertView.getTag();
            final Contact contact = FList.getInstance().get(position);
            int deviceType = contact.contactType;
            HKHorizontalSlideView swipeLayout = (HKHorizontalSlideView) convertView.findViewById(C0291R.id.swipe);
            final int i = position;
            convertView.findViewById(C0291R.id.ll_menu).setOnClickListener(new OnClickListener() {

                class C10981 implements OnButtonOkListener {
                    C10981() {
                    }

                    public void onClick() {
                        FList.getInstance().delete(contact, i, MainSwipeAdapter.this.handler);
                        Utils.deleteFile(new File(Environment.getExternalStorageDirectory().getPath() + "/screenshot/tempHead/" + NpcCommon.mThreeNum + "/" + contact.contactId + ".jpg"));
                    }
                }

                public void onClick(View arg0) {
                    NormalDialog dialog = new NormalDialog(MainSwipeAdapter.this.context, MainSwipeAdapter.this.context.getResources().getString(C0291R.string.delete_contact), MainSwipeAdapter.this.context.getResources().getString(C0291R.string.are_you_sure_delete) + " " + Utils.showShortDevID(contact.contactId) + "?", MainSwipeAdapter.this.context.getResources().getString(C0291R.string.delete), MainSwipeAdapter.this.context.getResources().getString(C0291R.string.cancel));
                    dialog.setOnButtonOkListener(new C10981());
                    dialog.showDialog();
                }
            });
            if (contact.onLineState == 1) {
                holder.getHead().updateImage(contact.contactId, false);
                holder.getOnline_state().setText(C0291R.string.online_state);
                holder.getOnline_state().setTextColor(this.context.getResources().getColor(C0291R.color.text_color_blue));
                if (contact.contactType == 0 || contact.contactType == 3) {
                    holder.getLayout_defence_btn().setVisibility(8);
                } else {
                    holder.getLayout_defence_btn().setVisibility(0);
                    if (contact.defenceState == 2) {
                        holder.getProgress_defence().setVisibility(0);
                        holder.getImage_defence_state().setVisibility(4);
                    } else if (contact.defenceState == 1) {
                        holder.getProgress_defence().setVisibility(8);
                        holder.getImage_defence_state().setVisibility(0);
                        holder.getImage_defence_state().setImageResource(C0291R.drawable.a_list_alarm_on);
                        if ("123".equals(contact.userPassword)) {
                            if (this.normalDialog == null || !this.normalDialog.isShowing()) {
                                this.normalDialog = new NormalDialog(this.context, this.context.getResources().getString(C0291R.string.attention_prompt), this.context.getResources().getString(C0291R.string.contact_iniy_password_prompt, new Object[]{contact.contactName}), null, null);
                                this.normalDialog.setOnButtonCancelListener(new OnButtonCancelListener() {
                                    public void onClick() {
                                        MainSwipeAdapter.this.normalDialog.dismiss();
                                        MainSwipeAdapter.this.normalDialog = null;
                                        Intent modify_password = new Intent(MainSwipeAdapter.this.context, SetInitPasswordActivity.class);
                                        modify_password.putExtra(ContactDB.TABLE_NAME, contact);
                                        MainSwipeAdapter.this.context.startActivity(modify_password);
                                    }
                                });
                                this.normalDialog.showPromptDialog();
                            } else {
                                Log.e("my", "isShowing");
                            }
                        }
                    } else if (contact.defenceState == 0) {
                        holder.getProgress_defence().setVisibility(8);
                        holder.getImage_defence_state().setVisibility(0);
                        holder.getImage_defence_state().setImageResource(C0291R.drawable.a_list_alarm_off);
                        if ("123".equals(contact.userPassword)) {
                            if (this.normalDialog == null || !this.normalDialog.isShowing()) {
                                this.normalDialog = new NormalDialog(this.context, this.context.getResources().getString(C0291R.string.attention_prompt), this.context.getResources().getString(C0291R.string.contact_iniy_password_prompt, new Object[]{contact.contactName}), null, null);
                                this.normalDialog.setOnButtonCancelListener(new OnButtonCancelListener() {
                                    public void onClick() {
                                        MainSwipeAdapter.this.normalDialog.dismiss();
                                        MainSwipeAdapter.this.normalDialog = null;
                                        Intent modify_password = new Intent(MainSwipeAdapter.this.context, SetInitPasswordActivity.class);
                                        modify_password.putExtra(ContactDB.TABLE_NAME, contact);
                                        MainSwipeAdapter.this.context.startActivity(modify_password);
                                    }
                                });
                                this.normalDialog.showPromptDialog();
                            } else {
                                Log.e("my", "isShowing");
                            }
                        }
                    } else if (contact.defenceState == 4) {
                        holder.getProgress_defence().setVisibility(8);
                        holder.getImage_defence_state().setVisibility(0);
                        holder.getImage_defence_state().setImageResource(C0291R.drawable.ic_defence_warning);
                    } else if (contact.defenceState == 3) {
                        holder.getProgress_defence().setVisibility(8);
                        holder.getImage_defence_state().setVisibility(0);
                        holder.getImage_defence_state().setImageResource(C0291R.drawable.ic_defence_warning);
                    } else if (contact.defenceState == 5) {
                        holder.getProgress_defence().setVisibility(8);
                        holder.getImage_defence_state().setVisibility(0);
                        holder.getImage_defence_state().setImageResource(C0291R.drawable.limit);
                    }
                }
            } else {
                holder.getHead().updateImage(contact.contactId, true);
                holder.getOnline_state().setText(C0291R.string.offline_state);
                holder.getOnline_state().setTextColor(this.context.getResources().getColor(C0291R.color.text_color_gray));
                holder.getLayout_defence_btn().setVisibility(8);
                if (!P2PValue.HikamDeviceModelList.contains(contact.contactModel)) {
                    Log.e("alex", "camera is offline, setWifi start");
                    P2PHandler.getInstance().setWifi(contact.contactModel, contact.contactId, contact.contactPassword, 0, "HiKam_WiFi", "0");
                    Log.e("alex", "camera is offline, setWifi end");
                }
            }
            if (contact.messageCount > 0) {
                holder.getAlarm_msg_icon().setVisibility(0);
            } else {
                holder.getAlarm_msg_icon().setVisibility(8);
            }
            holder.getAlarm_history().setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    Intent go_alarm_record = new Intent(MainSwipeAdapter.this.context, AlarmRecordActivity.class);
                    go_alarm_record.putExtra(ContactDB.TABLE_NAME, contact);
                    MainSwipeAdapter.this.context.startActivity(go_alarm_record);
                }
            });
            holder.getName().setText(contact.contactName);
            if (deviceType == 2 || deviceType == 7 || deviceType == 5) {
                holder.getHeader_icon_play().setOnClickListener(new OnClickListener() {
                    public void onClick(View arg0) {
                        if (MainSwipeAdapter.this.onCheckVersionBeforeOpen != null) {
                            MainSwipeAdapter.this.onCheckVersionBeforeOpen.onCheckContact(contact);
                        } else if (FList.getInstance().isContactUnSetPassword(contact.contactId) != null) {
                        } else {
                            if (contact.onLineState == 0) {
                                C0568T.showShort(MainSwipeAdapter.this.context, (int) C0291R.string.offline);
                            } else if (contact.contactId == null || contact.contactId.equals("")) {
                                C0568T.showShort(MainSwipeAdapter.this.context, (int) C0291R.string.username_error);
                            } else if (contact.contactPassword == null || contact.contactPassword.equals("")) {
                                C0568T.showShort(MainSwipeAdapter.this.context, (int) C0291R.string.password_error);
                            } else {
                                Intent monitor = new Intent();
                                monitor.setClass(MainSwipeAdapter.this.context, CallActivity.class);
                                monitor.putExtra("callModel", contact.contactModel);
                                monitor.putExtra("callId", contact.contactId);
                                monitor.putExtra(ContactDB.COLUMN_CONTACT_NAME, contact.contactName);
                                monitor.putExtra("password", contact.contactPassword);
                                monitor.putExtra("isOutCall", true);
                                monitor.putExtra("type", 1);
                                Log.e("oaosj", "call: " + contact.contactModel + " " + contact.contactId + " " + contact.contactName + " " + contact.contactPassword + " " + true + " " + 1);
                                MainSwipeAdapter.this.context.startActivity(monitor);
                            }
                        }
                    }
                });
                holder.getHeader_icon_play().setVisibility(0);
            } else if (deviceType == 3) {
                holder.getHeader_icon_play().setOnClickListener(new OnClickListener() {
                    public void onClick(View arg0) {
                        if (contact.contactId == null || contact.contactId.equals("")) {
                            C0568T.showShort(MainSwipeAdapter.this.context, (int) C0291R.string.username_error);
                            return;
                        }
                        Intent call = new Intent();
                        call.setClass(MainSwipeAdapter.this.context, CallActivity.class);
                        call.putExtra("callId", contact.contactId);
                        call.putExtra("isOutCall", true);
                        call.putExtra("type", 0);
                        MainSwipeAdapter.this.context.startActivity(call);
                    }
                });
                holder.getHeader_icon_play().setVisibility(0);
            } else if (Integer.parseInt(contact.contactId) < 256) {
                holder.getHeader_icon_play().setOnClickListener(new OnClickListener() {
                    public void onClick(View arg0) {
                        Intent monitor = new Intent();
                        monitor.setClass(MainSwipeAdapter.this.context, CallActivity.class);
                        monitor.putExtra("callId", contact.contactId);
                        monitor.putExtra(ContactDB.COLUMN_CONTACT_NAME, contact.contactName);
                        monitor.putExtra("password", contact.contactPassword);
                        monitor.putExtra("isOutCall", true);
                        monitor.putExtra("type", 1);
                        MainSwipeAdapter.this.context.startActivity(monitor);
                    }
                });
            } else {
                holder.getHeader_icon_play().setVisibility(8);
            }
            final ViewHolder viewHolder3 = holder;
            holder.getLayout_defence_btn().setOnClickListener(new OnClickListener() {
                public void onClick(View arg0) {
                    if (contact.defenceState == 4 || contact.defenceState == 3) {
                        viewHolder3.getProgress_defence().setVisibility(0);
                        viewHolder3.getImage_defence_state().setVisibility(4);
                        P2PHandler.getInstance().getDefenceStates(contact.contactModel, contact.contactId, contact.contactPassword);
                        FList.getInstance().setIsClickGetDefenceState(contact.contactId, true);
                    } else if (contact.defenceState == 1) {
                        viewHolder3.getProgress_defence().setVisibility(0);
                        viewHolder3.getImage_defence_state().setVisibility(4);
                        P2PHandler.getInstance().setRemoteDefence(contact.contactModel, contact.contactId, contact.contactPassword, 0);
                        FList.getInstance().setIsClickGetDefenceState(contact.contactId, true);
                    } else if (contact.defenceState == 0) {
                        viewHolder3.getProgress_defence().setVisibility(0);
                        viewHolder3.getImage_defence_state().setVisibility(4);
                        P2PHandler.getInstance().setRemoteDefence(contact.contactModel, contact.contactId, contact.contactPassword, 1);
                        FList.getInstance().setIsClickGetDefenceState(contact.contactId, true);
                    }
                }
            });
            holder.getPlayback().setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (contact.onLineState == 0) {
                        C0568T.showShort(MainSwipeAdapter.this.context, (int) C0291R.string.offline);
                    }
                    Intent playback;
                    if (P2PValue.HikamDeviceModelList.contains(contact.contactModel)) {
                        playback = new Intent();
                        playback.setClass(MainSwipeAdapter.this.context, PlayBackManagerActivity.class);
                        playback.putExtra(ContactDB.TABLE_NAME, contact);
                        MainSwipeAdapter.this.context.startActivity(playback);
                    } else {
                        playback = new Intent();
                        playback.setClass(MainSwipeAdapter.this.context, PlayBackManagerActivity.class);
                        playback.putExtra(ContactDB.TABLE_NAME, contact);
                        MainSwipeAdapter.this.context.startActivity(playback);
                    }
                }
            });
            holder.getSetting().setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (contact.contactId == null || contact.contactId.equals("")) {
                        C0568T.showShort(MainSwipeAdapter.this.context, (int) C0291R.string.username_error);
                    } else if (contact.contactPassword == null || contact.contactPassword.equals("")) {
                        C0568T.showShort(MainSwipeAdapter.this.context, (int) C0291R.string.password_error);
                    } else {
                        Intent setting = new Intent();
                        setting.setClass(MainSwipeAdapter.this.context, MainControlActivity.class);
                        setting.putExtra(ContactDB.TABLE_NAME, contact);
                        setting.putExtra("type", 2);
                        MainSwipeAdapter.this.context.startActivity(setting);
                    }
                }
            });
        } else {
            ViewHolder2 holder2 = (ViewHolder2) convertView.getTag();
            LocalDevice localDevice = (LocalDevice) FList.getInstance().getUnsetPasswordLocalDevices().get(position - size1);
            holder2.name.setText(localDevice.getContactId());
            switch (localDevice.getType()) {
                case 0:
                    holder2.device_type.setImageResource(C0291R.drawable.ic_device_type_unknown);
                    break;
                case 2:
                    holder2.device_type.setImageResource(C0291R.drawable.ic_device_type_npc);
                    break;
                case 5:
                    holder2.device_type.setImageResource(C0291R.drawable.ic_device_type_door_bell);
                    break;
                case 7:
                    holder2.device_type.setImageResource(C0291R.drawable.ic_device_type_ipc);
                    break;
                default:
                    holder2.device_type.setImageResource(C0291R.drawable.ic_device_type_unknown);
                    break;
            }
            final LocalDevice localDevice2 = localDevice;
            convertView.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    Contact saveContact = new Contact();
                    saveContact.contactId = localDevice2.contactId;
                    saveContact.contactType = localDevice2.type;
                    saveContact.messageCount = 0;
                    saveContact.activeUser = NpcCommon.mThreeNum;
                    Intent modify = new Intent();
                    modify.setClass(MainSwipeAdapter.this.context, AddContactNextActivity.class);
                    modify.putExtra("isCreatePassword", true);
                    modify.putExtra(ContactDB.TABLE_NAME, saveContact);
                    String mark = localDevice2.address.getHostAddress();
                    modify.putExtra("ipFlag", mark.substring(mark.lastIndexOf(".") + 1, mark.length()));
                    MainSwipeAdapter.this.context.startActivity(modify);
                }
            });
        }
        return convertView;
    }
}
