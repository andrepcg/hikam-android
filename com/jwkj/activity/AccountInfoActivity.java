package com.jwkj.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.hikam.C0291R;
import com.jwkj.entity.Account;
import com.jwkj.global.AccountPersist;
import com.jwkj.global.Constants;
import com.jwkj.utils.C0568T;

public class AccountInfoActivity extends BaseActivity implements OnClickListener {
    ImageView back_btn;
    RelativeLayout change_3c;
    RelativeLayout change_email;
    TextView email_text;
    Context mContext;
    RelativeLayout modify_login_pwd;
    TextView phone_text;
    TextView three_number_text;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0291R.layout.activity_account);
        this.mContext = this;
        initCompent();
        loadAccountInfo();
    }

    public void initCompent() {
        this.back_btn = (ImageView) findViewById(C0291R.id.back_btn);
        this.change_3c = (RelativeLayout) findViewById(C0291R.id.change_3c);
        this.change_email = (RelativeLayout) findViewById(C0291R.id.change_email);
        this.three_number_text = (TextView) findViewById(C0291R.id.three_number_text);
        this.email_text = (TextView) findViewById(C0291R.id.email_text);
        this.phone_text = (TextView) findViewById(C0291R.id.phone_text);
        Account account = AccountPersist.getInstance().getActiveAccountInfo(this.mContext);
        String three_number = "N/A";
        if (account != null) {
            three_number = (account.three_number2 == null || "000000".equals(account.three_number2)) ? "N/A" : account.three_number2;
        }
        this.three_number_text.setText(three_number);
        this.modify_login_pwd = (RelativeLayout) findViewById(C0291R.id.modify_login_pwd);
        this.modify_login_pwd.setOnClickListener(this);
        this.back_btn.setOnClickListener(this);
        this.change_3c.setOnClickListener(this);
        this.change_email.setOnClickListener(this);
    }

    void loadAccountInfo() {
        Account account = AccountPersist.getInstance().getActiveAccountInfo(this.mContext);
        String email = "";
        String phone = "";
        String countryCode = "86";
        if (account != null) {
            email = account.email;
            phone = account.phone;
            countryCode = account.countryCode;
        }
        if (email.equals("")) {
            this.email_text.setText(C0291R.string.unbound);
        } else {
            if (email.endsWith(Constants.HIKAM_EMAIL_SUFFIX)) {
                email = email.substring(0, email.indexOf(Constants.HIKAM_EMAIL_SUFFIX));
            }
            if (account.three_number.contains(Constants.HIKAM_EMAIL_SUFFIX)) {
                email = account.three_number;
                email = email.substring(0, email.indexOf(Constants.HIKAM_EMAIL_SUFFIX));
            }
            this.email_text.setText(email);
        }
        if (phone.equals("0") || phone.equals("")) {
            this.phone_text.setText(C0291R.string.unbound);
        } else {
            this.phone_text.setText("+" + countryCode + "-" + phone);
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case C0291R.id.back_btn:
                finish();
                return;
            case C0291R.id.modify_login_pwd:
                C0568T.showShort(this.mContext, (int) C0291R.string.not_support);
                return;
            default:
                return;
        }
    }

    public void onResume() {
        super.onResume();
        loadAccountInfo();
    }

    public int getActivityInfo() {
        return 5;
    }
}
