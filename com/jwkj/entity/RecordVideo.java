package com.jwkj.entity;

import java.io.Serializable;

public class RecordVideo implements Serializable {
    private static final long serialVersionUID = 1;
    private String audiopath;
    private String name;
    private String path;
    private String videopath;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getVideopath() {
        return this.videopath;
    }

    public void setVideopath(String videopath) {
        this.videopath = videopath;
    }

    public String getAudiopath() {
        return this.audiopath;
    }

    public void setAudiopath(String audiopath) {
        this.audiopath = audiopath;
    }

    public String toString() {
        return "RecordVideo [name=" + this.name + ", path=" + this.path + ", videopath=" + this.videopath + ", audiopath=" + this.audiopath + "]";
    }
}
