package com.elegion.myfirstapplication.model;

import android.arch.persistence.room.ColumnInfo;

import com.google.gson.annotations.SerializedName;

public class BaseComment {
    @SerializedName("album_id")
    @ColumnInfo(name = "album_id")
    private int albumId;

    @SerializedName("text")
    @ColumnInfo(name = "text")
    private String text;

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
