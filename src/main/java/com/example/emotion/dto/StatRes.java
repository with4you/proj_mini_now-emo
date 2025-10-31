package com.example.emotion.dto;

public class StatRes {
    private String label;
    private long count;
    private double avg;

    public StatRes(String label, long count, double avg) {
        this.label = label;
        this.count = count;
        this.avg = avg;
    }

    public String getLabel() {
        return label;
    }

    public long getCount() {
        return count;
    }

    public double getAvg() {
        return avg;
    }
}


