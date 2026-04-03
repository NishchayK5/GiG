package com.g08.gig.persistence;
import com.g08.gig.models.Appointment;
import com.g08.gig.models.Pro;


import java.util.List;

public interface AppointmentRepository {
    List<Appointment> getAll();
    Appointment add(Appointment appointment);
    List<Appointment> getAllByPro(Pro pro);
    Appointment getById(int id);
    boolean delete(int id);
}
