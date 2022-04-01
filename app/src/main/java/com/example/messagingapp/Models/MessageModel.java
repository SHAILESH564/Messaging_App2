package com.example.messagingapp.Models;


import com.example.messagingapp.EncryDecry;

public class MessageModel {


    private String uid, message,messageId, imageurl;
    Long timestamp;
    long feeling = -1;



    public MessageModel(String uid, String message, Long timestamp) {
        this.uid = uid;
        this.message = message;
        this.timestamp = timestamp;
    }
    public MessageModel(String uid, String message) {
        this.uid = uid;
        this.message = message;
    }

    public MessageModel(){}

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public long getFeeling() {
        return feeling;
    }

    public void setFeeling(long feeling) {
        this.feeling = feeling;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
            this.message = message;

    }
    public Long getTimestamp() {
        return timestamp;
    }

    public Long setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
        return null;
    }
}
