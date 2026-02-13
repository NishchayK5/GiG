package com.g08.gig.models;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BidTest {

    @Test
    void constructor_setsAllFields() {
        Pro pro = new Pro(1, "tony.mechanic@autoshop.ca", "Tony", "Santos", "2045559900",
                new String[]{"Brake Service", "Suspension"}, "Winnipeg");
        LocalDateTime time = LocalDateTime.of(2025, 6, 20, 9, 0);
        Bid bid = new Bid(7, pro, 289.99, time);

        assertEquals(7, bid.getId());
        assertSame(pro, bid.getPro());
        assertEquals(289.99, bid.getCost(), 0.001);
        assertEquals(time, bid.getTimeOfBid());
    }

    @Test
    void setters_updateFields() {
        Pro pro = new Pro(1, "jose.garza@quicklube.ca", "Jose", "Garza", "2045551010",
                new String[]{"Oil Change"}, "Winnipeg");
        LocalDateTime time = LocalDateTime.now();
        Bid bid = new Bid(1, pro, 79.99, time);

        bid.setId(2);
        assertEquals(2, bid.getId());

        Pro newPro = new Pro(2, "anna.kowalski@tiresplus.ca", "Anna", "Kowalski", "2045552121",
                new String[]{"Tire Change", "Alignment"}, "Winnipeg");
        bid.setPro(newPro);
        assertSame(newPro, bid.getPro());

        bid.setCost(149.50);
        assertEquals(149.50, bid.getCost(), 0.001);

        LocalDateTime newTime = LocalDateTime.of(2025, 8, 1, 12, 0);
        bid.setTimeOfBid(newTime);
        assertEquals(newTime, bid.getTimeOfBid());
    }
}
