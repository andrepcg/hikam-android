package com.jwkj.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.hikam.C0291R;
import com.jwkj.CallActivity;
import com.jwkj.PlayBackListActivity;
import com.jwkj.activity.AddContactNextActivity;
import com.jwkj.activity.AlarmRecordActivity;
import com.jwkj.data.Contact;
import com.jwkj.data.ContactDB;
import com.jwkj.entity.LocalDevice;
import com.jwkj.fragment.ContactFrag;
import com.jwkj.global.Constants.Image;
import com.jwkj.global.FList;
import com.jwkj.global.NpcCommon;
import com.jwkj.utils.C0568T;
import com.jwkj.utils.Utils;
import com.jwkj.widget.HeaderView;
import com.jwkj.widget.NormalDialog;
import com.jwkj.widget.NormalDialog.OnButtonOkListener;
import com.p2p.core.P2PHandler;
import java.io.File;

public class MainListAdapter extends BaseAdapter {
    private ContactFrag cf;
    Context context;
    Handler handler = new Handler(new Callback() {
        public boolean handleMessage(Message msg) {
            MainListAdapter.this.notifyDataSetChanged();
            return true;
        }
    });

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
        private LinearLayout alarm_history;
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

        public LinearLayout getAlarm_history() {
            return this.alarm_history;
        }

