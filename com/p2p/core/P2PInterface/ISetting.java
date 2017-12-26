package com.p2p.core.P2PInterface;

import java.util.ArrayList;

public interface ISetting {
    void ACK_VRetSetVisitorDevicePassword(int i, int i2);

    void ACK_vRetCancelDeviceUpdate(int i, int i2);

    void ACK_vRetCheckDevicePassword(int i, int i2);

    void ACK_vRetCheckDevicePassword2(int i, int i2, String str);

    void ACK_vRetCheckDeviceUpdate(int i, int i2);

    void ACK_vRetClearDefenceAreaState(int i, int i2);

    void ACK_vRetCustomCmd(int i, int i2);

    void ACK_vRetDoDeviceUpdate(int i, int i2);

    void ACK_vRetGetAlarmBindId(int i, int i2);

    void ACK_vRetGetAlarmEmail(int i, int i2);

    void ACK_vRetGetDefenceArea(int i, int i2);

    void ACK_vRetGetDefenceStates(String str, int i, int i2);

    void ACK_vRetGetDeviceTime(int i, int i2);

    void ACK_vRetGetDeviceVersion(int i, int i2);

    void ACK_vRetGetNpcSettings(String str, int i, int i2);

    void ACK_vRetGetRecordFileList(int i, int i2);

    void ACK_vRetGetSDCard(int i, int i2);

    void ACK_vRetGetWifiList(int i, int i2);

    void ACK_vRetMessage(int i, int i2);

    void ACK_vRetSdFormat(int i, int i2);

    void ACK_vRetSetAlarmBindId(int i, int i2);

    void ACK_vRetSetAlarmEmail(int i, int i2);

    void ACK_vRetSetAutomaticUpgrade(int i, int i2);

    void ACK_vRetSetDefenceArea(int i, int i2);

    void ACK_vRetSetDevicePassword(int i, int i2);

    void ACK_vRetSetDeviceTime(int i, int i2);

    void ACK_vRetSetGPIO(int i, int i2);

    void ACK_vRetSetGPIO1_0(int i, int i2);

    void ACK_vRetSetImageReverse(int i, int i2);

    void ACK_vRetSetInfraredSwitch(int i, int i2);

    void ACK_vRetSetInitPassword(int i, int i2);

    void ACK_vRetSetNpcSettingsBuzzer(int i, int i2);

    void ACK_vRetSetNpcSettingsMotion(int i, int i2);

    void ACK_vRetSetNpcSettingsNetType(int i, int i2);

    void ACK_vRetSetNpcSettingsRecordPlanTime(int i, int i2);

    void ACK_vRetSetNpcSettingsRecordResolution(int i, int i2);

    void ACK_vRetSetNpcSettingsRecordTime(int i, int i2);

    void ACK_vRetSetNpcSettingsRecordType(int i, int i2);

    void ACK_vRetSetNpcSettingsVideoFormat(int i, int i2);

    void ACK_vRetSetNpcSettingsVideoVolume(int i, int i2);

    void ACK_vRetSetRemoteDefence(String str, int i, int i2);

    void ACK_vRetSetRemoteRecord(int i, int i2);

    void ACK_vRetSetTimeZone(int i, int i2);

    void ACK_vRetSetWifi(int i, int i2);

    void ACK_vRetSetWiredAlarmInput(int i, int i2);

    void ACK_vRetSetWiredAlarmOut(int i, int i2);

    void VRetGetUsb(int i, int i2, int i3, int i4);

    void vRetAlarmCodeStatus(int i, int i2, int i3, byte[] bArr, int i4);

    void vRetAlarmEmailResult(int i, String str);

    void vRetAlarmEmailResultWithSMTP(int i, String str, int i2, String[] strArr);

    void vRetAlarmLogSyncData(int i, int[] iArr, int[] iArr2, int[] iArr3, String[] strArr);

    void vRetAlarmLogSyncStatus(int i);

    void vRetBindAlarmIdResult(int i, int i2, String[] strArr);

    void vRetCancelDeviceUpdate(int i);

    void vRetCheckDeviceUpdate(int i, String str, String str2);

    void vRetClearDefenceAreaState(int i);

    void vRetCustomCmd(String str, String str2);

