package com.jwkj.fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import com.hikam.C0291R;
import com.jwkj.CallActivity;
import com.jwkj.activity.MainActivity;
import com.jwkj.activity.ShakeActivity;
import com.jwkj.adapter.FilterUserAdapter;
import com.jwkj.data.Contact;
import com.jwkj.data.ContactDB;
import com.jwkj.data.DataManager;
import com.jwkj.global.Constants.Action;
import com.jwkj.global.FList;
import com.jwkj.global.NpcCommon;
import com.jwkj.utils.C0568T;
import com.jwkj.utils.ComparatorUserByFilterUser;
import com.jwkj.widget.MyInputDialog;
import com.jwkj.widget.MyInputDialog.OnButtonOkListener;
import com.p2p.core.network.NetManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.compress.archivers.tar.TarConstants;
import org.apache.http.HttpStatus;

public class KeyboardFrag extends BaseFragment implements OnClickListener {
    static int[] regionCode = new int[]{1264, 1268, 1246, 1242, 1441, 1787, 1809, 1767, 1473, 1671, 1721, 1345, 1684, 1340, 1664, 6723, 1869, 1758, 1784, 1649, 1868, 1876, 1284, 355, 213, 971, 297, 968, 994, 247, 251, 353, 372, 376, 244, 358, 853, 675, 595, 970, 973, HttpStatus.SC_INSUFFICIENT_STORAGE, 375, 359, 229, 354, 591, 387, 267, HttpStatus.SC_NOT_IMPLEMENTED, 975, 226, 257, 850, 240, 670, 228, 593, ShakeActivity.SHAKING_END, 298, 689, 594, 590, 238, 500, 220, 242, 243, 506, 299, 995, 592, 509, 599, 382, 504, 686, 253, 996, 224, 245, 233, 241, 855, 420, TarConstants.VERSION_OFFSET, 237, 974, 269, 225, 965, 385, 254, 682, 371, 266, 856, 961, 231, 218, 370, HttpStatus.SC_LOCKED, 262, 352, 250, 261, 356, 960, 265, 223, 389, 692, 596, 230, 222, 976, 880, 691, 373, 212, 377, BZip2Constants.MAX_ALPHA_SIZE, 264, 211, 977, HttpStatus.SC_HTTP_VERSION_NOT_SUPPORTED, 227, 234, 683, 680, 351, HttpStatus.SC_SERVICE_UNAVAILABLE, 685, 381, 232, 221, 357, 248, 966, 239, 290, 378, TarConstants.XSTAR_MAGIC_OFFSET, 421, 386, 268, 249, 597, 252, 677, 992, 886, 255, 676, 216, 688, 993, 690, 681, 678, HttpStatus.SC_BAD_GATEWAY, 673, 256, 380, 598, NetManager.CONNECT_CHANGE, 852, 687, 963, 374, 967, 964, 972, 246, 962, 260, 235, 350, 236, 674, 379, 679, 93, 54, 20, 43, 61, 92, 55, 32, 48, 45, 49, 33, 63, 57, 44, 53, 82, 31, 40, 60, 51, 95, 52, 27, 47, 81, 46, 41, 94, 66, 90, 58, 34, 30, 65, 64, 36, 98, 39, 91, 62, 84, 56, 86, 1, 7};
    public static String searchTellNum = "";
    MyInputDialog dialog_input;
    RelativeLayout dialog_input_mask;
    private boolean isRegFilter = false;
    private FilterUserAdapter mAdapter;
    private Context mContext;
    private List<Contact> mFilterUser = new ArrayList();
    private Handler mHandler = new C05325();
    private AlertDialog mInputPasswordDialog;
    private RelativeLayout mKeyBoardAite;
    private RelativeLayout mKeyBoardBackspace;
    private RelativeLayout mKeyBoardEight;
    private RelativeLayout mKeyBoardFive;
    private RelativeLayout mKeyBoardFour;
    private RelativeLayout mKeyBoardNine;
    private RelativeLayout mKeyBoardOne;
    private RelativeLayout mKeyBoardSeven;
    private RelativeLayout mKeyBoardSix;
    private RelativeLayout mKeyBoardThree;
    private RelativeLayout mKeyBoardTwo;
    private RelativeLayout mKeyBoardVedio;
    private RelativeLayout mKeyBoardVedioPhone;
    private RelativeLayout mKeyBoardZero;
    private ListView mList;
    private TextView mPhoneNum;
    BroadcastReceiver mReceiver = new C05314();
    OnTouchListener onTouch = new C05303();
    private TextWatcher textWatcher = new C05346();

