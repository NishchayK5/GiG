package com.g08.gig.persistence.fake;

import static org.junit.jupiter.api.Assertions.*;

import com.g08.gig.business.validation.ValidationException;
import com.g08.gig.models.Bid;
import com.g08.gig.models.Client;
import com.g08.gig.models.Pro;
import com.g08.gig.models.Request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.g08.gig.business.services.BidServiceImpl;
import com.g08.gig.business.services.RequestServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

class FakeRequestRepositoryTest {

    private FakeBidRepository     bidRepo;
    private FakeRequestRepository requestRepo;
    private RequestServiceImpl    service;

    private static final Client CLIENT = new Client(
            1, "client@test.com", "Dayaveer", "Gill", "2041111111", "123 Main St", "hashed");

    private static final Client CLIENT2 = new Client(
            2, "client2@test.com", "Adejare", "Taiwo", "2042111111", "456 Oak Ave", "hashed");

    private static final Pro PRO = new Pro(
            10, "pro1@test.com", "Izzy", "Elskamp", "2042222222",
            "hashed", new String[]{"Oil Change", "Brakes"}, "Winnipeg");


    @BeforeEach
    void setUp() {
        bidRepo     = new FakeBidRepository();
        requestRepo = new FakeRequestRepository(bidRepo);
        service     = new RequestServiceImpl(requestRepo);
    }

    // ==================== addRequest ====================

    @Test
    void add_requestNull_returnsNull() {
        assertNull(requestRepo.add(null));
    }

    @Test
    void add_requestWithExistingId_returnsNull() {
        Request first = service.addRequest(CLIENT, "Oil Change", "Body");
        Request duplicate = new Request(first.getId(), first.getClient(), "Other", "Body2", first.getTimeOfRequest(), first.getRequestStatus());
        assertNull(requestRepo.add(duplicate));
        assertEquals(1, requestRepo.getAll().size());
    }

    @Test
    void update_requestNull_returnsFalse() {
        assertFalse(requestRepo.update(null));
    }

    @Test
    void update_requestIdNotExists_returnsFalse() {
        Request request = new Request(999, CLIENT, "Title", "Body", LocalDateTime.now(), true);
        assertFalse(requestRepo.update(request));
    }

    @Test
    void delete_unknownId_returnsFalse() {
        assertFalse(requestRepo.delete(999));
    }

    @Test
    void addRequest_validInputs_returnsRequestWithGeneratedId() {
        Request result = service.addRequest(CLIENT, "Oil Change", "Need oil");
        assertNotNull(result);
        assertTrue(result.getId() > 0);
    }

    @Test
    void addRequest_validInputs_persistedInRepository() {
        service.addRequest(CLIENT, "Oil Change", "Need oil");

        assertEquals(1, requestRepo.getAll().size());
    }

    @Test
    void addRequest_newRequest_isActiveByDefault() {
        Request result = service.addRequest(CLIENT, "Oil Change", "Need oil");

        assertTrue(result.getRequestStatus());
    }

    @Test
    void addRequest_storesCorrectTitleAndBody() {
        Request result = service.addRequest(CLIENT, "Oil Change", "Need oil");

        assertEquals("Oil Change", result.getTitle());
        assertEquals("Need oil",   result.getBody());
    }

    @Test
    void addRequest_storesCorrectClient() {
        Request result = service.addRequest(CLIENT, "Oil Change", "Need oil");

        assertEquals(CLIENT.getId(), result.getClient().getId());
    }

    @Test
    void addRequest_trimsTitleWhitespace() {
        Request result = service.addRequest(CLIENT, "  Oil Change  ", "Need oil");

        assertEquals("Oil Change", result.getTitle());
    }

    @Test
    void addRequest_trimsBodyWhitespace() {
        Request result = service.addRequest(CLIENT, "Oil Change", "  Need oil  ");

        assertEquals("Need oil", result.getBody());
    }

    @Test
    void addRequest_nullTitle_throwsValidationException() {
        assertThrows(ValidationException.class, () ->
                service.addRequest(CLIENT, null, "Need oil"));
    }

    @Test
    void addRequest_emptyTitle_throwsValidationException() {
        assertThrows(ValidationException.class, () ->
                service.addRequest(CLIENT, "", "Need oil"));
    }

    @Test
    void addRequest_whitespaceOnlyTitle_throwsValidationException() {
        assertThrows(ValidationException.class, () ->
                service.addRequest(CLIENT, "   ", "Need oil"));
    }

    @Test
    void addRequest_nullBody_throwsValidationException() {
        assertThrows(ValidationException.class, () ->
                service.addRequest(CLIENT, "Oil Change", null));
    }

    @Test
    void addRequest_emptyBody_throwsValidationException() {
        assertThrows(ValidationException.class, () ->
                service.addRequest(CLIENT, "Oil Change", ""));
    }

    @Test
    void addRequest_whitespaceOnlyBody_throwsValidationException() {
        assertThrows(ValidationException.class, () ->
                service.addRequest(CLIENT, "Oil Change", "   "));
    }

    @Test
    void addRequest_multipleRequests_eachGetsUniqueId() {
        Request r1 = service.addRequest(CLIENT, "Oil Change",    "Body");
        Request r2 = service.addRequest(CLIENT, "Brake Repair",  "Body");
        Request r3 = service.addRequest(CLIENT, "Tire Rotation", "Body");

        assertNotEquals(r1.getId(), r2.getId());
        assertNotEquals(r2.getId(), r3.getId());
        assertNotEquals(r1.getId(), r3.getId());
    }

    // ==================== getAllRequests ====================

