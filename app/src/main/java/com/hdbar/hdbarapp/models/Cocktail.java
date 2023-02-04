package com.hdbar.hdbarapp.models;

public class Cocktail {
    public String id;
    public String name;
    public String recipe;
    public String image;
    public String rating;
    public String creator;
    public String rating_count;

    public Cocktail(){

    }

    public Cocktail(String id, String name, String recipe, String image, String rating,String creator, String rating_count) {
        this.id = id;
        this.name = name;
        this.recipe = recipe;
        this.image = image;
        this.rating = rating;
        this.creator = creator;
        this.rating_count = rating_count;
    }
}