    class C05281 implements OnItemClickListener {
        C05281() {
        }

        public void onItemClick(AdapterView<?> adapterView, View arg1, int position, long arg3) {
            KeyboardFrag.this.mPhoneNum.setText(((Contact) KeyboardFrag.this.mFilterUser.get(position)).contactId);
        }
    }

    class C05292 implements OnLongClickListener {
        C05292() {
        }

        public boolean onLongClick(View v) {
            KeyboardFrag.this.mPhoneNum.setText("");
            return false;
        }
    }

    class C05303 implements OnTouchListener {
        int down_height;
        int down_y;
        boolean isActive = false;

        C05303() {
        }

        public boolean onTouch(View arg0, MotionEvent event) {
            int y = (int) event.getY();
            LayoutParams params = (LayoutParams) KeyboardFrag.this.mList.getLayoutParams();
            switch (event.getAction()) {
                case 0:
                    this.down_height = params.height;
                    this.down_y = (int) event.getY();
                    this.isActive = true;
                    break;
                case 1:
                    this.isActive = false;
                    break;
                case 2:
                    int change_y = (int) (event.getY() - ((float) this.down_y));
                    this.down_y = (int) event.getY();
                    this.down_height += change_y;
                    params.height = this.down_height;
                    KeyboardFrag.this.mList.setLayoutParams(params);
                    Log.e("my", this.down_height + "");
                    break;
            }
            return true;
        }
    }

