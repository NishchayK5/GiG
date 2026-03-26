package com.g08.gig.persistence.real.Review;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.g08.gig.models.Client;
import com.g08.gig.models.Pro;
import com.g08.gig.models.Review;
import com.g08.gig.persistence.ClientRepository;
import com.g08.gig.persistence.ProRepository;
import com.g08.gig.persistence.ReviewRepository;
import com.g08.gig.persistence.real.AppDbHelper;
import com.g08.gig.persistence.real.Request.RequestTable;

import java.util.ArrayList;
import java.util.List;

public class SqlReviewRepository implements ReviewRepository {

    private final AppDbHelper dbHelper;
    private final ClientRepository clientRepo;
    private final ProRepository proRepo;
    public SqlReviewRepository(AppDbHelper dbHelper, ClientRepository clientRepo, ProRepository proRepo){
        this.dbHelper = dbHelper;
        this.clientRepo = clientRepo;
        this.proRepo = proRepo;
    }

    @Override
    public List<Review> getAll(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(ReviewTable.TABLE_REVIEW,
                new String[]{ReviewTable.COL_REVIEW_ID, ReviewTable.COL_REVIEW_CLIENT_ID, ReviewTable.COL_REVIEW_PRO_ID, ReviewTable.COL_REVIEW_TITLE, ReviewTable.COL_REVIEW_BODY, ReviewTable.COL_REVIEW_STAR_RATING},
                null, null,null, null,
                ReviewTable.COL_REVIEW_ID + "DESC");
        List<Review> result = new ArrayList<>();
        try{
            while (c.moveToNext()){
                result.add(cursorToReview(c));
            }
        } finally {
            c.close();
        }
        return result;
    }

    @Override
    public List<Review> getAllReviewsForPro(Pro pro){
        if(pro==null) return null;

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(ReviewTable.TABLE_REVIEW,
                new String[]{ReviewTable.COL_REVIEW_ID, ReviewTable.COL_REVIEW_CLIENT_ID, ReviewTable.COL_REVIEW_PRO_ID, ReviewTable.COL_REVIEW_TITLE, ReviewTable.COL_REVIEW_BODY, ReviewTable.COL_REVIEW_STAR_RATING},
                ReviewTable.COL_REVIEW_PRO_ID + "=?",
                new String[]{String.valueOf(pro.getId())},null, null, null);
        List<Review> result = new ArrayList<>();
        try{
            while (c.moveToNext()){
                result.add(cursorToReview(c));
            }
        } finally {
            c.close();
        }
        return result;
    }

    @Override
    public Review getById(int id){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(ReviewTable.TABLE_REVIEW,
                new String[]{ReviewTable.COL_REVIEW_ID, ReviewTable.COL_REVIEW_CLIENT_ID, ReviewTable.COL_REVIEW_PRO_ID, ReviewTable.COL_REVIEW_TITLE, ReviewTable.COL_REVIEW_BODY, ReviewTable.COL_REVIEW_STAR_RATING},
                ReviewTable.COL_REVIEW_ID + "=?",
                new String[]{String.valueOf(id)},null, null, null);
        try {
            if(!c.moveToNext()) return null;
            return cursorToReview(c);
        } finally {
            c.close();
        }
    }

    @Override
    public Review add(Review review){
        if(review==null) return null;

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(ReviewTable.COL_REVIEW_CLIENT_ID, review.getReviewer().getId());
            values.put(ReviewTable.COL_REVIEW_PRO_ID, review.getPro().getId());
            values.put(ReviewTable.COL_REVIEW_TITLE, review.getTitle());
            values.put(ReviewTable.COL_REVIEW_BODY, review.getBody());
            values.put(ReviewTable.COL_REVIEW_STAR_RATING, review.getStars());
            long reviewId = db.insert(ReviewTable.TABLE_REVIEW, null, values);
            if(reviewId==-1) return null;
            db.setTransactionSuccessful();
            return getById((int)reviewId);
        } finally {
            db.endTransaction();
        }
    }
    @Override
    public boolean delete(int id){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete(ReviewTable.TABLE_REVIEW,
                ReviewTable.TABLE_REVIEW +"=?",
                new String[]{String.valueOf(id)} );
        return rows > 0;
    }

    private Review cursorToReview(Cursor c){
        int id  = c.getInt(c.getColumnIndexOrThrow(ReviewTable.COL_REVIEW_ID));
        int clientId = c.getInt(c.getColumnIndexOrThrow(ReviewTable.COL_REVIEW_CLIENT_ID));
        Client reviewer = clientRepo.getById(clientId);
        int proId = c.getInt(c.getColumnIndexOrThrow(ReviewTable.COL_REVIEW_PRO_ID));
        Pro reviewee = proRepo.getById(proId);
        String title  = c.getString(c.getColumnIndexOrThrow(ReviewTable.COL_REVIEW_TITLE));
        String body = c.getString(c.getColumnIndexOrThrow(ReviewTable.COL_REVIEW_BODY));
        int starRating = c.getInt(c.getColumnIndexOrThrow(ReviewTable.COL_REVIEW_STAR_RATING));
        return new Review(id, reviewer, reviewee, title, body, starRating);
    }
}
