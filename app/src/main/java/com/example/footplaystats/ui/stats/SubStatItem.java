package com.example.footplaystats.ui.stats;

public class SubStatItem {

    private String name;
    private double value;

    public SubStatItem(String name, double value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public double getValue() {
        return value;
    }
}