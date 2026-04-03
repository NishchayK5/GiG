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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BidServiceImplTest {

    @Mock
    private BidRepository repo;

    @InjectMocks
    private BidServiceImpl service;

    private Client client;
    private Pro pro;
    private Request request;
    private LocalDateTime time;

    @BeforeEach
    void setUp() {
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
                "Alex",
                "Smith",
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
                LocalDateTime.now(),
                true
        );

        time = LocalDateTime.now();
    }

    @Test
    void addBid_callsRepositoryAndReturnsResult() {
        Bid stored = new Bid(10, pro, request, 150.0, time, false);
        when(repo.add(any(Bid.class))).thenReturn(stored);

        Bid result = service.addBid(pro, request, 150.0, time);

        assertNotNull(result);
        assertSame(stored, result);

        verify(repo).add(any(Bid.class));
    }

    @Test
    void addBid_passesCorrectDataToRepository() {
        when(repo.add(any(Bid.class))).thenReturn(null);

        service.addBid(pro, request, 200.0, time);

        ArgumentCaptor<Bid> captor = ArgumentCaptor.forClass(Bid.class);
        verify(repo).add(captor.capture());

        Bid passed = captor.getValue();

        assertEquals(pro, passed.getPro());
        assertEquals(request, passed.getRequest());
        assertEquals(200.0, passed.getCost());
    }

    @Test
    void getAllByRequest_returnsRepositoryResults() {
        Bid b1 = new Bid(1, pro, request, 100.0, time, false);
        Bid b2 = new Bid(2, pro, request, 120.0, time, false);

        when(repo.getAllByRequest(request)).thenReturn(List.of(b1, b2));

        List<Bid> result = service.getAllByRequest(request);

        assertEquals(2, result.size());
        assertSame(b1, result.get(0));
        assertSame(b2, result.get(1));

        verify(repo).getAllByRequest(request);
    }

    @Test
    void getAllByPro_returnsRepositoryResults() {
        Bid b1 = new Bid(1, pro, request, 90.0, time, false);

        when(repo.getAllByPro(pro)).thenReturn(List.of(b1));

        List<Bid> result = service.getAllByPro(pro);

        assertEquals(1, result.size());
        assertSame(b1, result.get(0));

        verify(repo).getAllByPro(pro);
    }
}