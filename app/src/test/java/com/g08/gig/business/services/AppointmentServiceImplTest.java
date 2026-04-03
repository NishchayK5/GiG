package com.g08.gig.business.services;

import com.g08.gig.models.Appointment;
import com.g08.gig.models.Client;
import com.g08.gig.models.Pro;
import com.g08.gig.models.Request;
import com.g08.gig.persistence.AppointmentRepository;

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
class AppointmentServiceImplTest {

    @Mock
    private AppointmentRepository repo;

    @InjectMocks
    private AppointmentServiceImpl service;

    private Client client;
    private Pro pro;
    private Request request;
    private LocalDateTime start;
    private LocalDateTime end;

    @BeforeEach
    void setUp() {
        client = new Client(
                1,
                "client@test.com",
                "Dayaveer",
                "Gill",
                "2041111111",
                "123 Main St",
                "hashedPass"
        );

        pro = new Pro(
                2,
                "pro@test.com",
                "Izzy",
                "Elskamp",
                "2042222222",
                "hashedPass",
                new String[]{"Electrical", "Wiring"},
                "Winnipeg"
        );

        request = new Request(
                3,
                client,
                "Fix Outlet",
                "Need help with an outlet",
                LocalDateTime.now(),
                true
        );

        start = LocalDateTime.of(2026, 4, 10, 9, 0);
        end = LocalDateTime.of(2026, 4, 10, 10, 0);
    }

    @Test
    void addAppt_validInput_createsAppointmentAndReturnsStoredAppointment() {
        Appointment stored = new Appointment(10, start, end, pro, client, request);
        when(repo.add(any(Appointment.class))).thenReturn(stored);

        Appointment result = service.addAppt(start, end, pro, client, request);

        assertNotNull(result);
        assertEquals(10, result.getId());
        assertEquals(start, result.getAppointmentStart());
        assertEquals(end, result.getAppointmentEnd());
        assertEquals(pro, result.getPro());
        assertEquals(client, result.getClient());
        assertEquals(request, result.getRequest());

        ArgumentCaptor<Appointment> captor = ArgumentCaptor.forClass(Appointment.class);
        verify(repo).add(captor.capture());

        Appointment passed = captor.getValue();
        assertEquals(0, passed.getId());
        assertEquals(start, passed.getAppointmentStart());
        assertEquals(end, passed.getAppointmentEnd());
        assertEquals(pro, passed.getPro());
        assertEquals(client, passed.getClient());
        assertEquals(request, passed.getRequest());
    }

    @Test
    void getAllApptsByPro_returnsRepoResults() {
        Appointment a1 = new Appointment(1, start, end, pro, client, request);
        Appointment a2 = new Appointment(2, start.plusDays(1), end.plusDays(1), pro, client, request);

        when(repo.getAllByPro(pro)).thenReturn(List.of(a1, a2));

        List<Appointment> result = service.getAllApptsByPro(pro);

        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals(2, result.get(1).getId());
        verify(repo).getAllByPro(pro);
    }

    @Test
    void getAllApptsByClient_returnsRepoResults() {
        Appointment a1 = new Appointment(1, start, end, pro, client, request);
        Appointment a2 = new Appointment(2, start.plusDays(2), end.plusDays(2), pro, client, request);

        when(repo.getAllByClient(client)).thenReturn(List.of(a1, a2));

        List<Appointment> result = service.getAllApptsByClient(client);

        assertEquals(2, result.size());
        assertEquals(client, result.get(0).getClient());
        assertEquals(client, result.get(1).getClient());
        verify(repo).getAllByClient(client);
    }

    @Test
    void getApptById_existingId_returnsAppointment() {
        Appointment stored = new Appointment(7, start, end, pro, client, request);
        when(repo.getById(7)).thenReturn(stored);

        Appointment result = service.getApptById(7);

        assertNotNull(result);
        assertEquals(7, result.getId());
        verify(repo).getById(7);
    }

    @Test
    void getApptById_missingId_returnsNull() {
        when(repo.getById(999)).thenReturn(null);

        Appointment result = service.getApptById(999);

        assertNull(result);
        verify(repo).getById(999);
    }
}