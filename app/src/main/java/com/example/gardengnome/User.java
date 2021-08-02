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

    public String getFullname() {
        return fullname;
    }

    public String getEmail() {
        return email;
    }

    public int getUserRoleID() {
        return userRoleID;
    }

    @Override
    public String toString() {
        return "User{" +
                "fullname='" + fullname + '\'' +
                ", email='" + email + '\'' +
                ", userRoleID=" + userRoleID +
                '}';
    }
}
