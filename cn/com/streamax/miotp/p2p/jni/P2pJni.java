package cn.com.streamax.miotp.p2p.jni;

import android.util.Log;
import cn.com.streamax.miotp.jni.AudioCallback;
import cn.com.streamax.miotp.jni.VideoCallback;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

public class P2pJni {
    private static final String TAG = "P2pJni";
    protected static EGLConfig mEGLConfig;
    protected static EGLContext mEGLContext;
    protected static EGLDisplay mEGLDisplay;
    protected static EGLSurface mEGLSurface;
    protected static int mGLMajor;
    protected static int mGLMinor;
    private static Object showView = null;

    public static native int P2PClientSdkActionControl(String str, int i);

    public static native int P2PClientSdkActivateDev(P2pActivateDev[] p2pActivateDevArr);

    public static native int P2PClientSdkActiveHumanDetect(String str, String str2, int i);

    public static native int P2PClientSdkAddSubUser(P2pSubuserInfo p2pSubuserInfo);

    public static native int P2PClientSdkAlarmLogSyncStart(String str, String str2, long j, long j2, int i);

    public static native int P2PClientSdkAlarmLogSyncStop(String str, String str2, int i);

    public static native int P2PClientSdkCancelDeviceUpgrade(String str);

    public static native int P2PClientSdkCaptureScreen(String str);

    public static native int P2PClientSdkCheckDevicePassword(String str, String str2, int i);

    public static native int P2PClientSdkClearRecordBackup();

    public static native int P2PClientSdkCloseStream(String str, P2pStreamIdentity p2pStreamIdentity);

    public static native int P2PClientSdkDeviceUpgrade(String str, String str2, String str3);

    public static native int P2PClientSdkFormatSDCard(String str);

    public static native int P2PClientSdkGetAlarmCodeStatus(String str, String str2, int i);

    public static native int P2PClientSdkGetAlarmEmail(String str, String str2, int i);

    public static native int P2PClientSdkGetAlarmPushStatus(String str, String str2, String str3, int i);

    public static native int P2PClientSdkGetAlarmTiming(String str, String str2, int i);

    public static native int P2PClientSdkGetAlarmTotalSwitch(String str, String str2, int i);

    public static native int P2PClientSdkGetAllPushAccount(String str, String str2, String str3, String str4, int i);

    public static native int P2PClientSdkGetBatchAlarmPushConfig(String str, String[] strArr);

    public static native int P2PClientSdkGetBindAlarmId(String str, String str2, String str3, int i);

    public static native int P2PClientSdkGetDevCover(String str, String str2, String str3, String str4, int i);

    public static native int P2PClientSdkGetDevList(int i, P2pDevInfos p2pDevInfos);

    public static native int P2PClientSdkGetDevNetworkType(String str, String str2, int i);

    public static native int P2PClientSdkGetDevSetting(String str, String str2, int i);

    public static native int P2PClientSdkGetDevWifiList(String str, String str2, int i);

    public static native int P2PClientSdkGetDeviceTime(String str, String str2, int i);

    public static native int P2PClientSdkGetDeviceVersion(String str, String str2, int i);

    public static native int P2PClientSdkGetHumanDetect(String str, String str2, int i);

    public static native int P2PClientSdkGetImageReverse(String str, String str2, int i);

    public static native int P2PClientSdkGetLDC(String str, String str2, int i);

    public static native int P2PClientSdkGetLampSwitch(String str, String str2, int i);

    public static native int P2PClientSdkGetObjList(int i, P2pObjInfos p2pObjInfos);

    public static native int P2PClientSdkGetOrgList(P2pOrgInfos p2pOrgInfos);

    public static native int P2PClientSdkGetPir(String str, String str2, int i);

    public static native int P2PClientSdkGetRecordConfig(String str, String str2, int i);

    public static native int P2PClientSdkGetRtspSwitch(String str, String str2, int i);

    public static native int P2PClientSdkGetSdcardInfo(String str, String str2, int i);

    public static native int P2PClientSdkGetShortAVPic(String str, String str2, String[] strArr, int i);

    public static native int P2PClientSdkGetSiren(String str, String str2, int i);

    public static native int P2PClientSdkGetStreamDescr(String str, P2pStreamResources p2pStreamResources);

    public static native int P2PClientSdkGetStreamStatus(P2pStreamIdentity p2pStreamIdentity);

