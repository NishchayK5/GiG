package com.g08.gig.persistence.real.Request;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.g08.gig.models.Client;
import com.g08.gig.models.Request;
import com.g08.gig.persistence.ClientRepository;
import com.g08.gig.persistence.RequestRepository;
import com.g08.gig.persistence.real.AppDbHelper;
import com.g08.gig.persistence.real.Client.SqlClientRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SqlRequestRepository implements RequestRepository {

    private final AppDbHelper dbHelper;

    private ClientRepository clientRepo;
    public SqlRequestRepository(AppDbHelper dbHelper, ClientRepository clientRepo) {
        this.dbHelper = dbHelper;
        this.clientRepo = clientRepo;
    }

    @Override
    public List<Request> getAll(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(RequestTable.TABLE_REQUEST, new String[]{RequestTable.COL_REQUEST_ID, RequestTable.COL_REQUEST_CLIENT_ID, RequestTable.COL_REQUEST_TITLE, RequestTable.COL_REQUEST_BODY, RequestTable.COL_REQUEST_TIME_OF_REQUEST},
                null, null, null, null,
                RequestTable.COL_REQUEST_ID + " DESC");

        List<Request> result = new ArrayList<>();
        try {
            while (c.moveToNext()){
                result.add(cursorToRequest(c));
            }
        } finally {
            c.close();
        }
        return result;
    }

    @Override
    public Request getById(int id){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(RequestTable.TABLE_REQUEST,
                new String[]{RequestTable.COL_REQUEST_ID, RequestTable.COL_REQUEST_CLIENT_ID, RequestTable.COL_REQUEST_TITLE, RequestTable.COL_REQUEST_BODY, RequestTable.COL_REQUEST_TIME_OF_REQUEST},
                RequestTable.COL_REQUEST_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null);
        try {
            if(!c.moveToNext()) return null;
            return cursorToRequest(c);
        } finally {
            c.close();
        }
    }

    @Override
    public List<Request> getByClient(int clientId){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(RequestTable.TABLE_REQUEST,
                new String[]{RequestTable.COL_REQUEST_ID, RequestTable.COL_REQUEST_CLIENT_ID, RequestTable.COL_REQUEST_TITLE, RequestTable.COL_REQUEST_BODY, RequestTable.COL_REQUEST_TIME_OF_REQUEST},
                RequestTable.COL_REQUEST_CLIENT_ID + "=?",
                new String[]{String.valueOf(clientId)},
                null, null, null);

        List<Request> result = new ArrayList<>();
        try {
            while (c.moveToNext()){
                result.add(cursorToRequest(c));
            }
        } finally {
            c.close();
        }
        return result;
    }

    public boolean delete(int id){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete(RequestTable.TABLE_REQUEST,
                RequestTable.COL_REQUEST_ID +"=?",
                new String[]{String.valueOf(id)} );
        return rows > 0;
    }

    public Request add(Request request){
        if(request==null) return null;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(RequestTable.COL_REQUEST_CLIENT_ID, request.getClient().getId());
            values.put(RequestTable.COL_REQUEST_TITLE, request.getTitle());
            values.put(RequestTable.COL_REQUEST_BODY, request.getBody());
            values.put(RequestTable.COL_REQUEST_TIME_OF_REQUEST, request.getTimeOfRequest().toString());

            long requestId = db.insert(RequestTable.TABLE_REQUEST, null, values);
            if(requestId==-1) return null;
            db.setTransactionSuccessful();
            return getById((int)requestId);
        } finally {
            db.endTransaction();
        }
    }

    private Request cursorToRequest(Cursor c) {
        int id = c.getInt(c.getColumnIndexOrThrow(RequestTable.COL_REQUEST_ID));
        int clientID = c.getInt(c.getColumnIndexOrThrow(RequestTable.COL_REQUEST_CLIENT_ID));
        Client client = clientRepo.getById(clientID);
        String title = c.getString(c.getColumnIndexOrThrow(RequestTable.COL_REQUEST_TITLE));
        String body = c.getString(c.getColumnIndexOrThrow(RequestTable.COL_REQUEST_BODY));
        String timeofRequestString = c.getString(c.getColumnIndexOrThrow(RequestTable.COL_REQUEST_TIME_OF_REQUEST));
        LocalDateTime timeOfRequest = LocalDateTime.parse(timeofRequestString);

        return new Request(id, client, title, body, timeOfRequest);
    }
}
