package com.g08.gig.business.services;

import com.g08.gig.models.Client;
import com.g08.gig.models.Request;
import com.g08.gig.persistence.RequestRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class RequestServiceImpl implements RequestService{

    private final RequestRepository reqRepo;

    public RequestServiceImpl(RequestRepository reqRepo)
    {
        this.reqRepo = reqRepo;
    }

    @Override
    public Request addRequest(Client client, String title, String body) {
        LocalDateTime dateTimeNow = LocalDateTime.now();

        Request toCreate = new Request(0, client, title, body, dateTimeNow);

        reqRepo.add(toCreate);

        return toCreate;
    }

    @Override
    public void removeRequest(int id) {
        reqRepo.delete(id);
    }

    @Override
    public List<Request> getAllRequests() {
        return reqRepo.getAll();
    }

    @Override
    public List<Request> getRequestsByClient(int id) {
        return reqRepo.getByClient(id);
    }


}
