package com.g08.gig.business.services;

import com.g08.gig.business.constants.FieldLimits;
import com.g08.gig.business.constants.ValidationMessages;
import com.g08.gig.business.validation.ValidationException;
import com.g08.gig.models.Client;
import com.g08.gig.models.Pro;
import com.g08.gig.models.Review;
import com.g08.gig.persistence.ReviewRepository;
import com.g08.gig.util.Strings;

import java.util.List;

public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepo;

    public ReviewServiceImpl(ReviewRepository reviewRepo) {
        this.reviewRepo = reviewRepo;
    }
    @Override
    public Review addReview(Client reviewer, Pro pro, String title, String body, int stars) {
        String trimmedTitle = Strings.safeTrim(title);
        if (trimmedTitle == null || trimmedTitle.isEmpty()) {
            throw new ValidationException(ValidationMessages.REQUEST_TITLE_REQUIRED, "title");
        }
        if (trimmedTitle.length() > FieldLimits.TITLE_MAX_LENGTH) {
            throw new ValidationException(ValidationMessages.TITLE_MAX_LENGTH, "title");
        }
        Review toCreate = new Review(0, reviewer, pro, trimmedTitle, body, stars);

        return reviewRepo.add(toCreate);
    }

    @Override
    public List<Review> getAllReviewsForPro(Pro pro) {
        return reviewRepo.getAllReviewsForPro(pro);
    }

}
