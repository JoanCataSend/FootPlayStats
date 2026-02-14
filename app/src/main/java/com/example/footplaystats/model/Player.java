package com.example.footplaystats.model;

import java.util.Map;

public class Player {

    private String id;
    private String name;
    private String role;
    private double totalPoints;
    private Map<String, Object> categories;

    public Player() {
        // Firestore necesita constructor vacío
    }

    public Player(String id, String name, String role, double totalPoints) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.totalPoints = totalPoints;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getRole() { return role; }
    public double getTotalPoints() { return totalPoints; }
    public Map<String, Object> getCategories() { return categories; }

    public void setId(String id) { this.id = id; }
}