    public static native int P2PClientSdkGetUpgradeState(String str);

    public static native int P2PClientSdkGetUserNum(String str, String str2, int i);

    public static native int P2PClientSdkGetValidDataHumanDetect(String str, String str2, int i);

    public static native int P2PClientSdkGetVideoFormat(String str, String str2, int i);

    public static native int P2PClientSdkInitDecoder();

    public static native int P2PClientSdkMoveView(int i, int i2, int i3, int i4);

    public static native int P2PClientSdkOpenStream(VideoCallback videoCallback, AudioCallback audioCallback, String str, P2pStreamIdentity p2pStreamIdentity, P2pStreamCBDataType p2pStreamCBDataType, String str2, int i);

    public static native int P2PClientSdkPTController(String str, String str2, int i, int i2);

    public static native int P2PClientSdkPlayBackNext(String str, int i, int i2, String str2, String str3, String str4);

    public static native int P2PClientSdkPlaybackControl(String str, int i, int i2, String str2, String str3);

    public static native int P2PClientSdkPlaybackDragPos(String str, int i, String str2, String str3);

    public static native int P2PClientSdkPtz(String str, int i);

    public static native int P2PClientSdkQueryLastVersion(String str, String str2, String str3);

    public static native String P2PClientSdkQuerySvrAddr(String str, String str2, int i);

    public static native void P2PClientSdkResizeView(int i, int i2, int i3, int i4);

    public static native int P2PClientSdkSearchRecordFile(String str, String str2, String str3, String str4, int i, int i2);

    public static native int P2PClientSdkSendAudioData(String str, byte[] bArr, int i);

    public static native int P2PClientSdkSetAlarmCodeStatus(String str, String str2, int i, int i2, int i3, int i4);

    public static native int P2PClientSdkSetAlarmEmail(String str, String str2, String str3, String str4, String str5, int i);

    public static native int P2PClientSdkSetAlarmPushStatus(String str, String str2, String str3, int i, int i2);

    public static native int P2PClientSdkSetAlarmTiming(String str, String str2, int[] iArr, int[] iArr2, int[] iArr3, int[] iArr4, int i);

    public static native int P2PClientSdkSetAlarmTotalSwitch(String str, String str2, int i, int i2);

    public static native int P2PClientSdkSetBindAlarmId(String str, String str2, String str3, String str4, int i);

    public static native int P2PClientSdkSetDevNetworkType(String str, String str2, int i, int i2);

    public static native int P2PClientSdkSetDevWifi(String str, String str2, String str3, int i, String str4, int i2);

    public static native int P2PClientSdkSetDevicePassword(String str, String str2, String str3, int i);

    public static native int P2PClientSdkSetDeviceTime(String str, String str2, int i, int i2, int i3, int i4, int i5, int i6, int i7);

    public static native int P2PClientSdkSetDeviceTimeZone(String str, String str2, int i, int i2);

    public static native int P2PClientSdkSetHumanDetect(String str, String str2, int i, int i2);

    public static native int P2PClientSdkSetImageReverse(String str, String str2, int i, int i2);

    public static native int P2PClientSdkSetLDC(String str, String str2, int i, int i2);

    public static native int P2PClientSdkSetLampSwitch(String str, String str2, int i, int i2);

    public static native int P2PClientSdkSetMotionDetect(String str, String str2, int i, int i2, int i3);

    public static native int P2PClientSdkSetMsgCB(ClientMsgCallback clientMsgCallback, int i);

    public static native int P2PClientSdkSetMuteFalse(String str);

    public static native int P2PClientSdkSetMuteTrue(String str);

    public static native int P2PClientSdkSetPir(String str, String str2, int i, int i2);

    public static native int P2PClientSdkSetRecordConfig(String str, String str2, int i, int i2, int i3, int i4, int i5, String str3, String str4, int i6);

    public static native int P2PClientSdkSetRtspSwitch(String str, String str2, int i, int i2);

    public static native int P2PClientSdkSetSiren(String str, String str2, int i, int i2, int i3);

    public static native int P2PClientSdkSetStatusCB(ClientStatusCallback clientStatusCallback, int i);

    public static native int P2PClientSdkSetUploadToSvr(String str, String str2, int i);

    public static native int P2PClientSdkSetVideoFormat(String str, String str2, int i, int i2);

    public static native int P2PClientSdkStartDownload(String str, String str2, String str3, int i, String str4);

