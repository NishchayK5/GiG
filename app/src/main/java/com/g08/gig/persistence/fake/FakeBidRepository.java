package com.g08.gig.persistence.fake;

import com.g08.gig.models.Bid;
import com.g08.gig.models.Pro;
import com.g08.gig.models.Request;
import com.g08.gig.persistence.BidRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FakeBidRepository implements BidRepository {
    private final List<Bid> bids;
    private int nextId;

    public FakeBidRepository(){
        bids = new ArrayList<>();
        nextId = 1;
    }

    @Override
    public List<Bid> getAll(){
        return Collections.unmodifiableList(bids);
    }

    @Override
    public Bid add(Bid bid){
        if(bid == null) return null;

        int id = bid.getId();
        if(id==0){
            id = nextId++;
        } else if (getById(id) != null) return null;

        Bid stored = new Bid(id, bid.getPro(), bid.getRequest(), bid.getCost(), bid.getTimeOfBid(), bid.getBidStatus());
        bids.add(stored);
        return stored;
    }

    @Override
    public boolean delete(int id){
        for(int i = 0; i<bids.size(); i++){
            if(bids.get(i).getId()==id){
                bids.remove(i);
                return true;
            }
        }
        return false;
    }

    @Override
    public Bid getById(int id){
        for(Bid bid : bids) {
            if(bid.getId() == id) return bid;
        }
        return null;
    }

    @Override
    public List<Bid> getAllByRequest(Request request){
        List<Bid> result = new ArrayList<>();
        for(Bid bid : bids){
            if(bid.getRequest().getId()==request.getId()){
                result.add(bid);
            }
        }
        return result;
    }

    @Override
    public List<Bid> getAllByPro(Pro pro){
        if (pro == null) {
            return new ArrayList<>();
        }
        List<Bid> result = new ArrayList<>();
        for(Bid bid : bids){
            if(bid.getPro()==pro){
                result.add(bid);
            }
        }
        return result;
    }

    @Override
    public boolean update(Bid bid) {
        if(bid==null){ return false;}

        for(int i = 0; i<bids.size(); i++){
            if (bids.get(i).getId() == bid.getId()) {
                Bid updated = new Bid(bid.getId(), bid.getPro(), bid.getRequest(), bid.getCost(), bid.getTimeOfBid(), bid.getBidStatus());
                bids.set(i, updated);
                return true;
            }
        }
        return false;
    }
}
