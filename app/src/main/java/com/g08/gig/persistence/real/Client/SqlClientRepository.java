package com.g08.gig.persistence.real.Client;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.g08.gig.persistence.ClientRepository;
import com.g08.gig.models.Client;
import com.g08.gig.persistence.real.AppDbHelper;
import com.g08.gig.persistence.real.user.UserTable;

import java.util.ArrayList;
import java.util.List;
public class SqlClientRepository implements ClientRepository {

    private final AppDbHelper dbHelper;

    public SqlClientRepository(AppDbHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    @Override
    public List<Client> getAll(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String SQL_statement = "SELECT users." + UserTable.COL_USER_ID + ", "+
                "users." + UserTable.COL_USER_EMAIL + ", "+
                "users." + UserTable.COL_USER_FIRST_NAME + ", "+
                "users." + UserTable.COL_USER_LAST_NAME + ", "+
                "users." + UserTable.COL_USER_PHONE + ", "+
                "clients." + ClientTable.COL_ADDRESS + ", " +
                "users." + UserTable.COL_USER_PASSWORD +  " FROM "
                + UserTable.TABLE_USER + " JOIN "+ ClientTable.TABLE_CLIENT +
                " ON clients." + ClientTable.COL_CLIENT_ID + " = users." + UserTable.COL_USER_ID + ";";
        Cursor c = db.rawQuery(SQL_statement,null);
        List<Client> result = new ArrayList<>();
        try {
            while (c.moveToNext()) {
                result.add(cursorToClient(c));
                }
            }
            finally{
                c.close();
            }
        return result;
    }

    private Client cursorToClient(Cursor c) {
        int id = c.getInt(c.getColumnIndexOrThrow(UserTable.COL_USER_ID));
        String email = c.getString(c.getColumnIndexOrThrow(UserTable.COL_USER_EMAIL));
        String firstName = c.getString(c.getColumnIndexOrThrow(UserTable.COL_USER_FIRST_NAME));
        String lastName = c.getString(c.getColumnIndexOrThrow(UserTable.COL_USER_LAST_NAME));
        String phoneNumber = c.getString(c.getColumnIndexOrThrow(UserTable.COL_USER_PHONE));
        String password = c.getString(c.getColumnIndexOrThrow(UserTable.COL_USER_PASSWORD));
        String address = c.getString(c.getColumnIndexOrThrow(ClientTable.COL_ADDRESS));
        return new Client(id, email, firstName, lastName, phoneNumber, address, password);
    }
    @Override
    public Client getById(int id){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String SQL_statement =  "SELECT users." + UserTable.COL_USER_ID + ", "+
                "users." + UserTable.COL_USER_EMAIL + ", "+
                "users." + UserTable.COL_USER_FIRST_NAME + ", "+
                "users." + UserTable.COL_USER_LAST_NAME + ", "+
                "users." + UserTable.COL_USER_PHONE + ", " +
                "clients." + ClientTable.COL_ADDRESS + ", " +
                "users." + UserTable.COL_USER_PASSWORD +  " FROM "
                + UserTable.TABLE_USER + " JOIN "+ ClientTable.TABLE_CLIENT+
                " ON clients." + ClientTable.COL_CLIENT_ID + " = users." + UserTable.COL_USER_ID + " WHERE users." + UserTable.COL_USER_ID + " = ? ";
        String[] selectionArgs = {String.valueOf(id)};
        Cursor c = db.rawQuery(SQL_statement, selectionArgs);
        try {
            if (!c.moveToFirst()) return null;
            return cursorToClient(c);
        } finally {
            c.close();
        }
    }

    @Override
    public Client add(Client client){
        if(client == null) return null;

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues clientValues = new ContentValues();
            clientValues.put(ClientTable.COL_CLIENT_ID, client.getId());
            clientValues.put(ClientTable.COL_ADDRESS, client.getAddress());
            long result = db.insert(ClientTable.TABLE_CLIENT, null, clientValues);
            if(result == -1) return null;
            db.setTransactionSuccessful();
            return getById(client.getId());
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public boolean update(Client client){
        if(client==null) return false;

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try{
            ContentValues userValues = new ContentValues();
            userValues.put(UserTable.COL_USER_EMAIL, client.getEmail());
            userValues.put(UserTable.COL_USER_FIRST_NAME, client.getFirstName());
            userValues.put(UserTable.COL_USER_LAST_NAME, client.getLastName());
            userValues.put(UserTable.COL_USER_PHONE, client.getPhoneNumber());
            userValues.put(UserTable.COL_USER_PASSWORD, client.getPassword());

            int rows = db.update(UserTable.TABLE_USER, userValues, UserTable.COL_USER_ID + "=?", new String[]{String.valueOf(client.getId())});
            if(rows==0) return false;
            ContentValues clientValues = new ContentValues();
            clientValues.put(ClientTable.COL_CLIENT_ID, client.getId());
            clientValues.put(ClientTable.COL_ADDRESS, client.getAddress());

            int cRows = db.update(ClientTable.TABLE_CLIENT, clientValues, ClientTable.COL_CLIENT_ID + "=?", new  String[]{String.valueOf(client.getId())});
            if(cRows==0) return false;
            db.setTransactionSuccessful();
            return true;
        } finally{
            db.endTransaction();
        }

    }

    @Override
    public boolean delete(int id){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete(UserTable.TABLE_USER,
                UserTable.COL_USER_ID + "=?",
                new String[]{String.valueOf(id)}
        );
        return rows>0;
    }
}