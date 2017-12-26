package com.jwkj.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.hikam.C0291R;
import com.jwkj.activity.MainControlActivity;
import com.jwkj.adapter.DateNumericAdapter;
import com.jwkj.data.Contact;
import com.jwkj.data.ContactDB;
import com.jwkj.global.Constants.Action;
import com.jwkj.global.Constants.P2P;
import com.jwkj.thread.DelayThread;
import com.jwkj.thread.DelayThread.OnRunListener;
import com.jwkj.utils.C0568T;
import com.jwkj.utils.Utils;
import com.jwkj.wheel.widget.OnWheelScrollListener;
import com.jwkj.wheel.widget.WheelView;
import com.p2p.core.P2PHandler;
import java.lang.reflect.Field;
import java.util.Calendar;
import org.apache.http.HttpStatus;
import org.jboss.netty.handler.codec.rtsp.RtspHeaders.Values;

public class TimeControlFrag extends BaseFragment implements OnClickListener {
    Button bt_set_time;
    Button bt_set_timezone;
    private Contact contact;
    String cur_modify_time;
    int current_urban;
    WheelView date_day;
    WheelView date_hour;
    WheelView date_minute;
    WheelView date_month;
    WheelView date_year;
    private boolean isRegFilter = false;
    private Context mContext;
    private BroadcastReceiver mReceiver = new C05502();
    ProgressBar progressBar;
    OnWheelScrollListener scrolledListener = new C11201();
    RelativeLayout setting_time;
    RelativeLayout setting_urban_title;
    TextView time_text;
    WheelView w_urban;
    private boolean wheelScrolled = false;

    class C05502 extends BroadcastReceiver {
        C05502() {
        }

        public void onReceive(Context arg0, Intent intent) {
            if (intent.getAction().equals(P2P.RET_GET_TIME)) {
                TimeControlFrag.this.time_text.setText(intent.getStringExtra(Values.TIME));
                TimeControlFrag.this.progressBar.setVisibility(8);
                TimeControlFrag.this.time_text.setVisibility(0);
                TimeControlFrag.this.setting_time.setEnabled(true);
            } else if (intent.getAction().equals(P2P.RET_SET_TIME)) {
                if (intent.getIntExtra("result", -1) == 0) {
                    TimeControlFrag.this.time_text.setText(TimeControlFrag.this.cur_modify_time);
                    TimeControlFrag.this.progressBar.setVisibility(8);
                    TimeControlFrag.this.time_text.setVisibility(0);
                    TimeControlFrag.this.setting_time.setEnabled(true);
                    C0568T.showShort(TimeControlFrag.this.mContext, (int) C0291R.string.modify_success);
                    return;
                }
                TimeControlFrag.this.progressBar.setVisibility(8);
                TimeControlFrag.this.time_text.setVisibility(0);
                TimeControlFrag.this.setting_time.setEnabled(true);
                C0568T.showShort(TimeControlFrag.this.mContext, (int) C0291R.string.operator_error);
            } else if (intent.getAction().equals(P2P.ACK_RET_GET_TIME)) {
                result = intent.getIntExtra("result", -1);
                if (result == 9999) {
                    i = new Intent();
                    i.setAction(Action.CONTROL_SETTING_PWD_ERROR);
                    TimeControlFrag.this.mContext.sendBroadcast(i);
                } else if (result == 9998) {
                    Log.e("my", "net error resend:get npc time");
                    P2PHandler.getInstance().getDeviceTime(TimeControlFrag.this.contact.contactModel, TimeControlFrag.this.contact.contactId, TimeControlFrag.this.contact.contactPassword);
                }
            } else if (intent.getAction().equals(P2P.ACK_RET_SET_TIME)) {
                result = intent.getIntExtra("result", -1);
                if (result == 9999) {
                    i = new Intent();
                    i.setAction(Action.CONTROL_SETTING_PWD_ERROR);
                    TimeControlFrag.this.mContext.sendBroadcast(i);
                } else if (result == 9998) {
                    Log.e("my", "net error resend:set npc time");
                    P2PHandler.getInstance().setDeviceTime(TimeControlFrag.this.contact.contactModel, TimeControlFrag.this.contact.contactId, TimeControlFrag.this.contact.contactPassword, TimeControlFrag.this.cur_modify_time);
                }
            } else if (intent.getAction().equals(P2P.RET_GET_TIME_ZONE)) {
                int timezone = intent.getIntExtra("state", -1);
                if (timezone != -1) {
                    TimeControlFrag.this.setting_urban_title.setVisibility(0);
                }
                TimeControlFrag.this.w_urban.setCurrentItem(timezone);
            } else if (intent.getAction().equals(P2P.ACK_RET_SET_TIME_ZONE)) {
                int state = intent.getIntExtra("state", -1);
                if (state == 9997) {
                    C0568T.showShort(TimeControlFrag.this.mContext, (int) C0291R.string.timezone_success);
                    P2PHandler.getInstance().getDeviceTime(TimeControlFrag.this.contact.contactModel, TimeControlFrag.this.contact.contactId, TimeControlFrag.this.contact.contactPassword);
                } else if (state == 9998) {
                    P2PHandler.getInstance().setTimeZone(TimeControlFrag.this.contact.contactModel, TimeControlFrag.this.contact.contactId, TimeControlFrag.this.contact.contactName, TimeControlFrag.this.current_urban);
                }
            }
        }
    }

