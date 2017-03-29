package com.example.guessthecelebrety;

/**
 * Created by noushad on 3/28/17.
 */

public class Celebrety {
    String name;
    String imageUrl;
    String description;

    public Celebrety(String name, String imageUrl, String description) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDescription() {
        return description;
    }
}
