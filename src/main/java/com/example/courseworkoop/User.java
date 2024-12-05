package com.example.courseworkoop;

public class User {
    private String username;
    private String password;
    private String FullName;

    public User(String FullName, String username, String password) {
        this.username = username;
        this.password = password;
        this.FullName = FullName;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }
}
