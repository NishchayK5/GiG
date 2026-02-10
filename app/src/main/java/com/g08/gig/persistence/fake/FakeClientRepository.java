package com.g08.gig.persistence.fake;

import android.util.Log;

import com.g08.gig.models.Client;
import com.g08.gig.persistence.ClientRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FakeClientRepository implements ClientRepository {

    private final List<Client> clients;
    private int nextId;

    public FakeClientRepository(){
        clients = new ArrayList<>();
        nextId = 1;
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
        Log.d("STUB_DB", "Added client: " + client);
        Log.d("STUB_DB", "Total Clients: " + clients.size());

        return stored;
    }

    @Override
    public boolean update(Client client){
        if(client == null) return false;

        for(int i = 0; i < clients.size(); i++){
            if(clients.get(i).getId()==client.getId()){
                Client updated = new Client(client.getId(), client.getEmail(), client.getFirstName(), client.getLastName(), client.getPhoneNumber(), client.getAddress(), client.getPassword());
                clients.set(i, updated);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean delete(int id){
        for(int i = 0; i<clients.size();i++){
            if(clients.get(i).getId() == id){
                clients.remove(i);
                return true;
            }
        }
        return false;
    }
}
