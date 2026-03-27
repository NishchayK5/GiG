package com.g08.gig.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReviewTest {

    @Test
    void constructor_setsAllFields() {
        Client reviewer = new Client(1, "lisa.park@email.com", "Lisa", "Park", "2045551122",
                "200 Main Street, Winnipeg", null);
        Pro pro = new Pro(1, "carlos.mendez@autocare.ca", "Carlos", "Mendez", "2045553232",
                new String[]{"Oil Change", "Brake Service"}, "Winnipeg");
        Review review = new Review(10, reviewer, pro, "Excellent brake job",
                "Had my brakes replaced last week. Very professional and the car stops perfectly now. Highly recommend.", 5);
        assertEquals(10, review.getId());
        assertSame(reviewer, review.getReviewer());
        assertEquals("Excellent brake job", review.getTitle());
        assertEquals("Had my brakes replaced last week. Very professional and the car stops perfectly now. Highly recommend.", review.getBody());
        assertEquals(5, review.getStars());
    }

    @Test
    void setters_updateFields() {
        Client reviewer = new Client(1, "andrew.brown@email.com", "Andrew", "Brown", "2045553344",
                "15 Henderson Hwy, Winnipeg", null);
        Pro pro = new Pro(1, "carlos.mendez@autocare.ca", "Carlos", "Mendez", "2045553232",
                new String[]{"Oil Change", "Brake Service"}, "Winnipeg");
        Review review = new Review(1, reviewer, pro, "Good oil change", "Fast and friendly service.", 3);

        review.setId(2);
        assertEquals(2, review.getId());

        Client newReviewer = new Client(2, "natalie.clark@gmail.com", "Natalie", "Clark", "2045555566",
                "100 St. Mary's Road, Winnipeg", null);
        review.setReviewer(newReviewer);
        assertSame(newReviewer, review.getReviewer());

        review.setTitle("Tire rotation was quick");
        assertEquals("Tire rotation was quick", review.getTitle());

        review.setBody("Came in for a rotation. Done in under an hour. Will be back.");
        assertEquals("Came in for a rotation. Done in under an hour. Will be back.", review.getBody());

        review.setStars(4);
        assertEquals(4, review.getStars());
    }
}
