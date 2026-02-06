package com.g08.gig.business.services;

import com.g08.gig.models.Client;
import com.g08.gig.models.User;

import java.util.List;

public interface ClientUserService {
    Client addClient(String firstName, String lastName, String email, String password, String confirmPassword, String phone, String address);
    boolean updateClient(int id, String firstName, String lastName, String email, String password, String phone, String address);
    boolean deleteClient(int id);
}
