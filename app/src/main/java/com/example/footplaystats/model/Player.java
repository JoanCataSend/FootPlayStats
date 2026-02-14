package com.example.footplaystats.model;

import java.util.Map;

public class Player {

    private String id;
    private String name;
    private String role;
    private double totalPoints;
    private String imageUrl;   // 🔥 NUEVO
    private Map<String, Object> categories;

    public Player() {
        // Firestore necesita constructor vacío
    }

    public Player(String id, String name, String role,
                  double totalPoints, String imageUrl) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.totalPoints = totalPoints;
        this.imageUrl = imageUrl;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getRole() { return role; }
    public double getTotalPoints() { return totalPoints; }
    public String getImageUrl() { return imageUrl; }
    public Map<String, Object> getCategories() { return categories; }

    public void setId(String id) { this.id = id; }
}