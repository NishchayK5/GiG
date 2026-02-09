package com.g08.gig.models;

public class User {
    private int id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String password;



    public User(int id, String email, String firstName, String lastName, String phoneNumber) {
        this(id, email, firstName, lastName, phoneNumber, null);
    }

    public User(int id, String email, String firstName, String lastName, String phoneNumber, String password) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }

    // Getters and setters for the fields
    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhoneNumber(){return phoneNumber;}

    public String getPassword() {
        return password;
    }
}
