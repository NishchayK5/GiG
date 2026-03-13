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

    private Pro pro;
    private Request request;

    @BeforeEach
    void setUp() {
        Client client = new Client(
                1,
                "client@email.com",
                "Dayaveer",
                "Gill",
                "2041111111",
                "123 Main",
                "hashed"
        );

        pro = new Pro(
                2,
                "pro@email.com",
                "Izzy",
                "Elskamp",
                "2042222222",
                "pass",
                new String[]{"Brakes", "Oil Change"},
                "Winnipeg"
        );

        request = new Request(
                10,
                client,
                "Brake Repair",
                "Need repair",
                LocalDateTime.now(),
                true
        );
    }

    @Test
    void addBid_validInput_createsBidAndCallsRepository() {
        Bid stored = new Bid(20, pro, request, 150.0, LocalDateTime.now());
        when(repo.add(any(Bid.class))).thenReturn(stored);

        Bid result = service.addBid(pro, request, 150.0);

        assertNotNull(result);
        assertEquals(pro, result.getPro());
        assertEquals(request, result.getRequest());
        assertEquals(150.0, result.getCost());
        assertNotNull(result.getTimeOfBid());

        ArgumentCaptor<Bid> captor = ArgumentCaptor.forClass(Bid.class);
        verify(repo).add(captor.capture());

        Bid passed = captor.getValue();
        assertEquals(pro, passed.getPro());
        assertEquals(request, passed.getRequest());
        assertEquals(150.0, passed.getCost());
        assertNotNull(passed.getTimeOfBid());
    }

    @Test
    void getAllByRequest_returnsRepoResults() {
        List<Bid> bids = List.of(
                new Bid(1, pro, request, 100.0, LocalDateTime.now()),
                new Bid(2, pro, request, 120.0, LocalDateTime.now())
        );

        when(repo.getAllByRequest(request)).thenReturn(bids);

        List<Bid> result = service.getAllByRequest(request);

        assertEquals(2, result.size());
        assertEquals(100.0, result.get(0).getCost());
        assertEquals(120.0, result.get(1).getCost());
        verify(repo).getAllByRequest(request);
    }

    @Test
    void getAllByPro_returnsRepoResults() {
        List<Bid> bids = List.of(
                new Bid(1, pro, request, 100.0, LocalDateTime.now())
        );

        when(repo.getAllByPro(pro)).thenReturn(bids);

        List<Bid> result = service.getAllByPro(pro);

        assertEquals(1, result.size());
        assertEquals(pro, result.get(0).getPro());
        verify(repo).getAllByPro(pro);
    }

    @Test
    void getRequestsWithBidCostsByPro_returnsRequestsWithBidCostSet() {
        Request request1 = new Request(
                11,
                request.getClient(),
                "Oil Change",
                "Need oil",
                LocalDateTime.now(),
                true
        );

        Request request2 = new Request(
                12,
                request.getClient(),
                "Battery Check",
                "Need battery checked",
                LocalDateTime.now(),
                true
        );

        Bid bid1 = new Bid(1, pro, request1, 95.0, LocalDateTime.now());
        Bid bid2 = new Bid(2, pro, request2, 140.0, LocalDateTime.now());

        when(repo.getAllByPro(pro)).thenReturn(List.of(bid1, bid2));

        List<Request> result = service.getRequestsWithBidCostsByPro(pro);

        assertEquals(2, result.size());

        assertEquals("Oil Change", result.get(0).getTitle());
        assertEquals(95.0, result.get(0).getBidCost());

        assertEquals("Battery Check", result.get(1).getTitle());
        assertEquals(140.0, result.get(1).getBidCost());

        verify(repo).getAllByPro(pro);
    }

    @Test
    void getRequestsWithBidCostsByPro_ignoresNullBidAndNullRequest() {
        Bid validBid = new Bid(1, pro, request, 175.0, LocalDateTime.now());
        Bid nullRequestBid = new Bid(2, pro, null, 120.0, LocalDateTime.now());

        when(repo.getAllByPro(pro)).thenReturn(Arrays.asList(null, nullRequestBid, validBid));

        List<Request> result = service.getRequestsWithBidCostsByPro(pro);

        assertEquals(1, result.size());
        assertEquals(request.getTitle(), result.get(0).getTitle());
        assertEquals(175.0, result.get(0).getBidCost());

        verify(repo).getAllByPro(pro);
    }
}