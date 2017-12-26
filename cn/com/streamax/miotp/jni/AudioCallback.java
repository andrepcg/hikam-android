package cn.com.streamax.miotp.jni;

import android.util.Log;
import com.p2p.core.MediaPlayer;

public class AudioCallback {
    private static final String TAG = AudioCallback.class.getSimpleName();
    private static AudioCallback audioCallback = null;
    private static final int audioEncoding = 2;
    private static final int channelConfiguration = 4;
    private static final int frequency = 8000;
    private static final int mode = 1;
    private static final int streamType = 3;
    private boolean flag = false;
    private int[] iPTS = new int[1];

    public static AudioCallback newInstance() {
        if (audioCallback == null) {
            audioCallback = new AudioCallback();
        }
        return audioCallback;
    }

    public static void unInstance() {
        if (audioCallback != null) {
            audioCallback = null;
        }
    }

    private AudioCallback() {
    }

    public void start() {
        Log.i(TAG, "start");
    }

    public void stop() {
        Log.i(TAG, "stop");
    }

    public void release() {
        Log.i(TAG, "release");
    }

    public void invoke(AudioDataStructure ads) {
        this.iPTS[0] = 0;
        if (MediaPlayer.isMute && ads != null && ads.buffer != null) {
            MediaPlayer.getAudioBuffer(ads.buffer, ads.buffer.length, this.iPTS);
        }
    }

    public boolean isFlag() {
        return this.flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
