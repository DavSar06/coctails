package com.hdbar.hdbarapp.models;

import java.util.Date;

public class Comment   {
    public String uid;
    public String uImage;
    public String comment;
    public String commenter;
    public Date date;

    public Comment(String comment, String commenter, Date date, String uid, String uImage) {
        this.comment = comment;
        this.commenter = commenter;
        this.date = date;
        this.uid = uid;
        this.uImage = uImage;
    }
}
