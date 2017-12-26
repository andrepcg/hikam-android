package com.jwkj.global;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

public class AsyncLoginService extends IntentService {
    private String name;
    private String pwd;

    public AsyncLoginService(String name) {
        super(name);
    }

    protected void onHandleIntent(@Nullable Intent intent) {
    }
}
