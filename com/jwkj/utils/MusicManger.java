package com.jwkj.utils;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Vibrator;
import android.util.Log;
import com.hikam.C0291R;
import com.jwkj.data.SharedPreferencesManager;
import com.jwkj.data.SystemDataManager;
import com.jwkj.global.MyApp;
import java.util.HashMap;
import org.apache.http.cookie.ClientCookie;

public class MusicManger {
    private static MusicManger manager = null;
    private static MediaPlayer player;
    private boolean isVibrate = false;
    private Vibrator vibrator;

    private MusicManger() {
    }

    public static synchronized MusicManger getInstance() {
        MusicManger musicManger;
        synchronized (MusicManger.class) {
            if (manager == null) {
                synchronized (MusicManger.class) {
                    if (manager == null) {
                        manager = new MusicManger();
                    }
                }
            }
            musicManger = manager;
        }
        return musicManger;
    }

    public void playCommingMusic() {
        if (player == null) {
            try {
                HashMap<String, String> data;
                player = new MediaPlayer();
                if (SharedPreferencesManager.getInstance().getCBellType(MyApp.app) == 0) {
                    data = SystemDataManager.getInstance().findSystemBellById(MyApp.app, SharedPreferencesManager.getInstance().getCSystemBellId(MyApp.app));
                } else {
                    data = SystemDataManager.getInstance().findSdBellById(MyApp.app, SharedPreferencesManager.getInstance().getCSdBellId(MyApp.app));
                }
                String path = (String) data.get(ClientCookie.PATH_ATTR);
                if (path != null && !"".equals(path)) {
                    player.reset();
                    player.setDataSource(path);
                    player.setLooping(true);
                    player.prepare();
                    player.start();
                }
            } catch (Exception e) {
                if (player != null) {
                    player.stop();
                    player.release();
                    player = null;
                }
            }
        }
    }

    public void playAlarmMusic() {
        if (player == null) {
            try {
                HashMap<String, String> data;
                player = new MediaPlayer();
                if (SharedPreferencesManager.getInstance().getABellType(MyApp.app) == 0) {
                    data = SystemDataManager.getInstance().findSystemBellById(MyApp.app, SharedPreferencesManager.getInstance().getASystemBellId(MyApp.app));
                } else {
                    data = SystemDataManager.getInstance().findSdBellById(MyApp.app, SharedPreferencesManager.getInstance().getASdBellId(MyApp.app));
                }
                if (data != null) {
                    String path = (String) data.get(ClientCookie.PATH_ATTR);
                    if (path != null && !"".equals(path)) {
                        player.reset();
                        player.setDataSource(path);
                        player.setLooping(true);
                        player.prepare();
                        player.start();
                    }
                }
            } catch (Exception e) {
                if (player != null) {
                    player.stop();
                    player.release();
                    player = null;
                }
            }
        }
    }

    public void playMsgMusic() {
        try {
            final MediaPlayer msgPlayer = MediaPlayer.create(MyApp.app, C0291R.raw.message);
            msgPlayer.start();
            msgPlayer.setOnCompletionListener(new OnCompletionListener() {
                public void onCompletion(MediaPlayer arg0) {
                    msgPlayer.release();
                }
            });
        } catch (Exception e) {
            Log.e("my", "msg music error!");
        }
    }

    public void stop() {
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
    }

    public void Vibrate() {
        if (!this.isVibrate) {
            if (this.vibrator == null) {
                this.vibrator = (Vibrator) MyApp.app.getSystemService("vibrator");
            }
            this.isVibrate = true;
            this.vibrator.vibrate(new long[]{100, 400, 1000, 400}, 2);
        }
    }

    public void stopVibrate() {
        this.isVibrate = false;
        if (this.vibrator != null) {
            this.vibrator.cancel();
        }
    }
}
