package com.example.footplaystats.ui.stats;

import java.util.List;

public class CategoryItem {

    private String name;
    private double average;
    private List<SubStatItem> subStats;
    private boolean expanded;

    public CategoryItem(String name,
                        double average,
                        List<SubStatItem> subStats) {
        this.name = name;
        this.average = average;
        this.subStats = subStats;
        this.expanded = false;
    }

    public String getName() {
        return name;
    }

    public double getAverage() {
        return average;
    }

    public List<SubStatItem> getSubStats() {
        return subStats;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
}