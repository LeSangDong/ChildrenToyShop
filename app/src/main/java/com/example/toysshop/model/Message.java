package com.example.toysshop.model;

public class Message {
    private String userId;
    private String message;
    private String timestamp;
    private boolean isImage;
    private boolean isFavorite;
    private boolean isSent;
    private boolean isSeen;

    public Message() {
    }

    public Message(String userId, String message, String timestamp, boolean isImage) {
        this.userId = userId;
        this.message = message;
        this.timestamp = timestamp;
        this.isImage = isImage;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isImage() {
        return isImage;
    }

    public void setImage(boolean image) {
        isImage = image;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public boolean isSent() {
        return isSent;
    }

    public void setSent(boolean sent) {
        isSent = sent;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }
}