    class C11201 implements OnWheelScrollListener {
        C11201() {
        }

        public void onScrollingStarted(WheelView wheel) {
            TimeControlFrag.this.wheelScrolled = true;
            TimeControlFrag.this.updateStatus();
        }

        public void onScrollingFinished(WheelView wheel) {
            TimeControlFrag.this.wheelScrolled = false;
            TimeControlFrag.this.updateStatus();
        }
    }

    class C11213 implements OnRunListener {
        C11213() {
        }

        public void run() {
            TimeControlFrag.this.cur_modify_time = Utils.convertDeviceTime(TimeControlFrag.this.date_year.getCurrentItem() + 10, TimeControlFrag.this.date_month.getCurrentItem() + 1, TimeControlFrag.this.date_day.getCurrentItem() + 1, TimeControlFrag.this.date_hour.getCurrentItem(), TimeControlFrag.this.date_minute.getCurrentItem());
            P2PHandler.getInstance().setDeviceTime(TimeControlFrag.this.contact.contactModel, TimeControlFrag.this.contact.contactId, TimeControlFrag.this.contact.contactPassword, TimeControlFrag.this.cur_modify_time);
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mContext = MainControlActivity.mContext;
        this.contact = (Contact) getArguments().getSerializable(ContactDB.TABLE_NAME);
        View view = inflater.inflate(C0291R.layout.fragment_time_control, container, false);
        initComponent(view);
        regFilter();
        P2PHandler.getInstance().getDeviceTime(this.contact.contactModel, this.contact.contactId, this.contact.contactPassword);
        P2PHandler.getInstance().getNpcSettings(this.contact.contactModel, this.contact.contactId, this.contact.contactPassword);
        return view;
    }

    public void initComponent(View view) {
        Calendar calendar = Calendar.getInstance();
        this.setting_time = (RelativeLayout) view.findViewById(C0291R.id.setting_time);
        this.time_text = (TextView) view.findViewById(C0291R.id.time_text);
        this.progressBar = (ProgressBar) view.findViewById(C0291R.id.progressBar);
        this.setting_time.setEnabled(false);
        int curYear = calendar.get(1);
        this.date_year = (WheelView) view.findViewById(C0291R.id.date_year);
        this.date_year.setViewAdapter(new DateNumericAdapter(this.mContext, 2010, 2036));
        this.date_year.setCurrentItem(curYear - 2010);
        this.date_year.addScrollingListener(this.scrolledListener);
        this.date_year.setCyclic(true);
        int curMonth = calendar.get(2) + 1;
        this.date_month = (WheelView) view.findViewById(C0291R.id.date_month);
        this.date_month.setViewAdapter(new DateNumericAdapter(this.mContext, 1, 12));
        this.date_month.setCurrentItem(curMonth - 1);
        this.date_month.addScrollingListener(this.scrolledListener);
        this.date_month.setCyclic(true);
        int curDay = calendar.get(5);
        this.date_day = (WheelView) view.findViewById(C0291R.id.date_day);
        this.date_day.setViewAdapter(new DateNumericAdapter(this.mContext, 1, 31));
        this.date_day.setCurrentItem(curDay - 1);
        this.date_day.addScrollingListener(this.scrolledListener);
        this.date_day.setCyclic(true);
        int curHour = calendar.get(11);
        this.date_hour = (WheelView) view.findViewById(C0291R.id.date_hour);
        this.date_hour.setViewAdapter(new DateNumericAdapter(this.mContext, 0, 23));
        this.date_hour.setCurrentItem(curHour);
        this.date_hour.setCyclic(true);
        int curMinute = calendar.get(12);
        this.date_minute = (WheelView) view.findViewById(C0291R.id.date_minute);
        this.date_minute.setViewAdapter(new DateNumericAdapter(this.mContext, 0, 59));
        this.date_minute.setCurrentItem(curMinute);
        this.date_minute.setCyclic(true);
        this.w_urban = (WheelView) view.findViewById(C0291R.id.w_urban);
        this.w_urban.setViewAdapter(new DateNumericAdapter(this.mContext, -11, 12));
        this.w_urban.setCyclic(true);
        this.bt_set_timezone = (Button) view.findViewById(C0291R.id.bt_set_timezone);
        this.bt_set_timezone.setOnClickListener(this);
        this.bt_set_time = (Button) view.findViewById(C0291R.id.bt_set_time);
        this.bt_set_time.setOnClickListener(this);
        this.setting_urban_title = (RelativeLayout) view.findViewById(C0291R.id.setting_urban_title);
    }

