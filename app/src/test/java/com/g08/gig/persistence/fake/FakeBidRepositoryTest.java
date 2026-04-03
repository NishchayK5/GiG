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

import java.time.LocalDateTime;
import java.util.List;

class FakeBidRepositoryTest {

    private FakeBidRepository bidRepo;
    private FakeRequestRepository requestRepo;
    private BidServiceImpl bidService;
    private RequestServiceImpl requestService;

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
        bidRepo = new FakeBidRepository();
        requestRepo = new FakeRequestRepository(bidRepo);
        bidService = new BidServiceImpl(bidRepo);
        requestService = new RequestServiceImpl(requestRepo);
    }

    @Test
    void add_nullBid_returnsNull() {
        assertNull(bidRepo.add(null));
    }

    @Test
    void add_bidWithExistingId_returnsNull() {
        Request request = requestService.addRequest(CLIENT, "Oil Change", "Need oil");
        Bid first = bidService.addBid(PRO1, request, 75.00, LocalDateTime.now());
        Bid duplicate = new Bid(first.getId(), PRO1, request, 50.00, LocalDateTime.now(), false);

        assertNull(bidRepo.add(duplicate));
        assertEquals(1, bidRepo.getAll().size());
    }

    @Test
    void add_bidWithExplicitUnusedNonZeroId_usesThatId() {
        Request request = requestService.addRequest(CLIENT, "Oil Change", "Need oil");
        LocalDateTime time = LocalDateTime.of(2026, 4, 3, 10, 0);
        Bid withId = new Bid(42, PRO1, request, 55.0, time, false);

        Bid stored = bidRepo.add(withId);

        assertNotNull(stored);
        assertEquals(42, stored.getId());
        assertSame(stored, bidRepo.getById(42));
    }

    @Test
    void update_unknownId_returnsFalse() {
        Request request = requestService.addRequest(CLIENT, "Oil Change", "Need oil");
        Bid ghost = new Bid(999, PRO1, request, 1.0, LocalDateTime.now(), false);

        assertFalse(bidRepo.update(ghost));
    }

    @Test
    void getAllByRequest_emptyWhenNoBidsForThatRequest() {
        Request withBids = requestService.addRequest(CLIENT, "Oil Change", "Need oil");
        Request withoutBids = requestService.addRequest(CLIENT, "Brake Repair", "Pads");
        bidService.addBid(PRO1, withBids, 75.00, LocalDateTime.now());

        assertTrue(bidRepo.getAllByRequest(withoutBids).isEmpty());
    }

    @Test
    void getAllByPro_emptyWhenNoBidsForThatPro() {
        Request request = requestService.addRequest(CLIENT, "Oil Change", "Need oil");
        bidService.addBid(PRO1, request, 75.00, LocalDateTime.now());

        assertTrue(bidRepo.getAllByPro(PRO2).isEmpty());
    }

    @Test
    void getAllByPro_matchesByReferenceEquality_notEqualProInstances() {
        Request request = requestService.addRequest(CLIENT, "Oil Change", "Need oil");
        bidService.addBid(PRO1, request, 75.00, LocalDateTime.now());

        Pro sameDataDifferentRef = new Pro(
                PRO1.getId(), PRO1.getEmail(), PRO1.getFirstName(), PRO1.getLastName(),
                PRO1.getPhoneNumber(), "hashed", PRO1.getSkillSet(), PRO1.getCityOfOperation());

        assertTrue(bidRepo.getAllByPro(sameDataDifferentRef).isEmpty());
        assertEquals(1, bidRepo.getAllByPro(PRO1).size());
    }

    @Test
    void delete_unknownId_returnsFalse() {
        assertFalse(bidRepo.delete(999));
    }

    @Test
    void getById_unknownId_returnsNull() {
        assertNull(bidRepo.getById(999));
    }

    @Test
    void addBid_validInputs_returnsNonNull() {
        Request request = requestService.addRequest(CLIENT, "Oil Change", "Need oil");
        Bid result = bidService.addBid(PRO1, request, 75.00, LocalDateTime.now());
        assertNotNull(result);
    }

    @Test
    void addBid_validInputs_persistedInRepository() {
        Request request = requestService.addRequest(CLIENT, "Oil Change", "Need oil");
        bidService.addBid(PRO1, request, 75.00, LocalDateTime.now());
        assertEquals(1, bidRepo.getAll().size());
    }

    @Test
    void addBid_storesCorrectCostAndTimeAndStatus() {
        Request request = requestService.addRequest(CLIENT, "Oil Change", "Need oil");
        LocalDateTime time = LocalDateTime.of(2026, 4, 3, 9, 30);

        bidService.addBid(PRO1, request, 89.99, time);
        Bid stored = bidRepo.getAll().get(0);

        assertEquals(89.99, stored.getCost(), 0.001);
        assertEquals(time, stored.getTimeOfBid());
        assertFalse(stored.getBidStatus());
    }

    @Test
    void addBid_storesCorrectProAndRequest() {
        Request request = requestService.addRequest(CLIENT, "Oil Change", "Need oil");
        bidService.addBid(PRO1, request, 75.00, LocalDateTime.now());
        Bid stored = bidRepo.getAll().get(0);
        assertEquals(PRO1.getId(), stored.getPro().getId());
        assertEquals(request.getId(), stored.getRequest().getId());
    }

    @Test
    void addBid_multipleBids_eachGetsUniqueId() {
        Request request = requestService.addRequest(CLIENT, "Oil Change", "Need oil");
        Bid b1 = bidService.addBid(PRO1, request, 75.00, LocalDateTime.now());
        Bid b2 = bidService.addBid(PRO2, request, 90.00, LocalDateTime.now().plusHours(1));
        assertNotEquals(b1.getId(), b2.getId());
    }

    @Test
    void getAllByRequest_returnsOnlyBidsForThatRequest() {
        Request r1 = requestService.addRequest(CLIENT, "Oil Change", "Need oil");
        Request r2 = requestService.addRequest(CLIENT, "Brake Repair", "Need pads");
        bidService.addBid(PRO1, r1, 75.00, LocalDateTime.now());
        bidService.addBid(PRO2, r1, 90.00, LocalDateTime.now().plusHours(1));
        bidService.addBid(PRO1, r2, 100.00, LocalDateTime.now().plusHours(2));

        List<Bid> bidsForR1 = bidService.getAllByRequest(r1);
        assertEquals(2, bidsForR1.size());
        for (Bid b : bidsForR1) {
            assertSame(r1, b.getRequest());
        }
    }

    @Test
    void getAllByPro_returnsOnlyThatProsBids() {
        Request r1 = requestService.addRequest(CLIENT, "Oil Change", "Need oil");
        Request r2 = requestService.addRequest(CLIENT, "Brake Repair", "Need pads");
        bidService.addBid(PRO1, r1, 75.00, LocalDateTime.now());
        bidService.addBid(PRO1, r2, 100.00, LocalDateTime.now().plusHours(1));
        bidService.addBid(PRO2, r1, 90.00, LocalDateTime.now().plusHours(2));

        List<Bid> pro1Bids = bidService.getAllByPro(PRO1);
        assertEquals(2, pro1Bids.size());
        for (Bid b : pro1Bids) {
            assertSame(PRO1, b.getPro());
        }
    }

    @Test
    void update_existingBid_persistsChangedAcceptedStatus() {
        Request request = requestService.addRequest(CLIENT, "Oil Change", "Need oil");
        Bid stored = bidService.addBid(PRO1, request, 75.00, LocalDateTime.now());

        stored.acceptBid();
        boolean updated = bidRepo.update(stored);

        assertTrue(updated);
        assertTrue(bidRepo.getById(stored.getId()).getBidStatus());
    }

    @Test
    void update_nullBid_returnsFalse() {
        assertFalse(bidRepo.update(null));
    }

    @Test
    void addBid_thenRemoveRequest_bidIsAlsoCascadeDeleted() {
        Request request = requestService.addRequest(CLIENT, "Oil Change", "Need oil");
        Bid bid = bidService.addBid(PRO1, request, 75.00, LocalDateTime.now());

        requestService.removeRequest(request.getId());

        assertNull(bidRepo.getById(bid.getId()));
    }

    @Test
    void addBid_thenRemoveRequest_doesNotAffectBidsOnOtherRequests() {
        Request r1 = requestService.addRequest(CLIENT, "Oil Change", "Need oil");
        Request r2 = requestService.addRequest(CLIENT, "Brake Repair", "Need pads");

        Bid bidOnR1 = bidService.addBid(PRO1, r1, 75.00, LocalDateTime.now());
        Bid bidOnR2 = bidService.addBid(PRO1, r2, 100.00, LocalDateTime.now().plusHours(1));

        requestService.removeRequest(r1.getId());

        assertNull(bidRepo.getById(bidOnR1.getId()));
        assertNotNull(bidRepo.getById(bidOnR2.getId()));
    }

    @Test
    void getAll_returnsUnmodifiableList() {
        Request request = requestService.addRequest(CLIENT, "Oil Change", "Need oil");
        bidService.addBid(PRO1, request, 75.00, LocalDateTime.now());

        assertThrows(UnsupportedOperationException.class, () ->
                bidRepo.getAll().add(new Bid(99, PRO1, request, 10.0, LocalDateTime.now(), false)));
    }
    @Test
    void getById_returnsNullWhenRepoHasOnlyNonMatchingBids() {
        Request request1 = new Request(
                10,
                CLIENT,
                "Job A",
                "Body A",
                LocalDateTime.now(),
                true
        );
        Request request2 = new Request(
                11,
                CLIENT,
                "Job B",
                "Body B",
                LocalDateTime.now(),
                true
        );

        LocalDateTime time = LocalDateTime.now();
        bidRepo.add(new Bid(10, PRO1, request1, 100.0, time, false));
        bidRepo.add(new Bid(20, PRO2, request2, 200.0, time.plusHours(1), false));

        Bid result = bidRepo.getById(999);

        assertNull(result);
    }
    @Test
    void delete_firstMatchingId_removesImmediately() {
        Request request = requestService.addRequest(CLIENT, "Oil Change", "Need oil");
        Bid first = bidService.addBid(PRO1, request, 75.00, LocalDateTime.now());
        bidService.addBid(PRO2, request, 90.00, LocalDateTime.now().plusHours(1));

        assertTrue(bidRepo.delete(first.getId()));
        assertNull(bidRepo.getById(first.getId()));
        assertEquals(1, bidRepo.getAll().size());
    }

    @Test
    void update_whenMultipleBids_updatesCorrectEntry() {
        Request request = requestService.addRequest(CLIENT, "Oil Change", "Need oil");
        bidService.addBid(PRO1, request, 75.00, LocalDateTime.now());
        Bid target = bidService.addBid(PRO2, request, 90.00, LocalDateTime.now().plusHours(1));

        Bid replacement = new Bid(
                target.getId(), PRO2, request, 120.0, target.getTimeOfBid(), true);

        assertTrue(bidRepo.update(replacement));
        assertEquals(120.0, bidRepo.getById(target.getId()).getCost(), 0.001);
        assertTrue(bidRepo.getById(target.getId()).getBidStatus());
    }

    @Test
    void delete_checksNonMatchingBidBeforeDeletingMatchingBid() {
        Request request1 = new Request(
                10,
                CLIENT,
                "Job A",
                "Body A",
                LocalDateTime.now(),
                true
        );
        Request request2 = new Request(
                11,
                CLIENT,
                "Job B",
                "Body B",
                LocalDateTime.now(),
                true
        );

        LocalDateTime time = LocalDateTime.now();
        bidRepo.add(new Bid(10, PRO1, request1, 100.0, time, false));
        bidRepo.add(new Bid(20, PRO2, request2, 200.0, time.plusHours(1), false));

        boolean deleted = bidRepo.delete(20);

        assertTrue(deleted);
        assertNotNull(bidRepo.getById(10));
        assertNull(bidRepo.getById(20));
        assertEquals(1, bidRepo.getAll().size());
    }

}
