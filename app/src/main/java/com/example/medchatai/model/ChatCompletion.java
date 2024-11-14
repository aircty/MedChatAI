package com.example.medchatai.model;

import android.graphics.Bitmap;

public class ChatCompletion {
    private String text = null;
    private Bitmap image = null;
    private Bitmap audio =null;

    public Bitmap getAudio() {
        return audio;
    }

    public void setAudio(Bitmap audio) {
        this.audio = audio;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
