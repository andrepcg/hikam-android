package com.jwkj.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.media.TransportMediator;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.hikam.C0291R;
import com.jwkj.CallActivity;
import com.jwkj.PlayBackListActivity;
import com.jwkj.activity.LocalDeviceListActivity;
import com.jwkj.activity.MainActivity;
import com.jwkj.activity.MainControlActivity;
import com.jwkj.activity.MessageActivity;
import com.jwkj.activity.ModifyContactActivity;
import com.jwkj.adapter.MainSwipeAdapter;
import com.jwkj.adapter.MainSwipeAdapter.OnCheckVersionBeforeOpen;
import com.jwkj.data.Contact;
import com.jwkj.data.ContactDB;
import com.jwkj.entity.LocalDevice;
import com.jwkj.global.Constants.Action;
import com.jwkj.global.Constants.P2P;
import com.jwkj.global.FList;
import com.jwkj.global.NpcCommon;
import com.jwkj.thread.CoverThread;
import com.jwkj.thread.MainThread;
import com.jwkj.thread.PingBingThread;
import com.jwkj.utils.C0568T;
import com.jwkj.utils.Utils;
import com.jwkj.widget.NormalDialog;
import com.jwkj.widget.NormalDialog.OnButtonCancelListener;
import com.jwkj.widget.NormalDialog.OnButtonOkListener;
import com.jwkj.widget.guide.IndexGuideActivity;
import com.lib.pullToRefresh.PullToRefreshBase;
import com.lib.pullToRefresh.PullToRefreshBase.OnRefreshListener;
import com.lib.pullToRefresh.PullToRefreshListView;
import com.lib.quick_action_bar.QuickAction;
import com.lib.quick_action_bar.QuickActionBar;
import com.lib.quick_action_bar.QuickActionWidget;
import com.lib.quick_action_bar.QuickActionWidget.OnQuickActionClickListener;
import com.p2p.core.P2PHandler;
import com.p2p.core.P2PValue.HikamDeviceModel;
import java.util.List;
import org.apache.http.cookie.ClientCookie;

public class ContactFrag extends BaseFragment implements OnClickListener {
    public static final int CHANGE_REFRESHING_LABLE = 18;
    private Contact checkedContact;
    public NormalDialog dialog;
    private Handler handler = new Handler(new C05111());
    boolean isActive;
    public boolean isCancelLoading;
    boolean isFirstRefresh = true;
    public boolean isMyself = false;
    boolean isOpenThread = false;
    private boolean isRegFilter = false;
    private RelativeLayout local_device_bar_top;
    private MainSwipeAdapter mAdapter;
    private ImageView mAddUser;
    private QuickActionWidget mBar;
    private Context mContext;
    private Handler mHandler = new Handler(new C05148());
    private ListView mListView;
    private PullToRefreshListView mPullRefreshListView;
    BroadcastReceiver mReceiver = new C05137();
    private LinearLayout net_work_status_bar;
    public Contact next_contact;
    boolean refreshEnd = false;
    private TextView text_local_device_count;

    class C05111 implements Callback {
        C05111() {
        }

        public boolean handleMessage(Message msg) {
            int what = msg.what;
            if (what == 0) {
                ContactFrag.this.dialog.dismiss();
                ContactFrag.this.showUpdate();
            } else if (what == 1) {
                ContactFrag.this.dialog.dismiss();
                ContactFrag.this.startMonitorAfterCheck(ContactFrag.this.checkedContact);
            } else if (what == 18) {
                String str = (String) msg.obj;
            }
            return true;
        }
    }

    class C05124 implements OnClickListener {
        C05124() {
        }

        public void onClick(View arg0) {
            ContactFrag.this.mContext.startActivity(new Intent(ContactFrag.this.mContext, LocalDeviceListActivity.class));
        }
    }

    class C05137 extends BroadcastReceiver {
        C05137() {
        }

