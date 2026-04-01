package com.g08.gig.business.services;

import com.g08.gig.application.GiGApp;
import com.g08.gig.models.Bid;
import com.g08.gig.models.Pro;
import com.g08.gig.models.Request;
import com.g08.gig.persistence.BidRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BidServiceImpl implements BidService {

    private final BidRepository bidRepo;
    public BidServiceImpl(BidRepository bidRepo) {
        this.bidRepo = bidRepo;
    }
    @Override
    public Bid addBid(Pro pro, Request request, double cost, LocalDateTime proposedTime) {

        Bid toCreate = new Bid(0, pro, request, cost, proposedTime, false);

        return bidRepo.add(toCreate);
    }

    @Override
    public List<Bid> getAllByRequest(Request request) {
        return bidRepo.getAllByRequest(request);
    }

    @Override
    public List<Bid> getAllByPro(Pro pro) {
        return bidRepo.getAllByPro(pro);
    }



    @Override
    public void acceptBid(Bid bid) {
        bid.acceptBid();
        bidRepo.update(bid);
    }

    @Override
    public Bid getAcceptedBidByRequest(Request request) {
        List<Bid> bids = bidRepo.getAllByRequest(request);
        for (Bid bid : bids) {
            if (bid.getBidStatus()) return bid;
        }
        return null;
    }

}