    public void updateStatus() {
        int year = this.date_year.getCurrentItem() + 2010;
        int month = this.date_month.getCurrentItem() + 1;
        if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
            this.date_day.setViewAdapter(new DateNumericAdapter(this.mContext, 1, 31));
        } else if (month == 2) {
            boolean isLeapYear;
            if (year % 100 == 0) {
                if (year % HttpStatus.SC_BAD_REQUEST == 0) {
                    isLeapYear = true;
                } else {
                    isLeapYear = false;
                }
            } else if (year % 4 == 0) {
                isLeapYear = true;
            } else {
                isLeapYear = false;
            }
            if (isLeapYear) {
                if (this.date_day.getCurrentItem() > 28) {
                    this.date_day.scroll(30, 2000);
                }
                this.date_day.setViewAdapter(new DateNumericAdapter(this.mContext, 1, 29));
                return;
            }
            if (this.date_day.getCurrentItem() > 27) {
                this.date_day.scroll(30, 2000);
            }
            this.date_day.setViewAdapter(new DateNumericAdapter(this.mContext, 1, 28));
        } else {
            if (this.date_day.getCurrentItem() > 29) {
                this.date_day.scroll(30, 2000);
            }
            this.date_day.setViewAdapter(new DateNumericAdapter(this.mContext, 1, 30));
        }
    }

    public void regFilter() {
        MainControlActivity.isCancelCheck = true;
        IntentFilter filter = new IntentFilter();
        filter.addAction(P2P.ACK_RET_SET_TIME);
        filter.addAction(P2P.ACK_RET_GET_TIME);
        filter.addAction(P2P.RET_SET_TIME);
        filter.addAction(P2P.RET_GET_TIME);
        filter.addAction(P2P.RET_GET_TIME_ZONE);
        filter.addAction(P2P.ACK_RET_SET_TIME_ZONE);
        this.mContext.registerReceiver(this.mReceiver, filter);
        this.isRegFilter = true;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case C0291R.id.bt_set_time:
                this.progressBar.setVisibility(0);
                this.time_text.setVisibility(8);
                this.setting_time.setEnabled(false);
                new DelayThread(0, new C11213()).start();
                return;
            case C0291R.id.bt_set_timezone:
                this.current_urban = this.w_urban.getCurrentItem();
                P2PHandler.getInstance().setTimeZone(this.contact.contactModel, this.contact.contactId, this.contact.contactPassword, this.current_urban);
                return;
            default:
                return;
        }
    }

    public void onDestroyView() {
        super.onDestroyView();
        if (this.isRegFilter) {
            this.mContext.unregisterReceiver(this.mReceiver);
            this.isRegFilter = false;
        }
        MainControlActivity.isCancelCheck = false;
    }

    public void onDestroy() {
        super.onDestroy();
        Intent it = new Intent();
        it.setAction(Action.CONTROL_BACK);
        this.mContext.sendBroadcast(it);
    }

    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e2) {
            throw new RuntimeException(e2);
        }
    }
}
