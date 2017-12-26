package com.p2p.core;

import android.content.Context;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.os.Build;
import android.os.Process;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.internal.view.SupportMenu;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceView;
import cn.com.streamax.miotp.jni.AudioCallback;
import cn.com.streamax.miotp.jni.VideoCallback;
import cn.com.streamax.miotp.p2p.jni.P2PStreamResource;
import cn.com.streamax.miotp.p2p.jni.P2pJni;
import cn.com.streamax.miotp.p2p.jni.P2pStreamCBDataType;
import cn.com.streamax.miotp.p2p.jni.P2pStreamResources;
import com.p2p.core.P2PInterface.IP2P;
import com.p2p.core.P2PInterface.ISetting;
import com.p2p.core.global.Constants.MsgSection;
import com.p2p.core.utils.MyUtils;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import org.jboss.netty.handler.codec.rtsp.RtspHeaders.Values;

public class MediaPlayer {
    static long AudioTrackPTSBegin = 0;
    public static final int MESG_SDCARD_NO_EXIST = 82;
    public static final int MESG_SET_GPIO_INFO = 96;
    public static final int MESG_TYPE_FORMAT_DISK = 81;
    public static final int MESG_TYPE_GET_DISK_INFO = 80;
    private static final String TAG = "2cu";
    private static Object buf;
    public static String callID = null;
    public static String callModel = null;
    static boolean fgdoPlayInit = true;
    static boolean fgdoRecordInit = true;
    static int frame = 0;
    private static AudioRecord hi_audioRecord = null;
    static int iAudioDataInputNs = 0;
    public static boolean isMute = false;
    public static boolean isSendAudio = false;
    private static AudioRecord mAudioRecord = null;
    private static Thread mAudioThread;
    private static AudioTrack mAudioTrack;
    private static ICapture mCapture = null;
    private static Context mContext;
    private static int mCpuVersion;
    private static EGLConfig mEGLConfig;
    private static EGLContext mEGLContext;
    private static EGLDisplay mEGLDisplay;
    private static EGLSurface mEGLSurface;
    private static EGL10 mEgl;
    private static int mGLMajor;
    private static int mGLMinor;
    private static volatile MediaPlayer manager;
    private static IP2P p2pInterface = null;
    public static int p2pType = 0;
    public static P2PStreamResource requestedStream = null;
    public static P2PStreamResource requestedStream1 = null;
    public static P2PStreamResource requestedStream2 = null;
    public static ISetting settingInterface = null;
    private static Object showView = null;
    static long timeStart = 0;
    private int mNativeContext;
    private boolean mScreenOnWhilePlaying;
    private Surface mSurface;

    class C06832 implements Runnable {
        C06832() {
        }

        public void run() {
            MediaPlayer.this.sendAudioDataThread();
        }
    }

    public interface ICapture {
        void vCaptureResult(int i);
    }

    public interface IFFMpegPlayer {
        void onError(String str, Exception exception);

        void onPlay();

        void onRelease();

        void onStop();
    }

    public static native void CancelGetRemoteFile();

    public static native void ChangeScreenSize(int i, int i2, int i3);

    public static native int EntryPwd(String str);

    public static native int GetAllarmImage(int i, int i2, String str, String str2);

    public static native int GetFileProgress();

    public static native String HTTPDecrypt(String str, String str2, int i);

    public static native String HTTPEncrypt(String str, String str2, int i);

    public static native int MoveView(int i, int i2);

    public static native byte[] P2PEntryPassword(byte[] bArr);

    public static native String RTSPEntry(String str);

    public static native int SendUserData(int i, int i2, byte[] bArr, int i3);

    public static native int SetRobortEmailNew(int i, int i2, int i3, byte b, String str, int i4, String str2, String str3, byte[] bArr, String str4, String str5, byte b2, byte b3, int i5, int i6);

    public static native int SetScreenShotPath(String str, String str2);

    public static native void SetSupperDrop(boolean z);

    public static native void SetSystemMessageIndex(int i, int i2);

    public static native int ZoomView(int i, int i2, float f);

    private native void _InitSession(int i, int i2, int i3) throws IllegalStateException;

    private native void _PauseSession() throws IllegalStateException;

    private native void _StartSending(int i) throws IllegalStateException;

    private native void _StopSession() throws IllegalStateException;

    private native void _setVideoSurface(SurfaceView surfaceView) throws IOException;

    public static native void cancelDeviceUpdate(int i, int i2, int i3);

    public static native void checkDeviceUpdate(int i, int i2, int i3);

    public static native void doDeviceUpdate(int i, int i2, int i3);

    public static native void getDeviceVersion(int i, int i2, int i3);

    public static native int iClearAlarmCodeGroup(int i, int i2, int i3, int i4);

    public static native int iExtendedCmd(int i, int i2, int i3, byte[] bArr, int i4);

    public static native int iGetAlarmCodeStatus(int i, int i2, int i3);

    public static native int iGetBindAlarmId(int i, int i2, int i3);

    public static native int iGetFriendsStatus(int[] iArr, int i);

    public static native int iGetNPCDateTime(int i, int i2, int i3);

    public static native int iGetNPCEmail(int i, int i2, int i3);

    public static native int iGetNPCSettings(int i, int i2, int i3);

    public static native int iGetNPCWifiList(int i, int i2, int i3);

    public static native int iGetRecFiles(int i, int i2, int i3, int i4, int i5);

    public static native int iLocalVideoControl(int i);

    public static native int iRecFilePlayingControl(int i, int i2, byte[] bArr);

    public static native int iSendCmdToFriend(int i, int i2, int i3, byte[] bArr, int i4);

    public static native int iSendCtlCmd(int i, int i2);

    public static native int iSendMesgToFriend(int i, int i2, byte[] bArr, int i3);

    public static native int iSetAlarmCodeStatus(int i, int i2, int i3, int i4, int i5, int[] iArr, int[] iArr2);

    public static native int iSetBindAlarmId(int i, int i2, int i3, int i4, int[] iArr);

    public static native int iSetDevicePwd(int i, int i2, int i3, int i4, int i5, byte[] bArr, int i6, byte[] bArr2);

    public static native int iSetInitPassword(int i, int i2, int i3, int i4, byte[] bArr, int i5, int i6, byte[] bArr2);

    public static native int iSetNPCDateTime(int i, int i2, int i3, int i4);

    public static native int iSetNPCEmail(int i, int i2, int i3, byte[] bArr, int i4);

    public static native int iSetNPCSettings(int i, int i2, int i3, int i4, int i5);

    public static native int iSetNPCWifi(int i, int i2, int i3, int i4, byte[] bArr, int i5, byte[] bArr2, int i6);

    public static native int iSetVideoMode(int i);

    public static native void nativeInit(Object obj);

    public static native void nativeInitPlayBack();

    public static native void nativePause();

    public static native void nativeQuit();

    public static native void nativeResume();

    public static native void nativeRunAudioThread();

    private static final native void native_init(int i) throws RuntimeException;

    private final native void native_setup(Object obj);

    public static native void onNativeAccel(float f, float f2, float f3);

    public static native void onNativeKeyDown(int i);

    public static native void onNativeKeyUp(int i);

    public static native void onNativeResize(int i, int i2, int i3);

    public static native void onNativeTouch(int i, int i2, int i3, float f, float f2, float f3);

    public static native void setBindFlag(int i);

    public static native void vSendWiFiCmd(int i, byte[] bArr, int i2, byte[] bArr2, int i3);

    public native void _CaptureScreen() throws IOException;

    public native int _FillVideoRawFrame(byte[] bArr, int i, int i2, int i3, int i4);

    public native void _SetMute(boolean z) throws IOException;

    public native void _SetRecvAVDataEnable(boolean z);

    public native void _StartPlaying(int i, int i2, int i3) throws IOException, IllegalStateException;

    public native boolean _isPlaying();

    public native long des_password();

    public native void native_p2p_accpet();

    public native int native_p2p_call(long j, int i, int i2, int i3, int i4, byte[] bArr, byte[] bArr2, String str, long j2);

