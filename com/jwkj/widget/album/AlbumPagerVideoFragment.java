package com.jwkj.widget.album;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import com.hikam.C0291R;
import com.jwkj.data.SharedPreferencesManager;
import com.jwkj.entity.MediaPacket;
import com.jwkj.utils.Utils;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class AlbumPagerVideoFragment extends Fragment implements Callback {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Handler handler = new Handler();
    private SurfaceHolder holder;
    private ImageView img;
    private ImageView img_mute;
    private ImageView img_play;
    private ImageView img_play2;
    private boolean isAutoPlay = false;
    private boolean isMute = true;
    private boolean isPlaying = false;
    private MediaPacket mediaPacket;
    private MediaPlayer mediaPlayer;
    private String media_path;
    private String pic_path;
    Runnable refresh_time = new C06235();
    private SeekBar seek;
    private int seekProgress = 0;
    private SurfaceView sfv;
    private TextView tv1;
    private TextView tv2;

    class C06191 implements OnClickListener {
        C06191() {
        }

        public void onClick(View v) {
            if (AlbumPagerVideoFragment.this.isMute) {
                AlbumPagerVideoFragment.this.openAudio();
            } else {
                AlbumPagerVideoFragment.this.closeAudio();
            }
        }
    }

    class C06202 implements OnClickListener {
        C06202() {
        }

        public void onClick(View v) {
            if (AlbumPagerVideoFragment.this.isPlaying) {
                AlbumPagerVideoFragment.this.stop();
            } else {
                AlbumPagerVideoFragment.this.play();
            }
        }
    }

    class C06213 implements OnClickListener {
        C06213() {
        }

        public void onClick(View v) {
            if (AlbumPagerVideoFragment.this.isPlaying) {
                AlbumPagerVideoFragment.this.stop();
            } else {
                AlbumPagerVideoFragment.this.play();
            }
        }
    }

    class C06224 implements OnSeekBarChangeListener {
        C06224() {
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            AlbumPagerVideoFragment.this.mediaPlayer.seekTo(AlbumPagerVideoFragment.this.seekProgress * 1000);
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
            AlbumPagerVideoFragment.this.handler.removeCallbacksAndMessages(null);
            AlbumPagerVideoFragment.this.mediaPlayer.pause();
        }

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            AlbumPagerVideoFragment.this.seekProgress = progress;
        }
    }

    class C06235 implements Runnable {
        C06235() {
        }

        public void run() {
            if (AlbumPagerVideoFragment.this.mediaPlayer != null) {
                int time = AlbumPagerVideoFragment.this.mediaPlayer.getCurrentPosition() / 1000;
                AlbumPagerVideoFragment.this.tv1.setText(Utils.getPlayTime(time + 1));
                AlbumPagerVideoFragment.this.seek.setProgress(time + 1);
                AlbumPagerVideoFragment.this.handler.postDelayed(AlbumPagerVideoFragment.this.refresh_time, 1000);
            }
        }
    }

    class C06246 implements OnPreparedListener {
        C06246() {
        }

        public void onPrepared(MediaPlayer mp) {
            AlbumPagerVideoFragment.this.prepare();
            Log.e("few", AlbumPagerVideoFragment.this.mediaPlayer.getVideoWidth() + " " + AlbumPagerVideoFragment.this.mediaPlayer.getVideoHeight());
            if (AlbumPagerVideoFragment.this.isAutoPlay) {
                AlbumPagerVideoFragment.this.play();
            } else if (AlbumPagerVideoFragment.this.isMute) {
                AlbumPagerVideoFragment.this.closeAudio();
            } else {
                AlbumPagerVideoFragment.this.openAudio();
            }
        }
    }

    class C06257 implements OnCompletionListener {
        C06257() {
        }

        public void onCompletion(MediaPlayer mp) {
            AlbumPagerVideoFragment.this.complete();
        }
    }

    class C06268 implements OnSeekCompleteListener {
        C06268() {
        }

        public void onSeekComplete(MediaPlayer mp) {
            AlbumPagerVideoFragment.this.play();
        }
    }

    public static AlbumPagerVideoFragment newInstance(MediaPacket param1, boolean isAutoPlay) {
        AlbumPagerVideoFragment fragment = new AlbumPagerVideoFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param1);
        args.putBoolean(ARG_PARAM2, isAutoPlay);
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.mediaPacket = (MediaPacket) getArguments().getSerializable(ARG_PARAM1);
            this.isAutoPlay = getArguments().getBoolean(ARG_PARAM2);
            Log.e("fewfew", "onCreate" + this.isAutoPlay);
            this.media_path = this.mediaPacket.getMediaPath();
            this.pic_path = this.mediaPacket.getPicPath();
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(C0291R.layout.fragment_album_video, container, false);
        this.img = (ImageView) view.findViewById(C0291R.id.img);
        this.img.setImageBitmap(getImage(this.pic_path));
        Log.e("fewfew", "onCreateView");
        this.img_mute = (ImageView) view.findViewById(C0291R.id.img_mute);
        this.img_mute.setOnClickListener(new C06191());
        this.img_play = (ImageView) view.findViewById(C0291R.id.img_play);
        this.img_play.setOnClickListener(new C06202());
        this.img_play2 = (ImageView) view.findViewById(C0291R.id.img_play2);
        this.img_play2.setOnClickListener(new C06213());
        this.sfv = (SurfaceView) view.findViewById(C0291R.id.sfv);
        this.holder = this.sfv.getHolder();
        this.holder.addCallback(this);
        this.tv1 = (TextView) view.findViewById(C0291R.id.tv_time_left);
        this.tv2 = (TextView) view.findViewById(C0291R.id.tv_time_right);
        this.seek = (SeekBar) view.findViewById(C0291R.id.seek);
        this.seek.setOnSeekBarChangeListener(new C06224());
        if (SharedPreferencesManager.getInstance().getRecordResolution(getActivity(), this.media_path.substring(0, this.media_path.length() - 4)) == 960) {
            reSize(2);
        } else {
            reSize(1);
        }
        return view;
    }

    public void reSize(int resolution) {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int mWindowWidth = dm.widthPixels;
        int mWindowHeight = dm.heightPixels;
        Log.e("my", "xWidth:" + mWindowWidth + " xHeight:" + mWindowHeight);
        int mWidth;
        int mHeight;
        LayoutParams layoutParams;
        if (resolution == 1) {
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
            this.img.setLayoutParams(layoutParams);
            Log.e("my", "change to 720PxWidth:" + mWidth + " xHeight:" + mHeight);
        } else if (resolution == 2) {
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
            this.img.setLayoutParams(layoutParams);
            Log.e("my", "change to 960PxWidth:" + mWidth + " xHeight:" + mHeight);
        }
    }

    public void prepare() {
        int s = this.mediaPlayer.getDuration() / 1000;
        this.tv1.setText("00:00");
        this.tv2.setText(Utils.getPlayTime(s + 1));
        this.seek.setMax(s + 1);
        this.seek.setProgress(0);
        this.img.setVisibility(0);
    }

    public void play() {
        this.isPlaying = true;
        this.mediaPlayer.start();
        this.handler.postDelayed(this.refresh_time, 1000);
        this.img_play.setImageResource(C0291R.drawable.playing_pause);
        this.img_play2.setVisibility(8);
        this.img.setVisibility(8);
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
                FragmentActivity activity = getActivity();
                getActivity();
                try {
                    ((AudioManager) activity.getSystemService("audio")).setStreamMute(3, false);
                } catch (NullPointerException e) {
                }
            }
        }
    }

    private void closeAudio() {
        FragmentActivity activity = getActivity();
        getActivity();
        AudioManager audioManager = (AudioManager) activity.getSystemService("audio");
        this.mediaPlayer.setAudioStreamType(3);
        if (VERSION.SDK_INT >= 23) {
            try {
                audioManager.setStreamMute(3, true);
            } catch (NullPointerException e) {
            }
        } else {
            audioManager.setStreamVolume(3, 0, 0);
        }
        this.img_mute.setImageResource(C0291R.drawable.btn_call_sound_out_s);
        this.isMute = true;
    }

    private void openAudio() {
        FragmentActivity activity = getActivity();
        getActivity();
        AudioManager audioManager = (AudioManager) activity.getSystemService("audio");
        this.mediaPlayer.setAudioStreamType(3);
        if (VERSION.SDK_INT >= 23) {
            try {
                audioManager.setStreamMute(3, false);
            } catch (NullPointerException e) {
            }
        }
        audioManager.setStreamVolume(3, (int) (((double) audioManager.getStreamMaxVolume(3)) * 0.8d), 0);
        this.img_mute.setImageResource(C0291R.drawable.play_volume_n);
        this.isMute = false;
    }

    public void onAudioKeyDown(int action, int keyCode) {
        ReflectiveOperationException e;
        FragmentActivity activity = getActivity();
        getActivity();
        AudioManager audioManager = (AudioManager) activity.getSystemService("audio");
        if (VERSION.SDK_INT >= 23) {
            try {
                this.isMute = ((Boolean) AudioManager.class.getMethod("isStreamMute", new Class[]{Integer.TYPE}).invoke(audioManager, new Object[]{Integer.valueOf(3)})).booleanValue();
            } catch (NoSuchMethodException e2) {
                e = e2;
                System.out.println(e);
                if (audioManager.getStreamVolume(3) != 0) {
                    this.isMute = false;
                } else {
                    this.isMute = true;
                }
                if (this.isMute) {
                    this.img_mute.setImageResource(C0291R.drawable.btn_call_sound_out_s);
                } else {
                    this.img_mute.setImageResource(C0291R.drawable.play_volume_n);
                }
            } catch (InvocationTargetException e3) {
                e = e3;
                System.out.println(e);
                if (audioManager.getStreamVolume(3) != 0) {
                    this.isMute = true;
                } else {
                    this.isMute = false;
                }
                if (this.isMute) {
                    this.img_mute.setImageResource(C0291R.drawable.btn_call_sound_out_s);
                } else {
                    this.img_mute.setImageResource(C0291R.drawable.play_volume_n);
                }
            } catch (IllegalAccessException e4) {
                e = e4;
                System.out.println(e);
                if (audioManager.getStreamVolume(3) != 0) {
                    this.isMute = false;
                } else {
                    this.isMute = true;
                }
                if (this.isMute) {
                    this.img_mute.setImageResource(C0291R.drawable.play_volume_n);
                } else {
                    this.img_mute.setImageResource(C0291R.drawable.btn_call_sound_out_s);
                }
            }
        }
        if (audioManager.getStreamVolume(3) != 0) {
            this.isMute = true;
        } else {
            this.isMute = false;
        }
        if (this.isMute) {
            this.img_mute.setImageResource(C0291R.drawable.btn_call_sound_out_s);
        } else {
            this.img_mute.setImageResource(C0291R.drawable.play_volume_n);
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    public void surfaceCreated(SurfaceHolder holder) {
        this.mediaPlayer.setDisplay(holder);
        holder.setKeepScreenOn(true);
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        holder.removeCallback(this);
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser && this.isPlaying) {
            stop();
        }
    }

    public void onResume() {
        super.onResume();
        Log.e("few", "onResume----------------");
        if (this.mediaPlayer == null) {
            this.mediaPlayer = new MediaPlayer();
            try {
                this.mediaPlayer.setDataSource(this.media_path);
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
            this.mediaPlayer.setOnPreparedListener(new C06246());
            this.mediaPlayer.setOnCompletionListener(new C06257());
            this.mediaPlayer.setOnSeekCompleteListener(new C06268());
        }
    }

    public void onPause() {
        super.onPause();
        if (this.isPlaying) {
            stop();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
        this.handler.removeCallbacksAndMessages(null);
    }

    public Bitmap getImage(String path) {
        return BitmapFactory.decodeFile(path);
    }
}
