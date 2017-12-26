package com.jwkj.widget;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import com.hikam.C0291R;
import com.jwkj.entity.RecordVideo;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlayerView extends RelativeLayout {
    private static final int DELAY_TIME = 3500;
    private RelativeLayout content_view;
    private Handler handler = new Handler();
    private Handler handler_pro = new Handler();
    private ImageView img_back;
    private ImageView img_full;
    private ImageView img_play;
    private boolean isPlaying = false;
    private boolean isSeekChanging = false;
    private boolean isShowComponent = false;
    private List<RecordVideo> list = new ArrayList();
    private MediaPlayer mediaPlayer;
    private OnOptListener optListener;
    private RelativeLayout player_tool_bottom;
    private RelativeLayout player_tool_top;
    private SeekBar seek;
    private int seekProgress;
    private SurfaceView svf;
    Runnable updateThread = new C06077();

    class C06011 implements OnClickListener {
        C06011() {
        }

        public void onClick(View v) {
            if (!PlayerView.this.isShowComponent || PlayerView.this.isSeekChanging) {
                PlayerView.this.showTools();
            } else {
                PlayerView.this.dismissTools();
            }
        }
    }

    class C06022 implements OnClickListener {
        C06022() {
        }

        public void onClick(View v) {
            PlayerView.this.showTools();
            if (PlayerView.this.optListener != null) {
                PlayerView.this.optListener.onFinish();
            }
        }
    }

    class C06033 implements OnClickListener {
        C06033() {
        }

        public void onClick(View v) {
            PlayerView.this.showTools();
            if (PlayerView.this.optListener != null) {
                PlayerView.this.optListener.onPlay();
            }
            if (PlayerView.this.isPlaying) {
                PlayerView.this.mediaPlayer.pause();
                PlayerView.this.isPlaying = false;
                PlayerView.this.img_play.setImageDrawable(PlayerView.this.getResources().getDrawable(C0291R.drawable.playing_start));
                return;
            }
            PlayerView.this.mediaPlayer.start();
            PlayerView.this.isPlaying = true;
            PlayerView.this.img_play.setImageDrawable(PlayerView.this.getResources().getDrawable(C0291R.drawable.playing_pause));
        }
    }

    class C06044 implements OnClickListener {
        C06044() {
        }

        public void onClick(View v) {
            PlayerView.this.showTools();
            if (PlayerView.this.optListener != null) {
                PlayerView.this.optListener.onFullScreenReq();
            }
        }
    }

    class C06055 implements OnSeekBarChangeListener {
        C06055() {
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            PlayerView.this.isSeekChanging = false;
            PlayerView.this.showTools();
            if (PlayerView.this.optListener != null) {
                PlayerView.this.optListener.onStopTrackingTouch();
            }
            PlayerView.this.mediaPlayer.seekTo(PlayerView.this.seekProgress);
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
            PlayerView.this.isSeekChanging = true;
            PlayerView.this.handler.removeCallbacksAndMessages(null);
            if (PlayerView.this.optListener != null) {
                PlayerView.this.optListener.onStartTrackingTouch();
            }
        }

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (PlayerView.this.optListener != null) {
                PlayerView.this.optListener.onProgressChanged(progress);
            }
            PlayerView.this.seekProgress = progress;
        }
    }

    class C06066 implements Runnable {
        C06066() {
        }

        public void run() {
            PlayerView.this.dismissTools();
        }
    }

    class C06077 implements Runnable {
        C06077() {
        }

        public void run() {
            if (PlayerView.this.mediaPlayer != null) {
                PlayerView.this.seek.setProgress(PlayerView.this.mediaPlayer.getCurrentPosition());
                PlayerView.this.handler_pro.postDelayed(PlayerView.this.updateThread, 1000);
            }
        }
    }

    class C06088 implements OnSeekCompleteListener {
        C06088() {
        }

        public void onSeekComplete(MediaPlayer mp) {
            mp.release();
        }
    }

    class C06099 implements OnErrorListener {
        C06099() {
        }

        public boolean onError(MediaPlayer mp, int what, int extra) {
            mp.release();
            return false;
        }
    }

    public interface OnOptListener {
        void onFinish();

        void onFullScreenReq();

        void onPlay();

        void onProgressChanged(int i);

        void onStartTrackingTouch();

        void onStopTrackingTouch();
    }

    private class PlayerViewHolder implements Callback {
        private PlayerViewHolder() {
        }

        public void surfaceCreated(SurfaceHolder holder) {
            holder.setKeepScreenOn(true);
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
        }
    }

    private class PreparedListener implements OnPreparedListener {
        private PreparedListener() {
        }

        public void onPrepared(MediaPlayer mp) {
            PlayerView.this.seek.setMax(PlayerView.this.mediaPlayer.getDuration());
            if (PlayerView.this.mediaPlayer != null) {
                PlayerView.this.mediaPlayer.start();
                PlayerView.this.handler_pro.postDelayed(PlayerView.this.updateThread, 1000);
            }
        }
    }

    public void setOnOptListener(OnOptListener opt) {
        this.optListener = opt;
    }

    public PlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.content_view = (RelativeLayout) LayoutInflater.from(context).inflate(C0291R.layout.widget_play_component, null);
        addView(this.content_view);
        initComponent();
        setOnClickListener(new C06011());
    }

    private void initComponent() {
        this.svf = (SurfaceView) findViewById(C0291R.id.sfv);
        this.svf.getHolder().setKeepScreenOn(true);
        this.svf.getHolder().addCallback(new PlayerViewHolder());
        this.svf.getHolder().setType(3);
        this.mediaPlayer = new MediaPlayer();
        this.mediaPlayer.setDisplay(this.svf.getHolder());
        this.player_tool_top = (RelativeLayout) this.content_view.findViewById(C0291R.id.player_tool_top);
        this.player_tool_bottom = (RelativeLayout) this.content_view.findViewById(C0291R.id.player_tool_bottom);
        this.img_back = (ImageView) this.content_view.findViewById(C0291R.id.img_back);
        this.img_back.setOnClickListener(new C06022());
        this.img_play = (ImageView) this.content_view.findViewById(C0291R.id.img_play);
        this.img_play.setOnClickListener(new C06033());
        this.img_full = (ImageView) this.content_view.findViewById(C0291R.id.img_full);
        this.img_full.setOnClickListener(new C06044());
        this.seek = (SeekBar) this.content_view.findViewById(C0291R.id.seek);
        this.seek.setOnSeekBarChangeListener(new C06055());
        showTools();
    }

    private void dismissTools() {
        this.isShowComponent = false;
        this.player_tool_top.setVisibility(8);
        this.player_tool_bottom.setVisibility(8);
        this.handler.removeCallbacksAndMessages(null);
    }

    private void showTools() {
        this.isShowComponent = true;
        this.handler.removeCallbacksAndMessages(null);
        this.player_tool_top.setVisibility(0);
        this.player_tool_bottom.setVisibility(0);
        this.handler.postDelayed(new C06066(), 3500);
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        dismissTools();
    }

    public void setDateSource(ArrayList<RecordVideo> list) {
        this.list = list;
    }

    public void play(String path) {
        if (this.mediaPlayer == null) {
            this.mediaPlayer = new MediaPlayer();
        }
        this.mediaPlayer.reset();
        try {
            this.mediaPlayer.setDataSource(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.mediaPlayer.setDisplay(this.svf.getHolder());
        this.mediaPlayer.setOnPreparedListener(new PreparedListener());
        this.mediaPlayer.setOnSeekCompleteListener(new C06088());
        this.mediaPlayer.setOnErrorListener(new C06099());
        this.mediaPlayer.prepareAsync();
    }

    public void play(int position) {
        play(((RecordVideo) this.list.get(position)).getPath());
    }
}
