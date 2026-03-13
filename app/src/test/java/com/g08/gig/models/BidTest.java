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
        Client client = new Client(1, "clientemail@test.com", "Mike", "Ross", "2049998888", "Wall Street", "HarvardNahh");
        Request request = new Request(1, client, "Table Repair", "I need someone to repair an adjustable office desk, which no more goes down with the button.", LocalDateTime.now(), true );
        Bid bid = new Bid(7, pro, request, 289.99, time);

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
        Client client = new Client(1, "clientemail@test.com", "Mike", "Ross", "2049998888", "Wall Street", "HarvardNahh");
        Request request = new Request(1, client, "Table Repair", "I need someone to repair an adjustable office desk, which no more goes down with the button.", LocalDateTime.now(), true );

        Bid bid = new Bid(1, pro, request,79.99, time);

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

    @Test
    void getRequest_returnsRequestSetInConstructor() {
        Pro pro = new Pro(1, "p@test.ca", "P", "R", "2045550000", new String[]{"Oil"}, "Winnipeg");
        Client client = new Client(1, "c@test.com", "C", "L", "2045551111", "Addr", "pwd");
        Request request = new Request(1, client, "Brake Job", "Need pads", LocalDateTime.now(), true);
        Bid bid = new Bid(1, pro, request, 150.0, LocalDateTime.now());

        assertSame(request, bid.getRequest());
        assertEquals("Brake Job", bid.getRequest().getTitle());
    }

    @Test
    void setRequest_updatesRequest() {
        Pro pro = new Pro(1, "p@test.ca", "P", "R", "2045550000", new String[]{"Oil"}, "Winnipeg");
        Client client = new Client(1, "c@test.com", "C", "L", "2045551111", "Addr", "pwd");
        Request request1 = new Request(1, client, "Req1", "Body1", LocalDateTime.now(), true);
        Request request2 = new Request(2, client, "Req2", "Body2", LocalDateTime.now(), false);
        Bid bid = new Bid(1, pro, request1, 100.0, LocalDateTime.now());

        bid.setRequest(request2);

        assertSame(request2, bid.getRequest());
        assertEquals("Req2", bid.getRequest().getTitle());
    }
}
