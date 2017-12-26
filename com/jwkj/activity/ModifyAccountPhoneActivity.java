package com.jwkj.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.hikam.C0291R;
import com.jwkj.entity.Account;
import com.jwkj.global.AccountPersist;
import com.jwkj.global.Constants.Action;
import com.jwkj.global.MyApp;
import com.jwkj.global.NpcCommon;
import com.jwkj.utils.C0568T;
import com.jwkj.utils.Utils;
import com.jwkj.widget.MyInputDialog;
import com.jwkj.widget.MyInputDialog.OnButtonOkListener;
import com.jwkj.widget.NormalDialog;
import com.p2p.core.network.NetManager;

public class ModifyAccountPhoneActivity extends BaseActivity implements OnClickListener {
    private RelativeLayout choose_country;
    private TextView dfault_count;
    private TextView dfault_name;
    NormalDialog dialog;
    MyInputDialog dialog_input;
    RelativeLayout dialog_input_mask;
    boolean isDialogCanel = false;
    private ImageView mBack;
    private Context mContext;
    private Button mNext;
    BroadcastReceiver mReceiver = new C04171();
    boolean myreceiverIsReg = false;
    private EditText phoneNum;

    class C04171 extends BroadcastReceiver {
        C04171() {
        }

        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Action.ACTION_COUNTRY_CHOOSE)) {
                String[] info = intent.getStringArrayExtra("info");
                ModifyAccountPhoneActivity.this.dfault_name.setText(info[0]);
                ModifyAccountPhoneActivity.this.dfault_count.setText("+" + info[1]);
            }
        }
    }

    class C04182 implements OnCancelListener {
        C04182() {
        }

        public void onCancel(DialogInterface dialog) {
            ModifyAccountPhoneActivity.this.isDialogCanel = true;
        }
    }

    class GetPhoneCodeTask extends AsyncTask {
        String CountryCode;
        String PhoneNO;

        public GetPhoneCodeTask(String CountryCode, String PhoneNO) {
            this.CountryCode = CountryCode;
            this.PhoneNO = PhoneNO;
        }

        protected Object doInBackground(Object... params) {
            Utils.sleepThread(1000);
            return Integer.valueOf(NetManager.getInstance(ModifyAccountPhoneActivity.this.mContext).getPhoneCode(this.CountryCode, this.PhoneNO));
        }

        protected void onPostExecute(Object object) {
            switch (((Integer) object).intValue()) {
                case 0:
                    if (!ModifyAccountPhoneActivity.this.isDialogCanel) {
                        if (ModifyAccountPhoneActivity.this.dialog != null) {
                            ModifyAccountPhoneActivity.this.dialog.dismiss();
                            ModifyAccountPhoneActivity.this.dialog = null;
                        }
                        if (!ModifyAccountPhoneActivity.this.isDialogCanel) {
                            if (this.CountryCode.equals("86")) {
                                Intent i = new Intent(ModifyAccountPhoneActivity.this.mContext, ModifyAccountPhoneActivity2.class);
                                i.putExtra("phone", this.PhoneNO);
                                i.putExtra("countryCode", this.CountryCode);
                                ModifyAccountPhoneActivity.this.startActivity(i);
                                ModifyAccountPhoneActivity.this.finish();
                                return;
                            }
                            ModifyAccountPhoneActivity.this.showInputPwd(this.PhoneNO, this.CountryCode);
                            return;
                        }
                        return;
                    }
                    return;
                case 6:
                    if (ModifyAccountPhoneActivity.this.dialog != null) {
                        ModifyAccountPhoneActivity.this.dialog.dismiss();
                        ModifyAccountPhoneActivity.this.dialog = null;
                    }
                    if (!ModifyAccountPhoneActivity.this.isDialogCanel) {
                        C0568T.showShort(ModifyAccountPhoneActivity.this.mContext, (int) C0291R.string.phone_number_used);
                        return;
                    }
                    return;
                case 23:
                    Intent relogin = new Intent();
                    relogin.setAction(Action.SESSION_ID_ERROR);
                    MyApp.app.sendBroadcast(relogin);
                    return;
                case 27:
                    if (ModifyAccountPhoneActivity.this.dialog != null) {
                        ModifyAccountPhoneActivity.this.dialog.dismiss();
                        ModifyAccountPhoneActivity.this.dialog = null;
                    }
                    if (!ModifyAccountPhoneActivity.this.isDialogCanel) {
                        C0568T.showShort(ModifyAccountPhoneActivity.this.mContext, (int) C0291R.string.get_phone_code_too_times);
                        return;
                    }
                    return;
                case NetManager.CONNECT_CHANGE /*998*/:
                    new GetPhoneCodeTask(this.CountryCode, this.PhoneNO).execute(new Object[0]);
                    return;
                default:
                    if (ModifyAccountPhoneActivity.this.dialog != null) {
                        ModifyAccountPhoneActivity.this.dialog.dismiss();
                        ModifyAccountPhoneActivity.this.dialog = null;
                    }
                    if (!ModifyAccountPhoneActivity.this.isDialogCanel) {
                        C0568T.showShort(ModifyAccountPhoneActivity.this.mContext, (int) C0291R.string.operator_error);
                        return;
                    }
                    return;
            }
        }
    }

    class SetAccountInfoTask extends AsyncTask {
        private String countryCode;
        private String password;
        private String phone;

        public SetAccountInfoTask(String password, String phone, String countryCode) {
            this.password = password;
            this.phone = phone;
            this.countryCode = countryCode;
        }

        protected Object doInBackground(Object... params) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Account account = AccountPersist.getInstance().getActiveAccountInfo(ModifyAccountPhoneActivity.this.mContext);
            return Integer.valueOf(NetManager.getInstance(ModifyAccountPhoneActivity.this.mContext).setAccountInfo(NpcCommon.mThreeNum, this.phone, account.email, this.countryCode, account.sessionId, this.password, "1", ""));
        }

        protected void onPostExecute(Object object) {
            switch (((Integer) object).intValue()) {
                case 0:
                    if (ModifyAccountPhoneActivity.this.dialog != null && ModifyAccountPhoneActivity.this.dialog.isShowing()) {
                        ModifyAccountPhoneActivity.this.dialog.dismiss();
                        ModifyAccountPhoneActivity.this.dialog = null;
                    }
                    Account account = AccountPersist.getInstance().getActiveAccountInfo(ModifyAccountPhoneActivity.this.mContext);
                    account.phone = this.phone;
                    account.countryCode = this.countryCode;
                    AccountPersist.getInstance().setActiveAccount(ModifyAccountPhoneActivity.this.mContext, account);
                    C0568T.showShort(ModifyAccountPhoneActivity.this.mContext, (int) C0291R.string.modify_success);
                    ModifyAccountPhoneActivity.this.finish();
                    return;
                case 3:
                    if (ModifyAccountPhoneActivity.this.dialog != null && ModifyAccountPhoneActivity.this.dialog.isShowing()) {
                        ModifyAccountPhoneActivity.this.dialog.dismiss();
                        ModifyAccountPhoneActivity.this.dialog = null;
                    }
                    C0568T.showShort(ModifyAccountPhoneActivity.this.mContext, (int) C0291R.string.password_error);
                    return;
                case 23:
                    Intent i = new Intent();
                    i.setAction(Action.SESSION_ID_ERROR);
                    MyApp.app.sendBroadcast(i);
                    return;
                case NetManager.CONNECT_CHANGE /*998*/:
                    new SetAccountInfoTask(this.password, this.phone, this.countryCode).execute(new Object[0]);
                    return;
                default:
                    if (ModifyAccountPhoneActivity.this.dialog != null && ModifyAccountPhoneActivity.this.dialog.isShowing()) {
                        ModifyAccountPhoneActivity.this.dialog.dismiss();
                        ModifyAccountPhoneActivity.this.dialog = null;
                    }
                    C0568T.showShort(ModifyAccountPhoneActivity.this.mContext, (int) C0291R.string.operator_error);
                    return;
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0291R.layout.activity_modify_account_phone);
        this.mContext = this;
        initCompent();
        regFilter();
    }

    public void initCompent() {
        this.mNext = (Button) findViewById(C0291R.id.next);
        this.phoneNum = (EditText) findViewById(C0291R.id.account_name);
        this.choose_country = (RelativeLayout) findViewById(C0291R.id.country);
        this.dfault_name = (TextView) findViewById(C0291R.id.name);
        this.dfault_count = (TextView) findViewById(C0291R.id.count);
        this.mBack = (ImageView) findViewById(C0291R.id.back_btn);
        Account account = AccountPersist.getInstance().getActiveAccountInfo(this.mContext);
        String phone = "";
        String countryCode = "86";
        if (account != null) {
            phone = account.phone;
            countryCode = account.countryCode;
            if (countryCode.equals("") || countryCode.equals("0")) {
                countryCode = "86";
            }
            if (phone.equals("0")) {
                phone = "";
            }
        }
        String countryName = SearchListActivity.getNameByCode(this.mContext, Integer.parseInt(countryCode));
        this.dfault_count.setText("+" + countryCode);
        this.dfault_name.setText(countryName);
        this.phoneNum.setText(phone);
        this.dialog_input_mask = (RelativeLayout) findViewById(C0291R.id.dialog_input_mask);
        this.mBack.setOnClickListener(this);
        this.mNext.setOnClickListener(this);
        this.choose_country.setOnClickListener(this);
    }

    public void regFilter() {
        this.myreceiverIsReg = true;
        IntentFilter filter = new IntentFilter();
        filter.addAction(Action.ACTION_COUNTRY_CHOOSE);
        registerReceiver(this.mReceiver, filter);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case C0291R.id.back_btn:
                finish();
                return;
            case C0291R.id.country:
                startActivity(new Intent(this, SearchListActivity.class));
                return;
            case C0291R.id.next:
                getPhoneCode();
                return;
            default:
                return;
        }
    }

    private void getPhoneCode() {
        String phone = this.phoneNum.getText().toString();
        if (phone == null || phone.equals("")) {
            C0568T.showShort(this.mContext, (int) C0291R.string.input_phone);
        } else if (phone.length() < 6 || phone.length() > 15) {
            C0568T.showShort((Context) this, (int) C0291R.string.phone_too_long);
        } else {
            this.dialog = new NormalDialog(this, getResources().getString(C0291R.string.waiting_verify_code), "", "", "");
            this.dialog.setStyle(2);
            this.dialog.setOnCancelListener(new C04182());
            this.isDialogCanel = false;
            this.dialog.showDialog();
            String count = this.dfault_count.getText().toString();
            new GetPhoneCodeTask(count.substring(1, count.length()), phone).execute(new Object[0]);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (this.myreceiverIsReg) {
            unregisterReceiver(this.mReceiver);
        }
    }

    public void showInputPwd(final String phone, final String countryCode) {
        this.dialog_input = new MyInputDialog(this.mContext);
        this.dialog_input.setTitle(this.mContext.getResources().getString(C0291R.string.change_phone));
        this.dialog_input.setBtn1_str(this.mContext.getResources().getString(C0291R.string.ensure));
        this.dialog_input.setBtn2_str(this.mContext.getResources().getString(C0291R.string.cancel));
        this.dialog_input.setOnButtonOkListener(new OnButtonOkListener() {
            public void onClick() {
                String password = ModifyAccountPhoneActivity.this.dialog_input.getInput1Text();
                if ("".equals(password.trim())) {
                    C0568T.showShort(ModifyAccountPhoneActivity.this.mContext, (int) C0291R.string.input_login_pwd);
                    return;
                }
                ModifyAccountPhoneActivity.this.dialog_input.hide(ModifyAccountPhoneActivity.this.dialog_input_mask);
                if (ModifyAccountPhoneActivity.this.dialog == null) {
                    ModifyAccountPhoneActivity.this.dialog = new NormalDialog(ModifyAccountPhoneActivity.this.mContext, ModifyAccountPhoneActivity.this.mContext.getResources().getString(C0291R.string.verification), "", "", "");
                    ModifyAccountPhoneActivity.this.dialog.setStyle(2);
                }
                ModifyAccountPhoneActivity.this.dialog.showDialog();
                new SetAccountInfoTask(password, phone, countryCode).execute(new Object[0]);
            }
        });
        this.dialog_input.show(this.dialog_input_mask);
        this.dialog_input.setInput1HintText((int) C0291R.string.input_login_pwd);
    }

    public void onBackPressed() {
        if (this.dialog_input == null || !this.dialog_input.isShowing()) {
            finish();
        } else {
            this.dialog_input.hide(this.dialog_input_mask);
        }
    }

    public int getActivityInfo() {
        return 15;
    }
}