    public static native int P2PClientSdkStartPlayback(VideoCallback videoCallback, AudioCallback audioCallback, String str, String str2, int i, int i2, String str3);

    public static native int P2PClientSdkStartShortAV(String str, String str2, String str3);

    public static native int P2PClientSdkStopDownload(String str);

    public static native int P2PClientSdkStopPlayback(String str);

    public static native int P2PClientSdkStopShortAV(String str);

    public static native int P2PClientSdkSwitchStream(String str, P2pStreamIdentity p2pStreamIdentity, P2pStreamIdentity p2pStreamIdentity2, String str2, int i);

    public static native int P2PClientSdkVideoRecordFinish();

    public static native int P2PClientSdkVideoRecordMuxer(String str);

    public static native int P2PClientSdkVideoRecordStart(String str);

    public static native int P2PClientSdkWifiSwitchAP(String str, String str2, String str3);

    public static native int P2PClientSdkZoomView(int i, int i2, float f);

    public static native int P2PMediaGetMute();

    public static native int P2PMediaSetMute(int i);

    public static native int P2pClientDisCallback();

    public static native int P2pClientSdkClosePeer(String str);

    public static native int P2pClientSdkConnectPeer(String str);

    public static native int P2pClientSdkCreateNewUser(String str, String str2, int i, GWellUserInfo gWellUserInfo);

    public static native int P2pClientSdkGetDeviceFullID(String str, P2pDevInfo p2pDevInfo);

    public static native int P2pClientSdkGetRoleId(P2pUserInfo p2pUserInfo);

    public static native int P2pClientSdkGetSessionStatus(String str);

    public static native int P2pClientSdkInit(P2pClientID p2pClientID, String str, String str2);

    public static native int P2pClientSdkRegister(int i, GWellUserInfo gWellUserInfo);

    public static native int P2pClientSdkUnInit();

    public static native int P2pClientSdkUnRegister();

    public static native void nativeInit();

    public static native void nativeLowMemory();

    public static native void nativePause();

    public static native void nativeQuit();

    public static native void nativeResume();

    public static native void onNativeAccel(float f, float f2, float f3);

    public static native void onNativeKeyDown(int i);

    public static native void onNativeKeyUp(int i);

    public static native void onNativeKeyboardFocusLost();

    public static native void onNativeResize(int i, int i2, int i3);

    public static native void onNativeTouch(int i, int i2, int i3, float f, float f2, float f3);

    static {
        Log.i(TAG, "load so!");
        System.loadLibrary("avutil-54");
        System.loadLibrary("swresample-1");
        System.loadLibrary("avcodec-56");
        System.loadLibrary("avformat-56");
        System.loadLibrary("swscale-3");
        System.loadLibrary("postproc-53");
        System.loadLibrary("avfilter-5");
        System.loadLibrary("avdevice-56");
        System.loadLibrary("SDL2");
        System.loadLibrary("MuCoDec");
        System.loadLibrary("p2pndk");
        Log.i(TAG, "load so successlly");
    }

    public static void setEglView(Object view) {
        showView = view;
    }

    public static boolean createGLContext(int majorVersion, int minorVersion, int[] attribs) {
        Log.e("SDL", "createGLContext");
        return initEGL(majorVersion, minorVersion, attribs);
    }

