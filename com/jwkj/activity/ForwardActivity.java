package com.jwkj.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.jwkj.global.AccountPersist;

public class ForwardActivity extends BaseActivity {
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        if (!(AccountPersist.getInstance().getActiveAccountInfo(this) == null || activity_stack.containsKey(Integer.valueOf(1)))) {
            Log.e("my", "forward:MainActivity");
            startActivity(new Intent(this, MainActivity.class));
        }
        finish();
    }

    public int getActivityInfo() {
        return 11;
    }
}
