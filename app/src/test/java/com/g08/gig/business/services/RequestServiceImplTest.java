package com.g08.gig.business.services;

import android.util.Pair;

import com.g08.gig.business.validation.ValidationException;
import com.g08.gig.models.Client;
import com.g08.gig.models.Request;
import com.g08.gig.persistence.RequestRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {

    @Mock
    private RequestRepository repo;

    @InjectMocks
    private RequestServiceImpl service;

    private Client client;

    @BeforeEach
    void setUp() {
        client = new Client(
                1,
                "client@email.com",
                "Sarah",
                "Jones",
                "2045551111",
                "123 Main St",
                "hashedPass"
        );
    }

    @Test
    void addRequest_validInput_trimsAndReturnsStoredRequest() {
        Request stored = new Request(
                10,
                client,
                "Brake Repair",
                "Need front pads changed",
                LocalDateTime.now(),
                true
        );

        when(repo.add(any(Request.class))).thenReturn(stored);

        Request result = service.addRequest(client, "  Brake Repair  ", "  Need front pads changed  ");

        assertNotNull(result);
        assertEquals(10, result.getId());
        assertEquals("Brake Repair", result.getTitle());
        assertEquals("Need front pads changed", result.getBody());

        ArgumentCaptor<Request> captor = ArgumentCaptor.forClass(Request.class);
        verify(repo).add(captor.capture());

        Request passed = captor.getValue();
        assertEquals(client, passed.getClient());
        assertEquals("Brake Repair", passed.getTitle());
        assertEquals("Need front pads changed", passed.getBody());
        assertTrue(passed.getRequestStatus());
        assertNotNull(passed.getTimeOfRequest());
    }

    @Test
    void addRequest_blankTitle_throwsValidationException() {
        assertThrows(ValidationException.class, () ->
                service.addRequest(client, "   ", "Valid body"));

        verify(repo, never()).add(any());
    }

    @Test
    void addRequest_blankBody_throwsValidationException() {
        assertThrows(ValidationException.class, () ->
                service.addRequest(client, "Valid title", "   "));

        verify(repo, never()).add(any());
    }

    @Test
    void getAllRequests_returnsRepoResults() {
        List<Request> requests = List.of(
                new Request(1, client, "A", "Body A", LocalDateTime.now(), true),
                new Request(2, client, "B", "Body B", LocalDateTime.now(), false)
        );

        when(repo.getAll()).thenReturn(requests);

        List<Request> result = service.getAllRequests();

        assertEquals(2, result.size());
        assertEquals("A", result.get(0).getTitle());
        assertEquals("B", result.get(1).getTitle());
        verify(repo).getAll();
    }

    @Test
    void getRequestsByClient_returnsRepoResults() {
        List<Request> requests = List.of(
                new Request(1, client, "Req 1", "Body 1", LocalDateTime.now(), true),
                new Request(2, client, "Req 2", "Body 2", LocalDateTime.now(), false)
        );

        when(repo.getByClient(1)).thenReturn(requests);

        List<Request> result = service.getRequestsByClient(1);

        assertEquals(2, result.size());
        verify(repo).getByClient(1);
    }

    @Test
    void getRequestsByClientCurrentAndPast_splitsActiveAndInactiveRequests() {
        Request current1 = new Request(1, client, "Current 1", "Body", LocalDateTime.now(), true);
        Request current2 = new Request(2, client, "Current 2", "Body", LocalDateTime.now(), true);
        Request past1 = new Request(3, client, "Past 1", "Body", LocalDateTime.now(), false);

        when(repo.getByClient(1)).thenReturn(List.of(current1, current2, past1));

        Pair<List<Request>, List<Request>> result = service.getRequestsByClientCurrentAndPast(1);
        //Pair<List<Request>, List<Request>> result = service.getRequestsByClientCurrentAndPast(1);



        assertNotNull(result);
        System.out.println("first is null? " + (result.first == null));
        System.out.println("second is null? " + (result.second == null));

        if (result.first != null) {
            System.out.println("first size = " + result.first.size());
            for (Request r : result.first) {
                System.out.println("first item title = " + r.getTitle());
            }
        }

        if (result.second != null) {
            System.out.println("second size = " + result.second.size());
            for (Request r : result.second) {
                System.out.println("second item title = " + r.getTitle());
            }
        }
        assertNotNull(result.first);
        assertNotNull(result.second);

        assertEquals(2, result.first.size());
        assertEquals(1, result.second.size());
        assertEquals("Current 1", result.first.get(0).getTitle());
        assertEquals("Current 2", result.first.get(1).getTitle());
        assertEquals("Past 1", result.second.get(0).getTitle());

        verify(repo).getByClient(1);
    }

    @Test
    void getRequestsByStatus_returnsRepoResults() {
        List<Request> activeRequests = List.of(
                new Request(1, client, "Active", "Body", LocalDateTime.now(), true)
        );

        when(repo.getAllWithStatus(true)).thenReturn(activeRequests);

        List<Request> result = service.getRequestsByStatus(true);

        assertEquals(1, result.size());
        assertEquals("Active", result.get(0).getTitle());
        verify(repo).getAllWithStatus(true);
    }

    @Test
    void completeRequest_existingRequest_expiresRequestAndUpdatesRepo() {
        Request existing = new Request(
                5,
                client,
                "Oil Change",
                "Need oil",
                LocalDateTime.now(),
                true
        );

        when(repo.getById(5)).thenReturn(existing);
        when(repo.update(any(Request.class))).thenReturn(true);

        service.completeRequest(5);

        ArgumentCaptor<Request> captor = ArgumentCaptor.forClass(Request.class);
        verify(repo).update(captor.capture());

        Request updated = captor.getValue();
        assertFalse(updated.getRequestStatus());
        assertEquals(5, updated.getId());
        assertEquals("Oil Change", updated.getTitle());
    }

    @Test
    void removeRequest_callsDeleteOnRepository() {
        service.removeRequest(9);

        verify(repo).delete(9);
    }
}