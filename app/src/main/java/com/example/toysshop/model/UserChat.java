package com.example.toysshop.model;

public class UserChat implements Comparable<UserChat>{
    private String userId;
    private String name;
    private String email;
    private String avatarUrl;
    private String chatWithUserId;
    private String lastMessage;
    private String lastMessageTime;


    public UserChat() {
    }

    public UserChat(String userId,String name, String email, String avatarUrl) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.avatarUrl = avatarUrl;
    }

    public UserChat(String chatWithUserId, String name, String email, String avatarUrl, String lastMessage, String lastMessageTime) {
        this.chatWithUserId = chatWithUserId;
        this.name = name;
        this.email = email;
        this.avatarUrl = avatarUrl;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getChatWithUserId() {
        return chatWithUserId;
    }

    public void setChatWithUserId(String chatWithUserId) {
        this.chatWithUserId = chatWithUserId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(String lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    @Override
    public int compareTo(UserChat other) {
        return other.lastMessageTime.compareTo(this.lastMessageTime); // Sort descending
    }
}
