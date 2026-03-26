package com.g08.gig.persistence.real.Appointment;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.g08.gig.models.Appointment;
import com.g08.gig.models.Client;
import com.g08.gig.models.Pro;
import com.g08.gig.models.Request;
import com.g08.gig.persistence.AppointmentRepository;
import com.g08.gig.persistence.ClientRepository;
import com.g08.gig.persistence.ProRepository;
import com.g08.gig.persistence.RequestRepository;
import com.g08.gig.persistence.real.AppDbHelper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SqlAppointmentRepository implements AppointmentRepository{
    private final AppDbHelper dbHelper;
    private final ProRepository proRepo;
    private final ClientRepository clientRepo;
    private final RequestRepository requestRepo;

    public SqlAppointmentRepository(AppDbHelper dbHelper, ClientRepository clientRepo, ProRepository proRepo, RequestRepository requestRepo){
        this.dbHelper = dbHelper;
        this.proRepo = proRepo;
        this.clientRepo = clientRepo;
        this.requestRepo = requestRepo;
    }

    @Override
    public List<Appointment> getAll(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(AppointmentTable.TABLE_APPOINTMENT,
                new String[]{AppointmentTable.COL_APPOINTMENT_ID, AppointmentTable.COL_APPOINTMENT_CLIENT_ID, AppointmentTable.COL_APPOINTMENT_PRO_ID, AppointmentTable.COL_APPOINTMENT_START_TIME, AppointmentTable.COL_APPOINTMENT_END_TIME, AppointmentTable.COL_APPOINTMENT_REQUEST_ID},
                null, null, null, null,
                AppointmentTable.COL_APPOINTMENT_ID + "DESC");

        List<Appointment> result = new ArrayList<>();
        try{
            while(c.moveToNext()){
                result.add(cursorToAppointment(c));
            }
        } finally{
            c.close();
        }
        return result;
    }

    @Override
    public Appointment add(Appointment appointment){
        if(appointment == null) return null;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try{
            ContentValues values = new ContentValues();
            values.put(AppointmentTable.COL_APPOINTMENT_CLIENT_ID, appointment.getClient().getId());
            values.put(AppointmentTable.COL_APPOINTMENT_PRO_ID, appointment.getPro().getId());
            values.put(AppointmentTable.COL_APPOINTMENT_REQUEST_ID, appointment.getRequest().getId());
            values.put(AppointmentTable.COL_APPOINTMENT_START_TIME, appointment.getAppointmentStart().toString());
            values.put(AppointmentTable.COL_APPOINTMENT_END_TIME, appointment.getAppointmentEnd().toString());
            long appointmentId = db.insert(AppointmentTable.TABLE_APPOINTMENT, null, values);
            if(appointmentId == -1) return null;
            db.setTransactionSuccessful();
            return getById((int)appointmentId);
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public Appointment getById(int id){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(AppointmentTable.TABLE_APPOINTMENT,
                new String[]{AppointmentTable.COL_APPOINTMENT_ID, AppointmentTable.COL_APPOINTMENT_CLIENT_ID, AppointmentTable.COL_APPOINTMENT_PRO_ID, AppointmentTable.COL_APPOINTMENT_START_TIME, AppointmentTable.COL_APPOINTMENT_END_TIME, AppointmentTable.COL_APPOINTMENT_REQUEST_ID},
                AppointmentTable.COL_APPOINTMENT_ID + "=?", new String[]{String.valueOf(id)},
                null, null, null);
        try{
            if(!c.moveToNext()) return null;
            return cursorToAppointment(c);
        } finally{
            c.close();
        }
    }

    @Override
    public List<Appointment> getAllByPro(Pro pro){
        if(pro == null) return null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(AppointmentTable.TABLE_APPOINTMENT,
                new String[]{AppointmentTable.COL_APPOINTMENT_ID, AppointmentTable.COL_APPOINTMENT_CLIENT_ID, AppointmentTable.COL_APPOINTMENT_PRO_ID, AppointmentTable.COL_APPOINTMENT_START_TIME, AppointmentTable.COL_APPOINTMENT_END_TIME, AppointmentTable.COL_APPOINTMENT_REQUEST_ID},
                AppointmentTable.COL_APPOINTMENT_PRO_ID + "=?", new String[]{String.valueOf(pro.getId())},
                null, null, null);
        List<Appointment> result = new ArrayList<>();
        try{
            while(c.moveToNext()){
                result.add(cursorToAppointment(c));
            }
        } finally {
            c.close();
        }
        return result;
    }

    @Override
    public List<Appointment> getAllByClient(Client client){
        if(client == null) return null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(AppointmentTable.TABLE_APPOINTMENT,
                new String[]{AppointmentTable.COL_APPOINTMENT_ID, AppointmentTable.COL_APPOINTMENT_CLIENT_ID, AppointmentTable.COL_APPOINTMENT_PRO_ID, AppointmentTable.COL_APPOINTMENT_START_TIME, AppointmentTable.COL_APPOINTMENT_END_TIME, AppointmentTable.COL_APPOINTMENT_REQUEST_ID},
                AppointmentTable.COL_APPOINTMENT_CLIENT_ID + "=?", new String[]{String.valueOf(client.getId())},
                null, null, null);
        List<Appointment> result = new ArrayList<>();
        try{
            while(c.moveToNext()){
                result.add(cursorToAppointment(c));
            }
        } finally {
            c.close();
        }
        return result;
    }

    @Override
    public Appointment getAppointmentOfRequest(Request request){
        if(request == null) return null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(AppointmentTable.TABLE_APPOINTMENT,
                new String[]{AppointmentTable.COL_APPOINTMENT_ID, AppointmentTable.COL_APPOINTMENT_CLIENT_ID, AppointmentTable.COL_APPOINTMENT_PRO_ID, AppointmentTable.COL_APPOINTMENT_START_TIME, AppointmentTable.COL_APPOINTMENT_END_TIME, AppointmentTable.COL_APPOINTMENT_REQUEST_ID},
                AppointmentTable.COL_APPOINTMENT_REQUEST_ID + "=?", new String[]{String.valueOf(request.getId())},
                null, null, null);
        try{
            if(!c.moveToNext()) return null;
            return cursorToAppointment(c);
        } finally {
            c.close();
        }
    }

    @Override
    public boolean delete(int id){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete(AppointmentTable.TABLE_APPOINTMENT,
                AppointmentTable.COL_APPOINTMENT_ID + "=?",
                new String[]{String.valueOf(id)});
        return rows>0;
    }

    //have to update null checks, after consulting
    private Appointment cursorToAppointment(Cursor c){
        int id = c.getInt(c.getColumnIndexOrThrow(AppointmentTable.COL_APPOINTMENT_ID));
        int clientId = c.getInt((c.getColumnIndexOrThrow(AppointmentTable.COL_APPOINTMENT_CLIENT_ID)));
        Client client = clientRepo.getById(clientId);
        int proId = c.getInt(c.getColumnIndexOrThrow(AppointmentTable.COL_APPOINTMENT_PRO_ID));
        Pro pro  = proRepo.getById(proId);
        String appointmentStartTimeString = c.getString(c.getColumnIndexOrThrow(AppointmentTable.COL_APPOINTMENT_START_TIME));
        LocalDateTime startTimeOfRequest = LocalDateTime.parse(appointmentStartTimeString);
        String appointmentEndTimeString = c.getString(c.getColumnIndexOrThrow(AppointmentTable.COL_APPOINTMENT_END_TIME));
        LocalDateTime endTimeOfRequest = LocalDateTime.parse(appointmentEndTimeString);
        int requestId = c.getInt(c.getColumnIndexOrThrow(AppointmentTable.COL_APPOINTMENT_REQUEST_ID));
        Request request = requestRepo.getById(requestId);

        return new Appointment(id, startTimeOfRequest, endTimeOfRequest, pro, client, request);
    }
}
