package com.hdbar.hdbarapp.models;

public class Cocktail {
    public String id;
    public String name;
    public String recipe;
    public String image;
    public Integer rating;

    public Cocktail(String id, String name, String recipe, String image, Integer rating) {
        this.id = id;
        this.name = name;
        this.recipe = recipe;
        this.image = image;
        this.rating = rating;
    }
}
