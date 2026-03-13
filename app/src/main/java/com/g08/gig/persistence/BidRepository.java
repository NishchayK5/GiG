package com.g08.gig.persistence;

import com.g08.gig.models.Bid;
import com.g08.gig.models.Pro;
import com.g08.gig.models.Request;

import java.util.List;

public interface BidRepository {

    List<Bid> getAll();

    Bid add(Bid bid);

    boolean delete(int id);

    Bid getById(int id);
    List<Bid> getAllByRequest(Request request);
    List<Bid> getAllByPro(Pro pro);
}
