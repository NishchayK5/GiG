package com.g08.gig.persistence.real;

/**
 * Contract for a database table definition. Used by AppDbHelper to create and drop tables.
 */
public interface TableSchema {

    String getTableName();

    String getCreateSql();
}