        public void onReceive(Context context, Intent intent) {
            List<LocalDevice> localDevices;
            if (intent.getAction().equals(Action.REFRESH_CONTANTS)) {
                ContactFrag.this.mAdapter.notifyDataSetChanged();
                localDevices = FList.getInstance().getSetPasswordLocalDevices();
                if (localDevices.size() > 0) {
                    ContactFrag.this.local_device_bar_top.setVisibility(0);
                    ContactFrag.this.text_local_device_count.setText("" + localDevices.size());
                    return;
                }
                ContactFrag.this.local_device_bar_top.setVisibility(8);
            } else if (intent.getAction().equals(Action.GET_FRIENDS_STATE)) {
                ContactFrag.this.mAdapter.notifyDataSetChanged();
                ContactFrag.this.refreshEnd = true;
            } else if (intent.getAction().equals(Action.LOCAL_DEVICE_SEARCH_END)) {
                localDevices = FList.getInstance().getSetPasswordLocalDevices();
                if (localDevices.size() > 0) {
                    ContactFrag.this.local_device_bar_top.setVisibility(0);
                    ContactFrag.this.text_local_device_count.setText("" + localDevices.size());
                } else {
                    ContactFrag.this.local_device_bar_top.setVisibility(8);
                }
                Log.e("my", "" + localDevices.size());
            } else if (intent.getAction().equals(Action.ACTION_NETWORK_CHANGE)) {
                NetworkInfo activeNetInfo = ((ConnectivityManager) ContactFrag.this.mContext.getSystemService("connectivity")).getActiveNetworkInfo();
                if (activeNetInfo == null) {
                    C0568T.showShort(ContactFrag.this.mContext, (int) C0291R.string.network_error);
                    ContactFrag.this.net_work_status_bar.setVisibility(0);
                } else if (activeNetInfo.isConnected()) {
                    ContactFrag.this.net_work_status_bar.setVisibility(8);
                } else {
                    C0568T.showShort(ContactFrag.this.mContext, ContactFrag.this.getString(C0291R.string.network_error) + " " + activeNetInfo.getTypeName());
                    ContactFrag.this.net_work_status_bar.setVisibility(0);
                }
            } else if (intent.getAction().equals(Action.ACTION_NETWORK_PING_FAILED)) {
                ContactFrag.this.net_work_status_bar.setVisibility(0);
            } else if (intent.getAction().equals(Action.ACTION_NETWORK_PING_SUCCESS)) {
                ContactFrag.this.net_work_status_bar.setVisibility(8);
            } else if (intent.getAction().equals(P2P.ACK_RET_CHECK_PASSWORD)) {
                if (ContactFrag.this.isActive && ContactFrag.this.isMyself) {
                    int result = intent.getIntExtra("result", -1);
                    ContactFrag.this.isMyself = false;
                    if (!ContactFrag.this.isCancelLoading) {
                        Intent control;
                        if (result == 9997) {
                            if (ContactFrag.this.dialog != null && ContactFrag.this.dialog.isShowing()) {
                                ContactFrag.this.dialog.dismiss();
                                ContactFrag.this.dialog = null;
                            }
                            control = new Intent();
                            control.setClass(ContactFrag.this.mContext, MainControlActivity.class);
                            control.putExtra(ContactDB.TABLE_NAME, ContactFrag.this.next_contact);
                            control.putExtra("type", 2);
                            ContactFrag.this.mContext.startActivity(control);
                        } else if (result == 9999) {
                            if (ContactFrag.this.dialog != null && ContactFrag.this.dialog.isShowing()) {
                                ContactFrag.this.dialog.dismiss();
                                ContactFrag.this.dialog = null;
                            }
                            control = new Intent();
                            control.setClass(ContactFrag.this.mContext, MainControlActivity.class);
                            control.putExtra(ContactDB.TABLE_NAME, ContactFrag.this.next_contact);
                            control.putExtra("type", 2);
                            control.putExtra("wrongPwd", true);
                            ContactFrag.this.mContext.startActivity(control);
                            C0568T.showLong(ContactFrag.this.mContext, (int) C0291R.string.device_password_error);
                        } else if (result == 9998) {
                            P2PHandler.getInstance().checkPassword(ContactFrag.this.next_contact.contactModel, ContactFrag.this.next_contact.contactId, ContactFrag.this.next_contact.contactPassword);
                            ContactFrag.this.isMyself = true;
                        } else if (result == 9996) {
                            if (ContactFrag.this.dialog != null && ContactFrag.this.dialog.isShowing()) {
                                ContactFrag.this.dialog.dismiss();
                                ContactFrag.this.dialog = null;
                            }
                            C0568T.showShort(ContactFrag.this.mContext, (int) C0291R.string.insufficient_permissions);
                        }
                    }
                }
            } else if (intent.getAction().equals(P2P.RET_GET_REMOTE_DEFENCE)) {
                int state = intent.getIntExtra("state", -1);
                String contactId = intent.getStringExtra(ContactDB.COLUMN_CONTACT_ID);
                Contact contact = FList.getInstance().isContact(contactId);
                if (state == 4) {
                    if (contact != null && contact.isClickGetDefenceState) {
                        C0568T.showShort(ContactFrag.this.mContext, (int) C0291R.string.net_error);
                    }
                } else if (state == 3 && contact != null && contact.isClickGetDefenceState) {
                    C0568T.showLong(ContactFrag.this.mContext, (int) C0291R.string.device_password_error);
                }
                if (contact != null && contact.isClickGetDefenceState) {
                    FList.getInstance().setIsClickGetDefenceState(contactId, false);
                }
                ContactFrag.this.mAdapter.notifyDataSetChanged();
            } else if (intent.getAction().equals(Action.SETTING_WIFI_SUCCESS)) {
                FList flist = FList.getInstance();
                flist.updateOnlineState();
                flist.searchLocalDevice();
                flist.updataCameraCover();
            } else if (intent.getAction().equals(P2P.RET_GET_DEVICE_INFO2)) {
                String cur_version = intent.getStringExtra("cur_version");
                Log.e("few", cur_version + ClientCookie.VERSION_ATTR);
                String[] tab = cur_version.split("\\.");
                Log.e("few", "" + tab.length);
                int version = ((Integer.valueOf(tab[0]).intValue() * 100) + (Integer.valueOf(tab[1]).intValue() * 10)) + Integer.valueOf(tab[2].substring(0, 1)).intValue();
                if (ContactFrag.this.checkedContact != null && ContactFrag.this.getUserVisibleHint()) {
                    String str = ContactFrag.this.checkedContact.contactModel;
                    Object obj = -1;
                    switch (str.hashCode()) {
                        case -352293907:
                            if (str.equals(HikamDeviceModel.Q3)) {
                                obj = 1;
                                break;
                            }
                            break;
                        case -352293905:
                            if (str.equals(HikamDeviceModel.Q5)) {
                                obj = null;
                                break;
                            }
                            break;
                    }
                    switch (obj) {
                        case null:
                            if (version >= 110) {
                                ContactFrag.this.handler.sendEmptyMessage(0);
                                return;
                            } else {
                                ContactFrag.this.handler.sendEmptyMessage(1);
                                return;
                            }
                        case 1:
                            if (version >= TransportMediator.KEYCODE_MEDIA_RECORD) {
                                ContactFrag.this.handler.sendEmptyMessage(0);
                                return;
                            } else {
                                ContactFrag.this.handler.sendEmptyMessage(1);
                                return;
                            }
                        default:
                            ContactFrag.this.handler.sendEmptyMessage(1);
                            return;
                    }
                }
            }
        }
    }

