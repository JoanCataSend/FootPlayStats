package com.example.footplaystats.ui.players;

public class PlayerItem {

    private String name;
    private double points;

    public PlayerItem(String name, double points) {
        this.name = name;
        this.points = points;
    }

    public String getName() {
        return name;
    }

    public double getPoints() {
        return points;
    }
}