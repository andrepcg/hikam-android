package com.jwkj.thread;

import android.os.Handler;
import android.os.Message;
import com.jwkj.global.MyApp;
import com.jwkj.utils.Utils;
import com.p2p.core.update.UpdateManager;

public class UpdateCheckVersionThread extends Thread {
    Handler handler;
    boolean isNeedUpdate = false;

    public UpdateCheckVersionThread(Handler handler) {
        this.handler = handler;
    }

    public void run() {
        try {
            this.isNeedUpdate = UpdateManager.getInstance().checkUpdate();
            Message msg;
            if (this.isNeedUpdate) {
                msg = new Message();
                msg.what = 18;
                String data = "";
                if (Utils.isZh(MyApp.app)) {
                    data = UpdateManager.getInstance().getUpdateDescription();
                } else {
                    data = UpdateManager.getInstance().getUpdateDescription_en();
                }
                msg.obj = data;
                this.handler.sendMessage(msg);
                return;
            }
            msg = new Message();
            msg.what = 17;
            this.handler.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
