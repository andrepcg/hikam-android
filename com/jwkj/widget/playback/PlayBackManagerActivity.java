package com.jwkj.widget.playback;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.com.streamax.miotp.p2p.jni.P2pJni;
import com.hikam.C0291R;
import com.jwkj.activity.BaseActivity;
import com.jwkj.adapter.DateNumericAdapter;
import com.jwkj.data.Contact;
import com.jwkj.data.ContactDB;
import com.jwkj.entity.RecordVideo;
import com.jwkj.utils.C0568T;
import com.jwkj.utils.PagerAdapter;
import com.jwkj.wheel.widget.OnWheelScrollListener;
import com.jwkj.wheel.widget.WheelView;
import com.jwkj.widget.HeaderView;
import com.jwkj.widget.SlidingTab;
import com.jwkj.widget.SlidingTab.OnTabClickListener;
import com.jwkj.widget.WrapPopupWindow;
import com.jwkj.widget.playback.PlayBackManagerFragment.OnFragmentInteractionListener;
import com.jwkj.widget.playback.RecordManagerFragment.OnPlayListener;
import java.sql.Timestamp;
import java.util.Calendar;
import org.apache.http.HttpStatus;

public class PlayBackManagerActivity extends BaseActivity implements OnFocusChangeListener, OnClickListener, OnFragmentInteractionListener, OnPlayListener {
    public static final int END_TIME = 1;
    public static final int START_TIME = 0;
    public static Contact contact;
    private ImageView btn_back;
    private Button btn_search;
    private int current_pager = 0;
    private Button date_cancel;
    public WheelView day;
    private EditText endTime;
    PlayBackManagerFragment fragment1;
    PlayBackManagerFragment fragment2;
    PlayBackManagerFragment fragment3;
    PlayBackManagerFragment fragment4;
    RecordManagerFragment fragment5;
    private Handler handler = new Handler();
    private HeaderView header_img;
    private TextView header_tv_name;
    public WheelView hour;
    private boolean isRecordMode = false;
    public WheelView minute;
    public WheelView month;
    private PagerAdapter pagerAdapter;
    private WrapPopupWindow popupWindow;
    private RelativeLayout rl_head;
    private RelativeLayout rl_title;
    OnWheelScrollListener scrolledListener = new C11404();
    int selected_Date;
    private EditText startTime;
    private SlidingTab tab;
    private ViewPager vp;
    private boolean wheelScrolled = false;
    public WheelView year;

    class C06461 implements Runnable {
        C06461() {
        }

        public void run() {
            switch (PlayBackManagerActivity.this.current_pager) {
                case 0:
                    if (PlayBackManagerActivity.this.fragment1 != null) {
                        PlayBackManagerActivity.this.fragment1.regFilter();
                        return;
                    }
                    return;
                case 1:
                    if (PlayBackManagerActivity.this.fragment2 != null) {
                        PlayBackManagerActivity.this.fragment2.regFilter();
                        return;
                    }
                    return;
                case 2:
                    if (PlayBackManagerActivity.this.fragment3 != null) {
                        PlayBackManagerActivity.this.fragment3.regFilter();
                        return;
                    }
                    return;
                case 3:
                    if (PlayBackManagerActivity.this.fragment4 != null) {
                        PlayBackManagerActivity.this.fragment4.regFilter();
                        return;
                    }
                    return;
                default:
                    return;
            }
        }
    }

    class C11382 implements OnPageChangeListener {
        C11382() {
        }

        public void onPageSelected(int position) {
            if (position == 3) {
                PlayBackManagerActivity.this.popupWindow.showAtLocation(PlayBackManagerActivity.this.tab, 8388693, 0, 0);
            }
            PlayBackManagerActivity.this.current_pager = position;
            if (position == 4) {
                PlayBackManagerActivity.this.openRecordMode();
            } else {
                PlayBackManagerActivity.this.closeRecordMode();
            }
        }

        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        public void onPageScrollStateChanged(int state) {
        }
    }

