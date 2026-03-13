package com.g08.gig.business.services;

import com.g08.gig.models.Bid;
import com.g08.gig.models.Pro;
import com.g08.gig.models.Request;
import com.g08.gig.persistence.BidRepository;
import com.g08.gig.persistence.RequestRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BidServiceImpl implements BidService {

    private final BidRepository bidRepo;

    public BidServiceImpl(BidRepository bidRepo)
    {
        this.bidRepo = bidRepo;
    }
    @Override
    public Bid addBid(Pro pro, Request request, double cost) {
        LocalDateTime dateTimeNow = LocalDateTime.now();

        Bid toCreate = new Bid(0, pro, request, cost, dateTimeNow);
        bidRepo.add(toCreate);

        return toCreate;
    }

    @Override
    public List<Bid> getAllByRequest(Request request) {
        return bidRepo.getAllByRequest(request);
    }

    @Override
    public List<Bid> getAllByPro(Pro pro) {
        return bidRepo.getAllByPro(pro);
    }

    // returns all the requests on which bid has been placed by the Pro
    @Override
    public List<Request> getRequestsWithBidCostsByPro(Pro pro) {
        List<Bid> bids = bidRepo.getAllByPro(pro);
        List<Request> result = new ArrayList<>();
        for (Bid bid : bids) {
            if (bid != null && bid.getRequest() != null) {
                Request r = bid.getRequest();
                r.setBidCost(bid.getCost());
                result.add(r);
            }
        }
        return result;
    }
}
