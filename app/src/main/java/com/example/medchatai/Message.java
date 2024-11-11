package com.example.medchatai;

import android.graphics.Bitmap;

import java.util.List;

public class Message {
    public static final int TYPE_TEXT = 0;
    public static final int TYPE_IMAGE = 1;
    public static final int TYPE_COMPOSITE = 2;
    public static final int TYPE_USER = 0;
    public static final int TYPE_BOT = 1;

    private String content;
    private Bitmap image;
    private List<Bitmap> images;
    private int type;
    private int senderType;

    public Message(String content, int type, int senderType) {
        this.content = content;
        this.type = type;
        this.senderType = senderType;
    }

    public Message(Bitmap image, int type, int senderType) {
        this.image = image;
        this.type = type;
        this.senderType = senderType;
    }

    public Message(String content, List<Bitmap> images, int type, int senderType) {
        this.content = content;
        this.images = images;
        this.type = type;
        this.senderType = senderType;
    }

    public String getContent() {
        return content;
    }

    public Bitmap getImage() {
        return image;
    }

    public List<Bitmap> getImages() {
        return images;
    }

    public int getType() {
        return type;
    }

    public int getSenderType() {
        return senderType;
    }
}