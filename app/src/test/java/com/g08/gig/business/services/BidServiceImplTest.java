package com.g08.gig.business.services;

import com.g08.gig.models.Bid;
import com.g08.gig.models.Client;
import com.g08.gig.models.Pro;
import com.g08.gig.models.Request;
import com.g08.gig.persistence.BidRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BidServiceImplTest {

    @Mock
    private BidRepository repo;

    private BidServiceImpl service;

    private Client client;
    private Pro pro;
    private Request request;
    private LocalDateTime time;

    @BeforeEach
    void setUp() {
        service = new BidServiceImpl(repo);

        client = new Client(
                1,
                "client@test.com",
                "Dayaveer",
                "Gill",
                "2041111111",
                "123 Main St",
                "pass"
        );

        pro = new Pro(
                2,
                "pro@test.com",
                "Izzy",
                "Elskamp",
                "2042222222",
                "pass",
                new String[]{"Electrical"},
                "Winnipeg"
        );

        request = new Request(
                3,
                client,
                "Fix outlet",
                "Need help",
                LocalDateTime.of(2026, 4, 1, 10, 0),
                true
        );

        time = LocalDateTime.of(2026, 4, 2, 14, 30);
    }

    @Test
    void addBid_createsBidWithCorrectDataAndReturnsRepositoryResult() {
        Bid stored = new Bid(10, pro, request, 150.0, time, false);
        when(repo.add(any(Bid.class))).thenReturn(stored);

        Bid result = service.addBid(pro, request, 150.0, time);

        assertSame(stored, result);

        ArgumentCaptor<Bid> captor = ArgumentCaptor.forClass(Bid.class);
        verify(repo).add(captor.capture());

        Bid passed = captor.getValue();
        assertEquals(0, passed.getId());
        assertSame(pro, passed.getPro());
        assertSame(request, passed.getRequest());
        assertEquals(150.0, passed.getCost());
        assertEquals(time, passed.getTimeOfBid());
        assertFalse(passed.getBidStatus());
    }

    @Test
    void getAllByRequest_returnsRepositoryResults() {
        Bid b1 = new Bid(1, pro, request, 100.0, time, false);
        Bid b2 = new Bid(2, pro, request, 120.0, time, false);
        when(repo.getAllByRequest(request)).thenReturn(List.of(b1, b2));

        List<Bid> result = service.getAllByRequest(request);

        assertEquals(List.of(b1, b2), result);
        verify(repo).getAllByRequest(request);
    }

    @Test
    void getAllByPro_returnsRepositoryResults() {
        Bid b1 = new Bid(1, pro, request, 90.0, time, false);
        when(repo.getAllByPro(pro)).thenReturn(List.of(b1));

        List<Bid> result = service.getAllByPro(pro);

        assertEquals(List.of(b1), result);
        verify(repo).getAllByPro(pro);
    }

    @Test
    void getAllByPro_nullPro_returnsEmptyWithoutCallingRepo() {
        assertTrue(service.getAllByPro(null).isEmpty());
        verify(repo, never()).getAllByPro(any());
    }

    @Test
    void acceptBid_marksBidAcceptedAndUpdatesRepository() {
        Bid bid = new Bid(5, pro, request, 180.0, time, false);

        service.acceptBid(bid);

        assertTrue(bid.getBidStatus());
        verify(repo).update(bid);
    }

    @Test
    void getAcceptedBidByRequest_returnsAcceptedBidWhenPresent() {
        Bid notAccepted = new Bid(1, pro, request, 100.0, time, false);
        Bid accepted = new Bid(2, pro, request, 120.0, time, true);
        Bid anotherAccepted = new Bid(3, pro, request, 140.0, time, true);

        when(repo.getAllByRequest(request)).thenReturn(List.of(notAccepted, accepted, anotherAccepted));

        Bid result = service.getAcceptedBidByRequest(request);

        assertSame(accepted, result);
        verify(repo).getAllByRequest(request);
    }

    @Test
    void getAcceptedBidByRequest_returnsNullWhenNoAcceptedBidExists() {
        Bid b1 = new Bid(1, pro, request, 100.0, time, false);
        Bid b2 = new Bid(2, pro, request, 120.0, time, false);

        when(repo.getAllByRequest(request)).thenReturn(List.of(b1, b2));

        Bid result = service.getAcceptedBidByRequest(request);

        assertNull(result);
        verify(repo).getAllByRequest(request);
    }

    @Test
    void getAcceptedBidByRequest_returnsNullWhenRepositoryReturnsEmptyList() {
        when(repo.getAllByRequest(request)).thenReturn(Collections.emptyList());

        Bid result = service.getAcceptedBidByRequest(request);

        assertNull(result);
        verify(repo).getAllByRequest(request);
    }
}