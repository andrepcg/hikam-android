package com.jwkj;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import com.hikam.C0291R;
import com.jwkj.activity.BaseActivity;
import com.jwkj.utils.Utils;
import com.p2p.core.BaseCoreActivity;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import org.apache.http.cookie.ClientCookie;

public class NativePlayerActivity extends BaseActivity implements Callback {
    private int RESOLUTION = 1;
    private boolean first = true;
    private Handler handler = new Handler();
    private SurfaceHolder holder;
    private ImageView img_back;
    private ImageView img_mute;
    private ImageView img_play;
    private ImageView img_play2;
    private boolean isMute = true;
    private boolean isPlaying = false;
    private MediaPlayer mediaPlayer;
    private String path;
    Runnable refresh_time = new C03129();
    private SeekBar seek;
    private int seekProgress = 0;
    private SurfaceView sfv;
    private TextView tv1;
    private TextView tv2;

    class C03041 implements OnClickListener {
        C03041() {
        }

        public void onClick(View v) {
            if (NativePlayerActivity.this.isPlaying) {
                NativePlayerActivity.this.stop();
            } else {
                NativePlayerActivity.this.play();
            }
        }
    }

    class C03052 implements OnClickListener {
        C03052() {
        }

        public void onClick(View v) {
            if (NativePlayerActivity.this.isPlaying) {
                NativePlayerActivity.this.stop();
            } else {
                NativePlayerActivity.this.play();
            }
        }
    }

    class C03063 implements OnClickListener {
        C03063() {
        }

        public void onClick(View v) {
            if (NativePlayerActivity.this.isMute) {
                NativePlayerActivity.this.openAudio();
            } else {
                NativePlayerActivity.this.closeAudio();
            }
        }
    }

    class C03074 implements OnClickListener {
        C03074() {
        }

        public void onClick(View view) {
            NativePlayerActivity.this.finish();
        }
    }

