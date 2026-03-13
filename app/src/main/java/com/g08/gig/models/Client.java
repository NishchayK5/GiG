package com.g08.gig.models;

public class Client extends User{

    private final String address;
    public Client(int id, String email, String firstName, String lastName, String phoneNumber, String address){
        super(id,  email, firstName, lastName, phoneNumber, "CLIENT");
        this.address = address;
    }
    public Client(int id, String email, String firstName, String lastName, String phoneNumber, String address, String password){
        super(id,  email, firstName, lastName, phoneNumber, password, "CLIENT");
        this.address = address;
    }

    public String getAddress(){
        return address;
    }
}
