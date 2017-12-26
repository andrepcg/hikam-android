package com.jwkj;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import com.hikam.C0291R;
import com.jwkj.activity.BaseActivity;
import com.jwkj.adapter.DateNumericAdapter;
import com.jwkj.adapter.RecordAdapter;
import com.jwkj.data.Contact;
import com.jwkj.data.ContactDB;
import com.jwkj.fragment.FaultFragment;
import com.jwkj.fragment.LoadingFragment;
import com.jwkj.fragment.RecordListFragment;
import com.jwkj.global.Constants.Action;
import com.jwkj.global.Constants.P2P;
import com.jwkj.utils.C0568T;
import com.jwkj.wheel.widget.OnWheelScrollListener;
import com.jwkj.wheel.widget.WheelView;
import com.jwkj.widget.HeaderView;
import com.p2p.core.P2PHandler;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.http.HttpStatus;

public class PlayBackListActivity extends BaseActivity implements OnFocusChangeListener, OnClickListener {
    public static final int END_TIME = 1;
    public static boolean IS_SCREEN_OFF = false;
    public static final int START_TIME = 0;
    public static String fileName;
    public static Context mContext;
    ImageView back;
    Contact contact;
    private TextView contactName;
    private int currIndex = 0;
    ImageView cursor;
    private int cursorWidth;
    Button date_cancel;
    WheelView date_day;
    WheelView date_hour;
    WheelView date_minute;
    WheelView date_month;
    LinearLayout date_pick;
    WheelView date_year;
    EditText endTime;
    FaultFragment faultFrag;
    private String[] fragments = new String[]{"recordFrag", "loadingFrag", "faultFrag"};
    HeaderView header_img;
    boolean isDpShow = false;
    boolean isLoadingChange = false;
    boolean isSearchLayoutShow = false;
    List<String> list;
    LoadingFragment loadFrag;
    private boolean mIsReadyCall = false;
    RecordAdapter madapter = new RecordAdapter();
    private int offset = 0;
    private int position_one;
    private int position_three;
    private int position_two;
    List<Integer> rateList;
    BroadcastReceiver receiver = new C03172();
    boolean receiverIsReg;
    RecordListFragment rlFrag;
    OnWheelScrollListener scrolledListener = new C10671();
    Button search_btn;
    TextView search_detail;
    TextView search_one_day;
    TextView search_one_month;
    TextView search_three_day;
    int selected_Date;
    private int selected_condition = 0;
    EditText startTime;
    int waitload = 0;
    private boolean wheelScrolled = false;

    class C03172 extends BroadcastReceiver {
        C03172() {
        }

