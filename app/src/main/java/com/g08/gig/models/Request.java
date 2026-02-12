package com.g08.gig.models;
import java.time.LocalDateTime;

public class Request {
    private int id;
    private Client client;
    private String title;
    private String body;
    private LocalDateTime timeOfRequest;

    public Request(int id, Client client, String title, String body, LocalDateTime timeOfRequest) {
        this.id = id;
        this.client = client;
        this.title = title;
        this.body = body;
        this.timeOfRequest = timeOfRequest;
    }

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
}
