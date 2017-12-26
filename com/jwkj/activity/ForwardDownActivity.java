package com.jwkj.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import com.jwkj.global.Constants.Update;
import com.p2p.core.update.UpdateManager;
import java.io.File;

public class ForwardDownActivity extends BaseActivity {
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        switch (getIntent().getIntExtra("state", -1)) {
            case 17:
                UpdateManager.getInstance().cancelDown();
                break;
            case 18:
                Intent intent = new Intent("android.intent.action.VIEW");
                File file = new File(Environment.getExternalStorageDirectory() + "/" + Update.SAVE_PATH + "/" + Update.FILE_NAME);
                if (file.exists()) {
                    intent.setDataAndType(Uri.fromFile(file), Update.INSTALL_APK);
                    startActivity(intent);
                    break;
                }
                return;
        }
        finish();
    }

    public int getActivityInfo() {
        return 39;
    }
}
