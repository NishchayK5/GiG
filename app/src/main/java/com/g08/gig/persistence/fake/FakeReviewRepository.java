package com.g08.gig.persistence.fake;

import com.g08.gig.models.Pro;
import com.g08.gig.models.Review;
import com.g08.gig.persistence.ReviewRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FakeReviewRepository implements ReviewRepository{

    private final List<Review> reviews;
    private int nextId;

    public FakeReviewRepository(){
        reviews = new ArrayList<>();
        nextId = 1;
    }

    @Override
    public List<Review> getAll(){ return Collections.unmodifiableList(reviews);}

    @Override
    public List<Review> getAllReviewsForPro(Pro pro){
        if(pro == null) return null;
        List<Review> result = new ArrayList<>();
        for(Review review : reviews){
            if(review.getPro()==pro) result.add(review);
        }
        return result;
    }

    @Override
    public Review getById(int id){
        for(Review review: reviews){
            if(review.getId() == id) return review;
        }
        return null;
    }

    @Override
    public boolean delete(int id){
        for(int i = 0; i<reviews.size(); i++){
            if(reviews.get(i).getId()==id){
                reviews.remove(i);
                return true;
            }
        }
        return false;
    }

    @Override
    public Review add(Review review) {
        if (review == null) return null;
        int id = review.getId();
        if (id == 0) id = nextId++;
        else if (getById(id) != null) return null;

        Review toStore = new Review(id, review.getReviewer(), review.getPro(), review.getTitle(), review.getBody(), review.getStars());
        reviews.add(toStore);
        return toStore;
    }

}
