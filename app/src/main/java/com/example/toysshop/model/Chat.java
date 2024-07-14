package com.example.toysshop.model;

public class Chat {
    private String chatWithUserId;
    private String lastMessage;
    private String lastMessageTime;


    public Chat() {
    }

    public Chat(String chatWithUserId, String lastMessage, String lastMessageTime) {
        this.chatWithUserId = chatWithUserId;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
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
}