    public native int native_p2p_connect(int i, int i2, int i3, int i4, byte[] bArr, int[] iArr);

    public native void native_p2p_control(int i);

    public native void native_p2p_disconnect();

    public native void native_p2p_hungup();

    static {
        mCpuVersion = 0;
        System.loadLibrary("SDL");
        mCpuVersion = MyUtils.getCPUVesion();
        System.loadLibrary("mediaplayer");
        native_init(mCpuVersion);
    }

    public MediaPlayer() {
        native_setup(new WeakReference(this));
    }

    public MediaPlayer(Context context) {
        native_setup(new WeakReference(this));
        mContext = context;
    }

    public static MediaPlayer getInstance() {
        if (manager == null) {
            synchronized (MediaPlayer.class) {
                if (manager == null) {
                    manager = new MediaPlayer();
                }
            }
        }
        return manager;
    }

    public void setCaptureListener(ICapture captureLister) {
        mCapture = captureLister;
    }

    public void setP2PInterface(IP2P p2pInterface) {
        p2pInterface = p2pInterface;
    }

    public void setSettingInterface(ISetting settingInterface) {
        settingInterface = settingInterface;
    }

    public void setIsSendAudio(boolean bool) {
        isSendAudio = bool;
    }

    public void setP2pType(int p2pType) {
        p2pType = p2pType;
    }

    public void setCallModel(String callModel) {
        callModel = callModel;
    }

    public void setCallID(String callID) {
        callID = callID;
    }

    public static int getConvertAckResult(int result) {
        if (result == 0) {
            return 9997;
        }
        if (result == 1) {
            return 9999;
        }
        if (result == 2) {
            return 9998;
        }
        if (result == 4) {
            return 9996;
        }
        return result;
    }

    public void p2p_start_playback(String fileName, int frameRate, int offset, String password) {
        p2pInterface.vCalling(true, callID, 7);
        isMute = true;
        P2pJni.P2PClientSdkStopPlayback(callID);
        P2pJni.P2PClientSdkStartPlayback(VideoCallback.newInstance(), AudioCallback.newInstance(), callID, fileName, frameRate, offset, password);
        p2pInterface.vAccept(0, 0);
    }

    public void p2p_open_stream(final int mode, final String password) {
        new Thread(new Runnable() {
            public void run() {
                MediaPlayer.this.p2pOpenStreamThread(mode, MediaPlayer.callID, password);
            }
        }, "p2p call Thread").start();
    }

    public void p2p_switch_stream(String password, int mode) {
        int hikam_device_resolution = 0;
        if (callID != null && !"".equals(callID) && requestedStream != null && requestedStream.stream_id != null && requestedStream1 != null && requestedStream1.stream_id != null && requestedStream2 != null && requestedStream2.stream_id != null) {
            if (mode == 7) {
                if (720 == requestedStream1.stream_info.VideoHeight) {
                    hikam_device_resolution = 1;
                } else if (960 == requestedStream1.stream_info.VideoHeight) {
                    hikam_device_resolution = 2;
                } else if (1080 == requestedStream1.stream_info.VideoHeight) {
                    hikam_device_resolution = 3;
                }
                P2pJni.P2PClientSdkSwitchStream(callID, requestedStream.stream_id, requestedStream1.stream_id, password, hikam_device_resolution);
                requestedStream = requestedStream1;
                return;
            }
            if (360 == requestedStream2.stream_info.VideoHeight) {
                hikam_device_resolution = 0;
            }
            P2pJni.P2PClientSdkSwitchStream(callID, requestedStream.stream_id, requestedStream2.stream_id, password, hikam_device_resolution);
            requestedStream = requestedStream2;
        }
    }

    public void p2p_close_stream() {
        Log.e("2cu", "P2pJni.p2p_close_stream = 0");
        if (callID != null && !"".equals(callID) && requestedStream != null && requestedStream.stream_id != null) {
            P2pJni.P2PClientSdkCloseStream(callID, requestedStream.stream_id);
            Log.e("2cu", "P2pJni.p2p_close_stream = 1");
            requestedStream = null;
            requestedStream1 = null;
            requestedStream2 = null;
        }
    }

    private void p2pOpenStreamThread(int mode, String peer, String password) {
        p2pInterface.vCalling(true, peer, 7);
        int hikam_device_resolution = 0;
        P2pStreamResources descrs = new P2pStreamResources();
        if (P2pJni.P2PClientSdkGetStreamDescr(peer, descrs) != 0) {
            p2pInterface.vReject(13);
        } else if (descrs.streamResourceArr.length <= 0) {
            Log.e("2cu", "P2pJni.P2PClientSdkGetStreamDescr count = 0");
            p2pInterface.vReject(12);
        } else {
            if (descrs.streamResourceArr.length == 1) {
                requestedStream = descrs.streamResourceArr[0];
                requestedStream1 = descrs.streamResourceArr[0];
                requestedStream2 = descrs.streamResourceArr[0];
            } else if (descrs.streamResourceArr[0].stream_info.VideoWidth * descrs.streamResourceArr[0].stream_info.VideoHeight >= descrs.streamResourceArr[1].stream_info.VideoWidth * descrs.streamResourceArr[1].stream_info.VideoHeight) {
                requestedStream1 = descrs.streamResourceArr[0];
                requestedStream2 = descrs.streamResourceArr[1];
                if (mode == 7) {
                    requestedStream = descrs.streamResourceArr[0];
                    if (720 == requestedStream.stream_info.VideoHeight) {
                        hikam_device_resolution = 1;
                    } else if (960 == requestedStream.stream_info.VideoHeight) {
                        hikam_device_resolution = 2;
                    } else if (1080 == requestedStream.stream_info.VideoHeight) {
                        hikam_device_resolution = 3;
                    }
                } else {
                    requestedStream = descrs.streamResourceArr[1];
                    if (360 == requestedStream.stream_info.VideoHeight) {
                        hikam_device_resolution = 0;
                    }
                }
            } else {
                requestedStream1 = descrs.streamResourceArr[1];
                requestedStream2 = descrs.streamResourceArr[0];
                if (mode == 7) {
                    requestedStream = descrs.streamResourceArr[1];
                    if (720 == requestedStream.stream_info.VideoHeight) {
                        hikam_device_resolution = 1;
                    } else if (960 == requestedStream.stream_info.VideoHeight) {
                        hikam_device_resolution = 2;
                    } else if (1080 == requestedStream.stream_info.VideoHeight) {
                        hikam_device_resolution = 3;
                    }
                } else {
                    requestedStream = descrs.streamResourceArr[0];
                    if (360 == requestedStream.stream_info.VideoHeight) {
                        hikam_device_resolution = 0;
                    }
                }
            }
            P2pStreamCBDataType cbCMD = new P2pStreamCBDataType();
            cbCMD.data_type = 1;
            cbCMD.image_format = 5;
            cbCMD.sync_flag = 0;
            requestedStream.stream_id.audio_en = 1;
            if (P2pJni.P2PClientSdkOpenStream(VideoCallback.newInstance(), AudioCallback.newInstance(), peer, requestedStream.stream_id, cbCMD, password, hikam_device_resolution) >= 0) {
                p2pInterface.vAccept(0, 0);
                return;
            }
            Log.e("2cu", "P2pJni.P2PClientSdkOpenStream failed");
            p2pInterface.vReject(6);
        }
    }

