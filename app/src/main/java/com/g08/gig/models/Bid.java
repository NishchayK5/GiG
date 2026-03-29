package com.g08.gig.models;

import java.time.LocalDateTime;

public class Bid {
    private int id;
    private Request request;
    private Pro pro;
    private double cost;
    private LocalDateTime timeOfBid;
    private boolean isAccepted;

    public Bid(int id, Pro pro, Request request, double cost, LocalDateTime timeOfBid, boolean isAccepted) {
        this.id = id;
        this.request = request;
        this.pro = pro;
        this.cost = cost;
        this.timeOfBid = timeOfBid;
        this.isAccepted = isAccepted;
    }

    //getters and setters for the object
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Request getRequest(){return request;}
    public Request getRequestIfActive() {
        if(request.getRequestStatus()) {
            return request;
        }

        return null;

    }

    public void setRequest(Request request){this.request = request;}

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

    public boolean getBidStatus() {
        return isAccepted;
    }
    public void acceptBid() { this.isAccepted = true; }

}