    class C11393 implements OnTabClickListener {
        C11393() {
        }

        public void onTabClick(int position) {
            if (position == 3) {
                PlayBackManagerActivity.this.onPopupWinShow();
            }
        }
    }

    class C11404 implements OnWheelScrollListener {
        C11404() {
        }

        public void onScrollingStarted(WheelView wheel) {
            PlayBackManagerActivity.this.wheelScrolled = true;
            PlayBackManagerActivity.this.updateStatus();
            PlayBackManagerActivity.this.updateSearchEdit();
        }

        public void onScrollingFinished(WheelView wheel) {
            PlayBackManagerActivity.this.wheelScrolled = false;
            PlayBackManagerActivity.this.updateStatus();
            PlayBackManagerActivity.this.updateSearchEdit();
        }
    }

    class myPagerAdapter extends FragmentStatePagerAdapter {
        String[] title = new String[]{PlayBackManagerActivity.this.getResources().getString(C0291R.string.one_day), PlayBackManagerActivity.this.getResources().getString(C0291R.string.three_day), PlayBackManagerActivity.this.getResources().getString(C0291R.string.one_month), PlayBackManagerActivity.this.getResources().getString(C0291R.string.customize)};

        public myPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    PlayBackManagerActivity.this.fragment1 = PlayBackManagerFragment.newInstance(PlayBackManagerActivity.contact, 1);
                    return PlayBackManagerActivity.this.fragment1;
                case 1:
                    PlayBackManagerActivity.this.fragment2 = PlayBackManagerFragment.newInstance(PlayBackManagerActivity.contact, 3);
                    return PlayBackManagerActivity.this.fragment2;
                case 2:
                    PlayBackManagerActivity.this.fragment3 = PlayBackManagerFragment.newInstance(PlayBackManagerActivity.contact, 30);
                    return PlayBackManagerActivity.this.fragment3;
                case 3:
                    PlayBackManagerActivity.this.fragment4 = PlayBackManagerFragment.newInstance(PlayBackManagerActivity.contact, 0);
                    return PlayBackManagerActivity.this.fragment4;
                default:
                    return null;
            }
        }

        public int getCount() {
            return this.title.length;
        }

        public CharSequence getPageTitle(int position) {
            return this.title[position];
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0291R.layout.activity_playback_manager);
        contact = (Contact) getIntent().getSerializableExtra(ContactDB.TABLE_NAME);
        initComponent();
        initPopupWin();
        regFilter();
    }

    public Contact getContact() {
        return contact;
    }

    private void regFilter() {
    }

    private void initPopupWin() {
        View view = LayoutInflater.from(this).inflate(C0291R.layout.play_back_date_pick, null);
        view.getBackground().setAlpha(230);
        this.btn_search = (Button) view.findViewById(C0291R.id.search_btn);
        this.btn_search.setOnClickListener(this);
        this.date_cancel = (Button) view.findViewById(C0291R.id.date_cancel);
        this.date_cancel.setOnClickListener(this);
        this.year = (WheelView) view.findViewById(C0291R.id.date_year);
        this.month = (WheelView) view.findViewById(C0291R.id.date_month);
        this.day = (WheelView) view.findViewById(C0291R.id.date_day);
        this.hour = (WheelView) view.findViewById(C0291R.id.date_hour);
        this.minute = (WheelView) view.findViewById(C0291R.id.date_minute);
        this.startTime = (EditText) view.findViewById(C0291R.id.start_time);
        this.endTime = (EditText) view.findViewById(C0291R.id.end_time);
        this.startTime.setInputType(0);
        this.startTime.setOnFocusChangeListener(this);
        this.endTime.setInputType(0);
        this.endTime.setOnFocusChangeListener(this);
        Calendar calendar = Calendar.getInstance();
        int curYear = calendar.get(1);
        this.year.setViewAdapter(new DateNumericAdapter(this, 2010, 2036));
        this.year.setCurrentItem(curYear - 2010);
        this.year.addScrollingListener(this.scrolledListener);
        this.year.setCyclic(true);
        int curMonth = calendar.get(2) + 1;
        this.month.setViewAdapter(new DateNumericAdapter(this, 1, 12));
        this.month.setCurrentItem(curMonth - 1);
        this.month.addScrollingListener(this.scrolledListener);
        this.month.setCyclic(true);
        int curDay = calendar.get(5);
        this.day.setViewAdapter(new DateNumericAdapter(this, 1, 31));
        this.day.setCurrentItem(curDay - 1);
        this.day.addScrollingListener(this.scrolledListener);
        this.day.setCyclic(true);
        int curHour = calendar.get(11);
        this.hour.setViewAdapter(new DateNumericAdapter(this, 0, 23));
        this.hour.setCurrentItem(curHour);
        this.hour.addScrollingListener(this.scrolledListener);
        this.hour.setCyclic(true);
        int curMinute = calendar.get(12);
        this.minute.setViewAdapter(new DateNumericAdapter(this, 0, 59));
        this.minute.setCurrentItem(curMinute);
        this.minute.addScrollingListener(this.scrolledListener);
        this.minute.setCyclic(true);
        this.popupWindow = new WrapPopupWindow(this);
        this.popupWindow.setContentView(view);
        this.popupWindow.setWidth(-1);
        this.popupWindow.setHeight(-2);
        this.popupWindow.setOutsideTouchable(true);
        this.popupWindow.setFocusable(false);
    }

    protected void onResume() {
        super.onResume();
        P2pJni.P2PMediaSetMute(1);
        this.handler.postDelayed(new C06461(), 500);
    }

    private void initComponent() {
        this.btn_back = (ImageView) findViewById(C0291R.id.btn_back);
        this.btn_back.setOnClickListener(this);
        this.rl_head = (RelativeLayout) findViewById(C0291R.id.header_bar);
        this.rl_title = (RelativeLayout) findViewById(C0291R.id.layout_title);
        this.header_img = (HeaderView) findViewById(C0291R.id.header_img);
        this.header_img.updateImage(contact.contactId, false);
        this.header_tv_name = (TextView) findViewById(C0291R.id.contactName);
        this.header_tv_name.setText(contact.contactName);
        this.vp = (ViewPager) findViewById(C0291R.id.viewPager);
        this.tab = (SlidingTab) findViewById(C0291R.id.tabs);
        this.vp.setOffscreenPageLimit(3);
        this.vp.setAdapter(new myPagerAdapter(getSupportFragmentManager()));
        this.tab.setShouldExpand(true);
        this.tab.setTabPaddingLeftRight(20);
        this.tab.setTextColor(getResources().getColor(C0291R.color.black));
        this.tab.setIndicatorColor(getResources().getColor(C0291R.color.black));
        this.tab.setIndicatorHeight(4);
        this.tab.setViewPager(this.vp);
        this.tab.setOnPageChangeListener(new C11382());
        this.tab.SetOnTabClickListener(new C11393());
    }

    public void onFocusChange(View arg0, boolean arg1) {
        switch (arg0.getId()) {
            case C0291R.id.end_time:
                this.selected_Date = 1;
                this.startTime.setTextColor(ViewCompat.MEASURED_STATE_MASK);
                this.startTime.setHintTextColor(ViewCompat.MEASURED_STATE_MASK);
                this.endTime.setTextColor(-16776961);
                this.endTime.setHintTextColor(-16776961);
                return;
            case C0291R.id.start_time:
                this.selected_Date = 0;
                this.startTime.setTextColor(-16776961);
                this.startTime.setHintTextColor(-16776961);
                this.endTime.setTextColor(ViewCompat.MEASURED_STATE_MASK);
                this.endTime.setHintTextColor(ViewCompat.MEASURED_STATE_MASK);
                return;
            default:
                return;
        }
    }

    public void updateStatus() {
        int years = this.year.getCurrentItem() + 2010;
        int months = this.month.getCurrentItem() + 1;
        if (months == 1 || months == 3 || months == 5 || months == 7 || months == 8 || months == 10 || months == 12) {
            this.day.setViewAdapter(new DateNumericAdapter(this, 1, 31));
        } else if (months == 2) {
            boolean isLeapYear;
            if (years % 100 == 0) {
                if (years % HttpStatus.SC_BAD_REQUEST == 0) {
                    isLeapYear = true;
                } else {
                    isLeapYear = false;
                }
            } else if (years % 4 == 0) {
                isLeapYear = true;
            } else {
                isLeapYear = false;
            }
            if (isLeapYear) {
                if (this.day.getCurrentItem() > 28) {
                    this.day.scroll(30, 2000);
                }
                this.day.setViewAdapter(new DateNumericAdapter(this, 1, 29));
                return;
            }
            if (this.day.getCurrentItem() > 27) {
                this.day.scroll(30, 2000);
            }
            this.day.setViewAdapter(new DateNumericAdapter(this, 1, 28));
        } else {
            if (this.day.getCurrentItem() > 29) {
                this.day.scroll(30, 2000);
            }
            this.day.setViewAdapter(new DateNumericAdapter(this, 1, 30));
        }
    }

    public void updateSearchEdit() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        int years = this.year.getCurrentItem() + 2010;
        int months = this.month.getCurrentItem() + 1;
        int days = this.day.getCurrentItem() + 1;
        int hours = this.hour.getCurrentItem();
        int minutes = this.minute.getCurrentItem();
        StringBuilder sb = new StringBuilder();
        sb.append(years + "-");
        if (months < 10) {
            sb.append("0" + months + "-");
        } else {
            sb.append(months + "-");
        }
        if (days < 10) {
            sb.append("0" + days + " ");
        } else {
            sb.append(days + " ");
        }
        if (hours < 10) {
            sb.append("0" + hours + ":");
        } else {
            sb.append(hours + ":");
        }
        if (minutes < 10) {
            sb.append("0" + minutes);
        } else {
            sb.append("" + minutes);
        }
        if (this.selected_Date == 0) {
            this.startTime.setText(sb.toString());
        } else {
            this.endTime.setText(sb.toString());
        }
    }

    protected void onPause() {
        super.onPause();
        this.handler.removeCallbacksAndMessages(null);
    }

    public int getActivityInfo() {
        return 0;
    }

    public void onPopupWinShow() {
        if (!this.popupWindow.isShowing()) {
            this.popupWindow.showAtLocation(this.tab, 8388693, 0, 0);
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case C0291R.id.btn_back:
                finish();
                return;
            case C0291R.id.date_cancel:
                this.fragment4.showPopupDismiss();
                this.popupWindow.dismiss();
                return;
            case C0291R.id.search_btn:
                this.fragment4.customSearch(this.startTime.getText().toString(), this.endTime.getText().toString(), contact);
                this.popupWindow.dismiss();
                return;
            default:
                return;
        }
    }

    public void openRecordMode() {
        if (!this.isRecordMode) {
            this.isRecordMode = true;
            this.rl_head.setVisibility(8);
            this.rl_title.setVisibility(8);
        }
    }

    public void closeRecordMode() {
        if (this.isRecordMode) {
            this.isRecordMode = false;
            this.rl_head.setVisibility(0);
            this.rl_title.setVisibility(0);
        }
    }

    public void play(final RecordVideo rv) {
        if (rv.getPath() == null || !rv.getPath().endsWith(".mp4")) {
            new Thread(new Runnable() {
                public void run() {
                    String s = rv.getVideopath();
                    if (P2pJni.P2PClientSdkVideoRecordMuxer(s.substring(0, s.length() - 5)) != 0) {
                        C0568T.showShort(PlayBackManagerActivity.this, (CharSequence) "failed !");
                    }
                }
            }).start();
        }
    }
}