    void vRetDefenceAreaResult(int i, ArrayList<int[]> arrayList, int i2, int i3);

    void vRetDeviceNotSupport();

    void vRetDoDeviceUpdate(int i, int i2);

    void vRetDownloadShortAV(int i);

    void vRetDownloadShortAVPic(int i, int i2, String str);

    void vRetGetAlarmPushStatus(int i, String str, int i2);

    void vRetGetAlarmTiming(int i, int[] iArr, int[] iArr2, int[] iArr3, int[] iArr4);

    void vRetGetAllPushAccount(int i, int i2, String[] strArr);

    void vRetGetAudioDeviceType(int i);

    void vRetGetAutomaticUpgrade(int i);

    void vRetGetBuzzerResult(int i);

    void vRetGetDeviceTimeResult(String str);

    void vRetGetDeviceVersion(int i, String str, int i2, int i3, int i4);

    void vRetGetDeviceVersion2(int i, String str, int i2, int i3, int i4, String str2);

    void vRetGetFriendStatus(int i, String[] strArr, int[] iArr, int[] iArr2);

    void vRetGetHumanDetect(int i, int i2);

    void vRetGetHumanDetectValidData(int i, int i2, String str);

    void vRetGetImageReverseResult(int i);

    void vRetGetImgLdc(int i, int i2);

    void vRetGetInfraredSwitch(int i);

    void vRetGetLampSwitch(int i, int i2);

    void vRetGetMotionResult(int i);

    void vRetGetNetTypeResult(int i);

    void vRetGetPirLed(int i);

    void vRetGetRecordFiles(String[] strArr, int[] iArr);

    void vRetGetRecordPlanTimeResult(String str);

    void vRetGetRecordResolutionResult(int i);

    void vRetGetRecordTimeResult(int i);

    void vRetGetRecordTypeResult(int i);

    void vRetGetRemoteDefenceResult(String str, int i);

    void vRetGetRemoteRecordResult(int i);

    void vRetGetRtsp(int i, int i2);

    void vRetGetSdCard(int i, int i2, int i3, int i4);

    void vRetGetTimeZone(int i);

    void vRetGetVideoFormatResult(int i);

    void vRetGetVideoVolumeResult(int i);

    void vRetGetWiredAlarmInput(int i);

    void vRetGetWiredAlarmOut(int i);

    void vRetLowWarning(int i);

    void vRetMessage(String str, String str2);

    void vRetP2pSdkSessionTimeout();

    void vRetPlayShortAV(int i, String str);

    void vRetSdFormat(int i);

    void vRetSetAlarmPushStatus(int i);

    void vRetSetAlarmTiming(int i);

    void vRetSetAutomaticUpgrade(int i);

    void vRetSetBuzzerResult(int i);

    void vRetSetDevicePasswordResult(int i);

    void vRetSetDeviceTimeResult(int i);

    void vRetSetGPIO(int i);

    void vRetSetHumanDetect(int i);

    void vRetSetImageReverse(int i);

    void vRetSetImgLdc(int i);

    void vRetSetInfraredSwitch(int i);

    void vRetSetInitPasswordResult(int i);

    void vRetSetLampSwitch(int i);

    void vRetSetMotionResult(int i);

    void vRetSetNetTypeResult(int i);

    void vRetSetPirLed(int i);

    void vRetSetRecordPlanTimeResult(int i);

    void vRetSetRecordResolutionResult(int i);

    void vRetSetRecordTimeResult(int i);

    void vRetSetRecordTypeResult(int i);

    void vRetSetRemoteDefenceResult(int i);

    void vRetSetRemoteRecordResult(int i);

    void vRetSetRtsp(int i);

    void vRetSetTimeZone(int i);

    void vRetSetUploadToSvr(int i);

    void vRetSetVideoFormatResult(int i);

    void vRetSetVisitorDevicePassword(int i);

    void vRetSetVolumeResult(int i);

    void vRetSetWiredAlarmInput(int i);

    void vRetSetWiredAlarmOut(int i);

    void vRetSysMessage(String str);

    void vRetWifiResult(int i, int i2, int i3, int[] iArr, int[] iArr2, String[] strArr);
}
