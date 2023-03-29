package com.drini.driniapp.models;

public class User {
    public String uid;
    public String name;
    public String email;
    public String status;
    public String bio;
    public String image;


    public User(){
//        Default constructor
    }

    public User(String uid, String name, String status, String bio, String image) {
        this.uid = uid;
        this.name = name;
        this.status = status;
        this.bio = bio;
        this.image = image;
    }
}
