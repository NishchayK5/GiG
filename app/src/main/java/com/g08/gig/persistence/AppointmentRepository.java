/**
 * File: AppointmentRepository.java

 * Purpose:
 * This class is responsible for all the retrieval and storage of appointments to the database.
 *
 */
package com.g08.gig.persistence;
import com.g08.gig.models.Appointment;
import com.g08.gig.models.Client;
import com.g08.gig.models.Pro;
import com.g08.gig.models.Request;


import java.util.List;

public interface AppointmentRepository {
    List<Appointment> getAll();
    Appointment add(Appointment appointment);
    List<Appointment> getAllByPro(Pro pro);
    Appointment getById(int id);
    boolean delete(int id);
    List<Appointment> getAllByClient(Client client);
    Appointment getAppointmentOfRequest(Request request);
}
