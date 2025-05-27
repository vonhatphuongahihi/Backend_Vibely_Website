package com.example.vibely_backend.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Document(collection = "conversations")
public class Conversation {
    @Id
    private String id;
    private List<String> members = new ArrayList<>();
    private Map<String, String> nicknames = new HashMap<>();
    private String color;
    private String lastMessage;
    private java.util.Date lastMessageTime;

    // Constructors
    public Conversation() {}

    public Conversation(List<String> members) {
        this.members = members;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public Map<String, String> getNicknames() {
        return nicknames;
    }

    public void setNicknames(Map<String, String> nicknames) {
        this.nicknames = nicknames;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public java.util.Date getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(java.util.Date lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }
}
