package com.g08.gig.persistence.real.Skills;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.g08.gig.models.Pro;
import com.g08.gig.persistence.SkillRepository;
import com.g08.gig.persistence.real.AppDbHelper;
import java.util.ArrayList;
import java.util.List;

public class SqlSkillRepository implements SkillRepository {

    private final AppDbHelper dbHelper;

    public SqlSkillRepository(AppDbHelper dbHelper){
        this.dbHelper = dbHelper;
    }

    @Override
    public String[] getAllSkillsByPro(Pro pro){
        if(pro == null) return null;

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(SkillTable.TABLE_SKILLS,
                new String[]{SkillTable.COL_PRO_ID, SkillTable.COL_SKILL},
                SkillTable.COL_PRO_ID + "=?",
                new String[]{String.valueOf(pro.getId())}, null, null, null);

        return cursorToSkillSet(c);
    }

    @Override
    public boolean addSkillsForPro(int proId, String[] skills){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            for(int i = 0; i < skills.length; i++) {
                ContentValues values = new ContentValues();
                values.put(SkillTable.COL_PRO_ID, String.valueOf(proId));
                values.put(SkillTable.COL_SKILL, skills[i]);

                long addId = db.insert(SkillTable.TABLE_SKILLS, null, values);

                if(addId == -1) return false;
            }
            db.setTransactionSuccessful();
            return true;
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public boolean deleteAllSkillsByProId(int proId){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete(
                SkillTable.TABLE_SKILLS,
                SkillTable.COL_PRO_ID + "=?",
                new String[]{String.valueOf(proId)}
        );
        return rows > 0;
    }

    private String[] cursorToSkillSet(Cursor c){
        List<String> skills = new ArrayList<>();
        try{
            while (c.moveToNext()){
                skills.add(c.getString(c.getColumnIndexOrThrow(SkillTable.COL_SKILL)));
            }
        } finally {
            c.close();
        }

        return skills.toArray( new String[0] );
    }
}


