/**
 * File: BidsService.java

 * Purpose:
 * Manages the creation of bids by the pro on the requests posted by the clients and also responsible of showing the bids to the clients under their request.
 *
 */
package com.g08.gig.business.services;

import com.g08.gig.models.Bid;
import com.g08.gig.models.Pro;
import com.g08.gig.models.Request;

import java.time.LocalDateTime;
import java.util.List;

public interface BidService {
    Bid addBid(Pro pro, Request request, double cost, LocalDateTime proposedTime);
    List<Bid> getAllByRequest(Request request);
    List<Bid> getAllByPro(Pro pro);
    void acceptBid(Bid bid);

    Bid getAcceptedBidByRequest(Request request);


}
