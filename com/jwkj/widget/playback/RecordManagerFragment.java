package com.jwkj.widget.playback;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import com.hikam.C0291R;
import com.jwkj.data.Contact;
import com.jwkj.entity.RecordVideo;
import com.jwkj.global.Constants.P2P;

public class RecordManagerFragment extends Fragment implements OnClickListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String TAG = (getClass().getSimpleName().toString() + "--");
    private RecordListAdapter adapter;
    private boolean isFirstIn = true;
    private boolean isRegFilter = false;
    private ListView lv;
    private Contact mParam1;
    private int mType;
    private OnPlayListener onPlayListener;
    BroadcastReceiver receiver = new C06542();

    class C06531 implements OnItemClickListener {
        C06531() {
        }

        public void onItemClick(AdapterView<?> adapterView, View arg1, int position, long arg3) {
            if (RecordManagerFragment.this.onPlayListener != null) {
                RecordManagerFragment.this.onPlayListener.play(RecordManagerFragment.this.adapter.getRecordVideoByPosition(position));
            }
        }
    }

    class C06542 extends BroadcastReceiver {
        C06542() {
        }

        public void onReceive(Context arg0, Intent intent) {
            if (!intent.getAction().equals(P2P.RET_GET_PLAYBACK_FILES)) {
            }
        }
    }

    public interface OnPlayListener {
        void play(RecordVideo recordVideo);
    }

    public void setOnPlayListener(OnPlayListener onPlayListener) {
        this.onPlayListener = onPlayListener;
    }

    public static RecordManagerFragment newInstance(Contact param1, int type) {
        RecordManagerFragment fragment = new RecordManagerFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param1);
        args.putInt(ARG_PARAM2, type);
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.mParam1 = (Contact) getArguments().getSerializable(ARG_PARAM1);
            this.mType = getArguments().getInt(ARG_PARAM2);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(C0291R.layout.fragment_record_manager, container, false);
        initComponent(view);
        return view;
    }

    private void initComponent(View view) {
        this.adapter = new RecordListAdapter(getActivity());
        this.lv = (ListView) view.findViewById(C0291R.id.lv);
        this.lv.setAdapter(this.adapter);
        this.lv.setOnItemClickListener(new C06531());
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            regFilter();
            if (this.isFirstIn) {
                this.isFirstIn = false;
                return;
            }
            return;
        }
        unRegFilter();
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPlayListener) {
            this.onPlayListener = (OnPlayListener) context;
            return;
        }
        throw new RuntimeException(context.toString() + " must implement OnPlayListener");
    }

    public void onDetach() {
        super.onDetach();
        this.onPlayListener = null;
    }

    public void regFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.SCREEN_OFF");
        getActivity().registerReceiver(this.receiver, filter);
        this.isRegFilter = true;
    }

    public void unRegFilter() {
        if (this.receiver != null && this.isRegFilter) {
            getActivity().unregisterReceiver(this.receiver);
            this.isRegFilter = false;
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
        }
    }
}
