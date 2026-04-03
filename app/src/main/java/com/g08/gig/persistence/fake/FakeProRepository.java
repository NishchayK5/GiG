package com.g08.gig.persistence.fake;

import com.g08.gig.models.Pro;
import com.g08.gig.models.User;
import com.g08.gig.persistence.ProRepository;
import com.g08.gig.persistence.UserRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FakeProRepository implements ProRepository {
    private final List<Pro> pros;
    private int nextId;
    private final UserRepository users;


    public FakeProRepository(UserRepository userRepo){
        pros = new ArrayList<>();
        nextId = 1;
        users = userRepo;
    }


    @Override
    public List<Pro> getAll(){ return Collections.unmodifiableList(pros); };

    @Override
    public Pro getById(int id){
        for (Pro pro : pros) {
            if(pro.getId() == id) return pro;
        }
        return null;
    }
    @Override
    public Pro add(Pro pro){
        if(pro == null) return null;

        int id = pro.getId();
        if(id==0){
            id = nextId++;
        } else if (getById(id) != null){
            return null;
        }
        Pro stored = new Pro(id, pro.getEmail(), pro.getFirstName(), pro.getLastName(), pro.getPhoneNumber(), pro.getPassword(), pro.getSkillSet(), pro.getCityOfOperation());
        pros.add(stored);
        return stored;
    }
    @Override
    public boolean update(Pro pro){
        if(pro == null) return false;

        for(int i = 0; i<pros.size(); i++){
            if (pros.get(i).getId() == pro.getId()) {
                //Checking if user exists
                User existingUser = users.getById(pro.getId());
                if(existingUser == null) return false;
                //if so, update the attributes in user Repo
                if(!users.update(pro)) return false;
                //Once successfully updated user Repo, update pro(s) repo
                Pro updated = new Pro(pro.getId(), pro.getEmail(), pro.getFirstName(), pro.getLastName(), pro.getPhoneNumber(), pro.getPassword(), pro.getSkillSet(), pro.getCityOfOperation());
                pros.set(i, updated);
                return true;
            }
        }
        return false;
    }
    @Override
    public boolean delete(int id){
        for (int i = 0; i < pros.size(); i++) {
            if(pros.get(i).getId()==id){
                pros.remove(i);
                return true;
            }
        }
        return false;
    }
}
