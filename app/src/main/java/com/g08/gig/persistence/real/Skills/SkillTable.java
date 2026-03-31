package com.g08.gig.persistence.real.Skills;

import com.g08.gig.persistence.real.Pro.ProTable;
import com.g08.gig.persistence.real.TableSchema;

public class SkillTable implements TableSchema{

    public static final String TABLE_SKILLS = "skills";
    public static final String COL_PRO_ID = "proId";
    public static final String COL_SKILL = "skill";

    @Override
    public String getTableName(){ return  TABLE_SKILLS; }

    @Override
    public String getCreateSql(){
        return "CREATE TABLE " + TABLE_SKILLS + " (" +
                COL_PRO_ID + " INTEGER NOT NULL, " +
                COL_SKILL + " TEXT NOT NULL, " +
                "PRIMARY KEY (" + COL_PRO_ID + " , " + COL_SKILL + "), "  +
                "FOREIGN KEY (" + COL_PRO_ID + ") REFERENCES " + ProTable.TABLE_PRO + "(" + ProTable.COL_PRO_ID + ") ON DELETE CASCADE" +
                ");" ;
    }
}
