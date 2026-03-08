package com.g08.gig.persistence.real.Bid;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.g08.gig.models.Bid;
import com.g08.gig.models.Pro;
import com.g08.gig.models.Request;
import com.g08.gig.persistence.BidRepository;
import com.g08.gig.persistence.ProRepository;
import com.g08.gig.persistence.RequestRepository;
import com.g08.gig.persistence.real.AppDbHelper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SqlBidRepository implements BidRepository {

    private final AppDbHelper dbHelper;
    private final ProRepository proRepo;
    private final RequestRepository requestRepo;
    public SqlBidRepository(AppDbHelper dbHelper, ProRepository proRepo, RequestRepository requestRepo){
        this.dbHelper = dbHelper;
        this.proRepo = proRepo;
        this.requestRepo = requestRepo;
    }

    @Override
    public List<Bid> getAll(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(BidTable.TABLE_BID, new String[]{BidTable.COL_BID_ID, BidTable.COL_BID_REQUEST_ID, BidTable.COL_BID_PRO_ID, BidTable.COL_BID_COST, BidTable.COL_BID_TIME_OF_BID},
                null, null, null, null,
                BidTable.COL_BID_ID + " DESC");

        List<Bid> result = new ArrayList<>();
        try {
            while (c.moveToNext()){
                result.add(cursorToBid(c));
            }
        } finally {
            c.close();
        }
        return result;
    }

    @Override
    public Bid getById(int id){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(BidTable.TABLE_BID, new String[]{BidTable.COL_BID_ID, BidTable.COL_BID_REQUEST_ID, BidTable.COL_BID_PRO_ID, BidTable.COL_BID_COST, BidTable.COL_BID_TIME_OF_BID},
                BidTable.COL_BID_ID + " =?",
                new String[]{String.valueOf(id)},
                null, null,
                BidTable.COL_BID_ID + " DESC");

        try {
            if(!c.moveToFirst()) return null;
            return cursorToBid(c);
        } finally {
            c.close();
        }
    }

    @Override
    public List<Bid> getAllByRequest(Request request){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(BidTable.TABLE_BID, new String[]{BidTable.COL_BID_ID, BidTable.COL_BID_REQUEST_ID, BidTable.COL_BID_PRO_ID, BidTable.COL_BID_COST, BidTable.COL_BID_TIME_OF_BID},
                BidTable.COL_BID_REQUEST_ID + " =?",
                new String[]{String.valueOf(request.getId())},
                null, null, null);

        List<Bid> result = new ArrayList<>();
        try{
            while (c.moveToNext()){
                result.add(cursorToBid(c));
            }
        } finally {
            c.close();
        }
        return result;
    }

    @Override
    public List<Bid> getAllByPro(Pro pro){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(BidTable.TABLE_BID, new String[]{BidTable.COL_BID_ID, BidTable.COL_BID_REQUEST_ID, BidTable.COL_BID_PRO_ID, BidTable.COL_BID_COST, BidTable.COL_BID_TIME_OF_BID},
                BidTable.COL_BID_PRO_ID + " =?",
                new String[]{String.valueOf(pro.getId())},
                null, null, null);

        List<Bid> result = new ArrayList<>();
        try{
            while (c.moveToNext()){
                result.add(cursorToBid(c));
            }
        } finally {
            c.close();
        }
        return result;
    }
    @Override
    public Bid add(Bid bid){
        if(bid == null) return null;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try{
            ContentValues values = new ContentValues();
            values.put(BidTable.COL_BID_PRO_ID, bid.getPro().getId());
            values.put(BidTable.COL_BID_REQUEST_ID, bid.getRequest().getId());
            values.put(BidTable.COL_BID_COST, (int)(bid.getCost()*100));
            values.put(BidTable.COL_BID_TIME_OF_BID, bid.getTimeOfBid().toString());
            long bidId = db.insert(BidTable.TABLE_BID, null, values);
            if(bidId ==-1) return null;
            db.setTransactionSuccessful();
            return getById((int)bidId);
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public boolean delete(int id){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete(BidTable.TABLE_BID,
                BidTable.COL_BID_ID + " =?",
                new String[]{String.valueOf(id)} );
        return rows>0;
    }

    private Bid cursorToBid(Cursor c){
        int id = c.getInt(c.getColumnIndexOrThrow(BidTable.COL_BID_ID));
        int requestId = c.getInt(c.getColumnIndexOrThrow(BidTable.COL_BID_REQUEST_ID));
        Request request = requestRepo.getById(requestId);
        int proId = c.getInt(c.getColumnIndexOrThrow(BidTable.COL_BID_PRO_ID));
        Pro pro = proRepo.getById(proId);
        int integerCentsValue = c.getInt(c.getColumnIndexOrThrow(BidTable.COL_BID_COST));
        double cost = integerCentsValue/100.0;
        String timeOfBidString = c.getString(c.getColumnIndexOrThrow(BidTable.COL_BID_TIME_OF_BID));
        LocalDateTime timeOfBid = LocalDateTime.parse(timeOfBidString);
        return new Bid(id, pro, request, cost, timeOfBid);
    }

}