        public void onReceive(Context arg0, Intent intent) {
            if (intent.getAction().equals(P2P.RET_GET_PLAYBACK_FILES)) {
                Log.e("waitload", "waitload=" + PlayBackListActivity.this.waitload);
                if (PlayBackListActivity.this.rlFrag == null) {
                    PlayBackListActivity.this.rlFrag = new RecordListFragment();
                    PlayBackListActivity.this.rlFrag.setUser(PlayBackListActivity.this.contact);
                }
                PlayBackListActivity.this.rlFrag = new RecordListFragment();
                PlayBackListActivity.this.rlFrag.setUser(PlayBackListActivity.this.contact);
                String[] names = (String[]) intent.getCharSequenceArrayExtra("recordList");
                int[] rates = intent.getIntArrayExtra("rateList");
                Log.e("rates", "rates.length=" + rates.length);
                PlayBackListActivity.this.list = new ArrayList();
                PlayBackListActivity.this.rateList = new ArrayList();
                for (String str : names) {
                    PlayBackListActivity.this.list.add(str);
                }
                for (int valueOf : rates) {
                    PlayBackListActivity.this.rateList.add(Integer.valueOf(valueOf));
                }
                if (PlayBackListActivity.this.waitload > 0) {
                    PlayBackListActivity.this.rlFrag.setList(PlayBackListActivity.this.list, PlayBackListActivity.this.rateList);
                    PlayBackListActivity.this.isLoadingChange = false;
                    PlayBackListActivity.this.madapter.setList(PlayBackListActivity.this.list, PlayBackListActivity.this.rateList);
                    PlayBackListActivity.this.madapter.notifyDataSetChanged();
                    PlayBackListActivity.this.replaceFrag(PlayBackListActivity.this.rlFrag, PlayBackListActivity.this.fragments[0]);
                    PlayBackListActivity playBackListActivity = PlayBackListActivity.this;
                    playBackListActivity.waitload--;
                    Log.e("waitload", "loaded" + PlayBackListActivity.this.waitload);
                    return;
                }
                PlayBackListActivity.this.madapter.upLoadData(PlayBackListActivity.this.list);
                Intent it = new Intent();
                it.setAction(Action.REPEAT_LOADING_DATA);
                PlayBackListActivity.this.sendBroadcast(it);
            } else if (intent.getAction().equals(P2P.ACK_RET_GET_PLAYBACK_FILES)) {
                PlayBackListActivity.this.faultFrag = new FaultFragment();
                int result = intent.getIntExtra("result", -1);
                if (result == 9999) {
                    PlayBackListActivity.this.finish();
                    C0568T.showLong(PlayBackListActivity.mContext, (int) C0291R.string.device_password_error);
                } else if (result == 9998) {
                    Log.e("my", "net error resend:set npc time");
                    PlayBackListActivity.this.faultFrag.setErrorText(PlayBackListActivity.this.getResources().getString(C0291R.string.net_error));
                    PlayBackListActivity.this.waitload = 0;
                    PlayBackListActivity.this.replaceFrag(PlayBackListActivity.this.faultFrag, PlayBackListActivity.this.fragments[2]);
                } else if (result == 9996) {
                    PlayBackListActivity.this.finish();
                    C0568T.showShort(PlayBackListActivity.mContext, (int) C0291R.string.insufficient_permissions);
                }
            } else if (intent.getAction().equals(P2P.P2P_ACCEPT)) {
                Log.e("alex", "Constants.P2P.P2P_ACCEPT");
                P2PHandler.getInstance().openAudioAndStartPlaying(2);
            } else if (intent.getAction().equals(P2P.P2P_READY)) {
                Log.e("alex", "Constants.P2P.P2P_READY");
                Intent intentCall = new Intent();
                intentCall.setClass(PlayBackListActivity.this, PlayBackActivity.class);
                intentCall.putExtra("type", 2);
                intentCall.putExtra("fileName", PlayBackListActivity.fileName);
                intentCall.putExtra(ContactDB.COLUMN_CONTACT_MODEL, PlayBackListActivity.this.contact.contactModel);
                intentCall.putExtra(ContactDB.COLUMN_CONTACT_ID, PlayBackListActivity.this.contact.contactId);
                intentCall.putExtra(ContactDB.COLUMN_CONTACT_PASSWORD, PlayBackListActivity.this.contact.contactPassword);
                intentCall.setFlags(268435456);
                PlayBackListActivity.this.startActivity(intentCall);
                if (PlayBackListActivity.this.rlFrag != null) {
                    PlayBackListActivity.this.rlFrag.closeDialog();
                }
            } else if (intent.getAction().equals(P2P.P2P_REJECT)) {
                if (PlayBackListActivity.this.rlFrag != null) {
                    PlayBackListActivity.this.rlFrag.closeDialog();
                }
                P2PHandler.getInstance().reject();
            }
        }
    }

    class C10671 implements OnWheelScrollListener {
        C10671() {
        }

        public void onScrollingStarted(WheelView wheel) {
            PlayBackListActivity.this.wheelScrolled = true;
            PlayBackListActivity.this.updateStatus();
            PlayBackListActivity.this.updateSearchEdit();
        }

        public void onScrollingFinished(WheelView wheel) {
            PlayBackListActivity.this.wheelScrolled = false;
            PlayBackListActivity.this.updateStatus();
            PlayBackListActivity.this.updateSearchEdit();
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0291R.layout.activity_playback_list);
        mContext = this;
        this.contact = (Contact) getIntent().getSerializableExtra(ContactDB.TABLE_NAME);
        initComponent();
        regFilter();
        initWidth();
        this.selected_condition = 0;
        this.isLoadingChange = true;
        searchByTime(0);
        updateCondition();
        this.loadFrag = new LoadingFragment();
        replaceFrag(this.loadFrag, this.fragments[1]);
    }

    protected void onResume() {
        super.onResume();
        initWidth();
    }

