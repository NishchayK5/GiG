package com.g08.gig.models;

public class Pro extends User{

    public final String[] skillSet;
    public Pro(int id, String email, String firstName, String lastName, String[] skillSet){
        super(id, email, firstName, lastName);
        this.skillSet = skillSet;
    }
    public Pro(int id, String email, String firstName, String lastName, String password, String[] skillSet)
    {
        super(id, email, firstName, lastName, password);
        this.skillSet = skillSet;
    }

    //Getters and setters for the fields
    public String[] getSkillSet(){
        return skillSet;
    }

}