    class C05314 extends BroadcastReceiver {
        C05314() {
        }

        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Action.CLOSE_INPUT_DIALOG) && KeyboardFrag.this.dialog_input != null) {
                KeyboardFrag.this.dialog_input.hide(KeyboardFrag.this.dialog_input_mask);
            }
        }
    }

    class C05325 extends Handler {
        C05325() {
        }

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 17:
                    KeyboardFrag.this.mAdapter.upDateData(KeyboardFrag.this.mFilterUser);
                    return;
                default:
                    return;
            }
        }
    }

    class C05346 implements TextWatcher {

        class C05331 extends Thread {
            C05331() {
            }

            public void run() {
                KeyboardFrag.this.mFilterUser.clear();
                for (Contact contact : DataManager.findContactByActiveUser(KeyboardFrag.this.mContext, NpcCommon.mThreeNum)) {
                    if (contact.contactId.contains(KeyboardFrag.searchTellNum)) {
                        KeyboardFrag.this.mFilterUser.add(contact);
                    }
                }
                Collections.sort(KeyboardFrag.this.mFilterUser, new ComparatorUserByFilterUser(KeyboardFrag.searchTellNum));
                KeyboardFrag.this.mHandler.sendEmptyMessage(17);
            }
        }

        C05346() {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (s != null) {
                String searchTellNum = s.toString().trim();
                if (searchTellNum != null && !searchTellNum.equals("") && searchTellNum.length() <= 17) {
                }
            }
        }

        public void afterTextChanged(Editable s) {
            if (s != null) {
                KeyboardFrag.searchTellNum = s.toString().trim();
                if (KeyboardFrag.searchTellNum == null || KeyboardFrag.searchTellNum.equals("")) {
                    KeyboardFrag.this.removeAllFilterUesr();
                    return;
                } else if (KeyboardFrag.searchTellNum.length() < 17) {
                    new C05331().start();
                    return;
                } else {
                    return;
                }
            }
            KeyboardFrag.this.removeAllFilterUesr();
        }
    }

    class C11107 implements OnButtonOkListener {
        C11107() {
        }

        public void onClick() {
            String password = KeyboardFrag.this.dialog_input.getInput1Text();
            if ("".equals(password.trim())) {
                C0568T.showShort(KeyboardFrag.this.mContext, (int) C0291R.string.input_monitor_pwd);
            } else if (password.length() > 9) {
                C0568T.showShort(KeyboardFrag.this.mContext, (int) C0291R.string.password_length_error);
            } else {
                KeyboardFrag.this.dialog_input.hide(KeyboardFrag.this.dialog_input_mask);
                Intent monitor = new Intent();
                monitor.setClass(KeyboardFrag.this.mContext, CallActivity.class);
                monitor.putExtra("callId", KeyboardFrag.this.mPhoneNum.getText().toString());
                monitor.putExtra("password", password);
                monitor.putExtra("isOutCall", true);
                monitor.putExtra("type", 1);
                KeyboardFrag.this.startActivity(monitor);
            }
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(C0291R.layout.fragment_keyboard, container, false);
        Log.e("my", "createKeyboardFrag");
        this.mContext = MainActivity.mContext;
        initComponent(view);
        regFilter();
        return view;
    }

    public void initComponent(View view) {
        this.dialog_input_mask = (RelativeLayout) view.findViewById(C0291R.id.dialog_input_mask);
        this.mPhoneNum = (TextView) view.findViewById(C0291R.id.phoneNumber);
        this.mPhoneNum.addTextChangedListener(this.textWatcher);
        this.mPhoneNum.setOnTouchListener(this.onTouch);
        this.mList = (ListView) view.findViewById(C0291R.id.list_filter_user);
        this.mAdapter = new FilterUserAdapter(this.mContext, this.mFilterUser);
        this.mList.setAdapter(this.mAdapter);
        this.mList.setOnItemClickListener(new C05281());
        this.mKeyBoardOne = (RelativeLayout) view.findViewById(C0291R.id.button_one);
        this.mKeyBoardTwo = (RelativeLayout) view.findViewById(C0291R.id.button_two);
        this.mKeyBoardThree = (RelativeLayout) view.findViewById(C0291R.id.button_three);
        this.mKeyBoardFour = (RelativeLayout) view.findViewById(C0291R.id.button_four);
        this.mKeyBoardFive = (RelativeLayout) view.findViewById(C0291R.id.button_five);
        this.mKeyBoardSix = (RelativeLayout) view.findViewById(C0291R.id.button_six);
        this.mKeyBoardSeven = (RelativeLayout) view.findViewById(C0291R.id.button_seven);
        this.mKeyBoardEight = (RelativeLayout) view.findViewById(C0291R.id.button_eight);
        this.mKeyBoardNine = (RelativeLayout) view.findViewById(C0291R.id.button_nine);
        this.mKeyBoardAite = (RelativeLayout) view.findViewById(C0291R.id.button_aite);
        this.mKeyBoardZero = (RelativeLayout) view.findViewById(C0291R.id.button_zero);
        this.mKeyBoardBackspace = (RelativeLayout) view.findViewById(C0291R.id.button_backspace);
        this.mKeyBoardVedio = (RelativeLayout) view.findViewById(C0291R.id.button_vedio);
        this.mKeyBoardVedioPhone = (RelativeLayout) view.findViewById(C0291R.id.button_vedio_phone);
        this.mKeyBoardOne.setOnClickListener(this);
        this.mKeyBoardTwo.setOnClickListener(this);
        this.mKeyBoardThree.setOnClickListener(this);
        this.mKeyBoardFour.setOnClickListener(this);
        this.mKeyBoardFive.setOnClickListener(this);
        this.mKeyBoardSix.setOnClickListener(this);
        this.mKeyBoardSeven.setOnClickListener(this);
        this.mKeyBoardEight.setOnClickListener(this);
        this.mKeyBoardNine.setOnClickListener(this);
        this.mKeyBoardAite.setOnClickListener(this);
        this.mKeyBoardZero.setOnClickListener(this);
        this.mKeyBoardBackspace.setOnClickListener(this);
        this.mKeyBoardBackspace.setOnLongClickListener(new C05292());
        this.mKeyBoardVedio.setOnClickListener(this);
        this.mKeyBoardVedioPhone.setOnClickListener(this);
    }

    public void regFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Action.CLOSE_INPUT_DIALOG);
        this.mContext.registerReceiver(this.mReceiver, filter);
        this.isRegFilter = true;
    }

    private void removeAllFilterUesr() {
        this.mAdapter.clear();
    }

    private void spellNum(String value) {
        String number = this.mPhoneNum.getText().toString();
        int selectIndex;
        if (number == null || number.length() <= 0) {
            number = value;
            selectIndex = number.length();
        } else if (number.length() < 17) {
            int cursorIndex = this.mPhoneNum.getSelectionEnd();
            if (this.mPhoneNum.getSelectionEnd() == 0) {
                number = value + number;
                selectIndex = number.length();
            } else if (this.mPhoneNum.getSelectionEnd() > 0 && cursorIndex < number.length()) {
                String startNum = number.substring(0, cursorIndex);
                number = startNum + value + number.substring(cursorIndex);
                selectIndex = cursorIndex + 1;
            } else if (this.mPhoneNum.getSelectionEnd() <= 0 || cursorIndex != number.length()) {
                number = number + value;
            } else {
                number = number + value;
                selectIndex = number.length();
            }
        } else {
            return;
        }
        this.mPhoneNum.setText(number);
    }

    public void onDestroy() {
        super.onDestroy();
        if (this.isRegFilter) {
            this.isRegFilter = false;
            this.mContext.unregisterReceiver(this.mReceiver);
        }
        if (this.dialog_input != null) {
            this.dialog_input.hide(this.dialog_input_mask);
        }
        clearEditText();
    }

    public void onResume() {
        super.onResume();
    }

    private void clearEditText() {
        if (this.mPhoneNum != null) {
            this.mPhoneNum.setText("");
        }
    }

    public void onClick(View v) {
        String number = this.mPhoneNum.getText().toString();
        Contact contact;
        switch (v.getId()) {
            case C0291R.id.button_aite:
                spellNum("+");
                return;
            case C0291R.id.button_backspace:
                if (number == null || number.length() <= 0) {
                    removeAllFilterUesr();
                    return;
                }
                number = number.substring(0, number.length() - 1);
                this.mPhoneNum.setText(number);
                if (number == null || number.length() <= 0) {
                    removeAllFilterUesr();
                    return;
                }
                return;
            case C0291R.id.button_eight:
                spellNum("8");
                return;
            case C0291R.id.button_five:
                spellNum("5");
                return;
            case C0291R.id.button_four:
                spellNum("4");
                return;
            case C0291R.id.button_nine:
                spellNum("9");
                return;
            case C0291R.id.button_one:
                spellNum("1");
                return;
            case C0291R.id.button_seven:
                spellNum("7");
                return;
            case C0291R.id.button_six:
                spellNum("6");
                return;
            case C0291R.id.button_three:
                spellNum("3");
                return;
            case C0291R.id.button_two:
                spellNum("2");
                return;
            case C0291R.id.button_vedio:
                if (number == null || number.trim().length() <= 0) {
                    C0568T.showShort(this.mContext, (int) C0291R.string.dialog_tip);
                    return;
                }
                contact = FList.getInstance().isContact(number);
                if (contact == null || contact.contactId == null || contact.contactPassword.equals("")) {
                    showInputPwd();
                    return;
                }
                Intent monitor = new Intent();
                monitor.setClass(this.mContext, CallActivity.class);
                monitor.putExtra("callId", number);
                monitor.putExtra(ContactDB.COLUMN_CONTACT_NAME, contact.contactName);
                monitor.putExtra("password", contact.contactPassword);
                monitor.putExtra("isOutCall", true);
                monitor.putExtra("type", 1);
                startActivity(monitor);
                return;
            case C0291R.id.button_vedio_phone:
                if (number == null || number.trim().length() <= 0) {
                    C0568T.showShort(this.mContext, (int) C0291R.string.dialog_tip);
                    return;
                }
                String parseNum = number;
                Intent call = new Intent();
                call.setClass(this.mContext, CallActivity.class);
                call.putExtra("callId", parseNum);
                contact = DataManager.findContactByActiveUserAndContactId(this.mContext, NpcCommon.mThreeNum, number);
                if (!(contact == null || contact.contactName == null || contact.contactName.equals(""))) {
                    call.putExtra(ContactDB.COLUMN_CONTACT_NAME, contact.contactName);
                }
                call.putExtra("isOutCall", true);
                call.putExtra("type", 0);
                startActivity(call);
                return;
            case C0291R.id.button_zero:
                spellNum("0");
                return;
            default:
                return;
        }
    }

    public void showInputPwd() {
        this.dialog_input = new MyInputDialog(this.mContext);
        this.dialog_input.setTitle(this.mContext.getResources().getString(C0291R.string.monitor));
        this.dialog_input.setBtn1_str(this.mContext.getResources().getString(C0291R.string.ensure));
        this.dialog_input.setBtn2_str(this.mContext.getResources().getString(C0291R.string.cancel));
        this.dialog_input.setOnButtonOkListener(new C11107());
        this.dialog_input.show(this.dialog_input_mask);
        this.dialog_input.setInput1Type_number();
        this.dialog_input.setInput1HintText((int) C0291R.string.input_device_pwd);
    }

    public boolean IsInputDialogShowing() {
        if (this.dialog_input != null) {
            return this.dialog_input.isShowing();
        }
        return false;
    }
}