    public void initComponent() {
        this.search_btn = (Button) findViewById(C0291R.id.search_btn);
        this.back = (ImageView) findViewById(C0291R.id.back_btn);
        this.date_cancel = (Button) findViewById(C0291R.id.date_cancel);
        this.date_pick = (LinearLayout) findViewById(C0291R.id.date_pick);
        this.search_detail = (TextView) findViewById(C0291R.id.search_detail);
        this.search_one_day = (TextView) findViewById(C0291R.id.search_one_day);
        this.search_three_day = (TextView) findViewById(C0291R.id.search_three_day);
        this.search_one_month = (TextView) findViewById(C0291R.id.search_one_month);
        this.cursor = (ImageView) findViewById(C0291R.id.cursor);
        this.date_pick.getBackground().setAlpha(230);
        this.startTime = (EditText) findViewById(C0291R.id.start_time);
        this.endTime = (EditText) findViewById(C0291R.id.end_time);
        this.contactName = (TextView) findViewById(C0291R.id.contactName);
        this.header_img = (HeaderView) findViewById(C0291R.id.header_img);
        this.header_img.updateImage(this.contact.contactId, false);
        this.contactName.setText(this.contact.contactName);
        this.startTime.setOnFocusChangeListener(this);
        this.endTime.setOnFocusChangeListener(this);
        this.startTime.setOnClickListener(this);
        this.endTime.setOnClickListener(this);
        this.back.setOnClickListener(this);
        this.date_cancel.setOnClickListener(this);
        this.search_btn.setOnClickListener(this);
        this.search_detail.setOnClickListener(this);
        this.search_one_day.setOnClickListener(this);
        this.search_three_day.setOnClickListener(this);
        this.search_one_month.setOnClickListener(this);
        this.startTime.setInputType(0);
        this.endTime.setInputType(0);
        initWheel();
    }

    private void initWidth() {
        this.cursorWidth = this.cursor.getLayoutParams().width;
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;
        LayoutParams layoutParams = (LayoutParams) this.cursor.getLayoutParams();
        layoutParams.leftMargin = ((screenW / 4) - this.cursorWidth) / 2;
        this.cursor.setLayoutParams(layoutParams);
        this.offset = (int) (((((double) screenW) / 4.0d) - ((double) this.cursorWidth)) / 2.0d);
        this.position_one = (int) (((double) screenW) / 4.0d);
        this.position_two = this.position_one * 2;
        this.position_three = this.position_one * 3;
    }

    public void initWheel() {
        Calendar calendar = Calendar.getInstance();
        int curYear = calendar.get(1);
        this.date_year = (WheelView) findViewById(C0291R.id.date_year);
        this.date_year.setViewAdapter(new DateNumericAdapter(mContext, 2010, 2036));
        this.date_year.setCurrentItem(curYear - 2010);
        this.date_year.addScrollingListener(this.scrolledListener);
        this.date_year.setCyclic(true);
        int curMonth = calendar.get(2) + 1;
        this.date_month = (WheelView) findViewById(C0291R.id.date_month);
        this.date_month.setViewAdapter(new DateNumericAdapter(mContext, 1, 12));
        this.date_month.setCurrentItem(curMonth - 1);
        this.date_month.addScrollingListener(this.scrolledListener);
        this.date_month.setCyclic(true);
        int curDay = calendar.get(5);
        this.date_day = (WheelView) findViewById(C0291R.id.date_day);
        this.date_day.setViewAdapter(new DateNumericAdapter(mContext, 1, 31));
        this.date_day.setCurrentItem(curDay - 1);
        this.date_day.addScrollingListener(this.scrolledListener);
        this.date_day.setCyclic(true);
        int curHour = calendar.get(11);
        this.date_hour = (WheelView) findViewById(C0291R.id.date_hour);
        this.date_hour.setViewAdapter(new DateNumericAdapter(mContext, 0, 23));
        this.date_hour.setCurrentItem(curHour);
        this.date_hour.addScrollingListener(this.scrolledListener);
        this.date_hour.setCyclic(true);
        int curMinute = calendar.get(12);
        this.date_minute = (WheelView) findViewById(C0291R.id.date_minute);
        this.date_minute.setViewAdapter(new DateNumericAdapter(mContext, 0, 59));
        this.date_minute.setCurrentItem(curMinute);
        this.date_minute.addScrollingListener(this.scrolledListener);
        this.date_minute.setCyclic(true);
    }

