package cn.com.streamax.miotp.p2p.jni;

public class P2pDevAbility {
    public int alarmin_num;
    public int alarmout_num;
    public int audio_channel_num;
    public int hdd_num;
    public int playback_num;
    public int substream_num;
    public int video_channel_num;
    public int web_port;
    public int webservice_port;

    public String toString() {
        return "P2pDevAbility [video_channel_num=" + this.video_channel_num + ", audio_channel_num=" + this.audio_channel_num + ", alarmin_num=" + this.alarmin_num + ", alarmout_num=" + this.alarmout_num + ", substream_num=" + this.substream_num + ", playback_num=" + this.playback_num + ", web_port=" + this.web_port + ", webservice_port=" + this.webservice_port + ", hdd_num=" + this.hdd_num + "]";
    }
}
