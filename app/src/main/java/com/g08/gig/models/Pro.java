package com.g08.gig.models;

public class Pro extends User{

    private final String[] skillSet;
    private final String cityOfOperation;

    public Pro(int id, String email, String firstName, String lastName, String phoneNumber, String[] skillSet, String cityOfOperation){
        super(id, email, firstName, lastName, phoneNumber, "PRO");
        this.skillSet = skillSet;
        this.cityOfOperation = cityOfOperation;
    }
    public Pro(int id, String email, String firstName, String lastName, String phoneNumber, String password, String[] skillSet, String cityOfOperation)
    {
        super(id, email, firstName, lastName, phoneNumber, password, "PRO");
        this.skillSet = skillSet;
        this.cityOfOperation = cityOfOperation;
    }

    public String[] getSkillSet(){
        if(skillSet==null) return null;
        return skillSet.clone();
    }

    public String getCityOfOperation(){
        return cityOfOperation;
    }

}