    private static void postEventFromNative(Object mediaplayer_ref, int what, int iDesID, int arg1, int arg2, String msgStr) {
        int reason_code = 0;
        if (msgStr.equals("pw_incrrect")) {
            reason_code = 0;
        } else if (msgStr.equals("busy")) {
            reason_code = 1;
        } else if (msgStr.equals("none")) {
            reason_code = 2;
        } else if (msgStr.equals("id_disabled")) {
            reason_code = 3;
        } else if (msgStr.equals("id_overdate")) {
            reason_code = 4;
        } else if (msgStr.equals("id_inactived")) {
            reason_code = 5;
        } else if (msgStr.equals("offline")) {
            reason_code = 6;
        } else if (msgStr.equals("powerdown")) {
            reason_code = 7;
        } else if (msgStr.equals("nohelper")) {
            reason_code = 8;
        } else if (msgStr.equals("hungup")) {
            reason_code = 9;
        } else if (msgStr.equals(Values.TIMEOUT)) {
            reason_code = 10;
        } else if (msgStr.equals("nobody")) {
            reason_code = 11;
        } else if (msgStr.equals("internal_error")) {
            reason_code = 12;
        } else if (msgStr.equals("conn_fail")) {
            reason_code = 13;
        } else if (msgStr.equals("not_support")) {
            reason_code = 14;
        }
        switch (what) {
            case 1:
                String threeNumber = "";
                if (arg2 > 0) {
                    threeNumber = String.valueOf(arg2);
                } else {
                    threeNumber = "0" + String.valueOf(0 - arg2);
                }
                if (arg1 == 1) {
                    p2pInterface.vCalling(false, threeNumber, Integer.parseInt(msgStr));
                    return;
                } else {
                    p2pInterface.vCalling(true, threeNumber, Integer.parseInt(msgStr));
                    return;
                }
            case 2:
                p2pInterface.vReject(reason_code);
                return;
            case 3:
                p2pInterface.vAccept(arg1, arg2);
                return;
            case 4:
                p2pInterface.vConnectReady();
                return;
            case 5:
                if (mCapture != null) {
                    mCapture.vCaptureResult(arg1);
                    return;
                }
                return;
            case 6:
                if (arg1 < MsgSection.MSG_ID_SET_REMOTE_DEFENCE && arg1 >= MsgSection.MSG_ID_SET_REMOTE_DEFENCE + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED) {
                    settingInterface.ACK_vRetSetRemoteDefence(String.valueOf(iDesID), arg1, getConvertAckResult(arg2));
                    return;
                } else if (arg1 < MsgSection.MSG_ID_SET_REMOTE_RECORD && arg1 >= MsgSection.MSG_ID_SET_REMOTE_RECORD + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED) {
                    settingInterface.ACK_vRetSetRemoteRecord(arg1, getConvertAckResult(arg2));
                    return;
                } else if (arg1 < MsgSection.MSG_ID_SETTING_DEVICE_TIME && arg1 >= MsgSection.MSG_ID_SETTING_DEVICE_TIME + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED) {
                    settingInterface.ACK_vRetSetDeviceTime(arg1, getConvertAckResult(arg2));
                    return;
                } else if (arg1 < MsgSection.MSG_ID_GETTING_DEVICE_TIME && arg1 >= MsgSection.MSG_ID_GETTING_DEVICE_TIME + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED) {
                    settingInterface.ACK_vRetGetDeviceTime(arg1, getConvertAckResult(arg2));
                    return;
                } else if (arg1 < MsgSection.MSG_ID_GETTING_NPC_SETTINGS && arg1 >= MsgSection.MSG_ID_GETTING_NPC_SETTINGS + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED) {
                    settingInterface.ACK_vRetGetNpcSettings(String.valueOf(iDesID), arg1, getConvertAckResult(arg2));
                    return;
                } else if (arg1 < MsgSection.MSG_ID_SETTING_NPC_SETTINGS_VIDEO_FORMAT && arg1 >= MsgSection.MSG_ID_SETTING_NPC_SETTINGS_VIDEO_FORMAT + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED) {
                    settingInterface.ACK_vRetSetNpcSettingsVideoFormat(arg1, getConvertAckResult(arg2));
                    return;
                } else if (arg1 < MsgSection.MSG_ID_SETTING_NPC_SETTINGS_VIDEO_VOLUME && arg1 >= MsgSection.MSG_ID_SETTING_NPC_SETTINGS_VIDEO_VOLUME + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED) {
                    settingInterface.ACK_vRetSetNpcSettingsVideoVolume(arg1, getConvertAckResult(arg2));
                    return;
                } else if (arg1 < MsgSection.MSG_ID_SETTING_NPC_SETTINGS_BUZZER && arg1 >= MsgSection.MSG_ID_SETTING_NPC_SETTINGS_BUZZER + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED) {
                    settingInterface.ACK_vRetSetNpcSettingsBuzzer(arg1, getConvertAckResult(arg2));
                    return;
                } else if (arg1 < MsgSection.MSG_ID_SETTING_NPC_SETTINGS_MOTION && arg1 >= MsgSection.MSG_ID_SETTING_NPC_SETTINGS_MOTION + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED) {
                    settingInterface.ACK_vRetSetNpcSettingsMotion(arg1, getConvertAckResult(arg2));
                    return;
                } else if (arg1 < MsgSection.MSG_ID_SETTING_NPC_SETTINGS_RECORD_TYPE && arg1 >= MsgSection.MSG_ID_SETTING_NPC_SETTINGS_RECORD_TYPE + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED) {
                    settingInterface.ACK_vRetSetNpcSettingsRecordType(arg1, getConvertAckResult(arg2));
                    return;
                } else if (arg1 < MsgSection.MSG_ID_SETTING_NPC_SETTINGS_RECORD_TIME && arg1 >= MsgSection.MSG_ID_SETTING_NPC_SETTINGS_RECORD_TIME + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED) {
                    settingInterface.ACK_vRetSetNpcSettingsRecordTime(arg1, getConvertAckResult(arg2));
                    return;
                } else if (arg1 < MsgSection.MSG_ID_SETTING_NPC_SETTINGS_RECORD_PLAN_TIME && arg1 >= MsgSection.MSG_ID_SETTING_NPC_SETTINGS_RECORD_PLAN_TIME + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED) {
                    settingInterface.ACK_vRetSetNpcSettingsRecordPlanTime(arg1, getConvertAckResult(arg2));
                    return;
                } else if (arg1 < MsgSection.MSG_ID_SETTING_NPC_SETTINGS_NET_TYPE && arg1 >= MsgSection.MSG_ID_SETTING_NPC_SETTINGS_NET_TYPE + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED) {
                    settingInterface.ACK_vRetSetNpcSettingsNetType(arg1, getConvertAckResult(arg2));
                    return;
                } else if (arg1 < MsgSection.MSG_ID_SETTING_ALARM_EMAIL && arg1 >= MsgSection.MSG_ID_SETTING_ALARM_EMAIL + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED) {
                    settingInterface.ACK_vRetSetAlarmEmail(arg1, getConvertAckResult(arg2));
                    return;
                } else if (arg1 < MsgSection.MSG_ID_GETTING_ALARM_EMAIL && arg1 >= MsgSection.MSG_ID_GETTING_ALARM_EMAIL + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED) {
                    settingInterface.ACK_vRetGetAlarmEmail(arg1, getConvertAckResult(arg2));
                    return;
                } else if (arg1 < MsgSection.MSG_ID_SETTING_ALARM_BIND_ID && arg1 >= MsgSection.MSG_ID_SETTING_ALARM_BIND_ID + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED) {
                    settingInterface.ACK_vRetSetAlarmBindId(arg1, getConvertAckResult(arg2));
                    return;
                } else if (arg1 < MsgSection.MSG_ID_GETTING_ALARM_BIND_ID && arg1 >= MsgSection.MSG_ID_GETTING_ALARM_BIND_ID + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED) {
                    settingInterface.ACK_vRetGetAlarmBindId(arg1, getConvertAckResult(arg2));
                    return;
                } else if (arg1 < MsgSection.MSG_ID_SETTING_INIT_PASSWORD && arg1 >= MsgSection.MSG_ID_SETTING_INIT_PASSWORD + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED) {
                    settingInterface.ACK_vRetSetInitPassword(arg1, getConvertAckResult(arg2));
                    return;
                } else if (arg1 < MsgSection.MSG_ID_SETTING_DEVICE_PASSWORD && arg1 >= MsgSection.MSG_ID_SETTING_DEVICE_PASSWORD + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED) {
                    settingInterface.ACK_vRetSetDevicePassword(arg1, getConvertAckResult(arg2));
                    return;
                } else if (arg1 < MsgSection.MSG_ID_CHECK_DEVICE_PASSWORD && arg1 >= MsgSection.MSG_ID_CHECK_DEVICE_PASSWORD + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED) {
                    settingInterface.ACK_vRetCheckDevicePassword(arg1, getConvertAckResult(arg2));
                    return;
                } else if (arg1 < MsgSection.MSG_ID_SETTING_DEFENCEAREA && arg1 >= MsgSection.MSG_ID_SETTING_DEFENCEAREA + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED) {
                    settingInterface.ACK_vRetSetDefenceArea(arg1, getConvertAckResult(arg2));
                    return;
                } else if (arg1 < MsgSection.MSG_ID_GETTING_DEFENCEAREA && arg1 >= MsgSection.MSG_ID_GETTING_DEFENCEAREA + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED) {
                    settingInterface.ACK_vRetGetDefenceArea(arg1, getConvertAckResult(arg2));
                    return;
                } else if (arg1 < MsgSection.MSG_ID_SETTING_WIFI && arg1 >= MsgSection.MSG_ID_SETTING_WIFI + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED) {
                    settingInterface.ACK_vRetSetWifi(arg1, getConvertAckResult(arg2));
                    return;
                } else if (arg1 < MsgSection.MSG_ID_GETTING_WIFI_LIST && arg1 >= MsgSection.MSG_ID_GETTING_WIFI_LIST + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED) {
                    settingInterface.ACK_vRetGetWifiList(arg1, getConvertAckResult(arg2));
                    return;
                } else if (arg1 < MsgSection.MSG_ID_GETTING_RECORD_FILE_LIST && arg1 >= MsgSection.MSG_ID_GETTING_RECORD_FILE_LIST + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED) {
                    settingInterface.ACK_vRetGetRecordFileList(arg1, getConvertAckResult(arg2));
                    return;
                } else if (arg1 < MsgSection.MSG_ID_SEND_MESSAGE && arg1 >= MsgSection.MSG_ID_SEND_MESSAGE + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED) {
                    settingInterface.ACK_vRetMessage(arg1, getConvertAckResult(arg2));
                    return;
                } else if (arg1 < MsgSection.MSG_ID_SEND_CUSTOM_CMD && arg1 >= MsgSection.MSG_ID_SEND_CUSTOM_CMD + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED) {
                    settingInterface.ACK_vRetCustomCmd(arg1, getConvertAckResult(arg2));
                    return;
                } else if (arg1 < MsgSection.MSG_ID_CHECK_DEVICE_UPDATE && arg1 >= MsgSection.MSG_ID_CHECK_DEVICE_UPDATE + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED) {
                    settingInterface.ACK_vRetCheckDeviceUpdate(arg1, getConvertAckResult(arg2));
                    return;
                } else if (arg1 < MsgSection.MSG_ID_CANCEL_DEVICE_UPDATE && arg1 >= MsgSection.MSG_ID_CANCEL_DEVICE_UPDATE + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED) {
                    settingInterface.ACK_vRetCancelDeviceUpdate(arg1, getConvertAckResult(arg2));
                    return;
                } else if (arg1 < MsgSection.MSG_ID_DO_DEVICE_UPDATE && arg1 >= MsgSection.MSG_ID_DO_DEVICE_UPDATE + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED) {
                    settingInterface.ACK_vRetDoDeviceUpdate(arg1, getConvertAckResult(arg2));
                    return;
                } else if (arg1 < MsgSection.MSG_ID_GET_DEFENCE_STATE && arg1 >= MsgSection.MSG_ID_GET_DEFENCE_STATE + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED) {
                    settingInterface.ACK_vRetGetDefenceStates(String.valueOf(iDesID), arg1, getConvertAckResult(arg2));
                    return;
                } else if (arg1 < MsgSection.MSG_ID_GET_DEVICE_VERSION && arg1 >= MsgSection.MSG_ID_GET_DEVICE_VERSION + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED) {
                    settingInterface.ACK_vRetGetDeviceVersion(arg1, getConvertAckResult(arg2));
                    return;
                } else if (arg1 < MsgSection.MSG_ID_CLEAR_DEFENCE_GROUP && arg1 >= MsgSection.MSG_ID_CLEAR_DEFENCE_GROUP + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED) {
                    settingInterface.ACK_vRetClearDefenceAreaState(arg1, getConvertAckResult(arg2));
                    return;
                } else if (arg1 < MsgSection.MESG_ID_STTING_PIC_REVERSE && arg1 >= MsgSection.MESG_ID_STTING_PIC_REVERSE + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED) {
                    settingInterface.ACK_vRetSetImageReverse(arg1, getConvertAckResult(arg2));
                    return;
                } else if (arg1 < MsgSection.MESG_ID_STTING_IR_ALARM_EN && arg1 >= MsgSection.MESG_ID_STTING_IR_ALARM_EN + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED) {
                    settingInterface.ACK_vRetSetInfraredSwitch(arg1, getConvertAckResult(arg2));
                    return;
                } else if (arg1 < MsgSection.MESG_STTING_ID_EXTLINE_ALARM_IN_EN && arg1 >= MsgSection.MESG_STTING_ID_EXTLINE_ALARM_IN_EN + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED) {
                    settingInterface.ACK_vRetSetWiredAlarmInput(arg1, getConvertAckResult(arg2));
                    return;
                } else if (arg1 < MsgSection.MESG_STTING_ID_EXTLINE_ALARM_OUT_EN && arg1 >= MsgSection.MESG_STTING_ID_EXTLINE_ALARM_OUT_EN + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED) {
                    settingInterface.ACK_vRetSetWiredAlarmOut(arg1, getConvertAckResult(arg2));
                    return;
                } else if (arg1 < MsgSection.MESG_STTING_ID_SECUPGDEV && arg1 >= MsgSection.MESG_STTING_ID_SECUPGDEV + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED) {
                    settingInterface.ACK_vRetSetAutomaticUpgrade(arg1, getConvertAckResult(arg2));
                    return;
                } else if (arg1 < MsgSection.MESG_STTING_ID_GUEST_PASSWD && arg1 >= MsgSection.MESG_STTING_ID_GUEST_PASSWD + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED) {
                    settingInterface.ACK_VRetSetVisitorDevicePassword(arg1, getConvertAckResult(arg2));
                    return;
                } else if (arg1 < MsgSection.MESG_STTING_ID_TIMEZONE && arg1 >= MsgSection.MESG_STTING_ID_TIMEZONE + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED) {
                    settingInterface.ACK_vRetSetTimeZone(arg1, getConvertAckResult(arg2));
                    return;
                } else if (arg1 < MsgSection.MESG_GET_SD_CARD_CAPACITY && arg1 >= MsgSection.MESG_GET_SD_CARD_CAPACITY + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED) {
                    settingInterface.ACK_vRetGetSDCard(arg1, getConvertAckResult(arg2));
                    return;
                } else if (arg1 < MsgSection.MESG_SD_CARD_FORMAT && arg1 >= MsgSection.MESG_SD_CARD_FORMAT + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED) {
                    settingInterface.ACK_vRetSdFormat(arg1, getConvertAckResult(arg2));
                    return;
                } else {
                    return;
                }
            case 8:
                p2pInterface.vChangeVideoMask(arg1);
                return;
            default:
                return;
        }
    }