    class C05148 implements Callback {
        C05148() {
        }

        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 18:
                    String str = (String) msg.obj;
                    break;
            }
            return false;
        }
    }

    private class GetDataTask extends AsyncTask<Void, Void, String[]> {
        private GetDataTask() {
        }

        protected String[] doInBackground(Void... params) {
            Log.e("my", "doInBackground");
            FList flist = FList.getInstance();
            flist.searchLocalDevice();
            if (flist.size() != 0) {
                ContactFrag.this.refreshEnd = false;
                flist.updateOnlineState();
                while (!ContactFrag.this.refreshEnd) {
                    Utils.sleepThread(1000);
                }
                Message msg = new Message();
                msg.what = 18;
                msg.obj = ContactFrag.this.mContext.getResources().getString(C0291R.string.pull_to_refresh_refreshing_success_label);
                ContactFrag.this.mHandler.sendMessage(msg);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        protected void onPostExecute(String[] result) {
            ContactFrag.this.mPullRefreshListView.onRefreshComplete();
            super.onPostExecute(result);
        }
    }

    class C11042 implements OnButtonOkListener {
        C11042() {
        }

        public void onClick() {
            ContactFrag.this.dialog.dismiss();
            try {
                Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=com.hikam"));
                intent.addFlags(268435456);
                ContactFrag.this.startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(ContactFrag.this.getActivity(), "could not find market in your phone.", 0).show();
                e.printStackTrace();
            }
        }
    }

    class C11053 implements OnButtonCancelListener {
        C11053() {
        }

        public void onClick() {
            ContactFrag.this.dialog.dismiss();
        }
    }

    class C11065 implements OnRefreshListener<ListView> {
        C11065() {
        }

        public void onRefresh(PullToRefreshBase<ListView> refreshView) {
            refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(DateUtils.formatDateTime(ContactFrag.this.mContext, System.currentTimeMillis(), 524305));
            new GetDataTask().execute(new Void[0]);
        }
    }

    class C11076 implements OnCheckVersionBeforeOpen {
        C11076() {
        }

        public void onCheckContact(Contact contact) {
            if (HikamDeviceModel.Q5.equals(contact.contactModel) || HikamDeviceModel.Q3.equals(contact.contactModel)) {
                ContactFrag.this.checkedContact = contact;
                P2PHandler.getInstance().getDeviceVersion(contact.contactModel, contact.contactId, contact.contactPassword);
                ContactFrag.this.dialog = new NormalDialog(ContactFrag.this.getContext());
                ContactFrag.this.dialog.showLoadingDialog();
                return;
            }
            ContactFrag.this.startMonitorAfterCheck(contact);
        }
    }

    public void showUpdate() {
        this.dialog = new NormalDialog(getActivity(), "Update tips!", getString(C0291R.string.app_update_tips), "update app", "cancle");
        this.dialog.setOnButtonOkListener(new C11042());
        this.dialog.setOnButtonCancelListener(new C11053());
        this.dialog.showNormalDialog();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        boolean z = false;
        View view = inflater.inflate(C0291R.layout.fragment_contact, container, false);
        this.mContext = MainActivity.mContext;
        Log.e("my", "createContactFrag");
        initComponent(view);
        regFilter();
        if (this.isFirstRefresh) {
            if (!this.isFirstRefresh) {
                z = true;
            }
            this.isFirstRefresh = z;
            FList flist = FList.getInstance();
            flist.updateOnlineState();
            flist.searchLocalDevice();
            flist.updataCameraCover();
        }
        return view;
    }

    public void initComponent(View view) {
        this.mAddUser = (ImageView) view.findViewById(C0291R.id.button_add);
        this.net_work_status_bar = (LinearLayout) view.findViewById(C0291R.id.net_status_bar_top);
        this.local_device_bar_top = (RelativeLayout) view.findViewById(C0291R.id.local_device_bar_top);
        this.text_local_device_count = (TextView) view.findViewById(C0291R.id.text_local_device_count);
        this.mPullRefreshListView = (PullToRefreshListView) view.findViewById(C0291R.id.pull_refresh_list);
        this.mPullRefreshListView.setEmptyView(view.findViewById(C0291R.id.empty));
        this.local_device_bar_top.setOnClickListener(new C05124());
        this.mPullRefreshListView.setOnRefreshListener(new C11065());
        this.mPullRefreshListView.setShowIndicator(false);
        this.mListView = (ListView) this.mPullRefreshListView.getRefreshableView();
        this.mAdapter = new MainSwipeAdapter(this.mContext, this);
        this.mAdapter.setOnCheckVersionBeforeOpen(new C11076());
        this.mListView.setAdapter(this.mAdapter);
        this.mAddUser.setOnClickListener(this);
        List<LocalDevice> localDevices = FList.getInstance().getSetPasswordLocalDevices();
        if (localDevices.size() > 0) {
            this.local_device_bar_top.setVisibility(0);
            this.text_local_device_count.setText("" + localDevices.size());
            return;
        }
        this.local_device_bar_top.setVisibility(8);
    }

    public void startMonitorAfterCheck(Contact contact) {
        if (FList.getInstance().isContactUnSetPassword(contact.contactId) == null) {
            if (contact.onLineState == 0) {
                C0568T.showShort(getActivity(), (int) C0291R.string.offline);
            } else if (contact.contactId == null || contact.contactId.equals("")) {
                C0568T.showShort(getActivity(), (int) C0291R.string.username_error);
            } else if (contact.contactPassword == null || contact.contactPassword.equals("")) {
                C0568T.showShort(getActivity(), (int) C0291R.string.password_error);
            } else {
                if (HikamDeviceModel.Q5.equals(contact.contactModel) || HikamDeviceModel.Q3.equals(contact.contactModel)) {
                    P2PHandler.getInstance().getDeviceVersion(contact.contactModel, contact.contactId, contact.contactPassword);
                }
                Intent monitor = new Intent();
                monitor.setClass(getActivity(), CallActivity.class);
                monitor.putExtra("callModel", contact.contactModel);
                monitor.putExtra("callId", contact.contactId);
                monitor.putExtra(ContactDB.COLUMN_CONTACT_NAME, contact.contactName);
                monitor.putExtra("password", contact.contactPassword);
                monitor.putExtra("isOutCall", true);
                monitor.putExtra("type", 1);
                Log.e("oaosj", "call: " + contact.contactModel + " " + contact.contactId + " " + contact.contactName + " " + contact.contactPassword + " " + true + " " + 1);
                getActivity().startActivity(monitor);
            }
        }
    }

    public void regFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Action.REFRESH_CONTANTS);
        filter.addAction(Action.GET_FRIENDS_STATE);
        filter.addAction(Action.LOCAL_DEVICE_SEARCH_END);
        filter.addAction(Action.ACTION_NETWORK_CHANGE);
        filter.addAction(Action.ACTION_NETWORK_PING_FAILED);
        filter.addAction(Action.ACTION_NETWORK_PING_SUCCESS);
        filter.addAction(P2P.ACK_RET_CHECK_PASSWORD);
        filter.addAction(P2P.RET_GET_REMOTE_DEFENCE);
        filter.addAction(Action.SETTING_WIFI_SUCCESS);
        filter.addAction(P2P.RET_GET_DEVICE_INFO2);
        this.mContext.registerReceiver(this.mReceiver, filter);
        this.isRegFilter = true;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case C0291R.id.button_add:
                this.mContext.startActivity(new Intent(this.mContext, IndexGuideActivity.class));
                return;
            default:
                return;
        }
    }

    public void showQuickActionBar(View view, Contact contact) {
        if (contact.contactId != null && !contact.contactId.equals("")) {
            String type = contact.contactId.substring(0, 1);
            if (contact.contactType == 3) {
                showQuickActionBar_phone(view.findViewById(C0291R.id.user_icon), contact);
            } else if (contact.contactType == 2) {
                showQuickActionBar_npc(view.findViewById(C0291R.id.user_icon), contact);
            } else if (contact.contactType == 7) {
                showQuickActionBar_ipc(view.findViewById(C0291R.id.user_icon), contact);
            } else if (contact.contactType == 5) {
                showQuickActionBar_doorBell(view.findViewById(C0291R.id.user_icon), contact);
            } else if (Integer.parseInt(contact.contactId) < 256) {
                showQuickActionBar_ipc(view.findViewById(C0291R.id.user_icon), contact);
            } else {
                showQuickActionBar_unknwon(view.findViewById(C0291R.id.user_icon), contact);
            }
        }
    }

    private void showQuickActionBar_phone(View view, final Contact contact) {
        this.mBar = new QuickActionBar(getActivity());
        this.mBar.addQuickAction(new QuickAction(getActivity(), (int) C0291R.drawable.ic_action_message_pressed, (int) C0291R.string.message));
        this.mBar.addQuickAction(new QuickAction(getActivity(), (int) C0291R.drawable.ic_action_modify_pressed, (int) C0291R.string.edit));
        this.mBar.setOnQuickActionClickListener(new OnQuickActionClickListener() {
            public void onQuickActionClicked(QuickActionWidget widget, int position) {
                switch (position) {
                    case 0:
                        Intent goMessage = new Intent(ContactFrag.this.mContext, MessageActivity.class);
                        goMessage.putExtra(ContactDB.TABLE_NAME, contact);
                        ContactFrag.this.startActivity(goMessage);
                        return;
                    case 1:
                        Intent modify = new Intent();
                        modify.setClass(ContactFrag.this.mContext, ModifyContactActivity.class);
                        modify.putExtra(ContactDB.TABLE_NAME, contact);
                        ContactFrag.this.mContext.startActivity(modify);
                        return;
                    default:
                        return;
                }
            }
        });
        this.mBar.show(view);
    }

    private void showQuickActionBar_npc(View view, final Contact contact) {
        this.mBar = new QuickActionBar(getActivity());
        if (NpcCommon.mThreeNum.equals("517400")) {
            this.mBar.addQuickAction(new QuickAction(getActivity(), (int) C0291R.drawable.ic_action_playback_pressed, (int) C0291R.string.playback));
            this.mBar.addQuickAction(new QuickAction(getActivity(), (int) C0291R.drawable.ic_action_control_pressed, (int) C0291R.string.control));
            this.mBar.addQuickAction(new QuickAction(getActivity(), (int) C0291R.drawable.ic_action_modify_pressed, (int) C0291R.string.edit));
            this.mBar.setOnQuickActionClickListener(new OnQuickActionClickListener() {

                class C05071 implements OnCancelListener {
                    C05071() {
                    }

                    public void onCancel(DialogInterface arg0) {
                        ContactFrag.this.isCancelLoading = true;
                    }
                }

                public void onQuickActionClicked(QuickActionWidget widget, int position) {
                    switch (position) {
                        case 0:
                            Intent playback = new Intent();
                            playback.setClass(ContactFrag.this.mContext, PlayBackListActivity.class);
                            playback.putExtra(ContactDB.TABLE_NAME, contact);
                            ContactFrag.this.mContext.startActivity(playback);
                            return;
                        case 1:
                            if (contact.contactId == null || contact.contactId.equals("")) {
                                C0568T.showShort(ContactFrag.this.mContext, (int) C0291R.string.username_error);
                                return;
                            } else if (contact.contactPassword == null || contact.contactPassword.equals("")) {
                                C0568T.showShort(ContactFrag.this.mContext, (int) C0291R.string.password_error);
                                return;
                            } else {
                                ContactFrag.this.next_contact = contact;
                                ContactFrag.this.dialog = new NormalDialog(ContactFrag.this.mContext);
                                ContactFrag.this.dialog.setOnCancelListener(new C05071());
                                ContactFrag.this.dialog.showLoadingDialog2();
                                ContactFrag.this.dialog.setCanceledOnTouchOutside(false);
                                ContactFrag.this.isCancelLoading = false;
                                P2PHandler.getInstance().checkPassword(contact.contactModel, contact.contactId, contact.contactPassword);
                                ContactFrag.this.isMyself = true;
                                return;
                            }
                        case 2:
                            Intent modify = new Intent();
                            modify.setClass(ContactFrag.this.mContext, ModifyContactActivity.class);
                            modify.putExtra(ContactDB.TABLE_NAME, contact);
                            ContactFrag.this.mContext.startActivity(modify);
                            return;
                        default:
                            return;
                    }
                }
            });
            this.mBar.show(view);
            return;
        }
        this.mBar.addQuickAction(new QuickAction(getActivity(), (int) C0291R.drawable.ic_action_call_pressed, (int) C0291R.string.chat));
        this.mBar.addQuickAction(new QuickAction(getActivity(), (int) C0291R.drawable.ic_action_playback_pressed, (int) C0291R.string.playback));
        this.mBar.addQuickAction(new QuickAction(getActivity(), (int) C0291R.drawable.ic_action_control_pressed, (int) C0291R.string.control));
        this.mBar.addQuickAction(new QuickAction(getActivity(), (int) C0291R.drawable.ic_action_modify_pressed, (int) C0291R.string.edit));
        this.mBar.setOnQuickActionClickListener(new OnQuickActionClickListener() {

            class C05081 implements OnCancelListener {
                C05081() {
                }

                public void onCancel(DialogInterface arg0) {
                    ContactFrag.this.isCancelLoading = true;
                }
            }

            public void onQuickActionClicked(QuickActionWidget widget, int position) {
                switch (position) {
                    case 0:
                        if (contact.contactId == null || contact.contactId.equals("")) {
                            C0568T.showShort(ContactFrag.this.mContext, (int) C0291R.string.username_error);
                            return;
                        }
                        Intent call = new Intent();
                        call.setClass(ContactFrag.this.mContext, CallActivity.class);
                        call.putExtra("callId", contact.contactId);
                        call.putExtra(ContactDB.COLUMN_CONTACT_NAME, contact.contactName);
                        call.putExtra("isOutCall", true);
                        call.putExtra("type", 0);
                        ContactFrag.this.startActivity(call);
                        return;
                    case 1:
                        Intent playback = new Intent();
                        playback.setClass(ContactFrag.this.mContext, PlayBackListActivity.class);
                        playback.putExtra(ContactDB.TABLE_NAME, contact);
                        ContactFrag.this.mContext.startActivity(playback);
                        return;
                    case 2:
                        if (contact.contactId == null || contact.contactId.equals("")) {
                            C0568T.showShort(ContactFrag.this.mContext, (int) C0291R.string.username_error);
                            return;
                        } else if (contact.contactPassword == null || contact.contactPassword.equals("")) {
                            C0568T.showShort(ContactFrag.this.mContext, (int) C0291R.string.password_error);
                            return;
                        } else {
                            ContactFrag.this.next_contact = contact;
                            ContactFrag.this.dialog = new NormalDialog(ContactFrag.this.mContext);
                            ContactFrag.this.dialog.setOnCancelListener(new C05081());
                            ContactFrag.this.dialog.showLoadingDialog2();
                            ContactFrag.this.dialog.setCanceledOnTouchOutside(false);
                            ContactFrag.this.isCancelLoading = false;
                            P2PHandler.getInstance().checkPassword(contact.contactModel, contact.contactId, contact.contactPassword);
                            ContactFrag.this.isMyself = true;
                            return;
                        }
                    case 3:
                        Intent modify = new Intent();
                        modify.setClass(ContactFrag.this.mContext, ModifyContactActivity.class);
                        modify.putExtra(ContactDB.TABLE_NAME, contact);
                        ContactFrag.this.mContext.startActivity(modify);
                        return;
                    default:
                        return;
                }
            }
        });
        this.mBar.show(view);
    }

    private void showQuickActionBar_ipc(View view, final Contact contact) {
        this.mBar = new QuickActionBar(getActivity());
        this.mBar.addQuickAction(new QuickAction(getActivity(), (int) C0291R.drawable.ic_action_playback_pressed, (int) C0291R.string.playback));
        this.mBar.addQuickAction(new QuickAction(getActivity(), (int) C0291R.drawable.ic_action_control_pressed, (int) C0291R.string.sets_tab));
        this.mBar.addQuickAction(new QuickAction(getActivity(), (int) C0291R.drawable.ic_action_modify_pressed, (int) C0291R.string.edit));
        this.mBar.setOnQuickActionClickListener(new OnQuickActionClickListener() {

            class C05091 implements OnCancelListener {
                C05091() {
                }

                public void onCancel(DialogInterface arg0) {
                    ContactFrag.this.isCancelLoading = true;
                }
            }

            public void onQuickActionClicked(QuickActionWidget widget, int position) {
                switch (position) {
                    case 0:
                        Intent playback = new Intent();
                        playback.setClass(ContactFrag.this.mContext, PlayBackListActivity.class);
                        playback.putExtra(ContactDB.TABLE_NAME, contact);
                        ContactFrag.this.mContext.startActivity(playback);
                        return;
                    case 1:
                        if (contact.contactId == null || contact.contactId.equals("")) {
                            C0568T.showShort(ContactFrag.this.mContext, (int) C0291R.string.username_error);
                            return;
                        } else if (contact.contactPassword == null || contact.contactPassword.equals("")) {
                            C0568T.showShort(ContactFrag.this.mContext, (int) C0291R.string.password_error);
                            return;
                        } else {
                            ContactFrag.this.next_contact = contact;
                            ContactFrag.this.dialog = new NormalDialog(ContactFrag.this.mContext);
                            ContactFrag.this.dialog.setOnCancelListener(new C05091());
                            ContactFrag.this.dialog.showLoadingDialog2();
                            ContactFrag.this.dialog.setCanceledOnTouchOutside(false);
                            ContactFrag.this.isCancelLoading = false;
                            P2PHandler.getInstance().checkPassword(contact.contactModel, contact.contactId, contact.contactPassword);
                            ContactFrag.this.isMyself = true;
                            return;
                        }
                    case 2:
                        Intent modify = new Intent();
                        modify.setClass(ContactFrag.this.mContext, ModifyContactActivity.class);
                        modify.putExtra(ContactDB.TABLE_NAME, contact);
                        ContactFrag.this.mContext.startActivity(modify);
                        return;
                    default:
                        return;
                }
            }
        });
        this.mBar.show(view);
    }

    private void showQuickActionBar_doorBell(View view, final Contact contact) {
        this.mBar = new QuickActionBar(getActivity());
        this.mBar.addQuickAction(new QuickAction(getActivity(), (int) C0291R.drawable.ic_action_control_pressed, (int) C0291R.string.sets_tab));
        this.mBar.addQuickAction(new QuickAction(getActivity(), (int) C0291R.drawable.ic_action_modify_pressed, (int) C0291R.string.edit));
        this.mBar.setOnQuickActionClickListener(new OnQuickActionClickListener() {

            class C05101 implements OnCancelListener {
                C05101() {
                }

                public void onCancel(DialogInterface arg0) {
                    ContactFrag.this.isCancelLoading = true;
                }
            }

            public void onQuickActionClicked(QuickActionWidget widget, int position) {
                switch (position) {
                    case 0:
                        if (contact.contactId == null || contact.contactId.equals("")) {
                            C0568T.showShort(ContactFrag.this.mContext, (int) C0291R.string.username_error);
                            return;
                        } else if (contact.contactPassword == null || contact.contactPassword.equals("")) {
                            C0568T.showShort(ContactFrag.this.mContext, (int) C0291R.string.password_error);
                            return;
                        } else {
                            ContactFrag.this.next_contact = contact;
                            ContactFrag.this.dialog = new NormalDialog(ContactFrag.this.mContext);
                            ContactFrag.this.dialog.setOnCancelListener(new C05101());
                            ContactFrag.this.dialog.showLoadingDialog2();
                            ContactFrag.this.dialog.setCanceledOnTouchOutside(false);
                            ContactFrag.this.isCancelLoading = false;
                            P2PHandler.getInstance().checkPassword(contact.contactModel, contact.contactId, contact.contactPassword);
                            ContactFrag.this.isMyself = true;
                            return;
                        }
                    case 1:
                        Intent modify = new Intent();
                        modify.setClass(ContactFrag.this.mContext, ModifyContactActivity.class);
                        modify.putExtra(ContactDB.TABLE_NAME, contact);
                        ContactFrag.this.mContext.startActivity(modify);
                        return;
                    default:
                        return;
                }
            }
        });
        this.mBar.show(view);
    }

    private void showQuickActionBar_unknwon(View view, final Contact contact) {
        this.mBar = new QuickActionBar(getActivity());
        this.mBar.addQuickAction(new QuickAction(getActivity(), (int) C0291R.drawable.ic_action_modify_pressed, (int) C0291R.string.edit));
        this.mBar.setOnQuickActionClickListener(new OnQuickActionClickListener() {
            public void onQuickActionClicked(QuickActionWidget widget, int position) {
                switch (position) {
                    case 0:
                        Intent modify = new Intent();
                        modify.setClass(ContactFrag.this.mContext, ModifyContactActivity.class);
                        modify.putExtra(ContactDB.TABLE_NAME, contact);
                        ContactFrag.this.mContext.startActivity(modify);
                        return;
                    default:
                        return;
                }
            }
        });
        this.mBar.show(view);
    }

    public void onPause() {
        PingBingThread.setOpenThread(false);
        MainThread.setOpenThread(false);
        CoverThread.setOpenThread(false);
        super.onPause();
        this.isActive = false;
        if (this.isRegFilter) {
            this.isRegFilter = false;
            this.mContext.unregisterReceiver(this.mReceiver);
        }
    }

    public void onResume() {
        super.onResume();
        PingBingThread.setOpenThread(true);
        MainThread.setOpenThread(true);
        CoverThread.setOpenThread(true);
        this.isActive = true;
        regFilter();
    }

    public void onDestroy() {
        super.onDestroy();
        Log.e("my", "onDestroy");
        if (this.isRegFilter) {
            this.mContext.unregisterReceiver(this.mReceiver);
        }
    }

    public void onDestroyView() {
        super.onDestroyView();
    }
}
