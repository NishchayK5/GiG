package com.g08.gig.business.services;

import com.g08.gig.models.Client;
import com.g08.gig.models.Pro;
import com.g08.gig.models.Review;

import java.util.List;

public interface ReviewService {
    Review addReview(Client reviewer, Pro pro, String title, String body, int stars);
    List<Review> getAllReviewsForPro(Pro pro);
    Review getReviewById(int id);
    public boolean deleteReview(int id);
}
