package com.g08.gig.business.services;

import com.g08.gig.models.Appointment;
import com.g08.gig.models.Bid;
import com.g08.gig.models.Client;
import com.g08.gig.models.Pro;
import com.g08.gig.models.Request;
import com.g08.gig.persistence.AppointmentRepository;

import java.time.LocalDateTime;
import java.util.List;

public class AppointmentServiceImpl implements AppointmentService{

    private final AppointmentRepository apptRepo;

    public AppointmentServiceImpl(AppointmentRepository apptRepo) { this.apptRepo = apptRepo; };
    @Override
    public Appointment addAppt(LocalDateTime appointmentStart, LocalDateTime appointmentEnd, Pro pro, Client client, Request request) {

        Appointment toCreate = new Appointment(0, appointmentStart, appointmentEnd, pro, client, request);
        return apptRepo.add(toCreate);
    }

    @Override
    public List<Appointment> getAllApptsByPro(Pro pro) {
        return apptRepo.getAllByPro(pro);
    }

    @Override
    public List<Appointment> getAllApptsByClient(Client client) {
        return apptRepo.getAllByClient(client);
    }

    @Override
    public Appointment getApptById(int id) {
        return apptRepo.getById(id);
    }
}