    public void updateStatus() {
        int year = this.date_year.getCurrentItem() + 2010;
        int month = this.date_month.getCurrentItem() + 1;
        if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
            this.date_day.setViewAdapter(new DateNumericAdapter(mContext, 1, 31));
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
                this.date_day.setViewAdapter(new DateNumericAdapter(mContext, 1, 29));
                return;
            }
            if (this.date_day.getCurrentItem() > 27) {
                this.date_day.scroll(30, 2000);
            }
            this.date_day.setViewAdapter(new DateNumericAdapter(mContext, 1, 28));
        } else {
            if (this.date_day.getCurrentItem() > 29) {
                this.date_day.scroll(30, 2000);
            }
            this.date_day.setViewAdapter(new DateNumericAdapter(mContext, 1, 30));
        }
    }

    public void updateSearchEdit() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        int year = this.date_year.getCurrentItem() + 2010;
        int month = this.date_month.getCurrentItem() + 1;
        int day = this.date_day.getCurrentItem() + 1;
        int hour = this.date_hour.getCurrentItem();
        int minute = this.date_minute.getCurrentItem();
        StringBuilder sb = new StringBuilder();
        sb.append(year + "-");
        if (month < 10) {
            sb.append("0" + month + "-");
        } else {
            sb.append(month + "-");
        }
        if (day < 10) {
            sb.append("0" + day + " ");
        } else {
            sb.append(day + " ");
        }
        if (hour < 10) {
            sb.append("0" + hour + ":");
        } else {
            sb.append(hour + ":");
        }
        if (minute < 10) {
            sb.append("0" + minute);
        } else {
            sb.append("" + minute);
        }
        if (this.selected_Date == 0) {
            this.startTime.setText(sb.toString());
        } else {
            this.endTime.setText(sb.toString());
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
        registerReceiver(this.receiver, filter);
        this.receiverIsReg = true;
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

    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case C0291R.id.back_btn:
                finish();
                return;
            case C0291R.id.date_cancel:
                hideDatePick();
                return;
            case C0291R.id.search_btn:
                this.isLoadingChange = true;
                this.waitload++;
                if (this.startTime.getText().toString().equals("")) {
                    C0568T.showShort(mContext, (int) C0291R.string.search_error1);
                    return;
                } else if (this.endTime.getText().toString().equals("")) {
                    C0568T.showShort(mContext, (int) C0291R.string.search_error2);
                    return;
                } else {
                    if (this.loadFrag == null) {
                        this.loadFrag = new LoadingFragment();
                    }
                    replaceFrag(this.loadFrag, this.fragments[1]);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    try {
                        Date start = sdf.parse(this.startTime.getText().toString());
                        Date end = sdf.parse(this.endTime.getText().toString());
                        Log.e("time1", "search1" + start.toString());
                        Log.e("time2", "search2" + end.toString());
                        if (start.after(end)) {
                            C0568T.showShort(mContext, (int) C0291R.string.search_error3);
                            return;
                        }
                        RecordAdapter recordAdapter = this.madapter;
                        RecordAdapter.setStartTime(start);
                        P2PHandler.getInstance().getRecordFiles(this.contact.contactModel, this.contact.contactId, this.contact.contactPassword, start, end);
                        hideDatePick();
                        return;
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            case C0291R.id.search_detail:
                this.selected_condition = 3;
                updateCondition();
                this.startTime.requestFocus();
                if (!this.isDpShow) {
                    showDatePick();
                    return;
                }
                return;
            case C0291R.id.search_one_day:
                this.isLoadingChange = true;
                if (this.loadFrag == null) {
                    this.loadFrag = new LoadingFragment();
                }
                replaceFrag(this.loadFrag, this.fragments[1]);
                this.selected_condition = 0;
                searchByTime(0);
                updateCondition();
                return;
            case C0291R.id.search_one_month:
                this.rlFrag = new RecordListFragment();
                this.rlFrag.setUser(this.contact);
                this.loadFrag = new LoadingFragment();
                this.faultFrag = new FaultFragment();
                this.isLoadingChange = true;
                if (this.loadFrag == null) {
                    this.loadFrag = new LoadingFragment();
                }
                replaceFrag(this.loadFrag, this.fragments[1]);
                this.selected_condition = 2;
                searchByTime(2);
                updateCondition();
                return;
            case C0291R.id.search_three_day:
                this.isLoadingChange = true;
                if (this.loadFrag == null) {
                    this.loadFrag = new LoadingFragment();
                }
                replaceFrag(this.loadFrag, this.fragments[1]);
                this.selected_condition = 1;
                searchByTime(1);
                updateCondition();
                return;
            default:
                return;
        }
    }

    public void onBackPressed() {
        if (this.isDpShow) {
            hideDatePick();
        } else {
            finish();
        }
    }

    public void showDatePick() {
        this.isDpShow = true;
        this.date_pick.setVisibility(0);
        this.date_pick.startAnimation(AnimationUtils.loadAnimation(this, C0291R.anim.slide_in_bottom));
        if (this.rlFrag != null) {
            this.rlFrag.scrollOff();
        }
    }

    public void hideDatePick() {
        this.isDpShow = false;
        this.date_pick.startAnimation(AnimationUtils.loadAnimation(this, C0291R.anim.slide_out_top));
        this.date_pick.setVisibility(8);
        if (this.rlFrag != null) {
            this.rlFrag.scrollOn();
        }
    }

    public void onStop() {
        super.onStop();
        if (this.receiverIsReg) {
            this.receiverIsReg = false;
            unregisterReceiver(this.receiver);
        }
        if (getSupportFragmentManager().getFragments() != null && getSupportFragmentManager().getFragments().size() > 0) {
            getSupportFragmentManager().getFragments().clear();
        }
    }

    protected void onStart() {
        super.onStart();
        if (!this.receiverIsReg) {
            regFilter();
        }
    }

    public void onDestroy() {
        super.onDestroy();
    }

    public void searchByTime(int time) {
        this.waitload++;
        if (this.contact.contactPassword == null || this.contact.contactPassword.equals("")) {
            finish();
            C0568T.showShort(mContext, (int) C0291R.string.password_error);
            return;
        }
        Log.e("currenttime", String.valueOf(System.currentTimeMillis()));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date endDate = new Date(System.currentTimeMillis());
        String endTime = sdf.format(endDate);
        Log.e("currenttime", endTime);
        String year = endTime.substring(0, 4);
        Calendar ca = Calendar.getInstance();
        ca.setTime(new Date());
        Date resultDate = null;
        switch (time) {
            case 0:
                ca.add(1, 0);
                ca.add(2, 0);
                ca.add(5, -1);
                resultDate = ca.getTime();
                Log.e("starttime", sdf.format(resultDate));
                break;
            case 1:
                ca.add(1, 0);
                ca.add(2, 0);
                ca.add(5, -3);
                resultDate = ca.getTime();
                Log.e("starttime", sdf.format(resultDate));
                break;
            case 2:
                ca.add(1, 0);
                ca.add(2, -1);
                ca.add(5, 0);
                resultDate = ca.getTime();
                Log.e("starttime", sdf.format(resultDate));
                break;
        }
        try {
            Date startDate = sdf.parse(sdf.format(resultDate));
            RecordAdapter.setStartTime(startDate);
            P2PHandler.getInstance().getRecordFiles(this.contact.contactModel, this.contact.contactId, this.contact.contactPassword, startDate, endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void updateCondition() {
        Animation animation = null;
        Log.e("e", this.currIndex + ":index");
        switch (this.selected_condition) {
            case 0:
                this.search_one_day.setTextColor(ViewCompat.MEASURED_STATE_MASK);
                this.search_three_day.setTextColor(getResources().getColor(C0291R.color.gray));
                this.search_one_month.setTextColor(getResources().getColor(C0291R.color.gray));
                this.search_detail.setTextColor(getResources().getColor(C0291R.color.gray));
                this.date_pick.setVisibility(8);
                if (this.currIndex == 1) {
                    animation = new TranslateAnimation((float) this.position_one, 0.0f, 0.0f, 0.0f);
                } else if (this.currIndex == 2) {
                    animation = new TranslateAnimation((float) this.position_two, 0.0f, 0.0f, 0.0f);
                } else if (this.currIndex == 3) {
                    animation = new TranslateAnimation((float) this.position_three, 0.0f, 0.0f, 0.0f);
                }
                this.currIndex = 0;
                break;
            case 1:
                this.search_three_day.setTextColor(ViewCompat.MEASURED_STATE_MASK);
                this.search_one_day.setTextColor(getResources().getColor(C0291R.color.gray));
                this.search_one_month.setTextColor(getResources().getColor(C0291R.color.gray));
                this.search_detail.setTextColor(getResources().getColor(C0291R.color.gray));
                this.date_pick.setVisibility(8);
                if (this.currIndex == 0) {
                    animation = new TranslateAnimation((float) this.offset, (float) this.position_one, 0.0f, 0.0f);
                } else if (this.currIndex == 2) {
                    animation = new TranslateAnimation((float) this.position_two, (float) this.position_one, 0.0f, 0.0f);
                } else if (this.currIndex == 3) {
                    animation = new TranslateAnimation((float) this.position_three, (float) this.position_one, 0.0f, 0.0f);
                }
                this.currIndex = 1;
                break;
            case 2:
                this.search_one_month.setTextColor(ViewCompat.MEASURED_STATE_MASK);
                this.search_three_day.setTextColor(getResources().getColor(C0291R.color.gray));
                this.search_one_day.setTextColor(getResources().getColor(C0291R.color.gray));
                this.search_detail.setTextColor(getResources().getColor(C0291R.color.gray));
                this.date_pick.setVisibility(8);
                if (this.currIndex == 0) {
                    animation = new TranslateAnimation((float) this.offset, (float) this.position_two, 0.0f, 0.0f);
                } else if (this.currIndex == 1) {
                    animation = new TranslateAnimation((float) this.position_one, (float) this.position_two, 0.0f, 0.0f);
                } else if (this.currIndex == 3) {
                    animation = new TranslateAnimation((float) this.position_three, (float) this.position_two, 0.0f, 0.0f);
                }
                this.currIndex = 2;
                break;
            case 3:
                this.search_detail.setTextColor(ViewCompat.MEASURED_STATE_MASK);
                this.search_three_day.setTextColor(getResources().getColor(C0291R.color.gray));
                this.search_one_month.setTextColor(getResources().getColor(C0291R.color.gray));
                this.search_one_day.setTextColor(getResources().getColor(C0291R.color.gray));
                this.date_pick.setVisibility(0);
                if (this.currIndex == 0) {
                    animation = new TranslateAnimation((float) this.offset, (float) this.position_three, 0.0f, 0.0f);
                } else if (this.currIndex == 1) {
                    animation = new TranslateAnimation((float) this.position_one, (float) this.position_three, 0.0f, 0.0f);
                } else if (this.currIndex == 2) {
                    animation = new TranslateAnimation((float) this.position_two, (float) this.position_three, 0.0f, 0.0f);
                }
                this.currIndex = 3;
                break;
        }
        if (animation != null) {
            animation.setFillAfter(true);
            animation.setDuration(300);
            this.cursor.startAnimation(animation);
        }
    }

    public void replaceFrag(Fragment fragment, String mark) {
        try {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setCustomAnimations(17432576, 17432577);
            transaction.replace(C0291R.id.record_container, fragment, mark);
            transaction.addToBackStack(null);
            transaction.commitAllowingStateLoss();
            manager.executePendingTransactions();
        } catch (Exception e) {
            Log.e("my", "replaceFrag error");
        }
    }

    public int getActivityInfo() {
        return 32;
    }

    protected void onSaveInstanceState(Bundle bundle) {
        bundle.putString("recordFrag", this.fragments[0]);
        bundle.putString("loadingFrag", this.fragments[1]);
        bundle.putString("faultFrag", this.fragments[2]);
        super.onSaveInstanceState(bundle);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.e("playbacklist", "onRestore");
        initWidth();
        if (savedInstanceState != null) {
            List<Fragment> listFrag = getSupportFragmentManager().getFragments();
            if (listFrag != null && listFrag.size() > 0) {
                for (Fragment frag : listFrag) {
                    String tag = frag.getTag();
                    if (tag.equals(this.fragments[0])) {
                        this.rlFrag = (RecordListFragment) frag;
                    } else if (tag.equals(this.fragments[1])) {
                        this.loadFrag = (LoadingFragment) frag;
                    } else if (tag.equals(this.fragments[2])) {
                        this.faultFrag = (FaultFragment) frag;
                    }
                }
            }
        }
        super.onRestoreInstanceState(savedInstanceState);
    }
}
