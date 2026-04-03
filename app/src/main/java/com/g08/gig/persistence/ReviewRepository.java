/**
 * File: ReviewRepository.java

 * Purpose:
 * This class is responsible for all the retrieval and storage of reviews and rating of the pro to the database.
 *
 */
package com.g08.gig.persistence;

import com.g08.gig.models.Pro;
import com.g08.gig.models.Review;

import java.util.List;

public interface ReviewRepository {

    List<Review> getAll();
    List<Review> getAllReviewsForPro(Pro pro);
    Review add(Review review);
    boolean delete(int id);
    Review getById(int id);
}
