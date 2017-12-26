package com.jwkj.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import com.hikam.C0291R;
import com.jwkj.data.SharedPreferencesManager;
import com.jwkj.entity.Account;
import com.jwkj.global.AccountPersist;
import com.jwkj.utils.Utils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class LogoActivity extends BaseActivity {
    private static final String PATH = "/storage/emulated/0/Android/data/com.hikam/files/send_failed_log";
    private Handler handler = new Handler(new C04061());

    class C04061 implements Callback {
        C04061() {
        }

        public boolean handleMessage(Message msg) {
            Intent i;
            if (SharedPreferencesManager.getInstance().getIsFirstInstall(LogoActivity.this)) {
                AccountPersist.getInstance().setActiveAccount(LogoActivity.this, new Account());
                SharedPreferencesManager.getInstance().setIsFirstInstall(LogoActivity.this);
                i = new Intent(LogoActivity.this, LoginActivity.class);
            } else {
                i = new Intent(LogoActivity.this, MainActivity.class);
            }
            LogoActivity.this.startActivity(i);
            LogoActivity.this.finish();
            return true;
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0291R.layout.activity_logo);
        File file = new File(PATH);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            String ipAddr = Utils.getLocalIpAddress(this);
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(PATH));
            out.writeChars(ipAddr);
            out.close();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        Message msg = new Message();
        msg.what = 17;
        this.handler.sendMessageDelayed(msg, 1000);
    }

    public int getActivityInfo() {
        return 0;
    }
}