    class C03085 implements OnSeekBarChangeListener {
        C03085() {
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            NativePlayerActivity.this.mediaPlayer.seekTo(NativePlayerActivity.this.seekProgress * 1000);
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
            NativePlayerActivity.this.handler.removeCallbacksAndMessages(null);
            NativePlayerActivity.this.mediaPlayer.pause();
        }

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            NativePlayerActivity.this.seekProgress = progress;
        }
    }

    class C03096 implements OnPreparedListener {
        C03096() {
        }

        public void onPrepared(MediaPlayer mp) {
            NativePlayerActivity.this.prepare();
        }
    }

    class C03107 implements OnCompletionListener {
        C03107() {
        }

        public void onCompletion(MediaPlayer mp) {
            NativePlayerActivity.this.complete();
        }
    }

    class C03118 implements OnSeekCompleteListener {
        C03118() {
        }

        public void onSeekComplete(MediaPlayer mp) {
            NativePlayerActivity.this.play();
        }
    }

    class C03129 implements Runnable {
        C03129() {
        }

        public void run() {
            if (NativePlayerActivity.this.mediaPlayer != null) {
                int time = NativePlayerActivity.this.mediaPlayer.getCurrentPosition() / 1000;
                NativePlayerActivity.this.tv1.setText(Utils.getPlayTime(time + 1));
                NativePlayerActivity.this.seek.setProgress(time + 1);
                NativePlayerActivity.this.handler.postDelayed(NativePlayerActivity.this.refresh_time, 500);
            }
        }
    }

    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(C0291R.layout.activity_native_player);
        this.path = getIntent().getStringExtra(ClientCookie.PATH_ATTR);
        this.RESOLUTION = getIntent().getIntExtra("resolution", 1);
        initComponent();
        reSize();
    }

    private void initComponent() {
        this.img_play = (ImageView) findViewById(C0291R.id.img_play);
        this.img_play.setOnClickListener(new C03041());
        this.img_play2 = (ImageView) findViewById(C0291R.id.img_play2);
        this.img_play2.setOnClickListener(new C03052());
        this.img_mute = (ImageView) findViewById(C0291R.id.img_mute);
        this.img_mute.setOnClickListener(new C03063());
        this.img_back = (ImageView) findViewById(C0291R.id.img_back);
        this.img_back.setOnClickListener(new C03074());
        this.sfv = (SurfaceView) findViewById(C0291R.id.sfv);
        this.holder = this.sfv.getHolder();
        this.holder.addCallback(this);
        this.tv1 = (TextView) findViewById(C0291R.id.tv_time_left);
        this.tv2 = (TextView) findViewById(C0291R.id.tv_time_right);
        this.seek = (SeekBar) findViewById(C0291R.id.seek);
        this.seek.setOnSeekBarChangeListener(new C03085());
        this.mediaPlayer = new MediaPlayer();
        try {
            this.mediaPlayer.setDataSource(this.path);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e2) {
            e2.printStackTrace();
        } catch (IllegalStateException e3) {
            e3.printStackTrace();
        } catch (IOException e4) {
            e4.printStackTrace();
        }
        try {
            this.mediaPlayer.prepare();
        } catch (IllegalStateException e32) {
            e32.printStackTrace();
        } catch (IOException e42) {
            e42.printStackTrace();
        }
        this.mediaPlayer.setOnPreparedListener(new C03096());
        this.mediaPlayer.setOnCompletionListener(new C03107());
        this.mediaPlayer.setOnSeekCompleteListener(new C03118());
    }

    public void reSize() {
        DisplayMetrics dm = new DisplayMetrics();
        dm = getResources().getDisplayMetrics();
        int mWindowWidth = dm.widthPixels;
        int mWindowHeight = dm.heightPixels;
        Log.e("my", "xWidth:" + mWindowWidth + " xHeight:" + mWindowHeight);
        int mWidth;
        int mHeight;
        LayoutParams layoutParams;
        if (this.RESOLUTION == 1) {
            mWidth = mWindowWidth;
            mHeight = (mWindowWidth * 9) / 16;
            if (mHeight > mWindowHeight) {
                mWidth = (mWindowHeight * 16) / 9;
                mHeight = mWindowHeight;
            }
            layoutParams = this.sfv.getLayoutParams();
            layoutParams.width = mWidth;
            layoutParams.height = mHeight;
            this.sfv.setLayoutParams(layoutParams);
            Log.e("my", "change to 720PxWidth:" + mWidth + " xHeight:" + mHeight);
        } else if (this.RESOLUTION == 2) {
            mWidth = mWindowWidth;
            mHeight = (mWindowWidth * 3) / 4;
            if (mHeight > mWindowHeight) {
                mWidth = (mWindowHeight * 4) / 3;
                mHeight = mWindowHeight;
            }
            layoutParams = this.sfv.getLayoutParams();
            layoutParams.width = mWidth;
            layoutParams.height = mHeight;
            this.sfv.setLayoutParams(layoutParams);
            Log.e("my", "change to 960PxWidth:" + mWidth + " xHeight:" + mHeight);
        }
    }

    protected void onPause() {
        super.onPause();
    }

    public void onStop() {
        super.onStop();
        if (BaseCoreActivity.isBG()) {
            releasePlayer();
            this.handler.removeCallbacksAndMessages(null);
            finish();
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    public int getActivityInfo() {
        return 100;
    }

    private void closeAudio() {
        AudioManager audioManager = (AudioManager) getSystemService("audio");
        this.mediaPlayer.setAudioStreamType(3);
        try {
            audioManager.setStreamMute(3, true);
        } catch (NullPointerException e) {
        }
        this.img_mute.setImageResource(C0291R.drawable.btn_call_sound_out_s);
        this.isMute = true;
        Log.e("few", "*---close");
    }

    private void openAudio() {
        AudioManager audioManager = (AudioManager) getSystemService("audio");
        this.mediaPlayer.setAudioStreamType(3);
        try {
            audioManager.setStreamMute(3, false);
        } catch (NullPointerException e) {
        }
        audioManager.setStreamVolume(3, (int) (((double) audioManager.getStreamMaxVolume(3)) * 0.8d), 0);
        this.img_mute.setImageResource(C0291R.drawable.play_volume_n);
        this.isMute = false;
        Log.e("few", "---open");
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        ReflectiveOperationException e;
        if (event.getKeyCode() != 25 && event.getKeyCode() != 24) {
            return super.dispatchKeyEvent(event);
        }
        AudioManager audioManager = (AudioManager) getSystemService("audio");
        if (VERSION.SDK_INT >= 23) {
            try {
                this.isMute = ((Boolean) AudioManager.class.getMethod("isStreamMute", new Class[]{Integer.TYPE}).invoke(audioManager, new Object[]{Integer.valueOf(3)})).booleanValue();
            } catch (NoSuchMethodException e2) {
                e = e2;
                System.out.println(e);
                if (audioManager.getStreamVolume(3) == 0) {
                    this.isMute = true;
                }
                if (this.isMute) {
                    this.img_mute.setImageResource(C0291R.drawable.btn_call_sound_out_s);
                } else {
                    this.img_mute.setImageResource(C0291R.drawable.play_volume_n);
                }
                return super.dispatchKeyEvent(event);
            } catch (InvocationTargetException e3) {
                e = e3;
                System.out.println(e);
                if (audioManager.getStreamVolume(3) == 0) {
                    this.isMute = true;
                }
                if (this.isMute) {
                    this.img_mute.setImageResource(C0291R.drawable.btn_call_sound_out_s);
                } else {
                    this.img_mute.setImageResource(C0291R.drawable.play_volume_n);
                }
                return super.dispatchKeyEvent(event);
            } catch (IllegalAccessException e4) {
                e = e4;
                System.out.println(e);
                if (audioManager.getStreamVolume(3) == 0) {
                    this.isMute = true;
                }
                if (this.isMute) {
                    this.img_mute.setImageResource(C0291R.drawable.play_volume_n);
                } else {
                    this.img_mute.setImageResource(C0291R.drawable.btn_call_sound_out_s);
                }
                return super.dispatchKeyEvent(event);
            }
        }
        try {
            audioManager.setStreamMute(3, false);
        } catch (NullPointerException e5) {
            System.out.println(e5);
        }
        this.img_mute.setImageResource(C0291R.drawable.play_volume_n);
        this.isMute = false;
        if (audioManager.getStreamVolume(3) == 0) {
            this.isMute = true;
        }
        if (this.isMute) {
            this.img_mute.setImageResource(C0291R.drawable.btn_call_sound_out_s);
        } else {
            this.img_mute.setImageResource(C0291R.drawable.play_volume_n);
        }
        return super.dispatchKeyEvent(event);
    }

    protected void onResume() {
        super.onResume();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    public void surfaceCreated(SurfaceHolder holder) {
        this.mediaPlayer.setDisplay(holder);
        holder.setKeepScreenOn(true);
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    public void prepare() {
        int s = this.mediaPlayer.getDuration() / 1000;
        this.tv1.setText("00:00");
        this.tv2.setText(Utils.getPlayTime(s + 1));
        this.seek.setMax(s + 1);
        this.seek.setProgress(0);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                NativePlayerActivity.this.play();
            }
        }, 100);
    }

    public void play() {
        this.isPlaying = true;
        this.mediaPlayer.start();
        this.handler.postDelayed(this.refresh_time, 1000);
        this.img_play.setImageResource(C0291R.drawable.playing_pause);
        this.img_play2.setVisibility(8);
        if (this.isMute) {
            closeAudio();
        } else {
            openAudio();
        }
    }

    public void stop() {
        this.isPlaying = false;
        this.mediaPlayer.pause();
        this.handler.removeCallbacksAndMessages(null);
        this.img_play.setImageResource(C0291R.drawable.playing_start);
        this.img_play2.setVisibility(0);
    }

    public void complete() {
        this.handler.removeCallbacksAndMessages(null);
        int max = this.seek.getMax();
        this.tv1.setText(Utils.getPlayTime(max));
        this.seek.setProgress(max);
        this.isPlaying = false;
        this.img_play.setImageResource(C0291R.drawable.playing_start);
        this.img_play2.setVisibility(0);
    }

    private void releasePlayer() {
        if (this.mediaPlayer != null) {
            this.mediaPlayer.stop();
            this.mediaPlayer.reset();
            this.mediaPlayer.release();
            this.mediaPlayer = null;
            if (this.isMute) {
                try {
                    ((AudioManager) getSystemService("audio")).setStreamMute(3, false);
                } catch (NullPointerException e) {
                }
            }
        }
    }
}
