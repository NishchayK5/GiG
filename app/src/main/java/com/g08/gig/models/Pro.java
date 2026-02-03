package com.g08.gig.models;

public class Pro extends User{

    public final String[] skillSet;
    public Pro(int id, String email, String firstName, String lastName, String phoneNumber, String[] skillSet){
        super(id, email, firstName, lastName, phoneNumber);
        this.skillSet = skillSet;
    }
    public Pro(int id, String email, String firstName, String lastName, String phoneNumber, String password, String[] skillSet)
    {
        super(id, email, firstName, lastName, phoneNumber, password);
        this.skillSet = skillSet;
    }

    //Getters and setters for the fields
    public String[] getSkillSet(){
        return skillSet;
    }

}
