package com.p2p.core.P2PInterface;

public interface IP2P {
    void vAccept(int i, int i2);

    void vAllarming(String str, String str2, int i, boolean z, int i2, int i3, boolean z2, String str3);

    void vAllarmingWitghTime(String str, int i, boolean z, int i2, int i3, int i4, String str2, String str3, String str4);

    void vCalling(boolean z, String str, int i);

    void vChangeVideoMask(int i);

    void vConnectReady();

    void vGXNotifyFlag(int i);

    void vRecvAudioVideoData(byte[] bArr, int i, int i2, long j, byte[] bArr2, int i3, long j2);

    void vReject(int i);

    void vRetPlayBackPos(int i, int i2);

    void vRetPlayBackStatus(int i);

    void vRetPlayNumber(int i);

    void vRetPlaySize(int i, int i2);

    void vRetRTSPNotify(String str);
}
