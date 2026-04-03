package com.g08.gig.persistence.fake;

import com.g08.gig.models.Bid;
import com.g08.gig.models.Request;
import com.g08.gig.persistence.BidRepository;
import com.g08.gig.persistence.RequestRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FakeRequestRepository implements RequestRepository {

    private final List<Request> requests;
    private int nextId;
    private final BidRepository bidRepo;
    public FakeRequestRepository(BidRepository bidRepo) {
        requests = new ArrayList<>();
        nextId = 1;
        this.bidRepo = bidRepo;
    }

    @Override
    public List<Request> getAll() {
        return Collections.unmodifiableList(requests);
    }

    @Override
    public Request getById(int id) {
        for (Request request : requests) {
            if (request.getId() == id) return request;
        }
        return null;
    }

    @Override
    public Request add(Request request) {
        if (request == null) return null;

        int id = request.getId();
        if (id == 0) {
            id = nextId++;
        } else if (getById(id) != null) {
            return null;
        }

        Request stored = new Request(id, request.getClient(), request.getTitle(), request.getBody(), request.getTimeOfRequest(), request.getRequestStatus());
        requests.add(stored);
        return stored;
    }

    @Override
    public boolean delete(int id){
        for(int i = 0; i<requests.size(); i++){
            if(requests.get(i).getId()==id) {
                deleteAllBidsForRequest(id);  //deletes all bids for the request
                requests.remove(i);
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Request> getByClient(int clientId){
        List<Request> result = new ArrayList<>();
        for(Request request : requests){
            if(request.getClient().getId() == clientId){
                result.add(request);
            }
        }
        return result;
    }

    @Override
    public List<Request> getAllWithStatus(boolean status){
        List<Request> result = new ArrayList<>();
        for(Request request : requests){
            if(request.getRequestStatus()==status){
                result.add(request);
            }
        }
        return result;
    }

    @Override
    public boolean update(Request request) {
        if(request==null){ return false;}

        for(int i = 0; i<requests.size(); i++){
            if (requests.get(i).getId() == request.getId()) {
                Request updated = new Request(request.getId(), request.getClient(), request.getTitle(), request.getBody(), request.getTimeOfRequest(), request.getRequestStatus());
                requests.set(i, updated);
                return true;
            }
        }
        return false;
    }
    //this function mimics on delete cascade
    private void deleteAllBidsForRequest(int requestId){
        Request request = getById(requestId);
        if(request == null) return;

        List<Bid> bidsForRequest = bidRepo.getAllByRequest(request);

        for(Bid bid: bidsForRequest){
            bidRepo.delete(bid.getId());
        }
    }
}
