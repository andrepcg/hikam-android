package com.jwkj.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.hikam.C0291R;
import com.jwkj.data.Contact;
import com.jwkj.data.ContactDB;
import com.jwkj.data.DataManager;
import com.jwkj.global.Constants.Action;
import com.jwkj.global.Constants.Image;
import com.jwkj.global.Constants.P2P;
import com.jwkj.global.FList;
import com.jwkj.global.NpcCommon;
import com.jwkj.utils.C0568T;
import com.jwkj.utils.ImageUtils;
import com.jwkj.utils.Utils;
import com.jwkj.widget.HeaderView;
import com.jwkj.widget.NormalDialog;
import com.p2p.core.P2PHandler;
import com.p2p.core.P2PValue;
import java.io.File;

public class AddContactNextActivity extends BaseActivity implements OnClickListener {
    private static final int RESULT_CUT_IMAGE = 19;
    private static final int RESULT_GETIMG_FROM_CAMERA = 17;
    private static final int RESULT_GETIMG_FROM_GALLERY = 18;
    TextView contactId;
    EditText contactName;
    EditText contactPwd;
    EditText createPwd1;
    EditText createPwd2;
    NormalDialog dialog;
    HeaderView header_img;
    String input_create_pwd1;
    String input_create_pwd2;
    String input_name;
    String input_pwd;
    String ipFlag;
    public boolean isCancelLoading;
    boolean isCreatePassword = false;
    boolean isRegFilter;
    boolean isSave = false;
    LinearLayout layout_create_pwd;
    LinearLayout layout_device_pwd;
    private ImageView mBack;
    Context mContext;
    private BroadcastReceiver mReceiver = new C03281();
    private TextView mSave;
    Contact mSaveContact;
    RelativeLayout modify_header;
    boolean needInitPwd = false;
    int network_error_count = 0;
    private Bitmap tempHead;
    String userPassword;

    class C03281 extends BroadcastReceiver {
        C03281() {
        }

