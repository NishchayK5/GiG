package com.g08.gig.business.services;

import com.g08.gig.models.Client;
import com.g08.gig.models.Request;

import java.util.List;

public interface RequestService {

    Request addRequest(Client client, String title, String body);
    void removeRequest(int id);
    List<Request> getAllRequests();

    List<Request> getRequestsByClient(int id);

    List<Request> getRequestsByStatus(boolean status);
}
