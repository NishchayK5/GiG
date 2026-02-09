package com.g08.gig.models;

public class Client extends User{

    public final String address;
    public Client(int id, String email, String firstName, String lastName, String phoneNumber, String address){
        super(id,  email, firstName, lastName, phoneNumber);
        this.address = address;
    }
    public Client(int id, String email, String firstName, String lastName, String phoneNumber, String address, String password){
        super(id,  email, firstName, lastName, phoneNumber, password);
        this.address = address;
    }

    // Getters and setters for the fields
    public String getAddress(){
        return address;
    }
}
