package com.jwkj.global;

import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

public class HikamService extends IntentService {
    public static final String ACTION_SCAN_SHORTAV_NUM = "com.jwkj.global.HikamService.scan_shortav_num";
    private String SHORT_AVPIC_DIRECTORY = (this.SHORT_AV_DIRECTORY + "tmp/mmc/clips/images/");
    private String SHORT_AV_DIRECTORY = (Environment.getExternalStorageDirectory().getPath() + File.separator + "hikam_shortav" + File.separator);

    class C05621 implements Comparator<File> {
        C05621() {
        }

        public int compare(File f1, File f2) {
            long diff = f1.lastModified() - f2.lastModified();
            if (diff > 0) {
                return 1;
            }
            if (diff == 0) {
                return 0;
            }
            return -1;
        }

        public boolean equals(Object obj) {
            return true;
        }
    }

    public HikamService() {
        super("HikamService");
    }

    protected void onHandleIntent(@Nullable Intent intent) {
        if (ACTION_SCAN_SHORTAV_NUM.equalsIgnoreCase(intent.getAction())) {
            File file = new File(this.SHORT_AV_DIRECTORY);
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                int len = files.length - 1;
                Log.i("HikamService", "HikamService file num" + len);
                if (len > 500) {
                    Log.i("HikamService", "HikamService do delete file");
                    for (File item : files) {
                        if (item.isFile()) {
                            item.delete();
                        }
                    }
                }
            }
        }
    }

    public void orderByDate(String fliePath) {
        File[] fs = new File(fliePath).listFiles();
        Arrays.sort(fs, new C05621());
        for (int i = fs.length - 1; i > -1; i--) {
            System.out.println("servicesss " + fs[i].getName() + " -- " + new Date(fs[i].lastModified()));
        }
    }
}
