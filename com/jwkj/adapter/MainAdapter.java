package com.jwkj.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.hikam.C0291R;
import com.jwkj.CallActivity;
import com.jwkj.activity.AddContactNextActivity;
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

public class MainAdapter extends BaseAdapter {
    private ContactFrag cf;
    Context context;
    Handler handler = new Handler(new C04818());

    class C04818 implements Callback {
        C04818() {
        }

        public boolean handleMessage(Message msg) {
            MainAdapter.this.notifyDataSetChanged();
            return true;
        }
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
        private HeaderView head;
        private ImageView header_icon_play;
        private ImageView image_defence_state;
        private RelativeLayout layout_defence_btn;
        private ImageView login_type;
        private TextView msgCount;
        private TextView name;
        private TextView online_state;
        private ProgressBar progress_defence;

        ViewHolder() {
        }

        public TextView getMsgCount() {
            return this.msgCount;
        }

        public void setMsgCount(TextView msgCount) {
            this.msgCount = msgCount;
        }

        public ImageView getLogin_type() {
            return this.login_type;
        }

        public void setLogin_type(ImageView login_type) {
            this.login_type = login_type;
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
    }

    public MainAdapter(Context context, ContactFrag cf) {
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
                view = LayoutInflater.from(this.context).inflate(C0291R.layout.list_contact_item, null);
                holder = new ViewHolder();
                holder.setHead((HeaderView) view.findViewById(C0291R.id.user_icon));
                holder.setName((TextView) view.findViewById(C0291R.id.user_name));
                holder.setOnline_state((TextView) view.findViewById(C0291R.id.online_state));
                holder.setLogin_type((ImageView) view.findViewById(C0291R.id.login_type));
                holder.setMsgCount((TextView) view.findViewById(C0291R.id.msgCount));
                holder.setHeader_icon_play((ImageView) view.findViewById(C0291R.id.header_icon_play));
                holder.setLayout_defence_btn((RelativeLayout) view.findViewById(C0291R.id.layout_defence_btn));
                holder.setImage_defence_state((ImageView) view.findViewById(C0291R.id.image_defence_state));
                holder.setProgress_defence((ProgressBar) view.findViewById(C0291R.id.progress_defence));
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
                        holder.getImage_defence_state().setVisibility(8);
                    } else if (contact.defenceState == 1) {
                        holder.getProgress_defence().setVisibility(8);
                        holder.getImage_defence_state().setVisibility(0);
                        holder.getImage_defence_state().setImageResource(C0291R.drawable.ic_defence_on);
                    } else if (contact.defenceState == 0) {
                        holder.getProgress_defence().setVisibility(8);
                        holder.getImage_defence_state().setVisibility(0);
                        holder.getImage_defence_state().setImageResource(C0291R.drawable.ic_defence_off);
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
            switch (deviceType) {
                case 0:
                    holder.getLogin_type().setImageResource(C0291R.drawable.ic_device_type_unknown);
                    break;
                case 2:
                    holder.getLogin_type().setImageResource(C0291R.drawable.ic_device_type_npc);
                    break;
                case 3:
                    holder.getLogin_type().setImageResource(C0291R.drawable.ic_device_type_phone);
                    break;
                case 5:
                    holder.getLogin_type().setImageResource(C0291R.drawable.ic_device_type_door_bell);
                    break;
                case 7:
                    holder.getLogin_type().setImageResource(C0291R.drawable.ic_device_type_ipc);
                    break;
                default:
                    holder.getLogin_type().setImageResource(C0291R.drawable.ic_device_type_unknown);
                    break;
            }
            if (contact.messageCount > 0) {
                TextView msgCount = holder.getMsgCount();
                msgCount.setVisibility(0);
                if (contact.messageCount > 10) {
                    msgCount.setText("10+");
                } else {
                    msgCount.setText(contact.messageCount + "");
                }
            } else {
                holder.getMsgCount().setVisibility(8);
            }
            holder.getName().setText(contact.contactName);
            if (deviceType == 2 || deviceType == 7 || deviceType == 5) {
                holder.getHead().setOnClickListener(new OnClickListener() {
                    public void onClick(View arg0) {
                        if (FList.getInstance().isContactUnSetPassword(contact.contactId) == null) {
                            if (contact.contactId == null || contact.contactId.equals("")) {
                                C0568T.showShort(MainAdapter.this.context, (int) C0291R.string.username_error);
                            } else if (contact.contactPassword == null || contact.contactPassword.equals("")) {
                                C0568T.showShort(MainAdapter.this.context, (int) C0291R.string.password_error);
                            } else {
                                Intent monitor = new Intent();
                                monitor.setClass(MainAdapter.this.context, CallActivity.class);
                                monitor.putExtra("callId", contact.contactId);
                                monitor.putExtra(ContactDB.COLUMN_CONTACT_NAME, contact.contactName);
                                monitor.putExtra("password", contact.contactPassword);
                                monitor.putExtra("isOutCall", true);
                                monitor.putExtra("type", 1);
                                MainAdapter.this.context.startActivity(monitor);
                            }
                        }
                    }
                });
                holder.getHeader_icon_play().setVisibility(0);
            } else if (deviceType == 3) {
                holder.getHead().setOnClickListener(new OnClickListener() {
                    public void onClick(View arg0) {
                        if (contact.contactId == null || contact.contactId.equals("")) {
                            C0568T.showShort(MainAdapter.this.context, (int) C0291R.string.username_error);
                            return;
                        }
                        Intent call = new Intent();
                        call.setClass(MainAdapter.this.context, CallActivity.class);
                        call.putExtra("callId", contact.contactId);
                        call.putExtra("isOutCall", true);
                        call.putExtra("type", 0);
                        MainAdapter.this.context.startActivity(call);
                    }
                });
                holder.getHeader_icon_play().setVisibility(0);
            } else if (Integer.parseInt(contact.contactId) < 256) {
                holder.getHead().setOnClickListener(new OnClickListener() {
                    public void onClick(View arg0) {
                        Intent monitor = new Intent();
                        monitor.setClass(MainAdapter.this.context, CallActivity.class);
                        monitor.putExtra("callId", contact.contactId);
                        monitor.putExtra(ContactDB.COLUMN_CONTACT_NAME, contact.contactName);
                        monitor.putExtra("password", contact.contactPassword);
                        monitor.putExtra("isOutCall", true);
                        monitor.putExtra("type", 1);
                        MainAdapter.this.context.startActivity(monitor);
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
                        holder.getImage_defence_state().setVisibility(8);
                        P2PHandler.getInstance().getDefenceStates(contact.contactModel, contact.contactId, contact.contactPassword);
                        FList.getInstance().setIsClickGetDefenceState(contact.contactId, true);
                    } else if (contact.defenceState == 1) {
                        holder.getProgress_defence().setVisibility(0);
                        holder.getImage_defence_state().setVisibility(8);
                        P2PHandler.getInstance().setRemoteDefence(contact.contactModel, contact.contactId, contact.contactPassword, 0);
                        FList.getInstance().setIsClickGetDefenceState(contact.contactId, true);
                    } else if (contact.defenceState == 0) {
                        holder.getProgress_defence().setVisibility(0);
                        holder.getImage_defence_state().setVisibility(8);
                        P2PHandler.getInstance().setRemoteDefence(contact.contactModel, contact.contactId, contact.contactPassword, 1);
                        FList.getInstance().setIsClickGetDefenceState(contact.contactId, true);
                    }
                }
            });
            view.setOnClickListener(new OnClickListener() {
                public void onClick(View arg0) {
                    LocalDevice localDevice = FList.getInstance().isContactUnSetPassword(contact.contactId);
                    if (localDevice != null) {
                        Contact saveContact = new Contact();
                        saveContact.contactId = localDevice.contactId;
                        saveContact.contactType = localDevice.type;
                        saveContact.messageCount = 0;
                        saveContact.activeUser = NpcCommon.mThreeNum;
                        Intent modify = new Intent();
                        modify.setClass(MainAdapter.this.context, AddContactNextActivity.class);
                        modify.putExtra("isCreatePassword", true);
                        modify.putExtra(ContactDB.TABLE_NAME, saveContact);
                        String mark = localDevice.address.getHostAddress();
                        modify.putExtra("ipFlag", mark.substring(mark.lastIndexOf(".") + 1, mark.length()));
                        MainAdapter.this.context.startActivity(modify);
                        return;
                    }
                    MainAdapter.this.cf.showQuickActionBar(arg0, contact);
                }
            });
            final int i = position;
            view.setOnLongClickListener(new OnLongClickListener() {

                class C10961 implements OnButtonOkListener {
                    C10961() {
                    }

                    public void onClick() {
                        FList.getInstance().delete(contact, i, MainAdapter.this.handler);
                        Utils.deleteFile(new File(Image.USER_HEADER_PATH + NpcCommon.mThreeNum + "/" + contact.contactId));
                    }
                }

                public boolean onLongClick(View arg0) {
                    NormalDialog dialog = new NormalDialog(MainAdapter.this.context, MainAdapter.this.context.getResources().getString(C0291R.string.delete_contact), MainAdapter.this.context.getResources().getString(C0291R.string.are_you_sure_delete) + " " + contact.contactId + "?", MainAdapter.this.context.getResources().getString(C0291R.string.delete), MainAdapter.this.context.getResources().getString(C0291R.string.cancel));
                    dialog.setOnButtonOkListener(new C10961());
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
                modify.setClass(MainAdapter.this.context, AddContactNextActivity.class);
                modify.putExtra("isCreatePassword", true);
                modify.putExtra(ContactDB.TABLE_NAME, saveContact);
                String mark = localDevice.address.getHostAddress();
                modify.putExtra("ipFlag", mark.substring(mark.lastIndexOf(".") + 1, mark.length()));
                MainAdapter.this.context.startActivity(modify);
            }
        });
        return view;
    }

    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}