    public static void deleteGLContext() {
        if (mEGLDisplay != null && mEGLContext != null) {
            EGL10 egl = (EGL10) EGLContext.getEGL();
            egl.eglMakeCurrent(mEGLDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
            egl.eglDestroyContext(mEGLDisplay, mEGLContext);
            mEGLContext = null;
            if (mEGLSurface != null) {
                egl.eglDestroySurface(mEGLDisplay, mEGLSurface);
                mEGLSurface = null;
            }
        }
    }

    public static void flipBuffers() {
        flipEGL();
    }

    public static boolean initEGL(int majorVersion, int minorVersion, int[] attribs) {
        Log.e("SDL", "initEGL");
        try {
            EGL10 egl = (EGL10) EGLContext.getEGL();
            if (mEGLDisplay == null) {
                mEGLDisplay = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
                egl.eglInitialize(mEGLDisplay, new int[2]);
            }
            Log.e("SDL", mEGLDisplay + ":" + mEGLContext + ":" + EGL10.EGL_NO_CONTEXT);
            if (mEGLDisplay != null && mEGLContext == null) {
                Log.v("SDL", "Starting up OpenGL ES " + majorVersion + "." + minorVersion);
                EGLConfig[] configs = new EGLConfig[128];
                int[] num_config = new int[1];
                if (!egl.eglChooseConfig(mEGLDisplay, attribs, configs, 1, num_config) || num_config[0] == 0) {
                    Log.e("SDL", "No EGL config available");
                    return false;
                }
                EGLConfig config = null;
                int bestdiff = -1;
                int[] value = new int[1];
                Log.v("SDL", "Got " + num_config[0] + " valid modes from egl");
                for (int i = 0; i < num_config[0]; i++) {
                    int bitdiff = 0;
                    int j = 0;
                    while (attribs[j] != 12344) {
                        if (attribs[j + 1] != -1 && (attribs[j] == 12324 || attribs[j] == 12323 || attribs[j] == 12322 || attribs[j] == 12321 || attribs[j] == 12325 || attribs[j] == 12326)) {
                            egl.eglGetConfigAttrib(mEGLDisplay, configs[i], attribs[j], value);
                            bitdiff += value[0] - attribs[j + 1];
                        }
                        j += 2;
                    }
                    if (bitdiff < bestdiff || bestdiff == -1) {
                        config = configs[i];
                        bestdiff = bitdiff;
                    }
                    if (bitdiff == 0) {
                        break;
                    }
                }
                Log.d("SDL", "Selected mode with a total bit difference of " + bestdiff);
                mEGLConfig = config;
                mGLMajor = majorVersion;
                mGLMinor = minorVersion;
            }
            return createEGLSurface();
        } catch (Exception e) {
            Log.v("SDL", e + "");
            for (StackTraceElement s : e.getStackTrace()) {
                Log.v("SDL", s.toString());
            }
            return false;
        }
    }

    public static boolean createEGLContext() {
        mEGLContext = ((EGL10) EGLContext.getEGL()).eglCreateContext(mEGLDisplay, mEGLConfig, EGL10.EGL_NO_CONTEXT, new int[]{12440, mGLMajor, 12344});
        if (mEGLContext != null) {
            return true;
        }
        Log.e("SDL", "Couldn't create context");
        return false;
    }

    public static boolean createEGLSurface() {
        if (mEGLDisplay == null || mEGLConfig == null) {
            Log.e("SDL", "Surface creation failed, display = " + mEGLDisplay + ", config = " + mEGLConfig);
            return false;
        }
        EGL10 egl = (EGL10) EGLContext.getEGL();
        if (mEGLContext == null) {
            createEGLContext();
        }
        if (mEGLSurface == null) {
            mEGLSurface = egl.eglCreateWindowSurface(mEGLDisplay, mEGLConfig, showView, null);
            if (mEGLSurface == null) {
                Log.e("SDL", "Couldn't create surface");
                return false;
            }
        }
        Log.v("SDL", "EGL Surface remains valid");
        if (egl.eglGetCurrentContext() == mEGLContext) {
            Log.v("SDL", "EGL Context remains current");
        } else if (egl.eglMakeCurrent(mEGLDisplay, mEGLSurface, mEGLSurface, mEGLContext)) {
            Log.v("SDL", "EGL Context made current");
        } else {
            Log.e("SDL", "Old EGL Context doesnt work, trying with a new one");
            createEGLContext();
            if (!egl.eglMakeCurrent(mEGLDisplay, mEGLSurface, mEGLSurface, mEGLContext)) {
                Log.e("SDL", "Failed making EGL Context current");
                return false;
            }
        }
        return true;
    }

    public static void flipEGL() {
        try {
            EGL10 egl = (EGL10) EGLContext.getEGL();
            egl.eglWaitNative(12379, null);
            egl.eglWaitGL();
            egl.eglSwapBuffers(mEGLDisplay, mEGLSurface);
        } catch (Exception e) {
            Log.v("SDL", "flipEGL(): " + e);
            for (StackTraceElement s : e.getStackTrace()) {
                Log.v("SDL", s.toString());
            }
        }
    }

    public static int audioInit(int sampleRate, boolean is16Bit, boolean isStereo, int desiredFrames) {
        return 0;
    }

    public static void audioWriteShortBuffer(short[] buffer) {
    }

    public static void audioWriteByteBuffer(byte[] buffer) {
    }

    public static void audioQuit() {
    }
}
