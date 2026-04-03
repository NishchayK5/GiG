package com.g08.gig.persistence.fake;

import com.g08.gig.models.Appointment;
import com.g08.gig.models.Client;
import com.g08.gig.models.Pro;
import com.g08.gig.models.Request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FakeAppointmentRepositoryTest {

    private FakeAppointmentRepository repo;

    private Client client1;
    private Client client2;
    private Pro pro1;
    private Pro pro2;
    private Request request1;
    private Request request2;
    private Request request3;
    private LocalDateTime start;
    private LocalDateTime end;

    @BeforeEach
    void setUp() {
        repo = new FakeAppointmentRepository();

        client1 = new Client(1, "client1@test.com", "Dayaveer", "Gill", "2041111111", "123 Main St", "pass");
        client2 = new Client(2, "client2@test.com", "Izzy", "Elskamp", "2042222222", "456 Main St", "pass");

        pro1 = new Pro(10, "pro1@test.com", "Nishchay", "Kathuria", "2043333333", "pass",
                new String[]{"Electrical"}, "Winnipeg");
        pro2 = new Pro(11, "pro2@test.com", "Adejare", "Taiwo", "2044444444", "pass",
                new String[]{"Plumbing"}, "Winnipeg");

        request1 = new Request(100, client1, "Fix outlet", "Need help", LocalDateTime.now(), true);
        request2 = new Request(101, client1, "Install light", "Need install", LocalDateTime.now(), true);
        request3 = new Request(102, client2, "Pipe repair", "Leak", LocalDateTime.now(), true);

        start = LocalDateTime.of(2026, 4, 10, 9, 0);
        end = LocalDateTime.of(2026, 4, 10, 10, 0);
    }

    @Test
    void add_nullAppointment_returnsNull() {
        assertNull(repo.add(null));
    }

    @Test
    void add_withIdZero_assignsNewIdAndStoresAppointment() {
        Appointment appointment = new Appointment(0, start, end, pro1, client1, request1);

        Appointment added = repo.add(appointment);

        assertNotNull(added);
        assertTrue(added.getId() > 0);
        assertEquals(start, added.getAppointmentStart());
        assertEquals(end, added.getAppointmentEnd());
        assertEquals(pro1, added.getPro());
        assertEquals(client1, added.getClient());
        assertEquals(request1, added.getRequest());
        assertEquals(1, repo.getAll().size());
    }

    @Test
    void add_withExplicitUniqueId_storesAppointment() {
        Appointment appointment = new Appointment(50, start, end, pro1, client1, request1);

        Appointment added = repo.add(appointment);

        assertNotNull(added);
        assertEquals(50, added.getId());
        assertEquals(1, repo.getAll().size());
    }

    @Test
    void add_duplicateId_returnsNull() {
        Appointment first = repo.add(new Appointment(50, start, end, pro1, client1, request1));
        Appointment duplicate = repo.add(new Appointment(50, start.plusDays(1), end.plusDays(1), pro2, client2, request2));

        assertNotNull(first);
        assertNull(duplicate);
        assertEquals(1, repo.getAll().size());
    }

    @Test
    void getById_existingId_returnsAppointment() {
        Appointment added = repo.add(new Appointment(0, start, end, pro1, client1, request1));

        Appointment found = repo.getById(added.getId());

        assertNotNull(found);
        assertEquals(added.getId(), found.getId());
    }

    @Test
    void getById_missingId_returnsNull() {
        assertNull(repo.getById(999));
    }

    @Test
    void delete_existingId_removesAppointment() {
        Appointment added = repo.add(new Appointment(0, start, end, pro1, client1, request1));

        boolean deleted = repo.delete(added.getId());

        assertTrue(deleted);
        assertNull(repo.getById(added.getId()));
        assertTrue(repo.getAll().isEmpty());
    }

    @Test
    void delete_missingId_returnsFalse() {
        assertFalse(repo.delete(999));
    }

    @Test
    void getAllByPro_returnsOnlyMatchingAppointments() {
        Appointment a1 = repo.add(new Appointment(0, start, end, pro1, client1, request1));
        Appointment a2 = repo.add(new Appointment(0, start.plusDays(1), end.plusDays(1), pro1, client1, request2));
        repo.add(new Appointment(0, start.plusDays(2), end.plusDays(2), pro2, client2, request3));

        List<Appointment> result = repo.getAllByPro(pro1);

        assertEquals(2, result.size());
        assertEquals(pro1, result.get(0).getPro());
        assertEquals(pro1, result.get(1).getPro());
        assertEquals(a1.getId(), result.get(0).getId());
        assertEquals(a2.getId(), result.get(1).getId());
    }

    @Test
    void getAllByPro_noMatches_returnsEmptyList() {
        repo.add(new Appointment(0, start, end, pro2, client2, request3));

        List<Appointment> result = repo.getAllByPro(pro1);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllByClient_returnsOnlyMatchingAppointments() {
        Appointment a1 = repo.add(new Appointment(0, start, end, pro1, client1, request1));
        Appointment a2 = repo.add(new Appointment(0, start.plusDays(1), end.plusDays(1), pro2, client1, request2));
        repo.add(new Appointment(0, start.plusDays(2), end.plusDays(2), pro2, client2, request3));

        List<Appointment> result = repo.getAllByClient(client1);

        assertEquals(2, result.size());
        assertEquals(client1, result.get(0).getClient());
        assertEquals(client1, result.get(1).getClient());
        assertEquals(a1.getId(), result.get(0).getId());
        assertEquals(a2.getId(), result.get(1).getId());
    }

    @Test
    void getAllByClient_noMatches_returnsEmptyList() {
        repo.add(new Appointment(0, start, end, pro2, client2, request3));

        List<Appointment> result = repo.getAllByClient(client1);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAppointmentOfRequest_existingRequest_returnsAppointment() {
        Appointment added = repo.add(new Appointment(0, start, end, pro1, client1, request1));

        Appointment found = repo.getAppointmentOfRequest(request1);

        assertNotNull(found);
        assertEquals(added.getId(), found.getId());
        assertEquals(request1, found.getRequest());
    }

    @Test
    void getAppointmentOfRequest_missingRequest_returnsNull() {
        repo.add(new Appointment(0, start, end, pro1, client1, request1));

        Appointment found = repo.getAppointmentOfRequest(request2);

        assertNull(found);
    }

    @Test
    void getAll_returnsUnmodifiableList() {
        repo.add(new Appointment(0, start, end, pro1, client1, request1));

        assertThrows(UnsupportedOperationException.class, () ->
                repo.getAll().add(new Appointment(99, start, end, pro1, client1, request1)));
    }
    @Test
    void delete_checksNonMatchingAppointmentBeforeDeletingMatchingOne() {
        Appointment first = repo.add(new Appointment(10, start, end, pro1, client1, request1));
        Appointment second = repo.add(new Appointment(20, start.plusDays(1), end.plusDays(1), pro2, client2, request2));

        boolean deleted = repo.delete(20);

        assertTrue(deleted);
        assertNotNull(repo.getById(10));
        assertNull(repo.getById(20));
        assertEquals(1, repo.getAll().size());
        assertEquals(10, repo.getAll().get(0).getId());
    }

    @Test
    void getById_returnsNullAfterCheckingExistingNonMatchingAppointments() {
        repo.add(new Appointment(10, start, end, pro1, client1, request1));
        repo.add(new Appointment(20, start.plusDays(1), end.plusDays(1), pro2, client2, request2));

        Appointment found = repo.getById(999);

        assertNull(found);
    }
}