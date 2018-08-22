package com.elegion.myfirstapplication.model;

import com.google.gson.annotations.SerializedName;

public class IdResult {
    @SerializedName("id")
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