    @Test
    void getAllRequests_emptyRepo_returnsEmptyList() {
        assertTrue(service.getAllRequests().isEmpty());
    }

    @Test
    void getAllRequests_multipleRequests_returnsAll() {
        service.addRequest(CLIENT,       "Oil Change",    "Body");
        service.addRequest(CLIENT,       "Brake Repair",  "Body");
        service.addRequest(CLIENT2, "Tire Rotation", "Body");

        assertEquals(3, service.getAllRequests().size());
    }

    // ==================== getRequestsByClient ====================

    @Test
    void getRequestsByClient_returnsOnlyThatClientsRequests() {
        service.addRequest(CLIENT,       "Oil Change",    "Body");
        service.addRequest(CLIENT,       "Brake Repair",  "Body");
        service.addRequest(CLIENT2, "Tire Rotation", "Body");

        List<Request> result = service.getRequestsByClient(CLIENT.getId());

        assertEquals(2, result.size());
        for (Request r : result) {
            assertEquals(CLIENT.getId(), r.getClient().getId());
        }
    }

    @Test
    void getRequestsByClient_noRequestsForClient_returnsEmptyList() {
        service.addRequest(CLIENT2, "Oil Change", "Body");

        assertTrue(service.getRequestsByClient(CLIENT.getId()).isEmpty());
    }

    @Test
    void getRequestsByClient_unknownId_returnsEmptyList() {
        assertTrue(service.getRequestsByClient(999).isEmpty());
    }

    // ==================== getRequestsByStatus ====================

    @Test
    void getRequestsByStatus_activeTrue_returnsOnlyActiveRequests() {
        service.addRequest(CLIENT, "Active A", "Body");
        service.addRequest(CLIENT, "Active B", "Body");
        Request toClose = service.addRequest(CLIENT, "Will Close", "Body");
        service.completeRequest(toClose.getId());

        List<Request> active = service.getRequestsByStatus(true);

        assertEquals(2, active.size());
        for (Request r : active) {
            assertTrue(r.getRequestStatus());
        }
    }

    @Test
    void getRequestsByStatus_activeFalse_returnsOnlyInactiveRequests() {
        service.addRequest(CLIENT, "Still Active", "Body");
        Request toClose = service.addRequest(CLIENT, "Will Close", "Body");
        service.completeRequest(toClose.getId());

        List<Request> inactive = service.getRequestsByStatus(false);

        assertEquals(1, inactive.size());
        assertFalse(inactive.get(0).getRequestStatus());
    }

    @Test
    void getRequestsByStatus_noMatch_returnsEmptyList() {
        service.addRequest(CLIENT, "Active A", "Body");

        assertTrue(service.getRequestsByStatus(false).isEmpty());
    }

    // ==================== completeRequest ====================

    @Test
    void completeRequest_setsStatusToInactive() {
        Request request = service.addRequest(CLIENT, "Oil Change", "Need oil");

        service.completeRequest(request.getId());

        assertFalse(requestRepo.getById(request.getId()).getRequestStatus());
    }

    @Test
    void completeRequest_doesNotDeleteRequest() {
        Request request = service.addRequest(CLIENT, "Oil Change", "Need oil");

        service.completeRequest(request.getId());

        assertNotNull(requestRepo.getById(request.getId()));
    }

    @Test
    void completeRequest_onlyAffectsTargetRequest() {
        Request r1 = service.addRequest(CLIENT, "Oil Change",   "Body");
        Request r2 = service.addRequest(CLIENT, "Brake Repair", "Body");

        service.completeRequest(r1.getId());

        assertFalse(requestRepo.getById(r1.getId()).getRequestStatus());
        assertTrue(requestRepo.getById(r2.getId()).getRequestStatus());
    }

    // ==================== removeRequest ====================

    @Test
    void removeRequest_deletesFromRepository() {
        Request request = service.addRequest(CLIENT, "Oil Change", "Body");
        int id = request.getId();

        service.removeRequest(id);

        assertNull(requestRepo.getById(id));
    }

    @Test
    void removeRequest_onlyDeletesTargetRequest() {
        Request r1 = service.addRequest(CLIENT, "Oil Change",   "Body");
        Request r2 = service.addRequest(CLIENT, "Brake Repair", "Body");

        service.removeRequest(r1.getId());

        assertNull(requestRepo.getById(r1.getId()));
        assertNotNull(requestRepo.getById(r2.getId()));
    }

    @Test
    void removeRequest_cascadesAndDeletesAssociatedBids() {
        Request request = service.addRequest(CLIENT, "Oil Change", "Body");
        Bid bid = bidRepo.add(new Bid(0, PRO, requestRepo.getById(request.getId()), 75.00, LocalDateTime.now(), false));

        service.removeRequest(request.getId());

        assertNull(bidRepo.getById(bid.getId()),
                "Bid must be cascade-deleted when its parent request is removed");
    }

    @Test
    void removeRequest_cascadeDoesNotAffectBidsOnOtherRequests() {
        Request r1 = service.addRequest(CLIENT, "Oil Change",   "Body");
        Request r2 = service.addRequest(CLIENT, "Brake Repair", "Body");
        Bid bidOnR1 = bidRepo.add(new Bid(0, PRO, requestRepo.getById(r1.getId()), 75.00,  LocalDateTime.now(), false));
        Bid bidOnR2 = bidRepo.add(new Bid(0, PRO, requestRepo.getById(r2.getId()), 100.00, LocalDateTime.now(), false));

        service.removeRequest(r1.getId());

        assertNull(bidRepo.getById(bidOnR1.getId()));
        assertNotNull(bidRepo.getById(bidOnR2.getId()),
                "Bid on other request must not be affected");
    }
}