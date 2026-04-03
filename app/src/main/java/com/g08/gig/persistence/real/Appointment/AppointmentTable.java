package com.g08.gig.persistence.real.Appointment;

import com.g08.gig.persistence.real.Client.ClientTable;
import com.g08.gig.persistence.real.Pro.ProTable;
import com.g08.gig.persistence.real.Request.RequestTable;
import com.g08.gig.persistence.real.TableSchema;

public class AppointmentTable implements TableSchema {

    public static final String TABLE_APPOINTMENT = "appointments";
    public static final String COL_APPOINTMENT_ID = "id";
    public static final String COL_APPOINTMENT_PRO_ID = "proId";
    public static final String COL_APPOINTMENT_CLIENT_ID = "clientId";
    public static final String COL_APPOINTMENT_START_TIME = "startTime";
    public static final String COL_APPOINTMENT_END_TIME = "endTime";
    public static final String COL_APPOINTMENT_REQUEST_ID = "requestId";

    @Override
    public String getTableName(){return TABLE_APPOINTMENT;}

    @Override
    public String getCreateSql(){
        return "CREATE TABLE "+ TABLE_APPOINTMENT + "( " +
                COL_APPOINTMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_APPOINTMENT_PRO_ID + " INTEGER NOT NULL, " +
                COL_APPOINTMENT_CLIENT_ID + " INTEGER NOT NULL, " +
                COL_APPOINTMENT_START_TIME + " TEXT NOT NULL, " +
                COL_APPOINTMENT_END_TIME + " TEXT NOT NULL, " +
                COL_APPOINTMENT_REQUEST_ID + " INTEGER NOT NULL, " +
                "FOREIGN KEY("+ COL_APPOINTMENT_CLIENT_ID +") REFERENCES " + ClientTable.TABLE_CLIENT + "(" + ClientTable.COL_CLIENT_ID + ") ON DELETE CASCADE, " +
                "FOREIGN KEY("+ COL_APPOINTMENT_PRO_ID +") REFERENCES " + ProTable.TABLE_PRO + "(" + ProTable.COL_PRO_ID + ") ON DELETE CASCADE, " +
                "FOREIGN KEY("+ COL_APPOINTMENT_REQUEST_ID +") REFERENCES " + RequestTable.TABLE_REQUEST + "(" + RequestTable.COL_REQUEST_ID + ") ON DELETE CASCADE " +
                ");";
    }
}