    public static void openAudioTrack() {
        try {
            int maxjitter = AudioTrack.getMinBufferSize(8000, 4, 2);
            if (Build.MODEL.equals("HTC One X")) {
                mAudioTrack = new AudioTrack(0, 8000, 4, 2, maxjitter * 2, 1);
            } else {
                mAudioTrack = new AudioTrack(3, 8000, 4, 2, maxjitter * 2, 1);
            }
            Log.i("2cu", "Audio Track min buffer size:" + maxjitter);
            iAudioDataInputNs = 0;
            AudioTrackPTSBegin = System.currentTimeMillis();
            mAudioTrack.play();
            fgdoPlayInit = true;
        } catch (Exception e) {
            Log.e("test", "error");
        }
    }

    public static void openAudioRecord() {
        int min = AudioRecord.getMinBufferSize(8000, 16, 2);
        if (P2PValue.HikamDeviceModelList.contains(callModel)) {
            hi_audioRecord = new AudioRecord(5, 8000, 16, 2, min);
            if (hi_audioRecord != null && hi_audioRecord.getState() == 1) {
                hi_audioRecord.startRecording();
                fgdoRecordInit = true;
                return;
            }
            return;
        }
        mAudioRecord = new AudioRecord(5, 8000, 16, 2, min);
        if (mAudioRecord != null && mAudioRecord.getState() == 1) {
            mAudioRecord.startRecording();
            fgdoRecordInit = true;
        }
    }

