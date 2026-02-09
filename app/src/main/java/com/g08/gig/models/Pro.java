package com.g08.gig.models;

public class Pro extends User{

    public final String[] skillSet;
    public final String cityOfOperation;
    public Pro(int id, String email, String firstName, String lastName, String phoneNumber, String[] skillSet, String cityOfOperation){
        super(id, email, firstName, lastName, phoneNumber);
        this.skillSet = skillSet;
        this.cityOfOperation = cityOfOperation;
    }
    public Pro(int id, String email, String firstName, String lastName, String phoneNumber, String password, String[] skillSet, String cityOfOperation)
    {
        super(id, email, firstName, lastName, phoneNumber, password);
        this.skillSet = skillSet;
        this.cityOfOperation = cityOfOperation;
    }

    //Getters and setters for the fields
    public String[] getSkillSet(){
        return skillSet;
    }

    public String getCityOfOperation(){
        return cityOfOperation;
    }

}
