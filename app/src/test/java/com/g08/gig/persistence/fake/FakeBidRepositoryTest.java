package com.g08.gig.persistence.fake;

import static org.junit.jupiter.api.Assertions.*;

import com.g08.gig.models.Bid;
import com.g08.gig.models.Client;
import com.g08.gig.models.Pro;
import com.g08.gig.models.Request;

import com.g08.gig.business.services.BidServiceImpl;
import com.g08.gig.business.services.RequestServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class FakeBidRepositoryTest {

    private FakeBidRepository     bidRepo;
    private FakeRequestRepository requestRepo;
    private BidServiceImpl        bidService;
    private RequestServiceImpl    requestService;

    private static final Client CLIENT = new Client(
            1, "client@test.com", "Dayaveer", "Gill", "2041111111", "123 Main St", "hashed");

    private static final Pro PRO1 = new Pro(
            10, "pro1@test.com", "Izzy", "Elskamp", "2042222222",
            "hashed", new String[]{"Oil Change", "Brakes"}, "Winnipeg");

    private static final Pro PRO2 = new Pro(
            11, "pro2@test.com", "Nishchay", "Kathuria", "2043333333",
            "hashed", new String[]{"AC Repair"}, "Winnipeg");

    @BeforeEach
    void setUp() {
        bidRepo        = new FakeBidRepository();
        requestRepo    = new FakeRequestRepository(bidRepo);
        bidService     = new BidServiceImpl(bidRepo);
        requestService = new RequestServiceImpl(requestRepo);
    }

    // ==================== addBid ====================

    @Test
    void addBid_validInputs_returnsNonNull() {
        Request request = requestService.addRequest(CLIENT, "Oil Change", "Need oil");
        Bid result = bidService.addBid(PRO1, request, 75.00);
        assertNotNull(result);
    }

    @Test
    void addBid_validInputs_persistedInRepository() {
        Request request = requestService.addRequest(CLIENT, "Oil Change", "Need oil");
        bidService.addBid(PRO1, request, 75.00);
        assertEquals(1, bidRepo.getAll().size());
    }

    @Test
    void addBid_storesCorrectCost() {
        Request request = requestService.addRequest(CLIENT, "Oil Change", "Need oil");
        bidService.addBid(PRO1, request, 89.99);
        Bid stored = bidRepo.getAll().get(0);
        assertEquals(89.99, stored.getCost(), 0.001);
    }

    @Test
    void addBid_storesCorrectPro() {
        Request request = requestService.addRequest(CLIENT, "Oil Change", "Need oil");
        bidService.addBid(PRO1, request, 75.00);
        Bid stored = bidRepo.getAll().get(0);
        assertEquals(PRO1.getId(), stored.getPro().getId());
    }

    @Test
    void addBid_storesCorrectRequest() {
        Request request = requestService.addRequest(CLIENT, "Oil Change", "Need oil");
        bidService.addBid(PRO1, request, 75.00);
        Bid stored = bidRepo.getAll().get(0);
        assertEquals(request.getId(), stored.getRequest().getId());
    }

    @Test
    void addBid_multipleBids_eachGetsUniqueId() {
        Request request = requestService.addRequest(CLIENT, "Oil Change", "Need oil");
        Bid b1 = bidService.addBid(PRO1, request, 75.00);
        Bid b2 = bidService.addBid(PRO2, request, 90.00);
        assertNotEquals(b1.getId(), b2.getId());
    }

    @Test
    void addBid_zeroCost_isAllowed() {
        Request request = requestService.addRequest(CLIENT, "Free Service", "Complimentary");
        Bid result = bidService.addBid(PRO1, request, 0.00);
        assertNotNull(result);
        assertEquals(0.00, bidRepo.getAll().get(0).getCost(), 0.001);
    }

    @Test
    void addBid_decimalCost_storedPrecisely() {
        Request request = requestService.addRequest(CLIENT, "AC Repair", "AC broken");
        bidService.addBid(PRO1, request, 199.99);
        assertEquals(199.99, bidRepo.getAll().get(0).getCost(), 0.001);
    }

    // ==================== getAllBidsByRequest ====================
    @Test
    void getAllByRequest_noBids_returnsEmptyList() {
        Request request = requestService.addRequest(CLIENT, "Oil Change", "Need oil");
        assertTrue(bidService.getAllByRequest(request).isEmpty());
    }

    @Test
    void getAllByRequest_returnsOnlyBidsForThatRequest() {
        Request r1 = requestService.addRequest(CLIENT, "Oil Change",   "Need oil");
        Request r2 = requestService.addRequest(CLIENT, "Brake Repair", "Need pads");
        bidService.addBid(PRO1, r1, 75.00);
        bidService.addBid(PRO2, r1, 90.00);
        bidService.addBid(PRO1, r2, 100.00);
        List<Bid> bidsForR1 = bidService.getAllByRequest(r1);
        assertEquals(2, bidsForR1.size());
        for (Bid b : bidsForR1) {
            assertEquals(r1.getId(), b.getRequest().getId());
        }
    }

    @Test
    void getAllByRequest_doesNotReturnBidsFromOtherRequests() {
        Request r1 = requestService.addRequest(CLIENT, "Oil Change",   "Need oil");
        Request r2 = requestService.addRequest(CLIENT, "Brake Repair", "Need pads");
        bidService.addBid(PRO1, r2, 100.00);
        assertTrue(bidService.getAllByRequest(r1).isEmpty());
    }

    @Test
    void getAllByRequest_multiplePros_allBidsReturned() {
        Request request = requestService.addRequest(CLIENT, "Oil Change", "Need oil");
        bidService.addBid(PRO1, request, 75.00);
        bidService.addBid(PRO2, request, 90.00);
        assertEquals(2, bidService.getAllByRequest(request).size());
    }

    // ==================== getAllByPro ====================

    @Test
    void getAllByPro_noBids_returnsEmptyList() {
        assertTrue(bidService.getAllByPro(PRO1).isEmpty());
    }

    @Test
    void getAllByPro_returnsOnlyThatProsBids() {
        Request r1 = requestService.addRequest(CLIENT, "Oil Change",   "Need oil");
        Request r2 = requestService.addRequest(CLIENT, "Brake Repair", "Need pads");
        bidService.addBid(PRO1, r1, 75.00);
        bidService.addBid(PRO1, r2, 100.00);
        bidService.addBid(PRO2, r1, 90.00);

        List<Bid> pro1Bids = bidService.getAllByPro(PRO1);
        assertEquals(2, pro1Bids.size());
        for (Bid b : pro1Bids) {
            assertEquals(PRO1.getId(), b.getPro().getId());
        }
    }

    @Test
    void getAllByPro_doesNotReturnOtherProsBids() {
        Request request = requestService.addRequest(CLIENT, "Oil Change", "Need oil");

        bidService.addBid(PRO2, request, 90.00);

        assertTrue(bidService.getAllByPro(PRO1).isEmpty());
    }

    @Test
    void getAllByPro_multipleRequests_allBidsReturned() {
        Request r1 = requestService.addRequest(CLIENT, "Oil Change",   "Need oil");
        Request r2 = requestService.addRequest(CLIENT, "Brake Repair", "Need pads");
        Request r3 = requestService.addRequest(CLIENT, "Tire Rotation","Need rotation");

        bidService.addBid(PRO1, r1, 75.00);
        bidService.addBid(PRO1, r2, 100.00);
        bidService.addBid(PRO1, r3, 50.00);

        assertEquals(3, bidService.getAllByPro(PRO1).size());
    }

    // ==================== getRequestsWithBidCostsByPro ====================

    @Test
    void getRequestsWithBidCostsByPro_noBids_returnsEmptyList() {
        assertTrue(bidService.getRequestsWithBidCostsByPro(PRO1).isEmpty());
    }

    @Test
    void getRequestsWithBidCostsByPro_returnsBidCostOnRequest() {
        Request request = requestService.addRequest(CLIENT, "Oil Change", "Need oil");

        bidService.addBid(PRO1, request, 89.99);

        List<Request> results = bidService.getRequestsWithBidCostsByPro(PRO1);
        assertEquals(1, results.size());
        assertEquals(89.99, results.get(0).getBidCost(), 0.001);
    }

    @Test
    void getRequestsWithBidCostsByPro_multipleRequests_allCostsCorrect() {
        Request r1 = requestService.addRequest(CLIENT, "Brakes",    "Need pads");
        Request r2 = requestService.addRequest(CLIENT, "Oil Change", "Need oil");

        bidService.addBid(PRO1, r1, 130.00);
        bidService.addBid(PRO1, r2,  65.50);

        List<Request> results = bidService.getRequestsWithBidCostsByPro(PRO1);
        assertEquals(2, results.size());

        Request brakes = null;
        Request oil    = null;
        for (Request r : results) {
            if ("Brakes".equals(r.getTitle()))     brakes = r;
            if ("Oil Change".equals(r.getTitle())) oil    = r;
        }

        assertNotNull(brakes);
        assertEquals(130.00, brakes.getBidCost(), 0.001);
        assertNotNull(oil);
        assertEquals(65.50, oil.getBidCost(), 0.001);
    }

    @Test
    void getRequestsWithBidCostsByPro_onlyReturnsThatProsBids() {
        Request request = requestService.addRequest(CLIENT, "Oil Change", "Need oil");

        bidService.addBid(PRO1, request, 75.00);
        bidService.addBid(PRO2, request, 90.00);

        List<Request> pro1Results = bidService.getRequestsWithBidCostsByPro(PRO1);
        assertEquals(1, pro1Results.size());
        assertEquals(75.00, pro1Results.get(0).getBidCost(), 0.001);

        List<Request> pro2Results = bidService.getRequestsWithBidCostsByPro(PRO2);
        assertEquals(1, pro2Results.size());
        assertEquals(90.00, pro2Results.get(0).getBidCost(), 0.001);
    }

    // ==================== bid + request interaction ====================

    @Test
    void addBid_thenRemoveRequest_bidIsAlsoCascadeDeleted() {
        Request request = requestService.addRequest(CLIENT, "Oil Change", "Need oil");
        Bid bid = bidService.addBid(PRO1, request, 75.00);

        requestService.removeRequest(request.getId());

        assertNull(bidRepo.getById(bid.getId()),
                "Bid must be cascade-deleted when its parent request is removed");
    }

    @Test
    void addBid_thenRemoveRequest_doesNotAffectBidsOnOtherRequests() {
        Request r1 = requestService.addRequest(CLIENT, "Oil Change",   "Need oil");
        Request r2 = requestService.addRequest(CLIENT, "Brake Repair", "Need pads");

        Bid bidOnR1 = bidService.addBid(PRO1, r1, 75.00);
        Bid bidOnR2 = bidService.addBid(PRO1, r2, 100.00);

        requestService.removeRequest(r1.getId());

        assertNull(bidRepo.getById(bidOnR1.getId()));
        assertNotNull(bidRepo.getById(bidOnR2.getId()),
                "Bid on other request must not be affected");
    }
}