        public void onReceive(Context arg0, Intent intent) {
            int result;
            Contact contact;
            if (intent.getAction().equals(P2P.RET_SET_INIT_PASSWORD)) {
                Log.e("oaosj", "RET_SET_INIT_PASSWORD");
                result = intent.getIntExtra("result", -1);
                if (AddContactNextActivity.this.dialog != null) {
                    AddContactNextActivity.this.dialog.dismiss();
                    AddContactNextActivity.this.dialog = null;
                }
                if (result == 0) {
                    contact = FList.getInstance().isContact(AddContactNextActivity.this.mSaveContact.contactId);
                    if (contact != null) {
                        contact.contactName = AddContactNextActivity.this.input_name;
                        contact.userPassword = AddContactNextActivity.this.userPassword;
                        contact.contactPassword = AddContactNextActivity.this.input_pwd;
                        FList.getInstance().update(contact);
                    } else {
                        AddContactNextActivity.this.mSaveContact.contactName = AddContactNextActivity.this.input_name;
                        AddContactNextActivity.this.mSaveContact.userPassword = AddContactNextActivity.this.userPassword;
                        AddContactNextActivity.this.mSaveContact.contactPassword = AddContactNextActivity.this.input_pwd;
                        FList.getInstance().insert(AddContactNextActivity.this.mSaveContact);
                    }
                    FList.getInstance().updateLocalDeviceFlag(AddContactNextActivity.this.mSaveContact.contactId, 1);
                    AddContactNextActivity.this.isSave = true;
                    AddContactNextActivity.this.sendSuccessBroadcast();
                    C0568T.showShort(AddContactNextActivity.this.mContext, (int) C0291R.string.add_success);
                    AddContactNextActivity.this.finish();
                } else if (result == 43) {
                    Intent createPwdSuccess = new Intent();
                    createPwdSuccess.setAction(Action.UPDATE_DEVICE_FALG);
                    createPwdSuccess.putExtra("threeNum", AddContactNextActivity.this.mSaveContact.contactId);
                    AddContactNextActivity.this.mContext.sendBroadcast(createPwdSuccess);
                    C0568T.showShort(AddContactNextActivity.this.mContext, (int) C0291R.string.already_init_passwd);
                    AddContactNextActivity.this.finish();
                } else {
                    C0568T.showShort(AddContactNextActivity.this.mContext, (int) C0291R.string.operator_error);
                }
            } else if (intent.getAction().equals(P2P.ACK_RET_SET_INIT_PASSWORD)) {
                Log.e("oaosj", "ACK_RET_SET_INIT_PASSWORD");
                result = intent.getIntExtra("result", -1);
                if (result == 9999) {
                    C0568T.showShort(AddContactNextActivity.this.mContext, (int) C0291R.string.password_error);
                } else if (result == 9998) {
                    C0568T.showShort(AddContactNextActivity.this.mContext, (int) C0291R.string.net_error_operator_fault);
                }
            } else if (intent.getAction().equals(P2P.RET_SET_DEVICE_PASSWORD)) {
                Log.e("oaosj", "RET_SET_DEVICE_PASSWORD");
                result = intent.getIntExtra("result", -1);
                if (AddContactNextActivity.this.dialog != null) {
                    AddContactNextActivity.this.dialog.dismiss();
                    AddContactNextActivity.this.dialog = null;
                }
                if (result == 0) {
                    contact = FList.getInstance().isContact(AddContactNextActivity.this.mSaveContact.contactId);
                    if (contact != null) {
                        contact.contactName = AddContactNextActivity.this.input_name;
                        contact.userPassword = AddContactNextActivity.this.userPassword;
                        contact.contactPassword = AddContactNextActivity.this.input_pwd;
                        FList.getInstance().update(contact);
                    } else {
                        AddContactNextActivity.this.mSaveContact.contactName = AddContactNextActivity.this.input_name;
                        AddContactNextActivity.this.mSaveContact.userPassword = AddContactNextActivity.this.userPassword;
                        AddContactNextActivity.this.mSaveContact.contactPassword = AddContactNextActivity.this.input_pwd;
                        FList.getInstance().insert(AddContactNextActivity.this.mSaveContact);
                    }
                    FList.getInstance().updateLocalDeviceFlag(AddContactNextActivity.this.mSaveContact.contactId, 1);
                    AddContactNextActivity.this.isSave = true;
                    AddContactNextActivity.this.sendSuccessBroadcast();
                    C0568T.showShort(AddContactNextActivity.this.mContext, (int) C0291R.string.add_success);
                    AddContactNextActivity.this.finish();
                    return;
                }
                C0568T.showShort(AddContactNextActivity.this.mContext, (int) C0291R.string.operator_error);
            } else if (intent.getAction().equals(P2P.ACK_RET_SET_DEVICE_PASSWORD)) {
                Log.e("oaosj", "ACK_RET_SET_DEVICE_PASSWORD");
                result = intent.getIntExtra("result", -1);
                if (result == 9999) {
                    if (AddContactNextActivity.this.dialog != null) {
                        AddContactNextActivity.this.dialog.dismiss();
                        AddContactNextActivity.this.dialog = null;
                    }
                    C0568T.showShort(AddContactNextActivity.this.mContext, (int) C0291R.string.old_pwd_error);
                } else if (result == 9998) {
                    C0568T.showShort(AddContactNextActivity.this.mContext, (int) C0291R.string.net_error_operator_fault);
                }
            } else if (intent.getAction().equals(P2P.ACK_RET_CHECK_PASSWORD)) {
                Log.e("oaosj", "ACK_RET_CHECK_PASSWORD");
                result = intent.getIntExtra("result", -1);
                if (!AddContactNextActivity.this.isCancelLoading) {
                    if (result == 9997) {
                        if (AddContactNextActivity.this.dialog != null && AddContactNextActivity.this.dialog.isShowing()) {
                            AddContactNextActivity.this.dialog.dismiss();
                            AddContactNextActivity.this.dialog = null;
                        }
                        FList.getInstance().insert(AddContactNextActivity.this.mSaveContact);
                        AddContactNextActivity.this.isSave = true;
                        AddContactNextActivity.this.sendSuccessBroadcast();
                        P2PHandler.getInstance().ActiveHumanDetect(AddContactNextActivity.this.mSaveContact.contactModel, AddContactNextActivity.this.mSaveContact.contactId, AddContactNextActivity.this.mSaveContact.contactPassword);
                        AddContactNextActivity.this.finish();
                    } else if (result == 9999) {
                        if (AddContactNextActivity.this.dialog != null && AddContactNextActivity.this.dialog.isShowing()) {
                            AddContactNextActivity.this.dialog.dismiss();
                            AddContactNextActivity.this.dialog = null;
                        }
                        C0568T.showShort(AddContactNextActivity.this.mContext, (int) C0291R.string.password_error);
                    } else if (result == 9998) {
                        if (AddContactNextActivity.this.network_error_count >= 5) {
                            if (AddContactNextActivity.this.dialog != null && AddContactNextActivity.this.dialog.isShowing()) {
                                AddContactNextActivity.this.dialog.dismiss();
                                AddContactNextActivity.this.dialog = null;
                            }
                            AddContactNextActivity.this.network_error_count = 0;
                            C0568T.showShort(AddContactNextActivity.this.mContext, (int) C0291R.string.timeout);
                            return;
                        }
                        P2PHandler.getInstance().checkPassword(AddContactNextActivity.this.mSaveContact.contactModel, AddContactNextActivity.this.mSaveContact.contactId, AddContactNextActivity.this.mSaveContact.contactPassword);
                        r4 = AddContactNextActivity.this;
                        r4.network_error_count++;
                    } else if (result == 9996) {
                        if (AddContactNextActivity.this.dialog != null && AddContactNextActivity.this.dialog.isShowing()) {
                            AddContactNextActivity.this.dialog.dismiss();
                            AddContactNextActivity.this.dialog = null;
                        }
                        C0568T.showShort(AddContactNextActivity.this.mContext, (int) C0291R.string.insufficient_permissions);
                    }
                }
            } else if (intent.getAction().equals(P2P.ACK_RET_CHECK_PASSWORD2)) {
                Log.e("oaosj", "ACK_RET_CHECK_PASSWORD2");
                result = intent.getIntExtra("result", -1);
                String model = intent.getStringExtra("model");
                if (!AddContactNextActivity.this.isCancelLoading) {
                    if (result == 9997) {
                        if (AddContactNextActivity.this.dialog != null && AddContactNextActivity.this.dialog.isShowing()) {
                            AddContactNextActivity.this.dialog.dismiss();
                            AddContactNextActivity.this.dialog = null;
                        }
                        AddContactNextActivity.this.mSaveContact.contactModel = model;
                        FList.getInstance().insert(AddContactNextActivity.this.mSaveContact);
                        AddContactNextActivity.this.isSave = true;
                        AddContactNextActivity.this.sendSuccessBroadcast();
                        P2PHandler.getInstance().ActiveHumanDetect(AddContactNextActivity.this.mSaveContact.contactModel, AddContactNextActivity.this.mSaveContact.contactId, AddContactNextActivity.this.mSaveContact.contactPassword);
                        AddContactNextActivity.this.finish();
                    } else if (result == 9999) {
                        if (AddContactNextActivity.this.dialog != null && AddContactNextActivity.this.dialog.isShowing()) {
                            AddContactNextActivity.this.dialog.dismiss();
                            AddContactNextActivity.this.dialog = null;
                        }
                        C0568T.showShort(AddContactNextActivity.this.mContext, (int) C0291R.string.password_error);
                    } else if (result == 9998) {
                        if (AddContactNextActivity.this.network_error_count >= 5) {
                            if (AddContactNextActivity.this.dialog != null && AddContactNextActivity.this.dialog.isShowing()) {
                                AddContactNextActivity.this.dialog.dismiss();
                                AddContactNextActivity.this.dialog = null;
                            }
                            AddContactNextActivity.this.network_error_count = 0;
                            C0568T.showShort(AddContactNextActivity.this.mContext, (int) C0291R.string.timeout);
                            return;
                        }
                        P2PHandler.getInstance().checkPassword(AddContactNextActivity.this.mSaveContact.contactModel, AddContactNextActivity.this.mSaveContact.contactId, AddContactNextActivity.this.mSaveContact.contactPassword);
                        r4 = AddContactNextActivity.this;
                        r4.network_error_count++;
                    } else if (result == 9996) {
                        if (AddContactNextActivity.this.dialog != null && AddContactNextActivity.this.dialog.isShowing()) {
                            AddContactNextActivity.this.dialog.dismiss();
                            AddContactNextActivity.this.dialog = null;
                        }
                        C0568T.showShort(AddContactNextActivity.this.mContext, (int) C0291R.string.insufficient_permissions);
                    }
                }
            }
        }
    }

