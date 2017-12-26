package com.jwkj.entity;

import java.io.Serializable;

public class MediaPacket implements Serializable {
    private static final long serialVersionUID = 1;
    private String audioPath;
    private String id;
    private long lastModified;
    private String mediaPath;
    private TYPE mediaType;
    private String name;
    private String picPath;
    private String videoPath;

    public enum TYPE {
        PICTURE,
        AUDIO,
        VIDEO,
        MEDIA
    }

    public long getLastModified() {
        return this.lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public TYPE getMediaType() {
        return this.mediaType;
    }

    public void setMediaType(TYPE mediaType) {
        this.mediaType = mediaType;
    }

    public String getPicPath() {
        return this.picPath;
    }

    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }

    public String getAudioPath() {
        return this.audioPath;
    }

    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
    }

    public String getVideoPath() {
        return this.videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getMediaPath() {
        return this.mediaPath;
    }

    public void setMediaPath(String mediaPath) {
        this.mediaPath = mediaPath;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
