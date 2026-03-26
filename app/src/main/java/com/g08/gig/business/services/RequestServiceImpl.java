package com.g08.gig.business.services;

import com.g08.gig.util.Pair;

import com.g08.gig.business.constants.ValidationMessages;
import com.g08.gig.business.validation.ValidationException;
import com.g08.gig.models.Client;
import com.g08.gig.models.Request;
import com.g08.gig.persistence.RequestRepository;
import com.g08.gig.util.Strings;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RequestServiceImpl implements RequestService {

    private final RequestRepository reqRepo;

    public RequestServiceImpl(RequestRepository reqRepo) {
        this.reqRepo = reqRepo;
    }

    @Override
    public Request addRequest(Client client, String title, String body) {
        String trimmedTitle = Strings.safeTrim(title);
        String trimmedBody = Strings.safeTrim(body);
        if (trimmedTitle == null || trimmedTitle.isEmpty()) {
            throw new ValidationException(ValidationMessages.REQUEST_TITLE_REQUIRED, "title");
        }
        if (trimmedBody == null || trimmedBody.isEmpty()) {
            throw new ValidationException(ValidationMessages.REQUEST_BODY_REQUIRED, "body");
        }
        LocalDateTime dateTimeNow = LocalDateTime.now();
        Request toCreate = new Request(0, client, trimmedTitle, trimmedBody, dateTimeNow, true);
        return reqRepo.add(toCreate);
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

    @Override
    public Pair<List<Request>, List<Request>> getRequestsByClientCurrentAndPast(int clientId) {
        List<Request> all = reqRepo.getByClient(clientId);
        List<Request> current = new ArrayList<>();
        List<Request> past = new ArrayList<>();
        for (Request r : all) {
            if (r.getRequestStatus()) {
                current.add(r);
            } else {
                past.add(r);
            }
        }
        return new Pair<>(current, past);
    }

    @Override
    public List<Request> getRequestsByStatus(boolean status) {
        return reqRepo.getAllWithStatus(status);
    }

    @Override
    public void completeRequest(int id) {
        Request toUpdate = reqRepo.getById(id);
        toUpdate.setRequestToExpire();
        reqRepo.update(toUpdate);
    }


}
