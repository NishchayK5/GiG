package com.g08.gig.models;

public class User {
    private int id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String password;
    private final String role;

    public User(int id, String email, String firstName, String lastName, String phoneNumber, String role) {
        this(id, email, firstName, lastName, phoneNumber, null, role);
    }

    public User(int id, String email, String firstName, String lastName, String phoneNumber, String password, String role) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.password = password;
    }

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
    public String getRole(){
        return role;
    }
}
