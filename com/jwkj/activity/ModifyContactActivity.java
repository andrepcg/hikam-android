package com.jwkj.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.hikam.C0291R;
import com.jwkj.data.Contact;
import com.jwkj.data.ContactDB;
import com.jwkj.global.Constants.Action;
import com.jwkj.global.Constants.Image;
import com.jwkj.global.FList;
import com.jwkj.utils.C0568T;
import com.jwkj.utils.ImageUtils;
import com.jwkj.utils.Utils;
import com.jwkj.widget.HeaderView;
import com.jwkj.widget.NormalDialog;
import com.p2p.core.P2PHandler;
import com.p2p.core.P2PValue;

public class ModifyContactActivity extends BaseActivity implements OnClickListener {
    private static final int RESULT_CUT_IMAGE = 19;
    private static final int RESULT_GETIMG_FROM_CAMERA = 17;
    private static final int RESULT_GETIMG_FROM_GALLERY = 18;
    TextView contactId;
    EditText contactName;
    EditText contactPwd;
    NormalDialog dialog;
    HeaderView header_img;
    LinearLayout layout_device_pwd;
    private ImageView mBack;
    Context mContext;
    Contact mModifyContact;
    private TextView mSave;
    RelativeLayout modify_header;
    private Bitmap tempHead;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0291R.layout.activity_modify_contact);
        this.mModifyContact = (Contact) getIntent().getSerializableExtra(ContactDB.TABLE_NAME);
        this.mContext = this;
        initCompent();
    }

    public void initCompent() {
        this.contactId = (TextView) findViewById(C0291R.id.contactId);
        this.contactName = (EditText) findViewById(C0291R.id.contactName);
        this.contactPwd = (EditText) findViewById(C0291R.id.contactPwd);
        this.contactPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
        this.layout_device_pwd = (LinearLayout) findViewById(C0291R.id.layout_device_pwd);
        this.header_img = (HeaderView) findViewById(C0291R.id.header_img);
        this.header_img.updateImage(this.mModifyContact.contactId, false);
        this.mBack = (ImageView) findViewById(C0291R.id.back_btn);
        this.mSave = (TextView) findViewById(C0291R.id.save);
        this.modify_header = (RelativeLayout) findViewById(C0291R.id.modify_header);
        if (this.mModifyContact.contactType != 3) {
            this.layout_device_pwd.setVisibility(0);
        } else {
            this.layout_device_pwd.setVisibility(8);
        }
        if (this.mModifyContact != null) {
            this.contactId.setText(Utils.showShortDevID(this.mModifyContact.contactId));
            this.contactName.setText(this.mModifyContact.contactName);
            this.contactPwd.setText(this.mModifyContact.contactPassword);
        }
        this.modify_header.setOnClickListener(this);
        this.mBack.setOnClickListener(this);
        this.mSave.setOnClickListener(this);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Intent cutImage;
        if (requestCode == 17) {
            try {
                this.tempHead = (Bitmap) data.getExtras().get("data");
                Log.e("my", this.tempHead.getWidth() + ":" + this.tempHead.getHeight());
                ImageUtils.saveImg(this.tempHead, Image.USER_HEADER_PATH, Image.USER_HEADER_TEMP_FILE_NAME);
                cutImage = new Intent(this.mContext, CutImageActivity.class);
                cutImage.putExtra(ContactDB.TABLE_NAME, this.mModifyContact);
                startActivityForResult(cutImage, 19);
            } catch (NullPointerException e) {
                Log.e("my", "用户终止..");
            }
        } else if (requestCode == 18) {
            try {
                this.tempHead = ImageUtils.getBitmap(ImageUtils.getAbsPath(this.mContext, data.getData()), 500, 500);
                ImageUtils.saveImg(this.tempHead, Image.USER_HEADER_PATH, Image.USER_HEADER_TEMP_FILE_NAME);
                cutImage = new Intent(this.mContext, CutImageActivity.class);
                cutImage.putExtra(ContactDB.TABLE_NAME, this.mModifyContact);
                startActivityForResult(cutImage, 19);
            } catch (NullPointerException e2) {
                Log.e("my", "用户终止..");
            }
        } else if (requestCode == 19) {
            Log.e("my", resultCode + "");
            if (resultCode == 1) {
                try {
                    this.header_img.updateImage(this.mModifyContact.contactId, false);
                    Intent refreshContans = new Intent();
                    refreshContans.setAction(Action.REFRESH_CONTANTS);
                    refreshContans.putExtra(ContactDB.TABLE_NAME, this.mModifyContact);
                    this.mContext.sendBroadcast(refreshContans);
                } catch (Exception e3) {
                    e3.printStackTrace();
                }
            }
        }
    }

    public void destroyTempHead() {
        if (this.tempHead != null && !this.tempHead.isRecycled()) {
            this.tempHead.recycle();
            this.tempHead = null;
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case C0291R.id.back_btn:
                finish();
                return;
            case C0291R.id.save:
                save();
                return;
            default:
                return;
        }
    }

    void save() {
        String input_name = this.contactName.getText().toString();
        String input_pwd = this.contactPwd.getText().toString();
        if (input_name != null && input_name.trim().equals("")) {
            C0568T.showShort(this.mContext, (int) C0291R.string.input_contact_name);
        } else if (input_name.length() > 32) {
            C0568T.showShort(this.mContext, (int) C0291R.string.input_name_too_long);
        } else {
            if (this.mModifyContact.contactType != 3) {
                if (input_pwd != null && input_pwd.trim().equals("")) {
                    C0568T.showShort(this.mContext, (int) C0291R.string.input_contact_pwd);
                    return;
                } else if (input_pwd.length() > 30) {
                    C0568T.showShort(this.mContext, (int) C0291R.string.contact_pwd_too_long);
                    return;
                } else if (input_pwd.length() < 6 && !"123".equals(input_pwd.trim())) {
                    C0568T.showShort(this.mContext, (int) C0291R.string.new_pwd_too_short);
                    return;
                } else if (!Utils.checkDevicePwd(input_pwd) && !"123".equals(input_pwd.trim())) {
                    C0568T.showShort(this.mContext, (int) C0291R.string.device_pwd_format_error);
                    return;
                } else if (input_pwd.charAt(0) == '0') {
                    C0568T.showShort(this.mContext, (int) C0291R.string.contact_pwd_not_beginning_0);
                    return;
                }
            }
            this.mModifyContact.contactName = input_name;
            this.mModifyContact.userPassword = input_pwd;
            if (!P2PValue.HikamDeviceModelList.contains(this.mModifyContact.contactModel)) {
                input_pwd = P2PHandler.getInstance().EntryPassword(input_pwd);
            }
            this.mModifyContact.contactPassword = input_pwd;
            FList.getInstance().update(this.mModifyContact);
            FList.getInstance().updateOnlineState();
            Intent refreshContans = new Intent();
            refreshContans.setAction(Action.REFRESH_CONTANTS);
            refreshContans.putExtra(ContactDB.TABLE_NAME, this.mModifyContact);
            this.mContext.sendBroadcast(refreshContans);
            C0568T.showShort(this.mContext, (int) C0291R.string.modify_success);
            finish();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        destroyTempHead();
    }

    public int getActivityInfo() {
        return 19;
    }
}
