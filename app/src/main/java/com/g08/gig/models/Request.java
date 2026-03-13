package com.g08.gig.models;
import java.time.LocalDateTime;

public class Request {
    private int id;
    private Client client;
    private String title;
    private String body;
    private boolean isActive;
    private LocalDateTime timeOfRequest;
    private double bidCost;


    public Request(int id, Client client, String title, String body, LocalDateTime timeOfRequest, boolean isActive) {
        this.id = id;
        this.client = client;
        this.title = title;
        this.body = body;
        this.timeOfRequest = timeOfRequest;
        this.isActive = isActive;
        this.bidCost = 0;
    }

    /** Copy of source with a specific bid cost (avoids mutating repository-owned instances). */
    public Request(Request source, double bidCost) {
        this.id = source.id;
        this.client = source.client;
        this.title = source.title;
        this.body = source.body;
        this.timeOfRequest = source.timeOfRequest;
        this.isActive = source.isActive;
        this.bidCost = bidCost;
    }

    //getters and setters for the object
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public LocalDateTime getTimeOfRequest() {
        return timeOfRequest;
    }

    public void setTimeOfRequest(LocalDateTime timeOfRequest) {
        this.timeOfRequest = timeOfRequest;
    }

    // Changes the status of the request to expired/completed
    public void setRequestToExpire(){
        isActive = false;
    }
    public boolean getRequestStatus(){return isActive;}
    public double getBidCost() { return bidCost; }
    public void setBidCost(double bidCost) { this.bidCost = bidCost; }
}
