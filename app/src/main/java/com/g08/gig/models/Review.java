package com.g08.gig.models;

public class Review {
    private int id;
    private Client reviewer;
    private Pro pro;
    private String title;
    private String body;
    private int stars;

    public Review(int id, Client reviewer, Pro pro, String title, String body, int stars) {
        this.id = id;
        this.reviewer = reviewer;
        this.pro = pro;
        this.title = title;
        this.body = body;
        this.stars = stars;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Client getReviewer() {
        return reviewer;
    }

    public void setReviewer(Client reviewer) {
        this.reviewer = reviewer;
    }

    public Pro getPro() { return pro;}

    public void setPro(Pro pro) { this.pro = pro;}

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

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }
}
