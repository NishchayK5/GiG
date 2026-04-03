package com.g08.gig.persistence.real.Request;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.g08.gig.models.Client;
import com.g08.gig.models.Request;
import com.g08.gig.persistence.ClientRepository;
import com.g08.gig.persistence.RequestRepository;
import com.g08.gig.persistence.real.AppDbHelper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SqlRequestRepository implements RequestRepository {

    private final AppDbHelper dbHelper;
    private final ClientRepository clientRepo;
    public SqlRequestRepository(AppDbHelper dbHelper, ClientRepository clientRepo) {
        this.dbHelper = dbHelper;
        this.clientRepo = clientRepo;
    }

    @Override
    public List<Request> getAll(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(RequestTable.TABLE_REQUEST, new String[]{RequestTable.COL_REQUEST_ID, RequestTable.COL_REQUEST_CLIENT_ID, RequestTable.COL_REQUEST_TITLE, RequestTable.COL_REQUEST_BODY, RequestTable.COL_REQUEST_TIME_OF_REQUEST, RequestTable.COL_REQUEST_STATUS},
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
                new String[]{RequestTable.COL_REQUEST_ID, RequestTable.COL_REQUEST_CLIENT_ID, RequestTable.COL_REQUEST_TITLE, RequestTable.COL_REQUEST_BODY, RequestTable.COL_REQUEST_TIME_OF_REQUEST, RequestTable.COL_REQUEST_STATUS},
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
                new String[]{RequestTable.COL_REQUEST_ID, RequestTable.COL_REQUEST_CLIENT_ID, RequestTable.COL_REQUEST_TITLE, RequestTable.COL_REQUEST_BODY, RequestTable.COL_REQUEST_TIME_OF_REQUEST, RequestTable.COL_REQUEST_STATUS},
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
            values.put(RequestTable.COL_REQUEST_STATUS, request.getRequestStatus()?1:0);
            long requestId = db.insert(RequestTable.TABLE_REQUEST, null, values);
            if(requestId==-1) return null;
            db.setTransactionSuccessful();
            return getById((int)requestId);
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public List<Request> getAllWithStatus(boolean status){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(RequestTable.TABLE_REQUEST,
                new String[]{RequestTable.COL_REQUEST_ID, RequestTable.COL_REQUEST_CLIENT_ID, RequestTable.COL_REQUEST_TITLE, RequestTable.COL_REQUEST_BODY, RequestTable.COL_REQUEST_TIME_OF_REQUEST, RequestTable.COL_REQUEST_STATUS},
                RequestTable.COL_REQUEST_STATUS + "=?",
                new String[]{String.valueOf(status ? 1 : 0)},
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

    @Override
    public boolean update(Request request) {
        if (request == null) return false;

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues requestValues = new ContentValues();
            requestValues.put(RequestTable.COL_REQUEST_CLIENT_ID, request.getClient().getId());
            requestValues.put(RequestTable.COL_REQUEST_TITLE, request.getTitle());
            requestValues.put(RequestTable.COL_REQUEST_BODY, request.getBody());
            requestValues.put(RequestTable.COL_REQUEST_TIME_OF_REQUEST, request.getTimeOfRequest().toString());
            requestValues.put(RequestTable.COL_REQUEST_STATUS, request.getRequestStatus()?1:0);

            int rows = db.update(
                    RequestTable.TABLE_REQUEST,
                    requestValues,
                    RequestTable.COL_REQUEST_ID + "=?",
                    new String[]{String.valueOf(request.getId())}
            );

            if (rows == 0) return false;

            db.setTransactionSuccessful();
            return true;
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
        String timeOfRequestString = c.getString(c.getColumnIndexOrThrow(RequestTable.COL_REQUEST_TIME_OF_REQUEST));
        LocalDateTime timeOfRequest = LocalDateTime.parse(timeOfRequestString);
        boolean isActive = c.getInt(c.getColumnIndexOrThrow(RequestTable.COL_REQUEST_STATUS))==1;
        return new Request(id, client, title, body, timeOfRequest, isActive);
    }
}
