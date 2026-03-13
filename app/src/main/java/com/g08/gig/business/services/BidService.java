package com.g08.gig.business.services;

import com.g08.gig.models.Bid;
import com.g08.gig.models.Pro;
import com.g08.gig.models.Request;

import java.util.List;

public interface BidService {
    Bid addBid(Pro pro, Request request, double cost);
    List<Bid> getAllByRequest(Request request);
    List<Bid> getAllByPro(Pro pro);
    List<Request> getRequestsWithBidCostsByPro(Pro pro);
}
