package com.jwkj.widget.playback;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.media.TransportMediator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.hikam.C0291R;
import com.jwkj.P2PConnect;
import com.jwkj.PlayBackActivity;
import com.jwkj.data.Contact;
import com.jwkj.data.ContactDB;
import com.jwkj.global.Constants.P2P;
import com.jwkj.utils.C0568T;
import com.jwkj.utils.Utils;
import com.jwkj.widget.LoadMoreListView;
import com.jwkj.widget.LoadMoreListView.OnLoadMoreListener;
import com.p2p.core.P2PHandler;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PlayBackManagerFragment extends Fragment implements OnClickListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String TAG = (getClass().getSimpleName().toString() + "--");
    private PlayBackListAdapter adapter;
    private Context context;
    private int current_pager = 0;
    private String fileName;
    private int frameRate;
    private Handler handler = new Handler();
    private int indicator = -1;
    private boolean isFirstIn = true;
    private boolean isRegFilter = false;
    private AlertDialog load_record;
    private View load_view;
    private LoadMoreListView lv;
    private OnFragmentInteractionListener mListener;
    private Contact mParam1;
    private int mType;
    private List<String> nameList = new ArrayList();
    private List<Integer> rateList = new ArrayList();
    BroadcastReceiver receiver = new C06513();
    private RelativeLayout widget_empty;
    private RelativeLayout widget_loading;

    class C06502 implements OnItemClickListener {

        class C06481 implements OnKeyListener {
            C06481() {
            }

            public boolean onKey(DialogInterface arg0, int arg1, KeyEvent event) {
                if (event.getAction() != 0 || event.getKeyCode() != 4) {
                    return false;
                }
                if (PlayBackManagerFragment.this.load_record.isShowing()) {
                    PlayBackManagerFragment.this.load_record.dismiss();
                    P2PHandler.getInstance().reject();
                }
                return true;
            }
        }

        C06502() {
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            if (PlayBackManagerFragment.this.adapter.getCount() == position) {
                C0568T.showShort(PlayBackManagerFragment.this.context, PlayBackManagerFragment.this.getResources().getString(C0291R.string.no_more_data));
                return;
            }
            PlayBackManagerFragment.this.indicator = position;
            PlayBackManagerFragment.this.fileName = (String) PlayBackManagerFragment.this.nameList.get(position);
            PlayBackManagerFragment.this.frameRate = ((Integer) PlayBackManagerFragment.this.rateList.get(position)).intValue();
            PlayBackManagerFragment.this.load_view = LayoutInflater.from(PlayBackManagerFragment.this.context).inflate(C0291R.layout.dialog_load_record, null);
            PlayBackManagerFragment.this.load_record = new Builder(PlayBackManagerFragment.this.context, C0291R.style.hikamDialog).create();
            PlayBackManagerFragment.this.load_record.show();
            PlayBackManagerFragment.this.load_record.setContentView(PlayBackManagerFragment.this.load_view);
            PlayBackManagerFragment.this.load_record.setOnKeyListener(new C06481());
            PlayBackManagerFragment.this.load_view.setLayoutParams(new LayoutParams(Utils.dip2px(PlayBackManagerFragment.this.context, 222), Utils.dip2px(PlayBackManagerFragment.this.context, TransportMediator.KEYCODE_MEDIA_RECORD)));
            ImageView img = (ImageView) PlayBackManagerFragment.this.load_view.findViewById(C0291R.id.load_record_img);
            final AnimationDrawable anim = (AnimationDrawable) img.getDrawable();
            img.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                public boolean onPreDraw() {
                    anim.start();
                    return true;
                }
            });
            P2PConnect.setCurrent_state(1);
            P2PConnect.setCurrent_call_id(PlayBackManagerFragment.this.mParam1.contactId);
            P2PHandler.getInstance().playbackConnect(PlayBackManagerFragment.this.mParam1.contactModel, PlayBackManagerFragment.this.mParam1.contactId, PlayBackManagerFragment.this.mParam1.contactPassword, PlayBackManagerFragment.this.fileName, PlayBackManagerFragment.this.frameRate, position);
            Log.e("playback", PlayBackManagerFragment.this.fileName);
        }
    }

    class C06513 extends BroadcastReceiver {
        C06513() {
        }

        public void onReceive(Context arg0, Intent intent) {
            if (intent.getAction().equals(P2P.RET_GET_PLAYBACK_FILES)) {
                PlayBackManagerFragment.this.updataList((String[]) intent.getCharSequenceArrayExtra("recordList"), intent.getIntArrayExtra("rateList"));
            } else if (intent.getAction().equals(P2P.ACK_RET_GET_PLAYBACK_FILES)) {
                int result = intent.getIntExtra("result", -1);
                if (result == 9999) {
                    C0568T.showLong(PlayBackManagerFragment.this.context, (int) C0291R.string.device_password_error);
                } else if (result != 9998 && result == 9996) {
                }
            } else if (intent.getAction().equals(P2P.P2P_ACCEPT)) {
                P2PHandler.getInstance().openAudioAndStartPlaying(2);
            } else if (intent.getAction().equals(P2P.P2P_READY)) {
                if (PlayBackManagerFragment.this.load_record != null && PlayBackManagerFragment.this.load_record.isShowing()) {
                    PlayBackManagerFragment.this.load_record.dismiss();
                }
                Intent intentCall = new Intent(PlayBackManagerFragment.this.context, PlayBackActivity.class);
                intentCall.putExtra("type", 2);
                intentCall.putExtra("fileName", PlayBackManagerFragment.this.fileName);
                intentCall.putExtra(ContactDB.COLUMN_CONTACT_MODEL, PlayBackManagerFragment.this.mParam1.contactModel);
                intentCall.putExtra(ContactDB.COLUMN_CONTACT_ID, PlayBackManagerFragment.this.mParam1.contactId);
                intentCall.putExtra(ContactDB.COLUMN_CONTACT_PASSWORD, PlayBackManagerFragment.this.mParam1.contactPassword);
                intentCall.putIntegerArrayListExtra("rateList", (ArrayList) PlayBackManagerFragment.this.rateList);
                intentCall.putStringArrayListExtra("nameList", (ArrayList) PlayBackManagerFragment.this.nameList);
                intentCall.putExtra("indicator", PlayBackManagerFragment.this.indicator);
                intentCall.putExtra("startTime", PlayBackManagerFragment.this.adapter.getStartTime());
                intentCall.setFlags(268435456);
                PlayBackManagerFragment.this.startActivity(intentCall);
            } else if (intent.getAction().equals(P2P.P2P_REJECT)) {
                if (PlayBackManagerFragment.this.load_record != null && PlayBackManagerFragment.this.load_record.isShowing()) {
                    PlayBackManagerFragment.this.load_record.dismiss();
                }
                P2PHandler.getInstance().reject();
            }
        }
    }

    public interface OnFragmentInteractionListener {
        void onPopupWinShow();
    }

    class C11411 implements OnLoadMoreListener {
        C11411() {
        }

        public void onLoadStart() {
            PlayBackManager.getInstance().searchNextPager(PlayBackManagerFragment.this.adapter.getStartTime(), PlayBackManagerFragment.this.adapter.getEndTime(), PlayBackManagerFragment.this.mParam1);
        }

        public void onLoadFinish() {
        }
    }

    public static PlayBackManagerFragment newInstance(Contact param1, int type) {
        PlayBackManagerFragment fragment = new PlayBackManagerFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param1);
        args.putInt(ARG_PARAM2, type);
        fragment.setArguments(args);
        Log.e("few", "newInstance");
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.mParam1 = (Contact) getArguments().getSerializable(ARG_PARAM1);
            this.mType = getArguments().getInt(ARG_PARAM2);
            Log.e("few", "onCreate");
        }
        if (this.mType == 1) {
            regFilter();
            PlayBackManager.getInstance().searchIndex(this.mType, this.mParam1);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(C0291R.layout.fragment_playback_manager, container, false);
        initComponent(view);
        return view;
    }

    private void initComponent(View view) {
        if (this.mType == 0) {
            ((TextView) view.findViewById(C0291R.id.tv_refresh)).setVisibility(8);
            ((TextView) view.findViewById(C0291R.id.tv_reset)).setVisibility(0);
        }
        this.adapter = new PlayBackListAdapter(this.context, this.nameList, this.rateList);
        Log.e("few", "getStartTimeByIndex");
        this.adapter.setStartTime(PlayBackManager.getInstance().getStartTimeByIndex(this.mType));
        this.lv = (LoadMoreListView) view.findViewById(C0291R.id.lv);
        this.lv.setAdapter(this.adapter);
        this.lv.setOnLoadMoreListener(new C11411());
        this.lv.setOnItemClickListener(new C06502());
        this.widget_loading = (RelativeLayout) view.findViewById(C0291R.id.widget_loading);
        this.widget_empty = (RelativeLayout) view.findViewById(C0291R.id.widget_empty);
        this.widget_empty.setOnClickListener(this);
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.e("few", this.mType + "-setUserVisibleHint-");
        if (isVisibleToUser) {
            Log.e("few", this.mType + "-v setUserVisibleHint-");
            regFilter();
            if (this.isFirstIn && this.mType != 0) {
                PlayBackManager.getInstance().searchIndex(this.mType, this.mParam1);
                this.isFirstIn = false;
                return;
            }
            return;
        }
        unRegFilter();
    }

    public void onResume() {
        super.onResume();
    }

    public void onButtonPressed(Uri uri) {
        if (this.mListener != null) {
            this.mListener.onPopupWinShow();
        }
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof OnFragmentInteractionListener) {
            this.mListener = (OnFragmentInteractionListener) context;
            return;
        }
        throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
    }

    public void onDetach() {
        super.onDetach();
        this.mListener = null;
        this.context = null;
    }

    public void updataList(String[] names, int[] rates) {
        Log.e(this.TAG, this.TAG + "new data come,the len is :" + rates.length);
        int len = names.length;
        int i;
        if (this.current_pager == 0) {
            if (len == 0) {
                showResNull();
                return;
            }
            showResNotNull();
            this.current_pager++;
            for (i = 0; i < len; i++) {
                this.nameList.add(names[i]);
                this.rateList.add(Integer.valueOf(rates[i]));
            }
        } else if (len == 0) {
            this.lv.loadClose();
            this.lv.invalidate();
            return;
        } else {
            for (i = 0; i < len; i++) {
                this.nameList.add(names[i]);
                this.rateList.add(Integer.valueOf(rates[i]));
            }
            this.adapter.notifyDataSetChanged();
        }
        this.lv.invalidate();
        this.lv.loadMoreFinish();
    }

    public void showPopupDismiss() {
        if (this.nameList.size() == 0) {
            this.widget_empty.setVisibility(0);
            this.widget_loading.setVisibility(8);
        }
    }

    public void showResNull() {
        this.widget_empty.setVisibility(0);
        this.widget_loading.setVisibility(8);
    }

    public void showResNotNull() {
        this.widget_empty.setVisibility(8);
        this.widget_loading.setVisibility(8);
    }

    public void showReq() {
        this.widget_empty.setVisibility(8);
        this.widget_loading.setVisibility(0);
        if (this.mType != 0) {
            PlayBackManager.getInstance().searchIndex(this.mType, this.mParam1);
        } else if (this.mListener != null) {
            this.mListener.onPopupWinShow();
        }
    }

    public void regFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(P2P.ACK_RET_GET_PLAYBACK_FILES);
        filter.addAction(P2P.RET_GET_PLAYBACK_FILES);
        filter.addAction(P2P.P2P_ACCEPT);
        filter.addAction(P2P.P2P_READY);
        filter.addAction(P2P.P2P_REJECT);
        filter.addAction("android.intent.action.SCREEN_OFF");
        this.context = getActivity();
        if (this.context != null) {
            this.context.registerReceiver(this.receiver, filter);
            Log.e("few", "register");
        }
        this.isRegFilter = true;
    }

    public void unRegFilter() {
        if (this.receiver != null && this.isRegFilter) {
            this.context.unregisterReceiver(this.receiver);
            this.isRegFilter = false;
        }
    }

    public void onPause() {
        super.onPause();
        unRegFilter();
        this.handler.removeCallbacksAndMessages(null);
    }

    public void customSearch(String startTime, String endTime, Contact contact) {
        if (this.nameList.size() != 0) {
            this.nameList.clear();
            this.rateList.clear();
            this.adapter.notifyDataSetChanged();
            this.current_pager = 0;
        }
        showResNull();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date start = sdf.parse(startTime);
            Date end = sdf.parse(endTime);
            if (start.after(end)) {
                C0568T.showShort(this.context, (int) C0291R.string.search_error3);
                return;
            }
            this.adapter.setStartTime(start);
            PlayBackManager.getInstance().searchNextPager(start, end, contact);
            this.widget_empty.setVisibility(8);
            this.widget_loading.setVisibility(0);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case C0291R.id.widget_empty:
                showReq();
                return;
            default:
                return;
        }
    }
}
