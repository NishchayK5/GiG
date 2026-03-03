package com.g08.gig.business.services;

import com.g08.gig.models.Client;
import com.g08.gig.models.Request;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class RequestServiceImpl implements RequestService{
    @Override
    public Request addRequest(Client client, String title, String body) {
        LocalDateTime dateTimeNow = LocalDateTime.now();

        Request toCreate = new Request(0, client, title, body, dateTimeNow);



        return toCreate;
    }

    @Override
    public void removeRequest(int id) {

    }

    @Override
    public List<Request> getAllRequests() {

        return Collections.emptyList();
    }


}
