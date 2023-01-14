package com.hdbar.hdbarapp.models;

public class Cocktail {
    public String id;
    public String name;
    public String image;
    public Integer rating;

    public Cocktail(String id, String name, String image, Integer rating) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.rating = rating;
    }
}
