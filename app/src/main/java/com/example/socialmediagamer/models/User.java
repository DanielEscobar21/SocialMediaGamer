package com.example.socialmediagamer.models;

public class User {

    private String id;
    private String email;
    private String username;
    private String phone;
    private Long timestamp;
    private boolean online;
    private Long lastConnection;


    public User(){

    }


    public User(String id, String email, String username, String phone, Long timestamp, boolean online, Long lastConnection) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.phone = phone;
        this.timestamp = timestamp;
        this.online = online;
        this.lastConnection = lastConnection;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public Long getLastConnection() {
        return lastConnection;
    }

    public void setLastConnection(Long lastConnection) {
        this.lastConnection = lastConnection;
    }
}
