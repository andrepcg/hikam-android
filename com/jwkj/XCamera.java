package com.jwkj;

import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Build.VERSION;
import android.util.Log;
import com.jwkj.global.Constants;

public class XCamera {
    public static final int SUPPORT_SDK_VERSION = 9;
    public static int current_camera_id;

    public static int getDefaultCameraIndex() {
        int index = 0;
        if (VERSION.SDK_INT >= 9) {
            Log.e(Constants.CACHE_FOLDER_NAME, "The current system version2: " + VERSION.SDK_INT);
            CameraInfo cameraInfo = new CameraInfo();
            int cameraCount = Camera.getNumberOfCameras();
            for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
                Camera.getCameraInfo(camIdx, cameraInfo);
                if (cameraInfo.facing == 1) {
                    index = camIdx;
                }
            }
        }
        return index;
    }

    public static Camera switchCamera() {
        if (VERSION.SDK_INT < 9) {
            return null;
        }
        int cameraCount = Camera.getNumberOfCameras();
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            if (current_camera_id != camIdx) {
                current_camera_id = camIdx;
                try {
                    return Camera.open(current_camera_id);
                } catch (Exception e) {
                    return null;
                }
            }
        }
        return null;
    }

    public static Camera open() {
        Camera camera = null;
        if (VERSION.SDK_INT <= 9) {
            try {
                camera = Camera.open();
            } catch (Exception e) {
            }
        } else {
            try {
                current_camera_id = getDefaultCameraIndex();
                camera = Camera.open(current_camera_id);
            } catch (Exception e2) {
            }
        }
        return camera;
    }
}