        public void setAlarm_history(LinearLayout alarm_history) {
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

    public MainListAdapter(Context context, ContactFrag cf) {
        this.context = context;
        this.cf = cf;
    }

    public int getCount() {
        return FList.getInstance().size() + FList.getInstance().getUnsetPasswordLocalDevices().size();
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

    public View getView(int position, View convertView, ViewGroup parent) {
        int size1 = FList.getInstance().list().size();
        View view;
        if (position < size1) {
            ViewHolder holder;
            view = convertView;
            if (view == null) {
                view = LayoutInflater.from(this.context).inflate(C0291R.layout.list_camera_item, null);
                holder = new ViewHolder();
                holder.setHead((HeaderView) view.findViewById(C0291R.id.user_icon));
                holder.setName((TextView) view.findViewById(C0291R.id.user_name));
                holder.setOnline_state((TextView) view.findViewById(C0291R.id.online_state));
                holder.setHeader_icon_play((ImageView) view.findViewById(C0291R.id.header_icon_play));
                holder.setLayout_defence_btn((RelativeLayout) view.findViewById(C0291R.id.layout_defence_btn));
                holder.setImage_defence_state((ImageView) view.findViewById(C0291R.id.image_defence_state));
                holder.setProgress_defence((ProgressBar) view.findViewById(C0291R.id.progress_defence));
                holder.setAlarm_msg_icon((ImageView) view.findViewById(C0291R.id.alarm_history_dot));
                holder.setAlarm_history((LinearLayout) view.findViewById(C0291R.id.alarm_history));
                holder.setAlarm((LinearLayout) view.findViewById(C0291R.id.alarm));
                holder.setPlayback((LinearLayout) view.findViewById(C0291R.id.play_back));
                holder.setSetting((LinearLayout) view.findViewById(C0291R.id.setting));
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            final Contact contact = FList.getInstance().get(position);
            int deviceType = contact.contactType;
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
                    } else if (contact.defenceState == 0) {
                        holder.getProgress_defence().setVisibility(8);
                        holder.getImage_defence_state().setVisibility(0);
                        holder.getImage_defence_state().setImageResource(C0291R.drawable.a_list_alarm_off);
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
            }
            if (contact.messageCount > 0) {
                holder.getAlarm_msg_icon().setVisibility(0);
                Log.e("messageCount > 0", "messageCount > 0");
            } else {
                holder.getAlarm_msg_icon().setVisibility(8);
                Log.e("messageCount < 0", "messageCount < 0");
            }
            holder.getAlarm_history().setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (contact.messageCount > 0) {
                        Log.e("history onclick", "f");
                        Intent go_alarm_record = new Intent(MainListAdapter.this.context, AlarmRecordActivity.class);
                        go_alarm_record.putExtra(ContactDB.TABLE_NAME, contact);
                        MainListAdapter.this.context.startActivity(go_alarm_record);
                    }
                }
            });
            holder.getName().setText(contact.contactName);
            if (deviceType == 2 || deviceType == 7 || deviceType == 5) {
                holder.getHead().setOnClickListener(new OnClickListener() {
                    public void onClick(View arg0) {
                        if (FList.getInstance().isContactUnSetPassword(contact.contactId) == null) {
                            if (contact.contactId == null || contact.contactId.equals("")) {
                                C0568T.showShort(MainListAdapter.this.context, (int) C0291R.string.username_error);
                            } else if (contact.contactPassword == null || contact.contactPassword.equals("")) {
                                C0568T.showShort(MainListAdapter.this.context, (int) C0291R.string.password_error);
                            } else {
                                Intent monitor = new Intent();
                                monitor.setClass(MainListAdapter.this.context, CallActivity.class);
                                monitor.putExtra("callId", contact.contactId);
                                monitor.putExtra(ContactDB.COLUMN_CONTACT_NAME, contact.contactName);
                                monitor.putExtra("password", contact.contactPassword);
                                monitor.putExtra("isOutCall", true);
                                monitor.putExtra("type", 1);
                                MainListAdapter.this.context.startActivity(monitor);
                            }
                        }
                    }
                });
                holder.getHeader_icon_play().setVisibility(0);
            } else if (deviceType == 3) {
                holder.getHead().setOnClickListener(new OnClickListener() {
                    public void onClick(View arg0) {
                        if (contact.contactId == null || contact.contactId.equals("")) {
                            C0568T.showShort(MainListAdapter.this.context, (int) C0291R.string.username_error);
                            return;
                        }
                        Intent call = new Intent();
                        call.setClass(MainListAdapter.this.context, CallActivity.class);
                        call.putExtra("callId", contact.contactId);
                        call.putExtra("isOutCall", true);
                        call.putExtra("type", 0);
                        MainListAdapter.this.context.startActivity(call);
                    }
                });
                holder.getHeader_icon_play().setVisibility(0);
            } else if (Integer.parseInt(contact.contactId) < 256) {
                holder.getHead().setOnClickListener(new OnClickListener() {
                    public void onClick(View arg0) {
                        Intent monitor = new Intent();
                        monitor.setClass(MainListAdapter.this.context, CallActivity.class);
                        monitor.putExtra("callId", contact.contactId);
                        monitor.putExtra(ContactDB.COLUMN_CONTACT_NAME, contact.contactName);
                        monitor.putExtra("password", contact.contactPassword);
                        monitor.putExtra("isOutCall", true);
                        monitor.putExtra("type", 1);
                        MainListAdapter.this.context.startActivity(monitor);
                    }
                });
            } else {
                holder.getHead().setOnClickListener(null);
                holder.getHeader_icon_play().setVisibility(8);
            }
            holder.getLayout_defence_btn().setOnClickListener(new OnClickListener() {
                public void onClick(View arg0) {
                    if (contact.defenceState == 4 || contact.defenceState == 3) {
                        holder.getProgress_defence().setVisibility(0);
                        holder.getImage_defence_state().setVisibility(4);
                        P2PHandler.getInstance().getDefenceStates(contact.contactModel, contact.contactId, contact.contactPassword);
                        FList.getInstance().setIsClickGetDefenceState(contact.contactId, true);
                    } else if (contact.defenceState == 1) {
                        holder.getProgress_defence().setVisibility(0);
                        holder.getImage_defence_state().setVisibility(4);
                        P2PHandler.getInstance().setRemoteDefence(contact.contactModel, contact.contactId, contact.contactPassword, 0);
                        FList.getInstance().setIsClickGetDefenceState(contact.contactId, true);
                    } else if (contact.defenceState == 0) {
                        holder.getProgress_defence().setVisibility(0);
                        holder.getImage_defence_state().setVisibility(4);
                        P2PHandler.getInstance().setRemoteDefence(contact.contactModel, contact.contactId, contact.contactPassword, 1);
                        FList.getInstance().setIsClickGetDefenceState(contact.contactId, true);
                    }
                }
            });
            holder.getPlayback().setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    Intent playback = new Intent();
                    playback.setClass(MainListAdapter.this.context, PlayBackListActivity.class);
                    playback.putExtra(ContactDB.TABLE_NAME, contact);
                    MainListAdapter.this.context.startActivity(playback);
                }
            });
            holder.getSetting().setOnClickListener(new OnClickListener() {

                class C04881 implements OnCancelListener {
                    C04881() {
                    }

                    public void onCancel(DialogInterface arg0) {
                        MainListAdapter.this.cf.isCancelLoading = true;
                    }
                }

                public void onClick(View v) {
                    Log.e("settingONk", "settingONk");
                    if (contact.contactId == null || contact.contactId.equals("")) {
                        C0568T.showShort(MainListAdapter.this.context, (int) C0291R.string.username_error);
                    } else if (contact.contactPassword == null || contact.contactPassword.equals("")) {
                        C0568T.showShort(MainListAdapter.this.context, (int) C0291R.string.password_error);
                    } else {
                        MainListAdapter.this.cf.next_contact = contact;
                        MainListAdapter.this.cf.dialog = new NormalDialog(MainListAdapter.this.context);
                        MainListAdapter.this.cf.dialog.setOnCancelListener(new C04881());
                        MainListAdapter.this.cf.dialog.showLoadingDialog2();
                        MainListAdapter.this.cf.dialog.setCanceledOnTouchOutside(false);
                        MainListAdapter.this.cf.isCancelLoading = false;
                        MainListAdapter.this.cf.isMyself = true;
                        P2PHandler.getInstance().checkPassword(contact.contactModel, contact.contactId, contact.contactPassword);
                    }
                }
            });
            final int i = position;
            view.setOnLongClickListener(new OnLongClickListener() {

                class C10971 implements OnButtonOkListener {
                    C10971() {
                    }

                    public void onClick() {
                        FList.getInstance().delete(contact, i, MainListAdapter.this.handler);
                        Utils.deleteFile(new File(Image.USER_HEADER_PATH + NpcCommon.mThreeNum + "/" + contact.contactId));
                    }
                }

                public boolean onLongClick(View arg0) {
                    NormalDialog dialog = new NormalDialog(MainListAdapter.this.context, MainListAdapter.this.context.getResources().getString(C0291R.string.delete_contact), MainListAdapter.this.context.getResources().getString(C0291R.string.are_you_sure_delete) + " " + contact.contactId + "?", MainListAdapter.this.context.getResources().getString(C0291R.string.delete), MainListAdapter.this.context.getResources().getString(C0291R.string.cancel));
                    dialog.setOnButtonOkListener(new C10971());
                    dialog.showDialog();
                    return true;
                }
            });
            return view;
        }
        ViewHolder2 holder2;
        view = convertView;
        if (view == null) {
            view = LayoutInflater.from(this.context).inflate(C0291R.layout.list_contact_item2, null);
            holder2 = new ViewHolder2();
            holder2.setName((TextView) view.findViewById(C0291R.id.user_name));
            holder2.setDevice_type((ImageView) view.findViewById(C0291R.id.login_type));
            view.setTag(holder2);
        } else {
            holder2 = (ViewHolder2) view.getTag();
        }
        final LocalDevice localDevice = (LocalDevice) FList.getInstance().getUnsetPasswordLocalDevices().get(position - size1);
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
        view.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Contact saveContact = new Contact();
                saveContact.contactId = localDevice.contactId;
                saveContact.contactType = localDevice.type;
                saveContact.messageCount = 0;
                saveContact.activeUser = NpcCommon.mThreeNum;
                Intent modify = new Intent();
                modify.setClass(MainListAdapter.this.context, AddContactNextActivity.class);
                modify.putExtra("isCreatePassword", true);
                modify.putExtra(ContactDB.TABLE_NAME, saveContact);
                String mark = localDevice.address.getHostAddress();
                modify.putExtra("ipFlag", mark.substring(mark.lastIndexOf(".") + 1, mark.length()));
                MainListAdapter.this.context.startActivity(modify);
            }
        });
        return view;
    }

    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}
