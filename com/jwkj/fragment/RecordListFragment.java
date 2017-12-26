package com.jwkj.fragment;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.media.TransportMediator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import com.hikam.C0291R;
import com.jwkj.P2PConnect;
import com.jwkj.PlayBackListActivity;
import com.jwkj.adapter.RecordAdapter;
import com.jwkj.data.Contact;
import com.jwkj.global.Constants.Action;
import com.jwkj.global.Constants.P2P;
import com.jwkj.utils.Utils;
import com.p2p.core.MediaPlayer;
import com.p2p.core.P2PHandler;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RecordListFragment extends Fragment implements OnScrollListener {
    RecordAdapter adapter;
    BroadcastReceiver br = new C05401();
    Contact contact;
    LayoutInflater inflater;
    boolean isDialogShowing = false;
    boolean isRegFilter = false;
    RelativeLayout layout_loading;
    List<String> list;
    ListView list_record;
    AlertDialog load_record;
    View load_view;
    Context mContext;
    private boolean mIsReadyCall = false;
    String[] names;
    List<Integer> rateList;
    private int visibleItemCount;
    private int visibleLastIndex = 0;

    class C05401 extends BroadcastReceiver {
        C05401() {
        }

        public void onReceive(Context arg0, Intent intent) {
            if (!intent.getAction().equals(P2P.RET_GET_PLAYBACK_FILES) && intent.getAction().equals(Action.REPEAT_LOADING_DATA)) {
                RecordListFragment.this.layout_loading.setVisibility(8);
            }
        }
    }

    class C05432 implements OnItemClickListener {

        class C05411 implements OnKeyListener {
            C05411() {
            }

            public boolean onKey(DialogInterface arg0, int arg1, KeyEvent event) {
                if (event.getAction() != 0 || event.getKeyCode() != 4) {
                    return false;
                }
                if (RecordListFragment.this.isDialogShowing) {
                    RecordListFragment.this.load_record.cancel();
                    RecordListFragment.this.isDialogShowing = false;
                    P2PHandler.getInstance().reject();
                }
                return true;
            }
        }

        C05432() {
        }

        public void onItemClick(AdapterView<?> adapterView, View arg1, int arg2, long arg3) {
            String filename = (String) RecordListFragment.this.adapter.getList().get(arg2);
            int framerate = ((Integer) RecordListFragment.this.adapter.getRateList().get(arg2)).intValue();
            RecordListFragment.this.load_view = RecordListFragment.this.inflater.inflate(C0291R.layout.dialog_load_record, null);
            Builder builder = new Builder(RecordListFragment.this.getActivity());
            RecordListFragment.this.load_record = builder.create();
            RecordListFragment.this.load_record.show();
            RecordListFragment.this.isDialogShowing = true;
            RecordListFragment.this.load_record.setContentView(RecordListFragment.this.load_view);
            RecordListFragment.this.load_record.setOnKeyListener(new C05411());
            RecordListFragment.this.load_view.setLayoutParams(new LayoutParams(Utils.dip2px(RecordListFragment.this.getActivity(), 222), Utils.dip2px(RecordListFragment.this.getActivity(), TransportMediator.KEYCODE_MEDIA_RECORD)));
            ImageView img = (ImageView) RecordListFragment.this.load_view.findViewById(C0291R.id.load_record_img);
            final AnimationDrawable anim = (AnimationDrawable) img.getDrawable();
            img.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                public boolean onPreDraw() {
                    anim.start();
                    return true;
                }
            });
            P2PConnect.setCurrent_state(1);
            P2PConnect.setCurrent_call_id(RecordListFragment.this.contact.contactId);
            PlayBackListActivity.fileName = filename;
            P2PHandler.getInstance().playbackConnect(RecordListFragment.this.contact.contactModel, RecordListFragment.this.contact.contactId, RecordListFragment.this.contact.contactPassword, filename, framerate, arg2);
            Log.e("playback", filename);
        }
    }

    class C05443 implements OnTouchListener {
        C05443() {
        }

        public boolean onTouch(View arg0, MotionEvent arg1) {
            return false;
        }
    }

    class C05454 implements OnTouchListener {
        C05454() {
        }

        public boolean onTouch(View arg0, MotionEvent arg1) {
            return true;
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("oaosj", "onCreate");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        this.mContext = PlayBackListActivity.mContext;
        View view = inflater.inflate(C0291R.layout.fragment_record, container, false);
        initComponent(view);
        regFilter();
        Log.e("oaosj", "onCreateView");
        return view;
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        closeDialog();
    }

    public void regFilter() {
        this.isRegFilter = true;
        IntentFilter filter = new IntentFilter();
        filter.addAction(P2P.RET_GET_PLAYBACK_FILES);
        filter.addAction(P2P.ACK_RET_GET_PLAYBACK_FILES);
        filter.addAction(Action.REPEAT_LOADING_DATA);
        this.mContext.registerReceiver(this.br, filter);
    }

    public void initComponent(View view) {
        this.list_record = (ListView) view.findViewById(C0291R.id.list_record);
        this.adapter = new RecordAdapter(this.mContext, this.list, this.rateList);
        this.list_record.setAdapter(this.adapter);
        this.layout_loading = (RelativeLayout) view.findViewById(C0291R.id.layout_loading);
        this.list_record.setOnScrollListener(this);
        this.list_record.setOnItemClickListener(new C05432());
    }

    public void cancelDialog() {
        this.load_record.cancel();
        this.isDialogShowing = false;
        MediaPlayer.getInstance().native_p2p_hungup();
        Log.e("my", "hungup");
    }

    public void onDestroy() {
        super.onDestroy();
        Log.e("my", "onDestroy");
        if (this.isRegFilter) {
            this.mContext.unregisterReceiver(this.br);
            this.isRegFilter = false;
        }
    }

    public void onPause() {
        super.onPause();
        Log.e("my", "onPause");
    }

    public void onResume() {
        super.onResume();
        Log.e("my", "onResume");
    }

    public void onStart() {
        super.onStart();
        Log.e("my", "onStart");
    }

    public void onStop() {
        super.onStop();
        if (this.isRegFilter) {
            this.mContext.unregisterReceiver(this.br);
            this.isRegFilter = false;
        }
    }

    public void setList(List<String> list, List<Integer> rateList) {
        this.list = list;
        this.rateList = rateList;
    }

    public void setUser(Contact contact) {
        this.contact = contact;
    }

    public void closeDialog() {
        if (this.load_record != null) {
            this.load_record.cancel();
            this.isDialogShowing = false;
        }
    }

    public void scrollOn() {
        this.list_record.setOnTouchListener(new C05443());
    }

    public void scrollOff() {
        this.list_record.setOnTouchListener(new C05454());
    }

    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.visibleItemCount = visibleItemCount;
        this.visibleLastIndex = firstVisibleItem + visibleItemCount;
    }

    public void onScrollStateChanged(AbsListView view, int scrollState) {
        int itemsLastIndex = this.adapter.getCount();
        int lastIndex = itemsLastIndex + 1;
        if (scrollState == 0 && this.visibleLastIndex == lastIndex) {
            Log.e("loading", "loading...");
        }
        if (itemsLastIndex == this.visibleLastIndex) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            try {
                Date nextStartTime = RecordAdapter.startTime;
                if (this.adapter.getLastItem() != null && nextStartTime != null) {
                    Date nextEndTime = sdf.parse(this.adapter.getLastItem());
                    if (nextEndTime != null && !nextEndTime.equals("") && nextStartTime != null && !nextStartTime.equals("")) {
                        this.layout_loading.setVisibility(0);
                        P2PHandler.getInstance().getRecordFiles(this.contact.contactModel, this.contact.contactId, this.contact.contactPassword, nextStartTime, nextEndTime);
                        Log.e("time1", nextStartTime.toString());
                        Log.e("time2", nextEndTime.toString());
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser) {
            closeDialog();
        }
    }
}
