package com.g08.gig.models;

import java.time.LocalDateTime;

public class Bid {
    private int id;
    private Pro pro;
    private double cost;
    private LocalDateTime timeOfBid;

    public Bid(int id, Pro pro, double cost, LocalDateTime timeOfBid) {
        this.id = id;
        this.pro = pro;
        this.cost = cost;
        this.timeOfBid = timeOfBid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Pro getPro() {
        return pro;
    }

    public void setPro(Pro pro) {
        this.pro = pro;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public LocalDateTime getTimeOfBid() {
        return timeOfBid;
    }

    public void setTimeOfBid(LocalDateTime timeOfBid) {
        this.timeOfBid = timeOfBid;
    }
}
