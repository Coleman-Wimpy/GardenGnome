package com.example.gardengnome;

public class User {
    public String fullname, email;
    public int userRoleID;

    public User() {

    }

    public User(String fullname, String email, int userRoleID) {
        this.fullname = fullname;
        this.email  = email;
        this.userRoleID = userRoleID;
    }

}
