package com.g08.gig.business.services;

import com.g08.gig.models.Appointment;
import com.g08.gig.models.Client;
import com.g08.gig.models.Pro;
import com.g08.gig.models.Request;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentService {
        Appointment addAppt(LocalDateTime appointmentStart, LocalDateTime appointmentEnd, Pro pro, Client client, Request request);
        List<Appointment> getAllApptsByPro(Pro pro);
        Appointment getApptById(int id);
        List<Appointment> getAllApptsByClient(Client client);
}
