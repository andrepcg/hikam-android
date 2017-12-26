package com.jwkj.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import com.hikam.C0291R;

public class AltogetherRegisterActivity extends BaseActivity implements OnClickListener {
    int current_type;
    RadioButton email_register;
    Context mcontext;
    Button mregister;
    RadioButton phone_register;

    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(C0291R.layout.activity_register);
        this.mcontext = this;
        initComponent();
    }

    public void initComponent() {
        this.mregister = (Button) findViewById(C0291R.id.register);
        this.phone_register = (RadioButton) findViewById(C0291R.id.register_type_phone);
        this.email_register = (RadioButton) findViewById(C0291R.id.register_type_email);
        this.mregister.setOnClickListener(this);
        this.phone_register.setOnClickListener(this);
        this.email_register.setOnClickListener(this);
        this.phone_register.setChecked(true);
        this.email_register.setChecked(false);
        this.current_type = 0;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case C0291R.id.register:
                if (this.current_type == 0) {
                    startActivity(new Intent(this.mcontext, RegisterActivity.class));
                } else {
                    Intent register_email = new Intent(this.mcontext, RegisterActivity2.class);
                    register_email.putExtra("isEmailRegister", true);
                    startActivity(register_email);
                }
                finish();
                return;
            case C0291R.id.register_type_email:
                this.email_register.setChecked(true);
                this.phone_register.setChecked(false);
                this.current_type = 1;
                return;
            case C0291R.id.register_type_phone:
                this.phone_register.setChecked(true);
                this.email_register.setChecked(false);
                this.current_type = 0;
                return;
            default:
                return;
        }
    }

    public int getActivityInfo() {
        return 45;
    }
}
