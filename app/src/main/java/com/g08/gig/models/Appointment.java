package com.g08.gig.models;

import java.time.LocalDateTime;

public class Appointment {
    private int id;
    private LocalDateTime appointmentStart;
    private LocalDateTime appointmentEnd;
    private Pro pro;
    private Client client;

    public Appointment(int id, LocalDateTime appointmentStart, LocalDateTime appointmentEnd, Pro pro, Client client) {
        this.id = id;
        this.appointmentStart = appointmentStart;
        this.appointmentEnd = appointmentEnd;
        this.pro = pro;
        this.client = client;
    }

    //getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getAppointmentStart() {
        return appointmentStart;
    }

    public void setAppointmentStart(LocalDateTime appointmentStart) {
        this.appointmentStart = appointmentStart;
    }

    public LocalDateTime getAppointmentEnd() {
        return appointmentEnd;
    }

    public void setAppointmentEnd(LocalDateTime appointmentEnd) {
        this.appointmentEnd = appointmentEnd;
    }

    public Pro getPro() {
        return pro;
    }

    public void setPro(Pro pro) {
        this.pro = pro;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
