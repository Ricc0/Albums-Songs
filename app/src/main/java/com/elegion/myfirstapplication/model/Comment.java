package com.elegion.myfirstapplication.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.sql.Timestamp;

@Entity(foreignKeys = @ForeignKey(entity = Album.class, parentColumns = "id", childColumns = "album_id"))
public class Comment extends BaseComment{

    @SerializedName("id")
    @PrimaryKey
    @ColumnInfo(name = "id")
    private int id;

    @SerializedName("author")
    @ColumnInfo(name = "author")
    private String author;

    @SerializedName("timestamp")
    @ColumnInfo(name = "timestamp")
    private String timestamp;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
