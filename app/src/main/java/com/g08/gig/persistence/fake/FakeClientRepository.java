package com.g08.gig.persistence.fake;

import com.g08.gig.models.Client;
import com.g08.gig.models.User;
import com.g08.gig.persistence.ClientRepository;
import com.g08.gig.persistence.UserRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FakeClientRepository implements ClientRepository {

    private final List<Client> clients;
    private int nextId;
    private final UserRepository users;


    public FakeClientRepository(UserRepository userRepo){
        clients = new ArrayList<>();
        nextId = 1;
        users = userRepo;
    }


    @Override
    public List<Client> getAll() { return Collections.unmodifiableList(clients);}

    @Override
    public Client getById(int id){
        for(Client client : clients){
            if(client.getId() == id) return client;
        }
        return null;
    }

    @Override
    public Client add(Client client){
        if(client == null) return null;

        int id = client.getId();
        if(id == 0){
            id = nextId++;
        } else if(getById(id)!=null){
            return null;
        }

        Client stored = new Client(id, client.getEmail(), client.getFirstName(), client.getLastName(), client.getPhoneNumber(), client.getAddress(), client.getPassword());
        clients.add(stored);
        return stored;
    }

    @Override
    public boolean update(Client client){
        if(client == null) return false;

        for(int i = 0; i < clients.size(); i++){
            if(clients.get(i).getId() == client.getId()){
                //Checking if user exists
                User existingUser = users.getById(client.getId());
                if(existingUser == null) return false;
                //if so, update the attributes
                if(!users.update(client)) return false;
                //Once successfully updated user repo, update clients repo.
                Client updated = new Client(client.getId(), client.getEmail(), client.getFirstName(), client.getLastName(), client.getPhoneNumber(), client.getAddress(), client.getPassword());
                clients.set(i, updated);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean delete(int id){
        for(int i = 0; i<clients.size(); i++){
            if(clients.get(i).getId() == id){
                clients.remove(i);
                return true;
            }
        }
        return false;
    }
}