    class C03292 implements OnCancelListener {
        C03292() {
        }

        public void onCancel(DialogInterface arg0) {
            AddContactNextActivity.this.isCancelLoading = true;
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0291R.layout.activity_add_contact_next);
        this.mSaveContact = (Contact) getIntent().getSerializableExtra(ContactDB.TABLE_NAME);
        this.isCreatePassword = getIntent().getBooleanExtra("isCreatePassword", false);
        this.ipFlag = getIntent().getStringExtra("ipFlag");
        this.mContext = this;
        initCompent();
        regFilter();
    }

    public void initCompent() {
        this.contactId = (TextView) findViewById(C0291R.id.contactId);
        this.contactName = (EditText) findViewById(C0291R.id.contactName);
        this.contactPwd = (EditText) findViewById(C0291R.id.contactPwd);
        this.contactPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
        this.layout_device_pwd = (LinearLayout) findViewById(C0291R.id.layout_device_pwd);
        this.layout_create_pwd = (LinearLayout) findViewById(C0291R.id.layout_create_pwd);
        this.createPwd1 = (EditText) findViewById(C0291R.id.createPwd1);
        this.createPwd2 = (EditText) findViewById(C0291R.id.createPwd2);
        this.createPwd1.setTransformationMethod(PasswordTransformationMethod.getInstance());
        this.createPwd2.setTransformationMethod(PasswordTransformationMethod.getInstance());
        this.mBack = (ImageView) findViewById(C0291R.id.back_btn);
        this.mSave = (TextView) findViewById(C0291R.id.save);
        this.header_img = (HeaderView) findViewById(C0291R.id.header_img);
        this.modify_header = (RelativeLayout) findViewById(C0291R.id.modify_header);
        this.header_img.updateImage(this.mSaveContact.contactId, false);
        if (this.isCreatePassword) {
            this.layout_create_pwd.setVisibility(0);
            this.layout_device_pwd.setVisibility(8);
        } else {
            this.layout_create_pwd.setVisibility(8);
            if (this.mSaveContact.contactType != 3) {
                this.layout_device_pwd.setVisibility(0);
            } else {
                this.layout_device_pwd.setVisibility(8);
            }
        }
        Contact contact = FList.getInstance().isContact(this.mSaveContact.contactId);
        if (contact != null) {
            this.contactName.setText(contact.contactName);
        }
        this.contactId.setText(Utils.showShortDevID(this.mSaveContact.contactId));
        this.modify_header.setOnClickListener(this);
        this.mBack.setOnClickListener(this);
        this.mSave.setOnClickListener(this);
    }

