package com.g08.gig.business.services;

import com.g08.gig.models.Client;

public interface ClientUserService {
    Client addClient(String firstName, String lastName, String email, String password, String confirmPassword, String phone, String address);
    boolean updateClient(int id, String firstName, String lastName, String email, String phone, String address);
    boolean deleteClient(int id);

    Client getClientById(int id);
}
