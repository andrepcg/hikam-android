package com.jwkj.widget.guide;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.hikam.C0291R;
import com.jwkj.CallActivity;
import com.jwkj.activity.BaseActivity;
import com.jwkj.data.Contact;
import com.jwkj.data.ContactDB;

public class AirLinkGuideDoneActivity extends BaseActivity implements OnClickListener {
    private ImageView img_back;
    private boolean isRegFilter = false;
    private Context mContext;
    private RelativeLayout rl_add;
    private RelativeLayout rl_exit;
    private RelativeLayout rl_live;
    private Contact saveContact;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0291R.layout.activity_guide_airlink_done);
        this.saveContact = (Contact) getIntent().getSerializableExtra(ContactDB.TABLE_NAME);
        this.mContext = this;
        initComponent();
        regFilter();
    }

    private void regFilter() {
    }

    private void initComponent() {
        this.img_back = (ImageView) findViewById(C0291R.id.img_back);
        this.rl_live = (RelativeLayout) findViewById(C0291R.id.rl_live);
        this.rl_exit = (RelativeLayout) findViewById(C0291R.id.rl_exit);
        this.rl_add = (RelativeLayout) findViewById(C0291R.id.rl_add);
        this.rl_add.setOnClickListener(this);
        this.rl_exit.setOnClickListener(this);
        this.rl_live.setOnClickListener(this);
        this.img_back.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case C0291R.id.img_back:
                finish();
                return;
            case C0291R.id.rl_add:
                startActivity(new Intent(this, IndexGuideActivity.class));
                finish();
                return;
            case C0291R.id.rl_exit:
                finish();
                return;
            case C0291R.id.rl_live:
                Intent monitor = new Intent();
                monitor.setClass(this, CallActivity.class);
                monitor.putExtra("callModel", this.saveContact.contactModel);
                monitor.putExtra("callId", this.saveContact.contactId);
                monitor.putExtra(ContactDB.COLUMN_CONTACT_NAME, this.saveContact.contactName);
                monitor.putExtra("password", this.saveContact.contactPassword);
                monitor.putExtra("isOutCall", true);
                monitor.putExtra("type", 1);
                Log.e("oaosj", "call: " + this.saveContact.contactModel + " " + this.saveContact.contactId + " " + this.saveContact.contactName + " " + this.saveContact.contactPassword + " " + true + " " + 1);
                startActivity(monitor);
                finish();
                return;
            default:
                return;
        }
    }

    public int getActivityInfo() {
        return 103;
    }
}