    public void regFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(P2P.ACK_RET_SET_INIT_PASSWORD);
        filter.addAction(P2P.RET_SET_INIT_PASSWORD);
        filter.addAction(P2P.ACK_RET_SET_DEVICE_PASSWORD);
        filter.addAction(P2P.RET_SET_DEVICE_PASSWORD);
        filter.addAction(P2P.ACK_RET_CHECK_PASSWORD);
        filter.addAction(P2P.ACK_RET_CHECK_PASSWORD2);
        this.mContext.registerReceiver(this.mReceiver, filter);
        this.isRegFilter = true;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Intent cutImage;
        if (requestCode == 17) {
            try {
                this.tempHead = (Bitmap) data.getExtras().get("data");
                Log.e("my", this.tempHead.getWidth() + ":" + this.tempHead.getHeight());
                ImageUtils.saveImg(this.tempHead, Image.USER_HEADER_PATH, Image.USER_HEADER_TEMP_FILE_NAME);
                cutImage = new Intent(this.mContext, CutImageActivity.class);
                cutImage.putExtra(ContactDB.TABLE_NAME, this.mSaveContact);
                startActivityForResult(cutImage, 19);
            } catch (NullPointerException e) {
                Log.e("my", "用户终止..");
            }
        } else if (requestCode == 18) {
            try {
                this.tempHead = ImageUtils.getBitmap(ImageUtils.getAbsPath(this.mContext, data.getData()), 500, 500);
                ImageUtils.saveImg(this.tempHead, Image.USER_HEADER_PATH, Image.USER_HEADER_TEMP_FILE_NAME);
                cutImage = new Intent(this.mContext, CutImageActivity.class);
                cutImage.putExtra(ContactDB.TABLE_NAME, this.mSaveContact);
                startActivityForResult(cutImage, 19);
            } catch (NullPointerException e2) {
                Log.e("my", "用户终止..");
            }
        } else if (requestCode == 19) {
            Log.e("my", resultCode + "");
            if (resultCode == 1) {
                try {
                    this.header_img.updateImage(this.mSaveContact.contactId, false);
                    Intent refreshContans = new Intent();
                    refreshContans.setAction(Action.REFRESH_CONTANTS);
                    refreshContans.putExtra(ContactDB.TABLE_NAME, this.mSaveContact);
                    this.mContext.sendBroadcast(refreshContans);
                } catch (Exception e3) {
                    e3.printStackTrace();
                }
            }
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case C0291R.id.back_btn:
                if (this.dialog != null && this.dialog.isShowing()) {
                    this.dialog.dismiss();
                    this.dialog = null;
                }
                finish();
                return;
            case C0291R.id.save:
                save();
                return;
            default:
                return;
        }
    }

    public void destroyTempHead() {
        if (this.tempHead != null && !this.tempHead.isRecycled()) {
            this.tempHead.recycle();
            this.tempHead = null;
        }
    }

    void save() {
        this.input_name = this.contactName.getText().toString();
        this.input_pwd = this.contactPwd.getText().toString();
        this.input_create_pwd1 = this.createPwd1.getText().toString();
        this.input_create_pwd2 = this.createPwd2.getText().toString();
        if (this.input_name != null && this.input_name.trim().equals("")) {
            C0568T.showShort(this.mContext, (int) C0291R.string.input_contact_name);
        } else if (this.input_name.length() > 32) {
            C0568T.showShort(this.mContext, (int) C0291R.string.input_name_too_long);
        } else {
            if (this.isCreatePassword) {
                if (this.input_create_pwd1 == null || "".equals(this.input_create_pwd1)) {
                    C0568T.showShort((Context) this, (int) C0291R.string.inputpassword);
                    return;
                } else if (this.input_create_pwd1.length() > 9) {
                    C0568T.showShort((Context) this, (int) C0291R.string.password_length_error);
                    return;
                } else if (this.input_create_pwd2 == null || "".equals(this.input_create_pwd2)) {
                    C0568T.showShort((Context) this, (int) C0291R.string.reinputpassword);
                    return;
                } else if (this.input_create_pwd1.equals(this.input_create_pwd2)) {
                    if (this.dialog == null) {
                        this.dialog = new NormalDialog(this, getResources().getString(C0291R.string.verification), "", "", "");
                        this.dialog.setStyle(2);
                    }
                    this.dialog.showDialog();
                    P2PHandler.getInstance().setDevicePassword(this.mSaveContact.contactModel, this.mSaveContact.contactId, "123", this.input_create_pwd1);
                } else {
                    C0568T.showShort((Context) this, (int) C0291R.string.differentpassword);
                    return;
                }
            } else if (this.input_pwd == null || this.input_pwd.trim().equals("")) {
                C0568T.showShort(this.mContext, (int) C0291R.string.input_contact_pwd);
                return;
            } else {
                if (!(this.mSaveContact.contactType == 3 || this.input_pwd == null || this.input_pwd.trim().equals(""))) {
                    if (this.input_pwd.length() > 30) {
                        C0568T.showShort(this.mContext, (int) C0291R.string.contact_pwd_too_long);
                        return;
                    } else if (this.input_pwd.charAt(0) == '0') {
                        C0568T.showShort(this.mContext, (int) C0291R.string.contact_pwd_must_digit);
                        return;
                    }
                }
                for (Contact contact : DataManager.findContactByActiveUser(this.mContext, NpcCommon.mThreeNum)) {
                    if (contact.contactId.equals(this.mSaveContact.contactId)) {
                        C0568T.showShort(this.mContext, (int) C0291R.string.contact_already_exist);
                        return;
                    }
                }
                this.dialog = new NormalDialog(this.mContext);
                this.dialog.setOnCancelListener(new C03292());
                this.dialog.showLoadingDialog2();
                this.dialog.setCanceledOnTouchOutside(false);
                this.isCancelLoading = false;
                this.mSaveContact.contactName = this.input_name;
                this.userPassword = this.input_pwd;
                this.mSaveContact.userPassword = this.userPassword;
                if (!P2PValue.HikamDeviceModelList.contains(this.mSaveContact.contactModel)) {
                    this.input_pwd = P2PHandler.getInstance().EntryPassword(this.input_pwd);
                }
                this.mSaveContact.contactPassword = this.input_pwd;
                this.network_error_count = 0;
                P2PHandler.getInstance().checkPassword(this.mSaveContact.contactModel, this.mSaveContact.contactId, this.mSaveContact.contactPassword);
            }
            InputMethodManager imm = (InputMethodManager) getSystemService("input_method");
            if (imm != null) {
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
            }
        }
    }

    public void sendSuccessBroadcast() {
        Intent refreshContans = new Intent();
        refreshContans.setAction(Action.REFRESH_CONTANTS);
        refreshContans.putExtra(ContactDB.TABLE_NAME, this.mSaveContact);
        this.mContext.sendBroadcast(refreshContans);
        Intent createPwdSuccess = new Intent();
        createPwdSuccess.setAction(Action.UPDATE_DEVICE_FALG);
        createPwdSuccess.putExtra("threeNum", this.mSaveContact.contactId);
        this.mContext.sendBroadcast(createPwdSuccess);
        Intent add_success = new Intent();
        add_success.setAction(Action.ADD_CONTACT_SUCCESS);
        add_success.putExtra(ContactDB.TABLE_NAME, this.mSaveContact);
        this.mContext.sendBroadcast(add_success);
        Intent refreshNearlyTell = new Intent();
        refreshNearlyTell.setAction(Action.ACTION_REFRESH_NEARLY_TELL);
        this.mContext.sendBroadcast(refreshNearlyTell);
        C0568T.showShort(this.mContext, (int) C0291R.string.add_success);
    }

    private void sendInitPwd() {
        Intent initPwd = new Intent(this.mContext, AddContactNextActivity.class);
        initPwd.putExtra("isCreatePassword", true);
        initPwd.putExtra(ContactDB.TABLE_NAME, this.mSaveContact);
        startActivity(initPwd);
    }

    protected void onDestroy() {
        super.onDestroy();
        destroyTempHead();
        if (this.isCreatePassword) {
            Contact contact = FList.getInstance().isContact(this.mSaveContact.contactId);
            if (!this.isSave && contact == null) {
                Utils.deleteFile(new File(Image.USER_HEADER_PATH + NpcCommon.mThreeNum + "/" + this.mSaveContact.contactId));
            }
        } else if (!this.isSave) {
            Utils.deleteFile(new File(Image.USER_HEADER_PATH + NpcCommon.mThreeNum + "/" + this.mSaveContact.contactId));
        }
        if (this.isRegFilter) {
            this.mContext.unregisterReceiver(this.mReceiver);
            this.isRegFilter = false;
        }
    }

    public int getActivityInfo() {
        return 8;
    }
}
