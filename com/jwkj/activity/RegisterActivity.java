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
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.hikam.C0291R;
import com.jwkj.global.Constants.Action;
import com.jwkj.global.MyApp;
import com.jwkj.utils.C0568T;
import com.jwkj.utils.Utils;
import com.jwkj.widget.NormalDialog;
import com.p2p.core.network.NetManager;

public class RegisterActivity extends BaseActivity implements OnClickListener {
    private RelativeLayout choose_country;
    private TextView dfault_count;
    private TextView dfault_name;
    NormalDialog dialog;
    boolean isDialogCanel = false;
    private Context mContext;
    private Button mNext;
    BroadcastReceiver mReceiver = new C04351();
    boolean myreceiverIsReg = false;
    private EditText phoneNum;

    class C04351 extends BroadcastReceiver {
        C04351() {
        }

        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Action.ACTION_COUNTRY_CHOOSE)) {
                String[] info = intent.getStringArrayExtra("info");
                RegisterActivity.this.dfault_name.setText(info[0]);
                RegisterActivity.this.dfault_count.setText("+" + info[1]);
            }
        }
    }

    class C04362 implements OnCancelListener {
        C04362() {
        }

        public void onCancel(DialogInterface dialog) {
            RegisterActivity.this.isDialogCanel = true;
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
            return Integer.valueOf(NetManager.getInstance(RegisterActivity.this.mContext).getPhoneCode(this.CountryCode, this.PhoneNO));
        }

        protected void onPostExecute(Object object) {
            switch (((Integer) object).intValue()) {
                case 0:
                    if (!RegisterActivity.this.isDialogCanel) {
                        if (RegisterActivity.this.dialog != null) {
                            RegisterActivity.this.dialog.dismiss();
                            RegisterActivity.this.dialog = null;
                        }
                        if (!RegisterActivity.this.isDialogCanel) {
                            Intent i;
                            if (this.CountryCode.equals("86")) {
                                i = new Intent(RegisterActivity.this.mContext, VerifyPhoneActivity.class);
                                i.putExtra("phone", this.PhoneNO);
                                i.putExtra("count", this.CountryCode);
                                RegisterActivity.this.startActivity(i);
                                RegisterActivity.this.finish();
                                return;
                            }
                            i = new Intent(RegisterActivity.this.mContext, RegisterActivity2.class);
                            i.putExtra("phone", this.PhoneNO);
                            i.putExtra("count", this.CountryCode);
                            RegisterActivity.this.startActivity(i);
                            RegisterActivity.this.finish();
                            return;
                        }
                        return;
                    }
                    return;
                case 6:
                    if (RegisterActivity.this.dialog != null) {
                        RegisterActivity.this.dialog.dismiss();
                        RegisterActivity.this.dialog = null;
                    }
                    if (!RegisterActivity.this.isDialogCanel) {
                        Utils.showPromptDialog(RegisterActivity.this.mContext, C0291R.string.prompt, C0291R.string.phone_number_used);
                        return;
                    }
                    return;
                case 9:
                    if (RegisterActivity.this.dialog != null) {
                        RegisterActivity.this.dialog.dismiss();
                        RegisterActivity.this.dialog = null;
                    }
                    if (!RegisterActivity.this.isDialogCanel) {
                        Utils.showPromptDialog(RegisterActivity.this.mContext, C0291R.string.prompt, C0291R.string.phone_format_error);
                        return;
                    }
                    return;
                case 23:
                    Intent relogin = new Intent();
                    relogin.setAction(Action.SESSION_ID_ERROR);
                    MyApp.app.sendBroadcast(relogin);
                    return;
                case 27:
                    if (RegisterActivity.this.dialog != null) {
                        RegisterActivity.this.dialog.dismiss();
                        RegisterActivity.this.dialog = null;
                    }
                    if (!RegisterActivity.this.isDialogCanel) {
                        C0568T.showShort(RegisterActivity.this.mContext, (int) C0291R.string.get_phone_code_too_times);
                        return;
                    }
                    return;
                case NetManager.CONNECT_CHANGE /*998*/:
                    new GetPhoneCodeTask(this.CountryCode, this.PhoneNO).execute(new Object[0]);
                    return;
                default:
                    if (RegisterActivity.this.dialog != null) {
                        RegisterActivity.this.dialog.dismiss();
                        RegisterActivity.this.dialog = null;
                    }
                    if (!RegisterActivity.this.isDialogCanel) {
                        Utils.showPromptDialog(RegisterActivity.this.mContext, C0291R.string.prompt, C0291R.string.registerfail);
                        return;
                    }
                    return;
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0291R.layout.register_form);
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
        if (getResources().getConfiguration().locale.getCountry().equals("TW")) {
            this.dfault_count.setText("+886");
            this.dfault_name.setText(SearchListActivity.getNameByCode(this.mContext, 886));
        } else if (getResources().getConfiguration().locale.getCountry().equals("CN")) {
            this.dfault_count.setText("+86");
            this.dfault_name.setText(SearchListActivity.getNameByCode(this.mContext, 86));
        } else {
            this.dfault_count.setText("+1");
            this.dfault_name.setText(SearchListActivity.getNameByCode(this.mContext, 1));
        }
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
            this.dialog.setOnCancelListener(new C04362());
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

    public int getActivityInfo() {
        return 21;
    }
}
