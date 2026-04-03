package com.g08.gig.persistence.fake;

import com.g08.gig.models.Appointment;
import com.g08.gig.models.Client;
import com.g08.gig.models.Pro;
import com.g08.gig.models.Request;
import com.g08.gig.persistence.AppointmentRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FakeAppointmentRepository implements AppointmentRepository {

    private final List<Appointment> appointments;
    private int nextId;
    public FakeAppointmentRepository(){
        appointments = new ArrayList<>();
        nextId = 1;
    }

    @Override
    public List<Appointment> getAll(){return Collections.unmodifiableList(appointments); }

    @Override
    public Appointment add(Appointment appointment){
        if(appointment==null) return null;
        int id = appointment.getId();
        if(id==0) id = nextId++;
        else if(getById(id)!=null) return null;

        Appointment toStore = new Appointment(id, appointment.getAppointmentStart(), appointment.getAppointmentEnd(), appointment.getPro(), appointment.getClient(), appointment.getRequest());
        appointments.add(toStore);
        return toStore;
    }

    @Override
    public boolean delete(int id){
        for(int i = 0; i<appointments.size();i++){
            if(appointments.get(i).getId()==id){
                appointments.remove(i);
                return true;
            }
        }
        return false;
    }

    @Override
    public Appointment getById(int id){
         for(Appointment appointment:appointments){
             if(appointment.getId()==id) return appointment;
         }
         return null;
    }

    @Override
    public List<Appointment> getAllByPro(Pro pro){
        List<Appointment> result = new ArrayList<>();
        for(Appointment appointment : appointments){
            if(appointment.getPro().getId()==pro.getId()) result.add(appointment);
        }
        return result;
    }

    @Override
    public List<Appointment> getAllByClient(Client client){
        List<Appointment> result = new ArrayList<>();
        for(Appointment appointment : appointments){
            if(appointment.getClient().getId()==client.getId()) result.add(appointment);
        }
        return result;
    }

    @Override
    public Appointment getAppointmentOfRequest(Request request){
        for(Appointment appointment:appointments){
            if(appointment.getRequest()==request) return appointment;
        }
        return null;
    }
}
