package com.g08.gig.models;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AppointmentTest {

    @Test
    void constructor_setsAllFields() {
        LocalDateTime start = LocalDateTime.of(2025, 7, 1, 10, 0);
        LocalDateTime end = LocalDateTime.of(2025, 7, 1, 11, 0);
        Pro pro = new Pro(1, "carlos.mendez@autocare.ca", "Carlos", "Mendez", "2045553232",
                new String[]{"Oil Change", "Brake Service"}, "Winnipeg");
        Client client = new Client(2, "olivia.hall@email.com", "Olivia", "Hall", "2045554343",
                "75 Sargent Avenue, Winnipeg", null);

        Appointment appointment = new Appointment(3, start, end, pro, client);

        assertEquals(3, appointment.getId());
        assertEquals(start, appointment.getAppointmentStart());
        assertEquals(end, appointment.getAppointmentEnd());
        assertSame(pro, appointment.getPro());
        assertSame(client, appointment.getClient());
    }

    @Test
    void setters_updateFields() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(1);
        Pro pro = new Pro(1, "mike.foster@garage.ca", "Mike", "Foster", "2045555454",
                new String[]{"AC Repair"}, "Winnipeg");
        Client client = new Client(2, "sophia.nguyen@gmail.com", "Sophia", "Nguyen", "2045556565",
                "120 Osborne Street, Winnipeg", null);

        Appointment appointment = new Appointment(1, start, end, pro, client);

        appointment.setId(2);
        assertEquals(2, appointment.getId());

        LocalDateTime newStart = LocalDateTime.of(2025, 8, 15, 14, 0);
        appointment.setAppointmentStart(newStart);
        assertEquals(newStart, appointment.getAppointmentStart());

        LocalDateTime newEnd = LocalDateTime.of(2025, 8, 15, 15, 0);
        appointment.setAppointmentEnd(newEnd);
        assertEquals(newEnd, appointment.getAppointmentEnd());

        Pro newPro = new Pro(3, "derek.ross@mechanics.ca", "Derek", "Ross", "2045557676",
                new String[]{"Engine Diagnostics"}, "Brandon");
        appointment.setPro(newPro);
        assertSame(newPro, appointment.getPro());

        Client newClient = new Client(4, "emma.taylor@outlook.com", "Emma", "Taylor", "2045558787",
                "88 Colony Street, Winnipeg", null);
        appointment.setClient(newClient);
        assertSame(newClient, appointment.getClient());
    }
}
