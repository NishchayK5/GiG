package com.g08.gig.business.services;

import com.g08.gig.models.Client;
import com.g08.gig.models.Pro;
import com.g08.gig.models.Review;
import com.g08.gig.persistence.ReviewRepository;

import java.util.List;

public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepo;
    public ReviewServiceImpl(ReviewRepository reviewRepo) { this.reviewRepo = reviewRepo; }
    @Override
    public Review addReview(Client reviewer, Pro pro, String title, String body, int stars) {
        Review toCreate = new Review(0, reviewer, pro, title, body, stars);

        return reviewRepo.add(toCreate);
    }

    @Override
    public List<Review> getAllReviewsForPro(Pro pro) {
        return reviewRepo.getAllReviewsForPro(pro);
    }

    @Override
    public boolean deleteReview(int id) {
        return reviewRepo.delete(id);
    }

    @Override
    public Review getReviewById(int id) {
        return reviewRepo.getById(id);
    }
}