    private void sendAudioDataThread() {
        byte[] buffer = new byte[320];
        try {
            int[] iPTS = new int[1];
            Log.e("few", "MediaPlayer sendAudioDataThread create byte");
            while (hi_audioRecord != null && hi_audioRecord.getRecordingState() == 3) {
                if (!isMute) {
                    int readNum = hi_audioRecord.read(buffer, 0, buffer.length);
                    if (readNum > 0) {
                        P2pJni.P2PClientSdkSendAudioData(callID, buffer, readNum);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int setAudioBuffer(byte[] buffer, int buffer_size, int[] iPTS) {
        if (mAudioRecord == null) {
            return 0;
        }
        if (fgdoRecordInit) {
            try {
                Process.setThreadPriority(-19);
            } catch (Exception e) {
            }
            fgdoRecordInit = false;
        }
        int readNum = mAudioRecord.read(buffer, 0, buffer_size);
        iPTS[0] = (int) ((System.currentTimeMillis() - AudioTrackPTSBegin) - ((long) (readNum / 16)));
        Log.e("few", "MediaPlayer setAudioBuffer use byte");
        return readNum;
    }

    public static void getAudioBuffer(byte[] buffer, int buffer_size, int[] iPTS) {
        if (mAudioTrack != null) {
            if (!P2PValue.HikamDeviceModelList.contains(callModel)) {
                if (mAudioTrack.getPlayState() != 1) {
                    int iTime1 = (int) (System.currentTimeMillis() - AudioTrackPTSBegin);
                    iPTS[0] = ((iAudioDataInputNs - mAudioTrack.getPlaybackHeadPosition()) / 8) + iTime1;
                    Log.e("few", "MediaPlayer getAudioBuffer use byte");
                } else {
                    return;
                }
            }
            if (fgdoPlayInit) {
                try {
                    Process.setThreadPriority(-19);
                } catch (Exception e) {
                }
                fgdoPlayInit = false;
            }
            if (mAudioTrack.getState() == 1 && mAudioTrack.getPlayState() != 1) {
                mAudioTrack.write(buffer, 0, 320);
                iAudioDataInputNs += buffer_size / 2;
            }
        }
    }

    private static void RecvAVData(byte[] AudioBuffer, int AudioLen, int AudioFrames, long AudioPTS, byte[] VideoBuffer, int VideoLen, long VideoPTS) {
        p2pInterface.vRecvAudioVideoData(AudioBuffer, AudioLen, AudioFrames, AudioPTS, VideoBuffer, VideoLen, VideoPTS);
    }

    public void setDisplay(SurfaceView sh) throws IOException {
        if (P2PValue.HikamDeviceModelList.contains(callModel)) {
            VideoCallback.sv = sh;
        } else {
            _setVideoSurface(sh);
        }
    }

    public void init(int width, int height, int fullScreenSize) throws IllegalStateException {
        if (!P2PValue.HikamDeviceModelList.contains(callModel)) {
            _InitSession(width, height, fullScreenSize);
        }
    }

    public void start(int iFrameRate) throws IllegalStateException {
        openAudioRecord();
        if (p2pType == 1 && P2PValue.HikamDeviceModelList.contains(callModel)) {
            new Thread(new C06832(), "AudioRecorder Thread").start();
        }
        if (!P2PValue.HikamDeviceModelList.contains(callModel)) {
            _StartSending(iFrameRate);
        }
    }

    public void stop() throws IllegalStateException {
        if (P2PValue.HikamDeviceModelList.contains(callModel)) {
            if (p2pType == 2) {
                P2pJni.P2PClientSdkStopPlayback(callID);
            }
            p2pInterface.vReject(9);
        }
        _StopSession();
        if (mAudioTrack != null) {
            mAudioTrack.flush();
            mAudioTrack.stop();
            mAudioTrack.release();
            mAudioTrack = null;
        }
        if (P2PValue.HikamDeviceModelList.contains(callModel)) {
            if (hi_audioRecord != null) {
                if (hi_audioRecord.getState() == 1) {
                    hi_audioRecord.stop();
                }
                hi_audioRecord.release();
                hi_audioRecord = null;
            }
        } else if (mAudioRecord != null) {
            if (mAudioRecord.getState() == 1) {
                mAudioRecord.stop();
            }
            mAudioRecord.release();
            mAudioRecord = null;
        }
    }

    public void pause() throws IllegalStateException {
        if (!P2PValue.HikamDeviceModelList.contains(callModel)) {
            _PauseSession();
        }
    }

    public void setScreenOnWhilePlaying(boolean screenOn) {
        if (this.mScreenOnWhilePlaying != screenOn) {
            this.mScreenOnWhilePlaying = screenOn;
        }
    }

    public void native_p2p_controls(String peer, String password, int control) {
        if (P2PValue.HikamDeviceModelList.contains(callModel)) {
            P2pJni.P2PClientSdkPTController(peer, password, control + 1, 735800);
        } else {
            native_p2p_control(control);
        }
    }

    public void release() {
    }

    public void reset() {
    }

    public static void testFunction(int sb1, int sb2) {
    }

    public static void GLNativeResize(int w, int h, int sdlFormat) {
        Log.e("few", "--miao---------------- " + w + " -- " + h + " -- " + sdlFormat);
        if (P2PValue.HikamDeviceModelList.contains(callModel)) {
            P2pJni.onNativeResize(w, h, sdlFormat);
        } else {
            onNativeResize(w, h, sdlFormat);
        }
    }

    public static boolean createGLContext(int majorVersion, int minorVersion) {
        Log.e("2cu", "createGLContext");
        return initEGL(majorVersion, minorVersion);
    }

    public static void flipBuffers() {
        flipEGL();
    }

    public static Object audioInit(int sampleRate, boolean is16Bit, boolean isStereo, int desiredFrames) {
        return null;
    }

    public static void audioWriteShortBuffer(short[] buffer) {
    }

    public static void audioWriteByteBuffer(byte[] buffer) {
    }

    public static void audioQuit() {
        Log.i("2cu", "++ audioQuit");
        if (mAudioThread != null) {
            try {
                mAudioThread.join();
            } catch (Exception e) {
                Log.v("2cu", "Problem stopping audio thread: " + e);
            }
            mAudioThread = null;
        }
        if (mAudioTrack != null) {
            mAudioTrack.stop();
            mAudioTrack.release();
            mAudioTrack = null;
        }
        Log.i("2cu", "-- audioQuit");
    }

    public static void audioStartThread() {
    }

    public static boolean initEGL(int majorVersion, int minorVersion) {
        Log.i("2cu", "++ initEGL");
        Log.i("surface", "initEGL");
        if (mEGLDisplay == null) {
            try {
                if (mEgl == null) {
                    mEgl = (EGL10) EGLContext.getEGL();
                }
                EGLDisplay dpy = mEgl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
                mEgl.eglInitialize(dpy, new int[2]);
                int renderableType = 0;
                if (majorVersion == 2) {
                    renderableType = 4;
                } else if (majorVersion == 1) {
                    renderableType = 1;
                }
                EGLConfig[] configs = new EGLConfig[1];
                int[] num_config = new int[1];
                if (!mEgl.eglChooseConfig(dpy, new int[]{12352, renderableType, 12344}, configs, 1, num_config) || num_config[0] == 0) {
                    Log.e("2cu", "No EGL config available");
                    return false;
                }
                EGLConfig config = configs[0];
                mEGLDisplay = dpy;
                mEGLConfig = config;
                mGLMajor = majorVersion;
                mGLMinor = minorVersion;
                Log.i("SDL", "majorVersion " + majorVersion);
                Log.i("SDL", "minorVersion " + minorVersion);
                createEGLSurface();
            } catch (Exception e) {
                Log.v("2cu", e + "");
                for (StackTraceElement s : e.getStackTrace()) {
                    Log.v("2cu", s.toString());
                }
            }
        } else {
            createEGLSurface();
        }
        Log.i("2cu", "-- initEGL");
        return true;
    }

    public static void setEglView(Object view) {
        if (P2PValue.HikamDeviceModelList.contains(callModel)) {
            P2pJni.setEglView(view);
        } else {
            showView = view;
        }
    }

    public static void flipEGL() {
        if (frame == 0) {
            try {
            } catch (Exception e) {
                Log.v("2cu", "flipEGL(): " + e);
                for (StackTraceElement s : e.getStackTrace()) {
                    Log.v("2cu", s.toString());
                }
                return;
            }
        }
        mEgl.eglMakeCurrent(mEGLDisplay, mEGLSurface, mEGLSurface, mEGLContext);
        mEgl.eglWaitNative(12379, null);
        mEgl.eglWaitGL();
        mEgl.eglSwapBuffers(mEGLDisplay, mEGLSurface);
    }

    public static boolean createEGLContext() {
        mEGLContext = mEgl.eglCreateContext(mEGLDisplay, mEGLConfig, EGL10.EGL_NO_CONTEXT, new int[]{12440, mGLMajor, 12344});
        if (mEGLContext != EGL10.EGL_NO_CONTEXT) {
            return true;
        }
        Log.e("SDL", "Couldn't create context");
        return false;
    }

    public static boolean createEGLSurface() {
        Log.i("2cu", "createEGLSurface");
        if (mEGLDisplay == null || mEGLConfig == null) {
            return false;
        }
        if (mEGLContext == null) {
            createEGLContext();
        }
        Log.v("2cu", "Creating new EGL Surface");
        EGLSurface surface = mEgl.eglCreateWindowSurface(mEGLDisplay, mEGLConfig, showView, null);
        if (surface == EGL10.EGL_NO_SURFACE) {
            Log.e("2cu", "Couldn't create surface");
            return false;
        }
        if (!mEgl.eglMakeCurrent(mEGLDisplay, surface, surface, mEGLContext)) {
            Log.e("2cu", "Old EGL Context doesnt work, trying with a new one");
            createEGLContext();
            if (!mEgl.eglMakeCurrent(mEGLDisplay, surface, surface, mEGLContext)) {
                Log.e("2cu", "Failed making EGL Context current");
                return false;
            }
        }
        mEGLSurface = surface;
        return true;
    }

    public static void ReleaseOpenGL() {
        if (P2PValue.HikamDeviceModelList.contains(callModel)) {
            P2pJni.deleteGLContext();
            return;
        }
        if (mEgl != null) {
            mEgl.eglMakeCurrent(EGL10.EGL_NO_DISPLAY, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
        }
        if (mEGLContext != null) {
            mEgl.eglDestroyContext(mEGLDisplay, mEGLContext);
            mEGLContext = null;
        }
        if (mEGLSurface != null) {
            mEgl.eglDestroySurface(mEGLDisplay, mEGLSurface);
            mEGLSurface = null;
        }
        if (mEGLDisplay != null) {
            mEgl.eglTerminate(mEGLDisplay);
            mEGLDisplay = null;
        }
    }

    public static void vRetNPCSettings(int iSrcID, int iCount, int[] iSettingID, int[] iValue, int iResult) {
        if (settingInterface != null) {
            if (iResult == 1) {
                Log.e("my", "获取");
                for (int i = 0; i < iCount; i++) {
                    if (iSettingID[i] == 0) {
                        settingInterface.vRetGetRemoteDefenceResult("" + iSrcID, iValue[i]);
                    }
                    if (iSettingID[i] == 1) {
                        settingInterface.vRetGetBuzzerResult(iValue[i]);
                    }
                    if (iSettingID[i] == 4) {
                        settingInterface.vRetGetRemoteRecordResult(iValue[i]);
                    }
                    if (iSettingID[i] == 2) {
                        settingInterface.vRetGetMotionResult(iValue[i]);
                    }
                    if (iSettingID[i] == 8) {
                        settingInterface.vRetGetVideoFormatResult(iValue[i]);
                    }
                    if (iSettingID[i] == 3) {
                        settingInterface.vRetGetRecordTypeResult(iValue[i]);
                    }
                    if (iSettingID[i] == 11) {
                        settingInterface.vRetGetRecordTimeResult(iValue[i]);
                    }
                    if (iSettingID[i] == 13) {
                        settingInterface.vRetGetNetTypeResult(iValue[i] & SupportMenu.USER_MASK);
                    }
                    if (iSettingID[i] == 14) {
                        settingInterface.vRetGetVideoVolumeResult(iValue[i]);
                    }
                    if (iSettingID[i] == 5) {
                        settingInterface.vRetGetRecordPlanTimeResult(MyUtils.convertPlanTime(iValue[i]));
                    }
                    if (iSettingID[i] == 24) {
                        settingInterface.vRetGetImageReverseResult(iValue[i]);
                    }
                    if (iSettingID[i] == 17) {
                        settingInterface.vRetGetInfraredSwitch(iValue[i]);
                    }
                    if (iSettingID[i] == 18) {
                        settingInterface.vRetGetWiredAlarmInput(iValue[i]);
                    }
                    if (iSettingID[i] == 19) {
                        settingInterface.vRetGetWiredAlarmOut(iValue[i]);
                    }
                    if (iSettingID[i] == 16) {
                        settingInterface.vRetGetAutomaticUpgrade(iValue[i]);
                    }
                    if (iSettingID[i] == 20) {
                        settingInterface.vRetGetTimeZone(iValue[i]);
                    }
                    if (iSettingID[i] == 27) {
                        settingInterface.vRetGetAudioDeviceType(iValue[i]);
                    }
                }
            } else if (iSettingID[0] == 8) {
                settingInterface.vRetSetVideoFormatResult(iResult);
            } else if (iSettingID[0] == 14) {
                settingInterface.vRetSetVolumeResult(iResult);
            } else if (iSettingID[0] == 1) {
                settingInterface.vRetSetBuzzerResult(iResult);
            } else if (iSettingID[0] == 3) {
                settingInterface.vRetSetRecordTypeResult(iResult);
            } else if (iSettingID[0] == 2) {
                settingInterface.vRetSetMotionResult(iResult);
            } else if (iSettingID[0] == 11) {
                settingInterface.vRetSetRecordTimeResult(iResult);
            } else if (iSettingID[0] == 5) {
                settingInterface.vRetSetRecordPlanTimeResult(iResult);
            } else if (iSettingID[0] == 0) {
                settingInterface.vRetSetRemoteDefenceResult(iResult);
            } else if (iSettingID[0] == 9) {
                settingInterface.vRetSetDevicePasswordResult(iResult);
            } else if (iSettingID[0] == 13) {
                settingInterface.vRetSetNetTypeResult(iResult);
            } else if (iSettingID[0] == 4) {
                settingInterface.vRetSetRemoteRecordResult(iResult);
            } else if (iSettingID[0] == 24) {
                settingInterface.vRetSetImageReverse(iResult);
            } else if (iSettingID[0] == 17) {
                settingInterface.vRetSetInfraredSwitch(iResult);
            } else if (iSettingID[0] == 18) {
                settingInterface.vRetSetWiredAlarmInput(iResult);
            } else if (iSettingID[0] == 19) {
                settingInterface.vRetSetWiredAlarmOut(iResult);
            } else if (iSettingID[0] == 16) {
                settingInterface.vRetSetAutomaticUpgrade(iResult);
            } else if (iSettingID[0] == 21) {
                settingInterface.vRetSetVisitorDevicePassword(iResult);
            } else if (iSettingID[0] == 20) {
                settingInterface.vRetSetTimeZone(iResult);
            }
        }
    }

    public static void vRetFriendsStatus(int iFriendsCount, int[] iIDArray, byte[] bStatus, byte[] bType) {
        String[] threeNumbers = new String[iFriendsCount];
        int[] status = new int[iFriendsCount];
        int[] types = new int[iFriendsCount];
        for (int i = 0; i < iFriendsCount; i++) {
            int id = iIDArray[i] & Integer.MAX_VALUE;
            int type = bType[i] & 15;
            status[i] = bStatus[i] & 15;
            types[i] = type;
            if ((iIDArray[i] & Integer.MIN_VALUE) != 0) {
                threeNumbers[i] = "0" + id;
            } else {
                threeNumbers[i] = "" + id;
            }
        }
        settingInterface.vRetGetFriendStatus(iFriendsCount, threeNumbers, status, types);
    }

    public static void vRetMessage(int srcID, int iLen, byte[] cString) {
        int id = srcID & Integer.MAX_VALUE;
        if (id == 10000) {
            settingInterface.vRetSysMessage(new String(cString));
        } else {
            settingInterface.vRetMessage("0" + String.valueOf(id), new String(cString));
        }
    }

    public static void vRetRecordFilesList(int id, int count, byte[] bytes) {
        Log.e("vRetRecordFilesList", count + ":" + count);
        String[] names_moveEndNull = new String[count];
        int[] frameRate = new int[count];
        System.arraycopy(new String(bytes).split("\\|"), 0, names_moveEndNull, 0, count);
        settingInterface.vRetGetRecordFiles(names_moveEndNull, frameRate);
    }

    public static void vRetPlayingStatus(int iStatus) {
        p2pInterface.vRetPlayBackStatus(iStatus);
    }

    public static void vRetPlayingPos(int iLength, int iCurrentSec) {
        Log.e("my", iLength + ":" + iCurrentSec);
        p2pInterface.vRetPlayBackPos(iLength, iCurrentSec);
    }

    public static void vRetPlayingSize(int iWidth, int iHeight) {
        p2pInterface.vRetPlaySize(iWidth, iHeight);
    }

    public static void vRetPlayingNumber(int iNumber) {
        p2pInterface.vRetPlayNumber(iNumber);
    }

    public static void vRetEmail(int srcID, int iLen, byte[] cString, int result) {
    }

    public static void vRetEmailWithSMTP(int srcID, byte boption, String emailaddress, int port, String server, String user, byte[] pwd, String subject, String content, byte Entry, byte reserve1, int reserve2, int pwdlen) {
        Log.e("vRetEmailWithSMTP", "emailaddress = " + emailaddress);
        Log.e("vRetEmailWithSMTP", "port = " + port);
        Log.e("vRetEmailWithSMTP", "server = " + server);
        Log.e("vRetEmailWithSMTP", "user = " + user);
        Log.e("vRetEmailWithSMTP", "Entry = " + Entry);
        settingInterface.vRetAlarmEmailResult(boption, emailaddress);
    }

    public static void vRetNPCWifiList(int srcID, int iCurrentId, int iCount, int[] iType, int[] iStrength, byte[] cString, int iResult) {
        String strbuffer = "--";
        for (int j = 0; j < cString.length; j++) {
            if (cString[j] == (byte) 0) {
                Log.e("wifidata", strbuffer);
                strbuffer = "--";
            }
            strbuffer = strbuffer + "  " + cString[j];
        }
        if (iResult == 1) {
            try {
                settingInterface.vRetWifiResult(iResult, iCurrentId, iCount, iType, iStrength, new String(cString, "UTF-8").split("\u0000"));
                return;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return;
            }
        }
        settingInterface.vRetWifiResult(iResult, 0, 0, null, null, null);
    }

    public static void vRetAlarmCodeStatus(int srcID, int iCount, int key, byte[] bData, int iResult) {
        if (srcID != 10086) {
            if (iResult == 1) {
                ArrayList<int[]> data = new ArrayList();
                int[] status_key = new int[]{(key >> 0) & 1, (key >> 1) & 1, (key >> 2) & 1, (key >> 3) & 1, (key >> 4) & 1, (key >> 5) & 1, (key >> 6) & 1, (key >> 7) & 1};
                Log.e("area", status_key[0] + " " + status_key[1] + " " + status_key[2] + " " + status_key[3] + " " + status_key[4] + " " + status_key[5] + " " + status_key[6] + " " + status_key[7] + " ");
                data.add(0, status_key);
                for (int i = 0; i < iCount; i++) {
                    byte b = bData[i];
                    int[] status = new int[]{(b >> 0) & 1, (b >> 1) & 1, (b >> 2) & 1, (b >> 3) & 1, (b >> 4) & 1, (b >> 5) & 1, (b >> 6) & 1, (b >> 7) & 1};
                    Log.e("area", status[0] + " " + status[1] + " " + status[2] + " " + status[3] + " " + status[4] + " " + status[5] + " " + status[6] + " " + status[7] + " ");
                    data.add(i + 1, status);
                }
                settingInterface.vRetDefenceAreaResult(iResult, data, 0, 0);
                return;
            }
            settingInterface.vRetDefenceAreaResult(iResult, null, bData[0], bData[4]);
        }
    }

    public static void vRetBindAlarmId(int srcID, int iMaxCount, int iCount, int[] iData, int iResult) {
        if (iResult != 1) {
            settingInterface.vRetBindAlarmIdResult(iResult, 0, null);
        } else if (iCount == 1 && iData[0] == 0) {
            settingInterface.vRetBindAlarmIdResult(iResult, iMaxCount, new String[0]);
        } else {
            String[] new_data = new String[iData.length];
            for (int i = 0; i < iData.length; i++) {
                new_data[i] = "0" + iData[i];
            }
            settingInterface.vRetBindAlarmIdResult(iResult, iMaxCount, new_data);
        }
    }

    public static void vRetDeviceNotSupport(int iNpcId) {
        Log.e("my", "device not support:" + iNpcId);
        settingInterface.vRetDeviceNotSupport();
    }

    public static void changeScreenSize(int windowWidth, int windowHeight, int isFullScreen, float scaleValue) {
        if (P2PValue.HikamDeviceModelList.contains(callModel)) {
            Log.e("few", "---java" + scaleValue);
        } else {
            ChangeScreenSize(windowWidth, windowHeight, isFullScreen);
        }
    }

    public static void vRetInitPassword(int iNpcId, int iResult) {
        settingInterface.vRetSetInitPasswordResult(iResult);
    }

    public static void vRetAlarm(int iSrcId, int iType, int isSupportExternAlarm, int iGroup, int iItem) {
        boolean bool;
        boolean isSupportDelete;
        if ((isSupportExternAlarm & 1) == 1) {
            bool = true;
        } else {
            bool = false;
        }
        Log.e("dxsAlarmActivity", "iSrcId-->" + iSrcId + "iType-->" + iType + "--isSupportExternAlarm-->" + isSupportExternAlarm);
        if (((isSupportExternAlarm >> 2) & 1) == 1) {
            isSupportDelete = true;
        } else {
            isSupportDelete = false;
        }
        if (iGroup > 8) {
        }
        if (p2pInterface != null) {
            p2pInterface.vAllarming("", String.valueOf(iSrcId), iType, bool, iGroup, iItem, isSupportDelete, "");
        }
    }

    public static void vRetNPCTime(int iTime, int result) {
        if (result == 1) {
            settingInterface.vRetGetDeviceTimeResult(MyUtils.convertDeviceTime(iTime));
        } else {
            settingInterface.vRetSetDeviceTimeResult(result);
        }
    }

    public static void vRetCustomCmd(int srcID, int iLen, byte[] cString) {
        settingInterface.vRetCustomCmd("0" + String.valueOf(srcID & Integer.MAX_VALUE), new String(cString, 0, cString.length - 1));
    }

    public static void vRetCheckDeviceUpdate(int iSrcID, int result, int iCurVersion, int iUpgVersion) {
        settingInterface.vRetCheckDeviceUpdate(result, ((iCurVersion >> 24) & 255) + "." + ((iCurVersion >> 16) & 255) + "." + ((iCurVersion >> 8) & 255) + "." + (iCurVersion & 255), ((iUpgVersion >> 24) & 255) + "." + ((iUpgVersion >> 16) & 255) + "." + ((iUpgVersion >> 8) & 255) + "." + (iUpgVersion & 255));
    }

    public static void vRetDoDeviceUpdate(int iSrcID, int result, int value) {
        settingInterface.vRetDoDeviceUpdate(result, value);
    }

    public static void vRetCancelDeviceUpdate(int iSrcID, int result) {
        settingInterface.vRetCancelDeviceUpdate(result);
    }

    public static void vRetGetDeviceVersion(int iSrcID, int result, int iCurVersion, int iUbootVersion, int iKernelVersion, int iRootfsVersion) {
        settingInterface.vRetGetDeviceVersion(result, ((iCurVersion >> 24) & 255) + "." + ((iCurVersion >> 16) & 255) + "." + ((iCurVersion >> 8) & 255) + "." + (iCurVersion & 255), iUbootVersion, iKernelVersion, iRootfsVersion);
    }

    public static void vGXNotifyFlag(int flag) {
        p2pInterface.vGXNotifyFlag(flag);
    }

    public static void vRetClearAlarmCodeGroup(int iSrcID, int result) {
        settingInterface.vRetClearDefenceAreaState(result);
    }

    public static void vRetExtenedCmd(int iSrcID, byte[] data, int datasize) {
        Log.e("sddata", data.toString());
        for (byte b : data) {
            Log.e("sddata", "data" + b);
        }
        if (data[0] == (byte) 80) {
            if (data[1] == (byte) 82) {
                settingInterface.vRetGetSdCard(0, 0, 0, 0);
                return;
            }
            int DiskCount = data[2] + (data[3] * 256);
            Log.e("2cu", "---" + DiskCount);
            int DiskID = data[4];
            Log.e("diskid", "DiskID" + DiskID);
            long[] longData = new long[]{(long) (data[5] & 255), longData[0] << null, (long) (data[6] & 255), longData[1] << 8, (long) (data[7] & 255), longData[2] << 16, (long) (data[8] & 255), longData[3] << 24};
            longData[4] = (long) (data[9] & 255);
            longData[4] = longData[4] << 32;
            longData[5] = (long) (data[10] & 255);
            longData[5] = longData[5] << 40;
            longData[6] = (long) (data[11] & 255);
            longData[6] = longData[6] << 48;
            longData[7] = (long) (data[12] & 255);
            longData[7] = longData[7] << 56;
            long TotalSpace = ((((((((longData[0] + longData[1]) + longData[2]) + longData[3]) + longData[4]) + longData[5]) + longData[6]) + longData[7]) / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID) / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID;
            longData[0] = (long) (data[13] & 255);
            longData[0] = longData[0] << null;
            longData[1] = (long) (data[14] & 255);
            longData[1] = longData[1] << 8;
            longData[2] = (long) (data[15] & 255);
            longData[2] = longData[2] << 16;
            longData[3] = (long) (data[16] & 255);
            longData[3] = longData[3] << 24;
            longData[4] = (long) (data[17] & 255);
            longData[4] = longData[4] << 32;
            longData[5] = (long) (data[18] & 255);
            longData[5] = longData[5] << 40;
            longData[6] = (long) (data[19] & 255);
            longData[6] = longData[6] << 48;
            longData[7] = (long) (data[20] & 255);
            longData[7] = longData[7] << 56;
            long FreeSpace = ((((((((longData[0] + longData[1]) + longData[2]) + longData[3]) + longData[4]) + longData[5]) + longData[6]) + longData[7]) / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID) / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID;
            Log.e("2cu", "TotalSpace=" + TotalSpace);
            Log.e("2cu", "FreeSpace=" + FreeSpace);
            settingInterface.vRetGetSdCard((int) TotalSpace, (int) FreeSpace, DiskID, 1);
            if (DiskCount > 1) {
                DiskID = data[21];
                Log.e("diskid", "DiskID" + DiskID);
                longData[0] = (long) (data[22] & 255);
                longData[0] = longData[0] << null;
                longData[1] = (long) (data[23] & 255);
                longData[1] = longData[1] << 8;
                longData[2] = (long) (data[24] & 255);
                longData[2] = longData[2] << 16;
                longData[3] = (long) (data[25] & 255);
                longData[3] = longData[3] << 24;
                longData[4] = (long) (data[26] & 255);
                longData[4] = longData[4] << 32;
                longData[5] = (long) (data[27] & 255);
                longData[5] = longData[5] << 40;
                longData[6] = (long) (data[28] & 255);
                longData[6] = longData[6] << 48;
                longData[7] = (long) (data[29] & 255);
                longData[7] = longData[7] << 56;
                TotalSpace = ((((((((longData[0] + longData[1]) + longData[2]) + longData[3]) + longData[4]) + longData[5]) + longData[6]) + longData[7]) / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID) / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID;
                longData[0] = (long) (data[30] & 255);
                longData[0] = longData[0] << null;
                longData[1] = (long) (data[31] & 255);
                longData[1] = longData[1] << 8;
                longData[2] = (long) (data[32] & 255);
                longData[2] = longData[2] << 16;
                longData[3] = (long) (data[33] & 255);
                longData[3] = longData[3] << 24;
                longData[4] = (long) (data[34] & 255);
                longData[4] = longData[4] << 32;
                longData[5] = (long) (data[35] & 255);
                longData[5] = longData[5] << 40;
                longData[6] = (long) (data[36] & 255);
                longData[6] = longData[6] << 48;
                longData[7] = (long) (data[37] & 255);
                longData[7] = longData[7] << 56;
                FreeSpace = ((((((((longData[0] + longData[1]) + longData[2]) + longData[3]) + longData[4]) + longData[5]) + longData[6]) + longData[7]) / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID) / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID;
                Log.e("2cu", "TotalSpace=" + TotalSpace);
                Log.e("2cu", "FreeSpace=" + FreeSpace);
                settingInterface.VRetGetUsb((int) TotalSpace, (int) FreeSpace, DiskID, 1);
            }
        } else if (data[0] == (byte) 81) {
            settingInterface.vRetSdFormat(data[1]);
        } else if (data[0] == (byte) 96) {
            int result;
            if (data[1] < (byte) 0) {
                result = data[1] + 256;
            } else {
                result = data[1];
            }
            settingInterface.vRetSetGPIO(result);
        }
    }

    public static void RetNewSystemMessage(int s, int y) {
    }

    public static void vRetAlarmWithTime(int iSrcId, int iType, int isSupportExternAlarm, int iGroup, int iItem, int capNums, byte[] Time, byte[] alarmCapDir, byte[] vedioPath, byte[] SensorName, int DeviceType) {
        boolean s;
        Log.e("dxsdxsallarm", "id-->" + iSrcId + "--iType-->" + iType + "--capNums-->" + capNums + "--Time-->" + Time + "--vediopath-->" + vedioPath + "--alarmCapDir-->" + alarmCapDir);
        if (isSupportExternAlarm == 0) {
            s = false;
        } else {
            s = true;
        }
        p2pInterface.vAllarmingWitghTime(String.valueOf(iSrcId), iType, s, iGroup, iItem, capNums, new String(Time), new String(alarmCapDir), new String(vedioPath));
    }

    public static void RetGetAllarmImage(int id, byte[] filename, int errorcode) {
        String file = new String(filename);
    }
}
