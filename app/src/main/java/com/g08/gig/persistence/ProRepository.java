/**
 * File: ProRepository.java

 * Purpose:
 * This class is responsible for all the retrieval and storage of pro-specific information to the database.
 *
 */
package com.g08.gig.persistence;

import com.g08.gig.models.Pro;
import java.util.List;

public interface ProRepository {
    List<Pro> getAll();
    Pro getById(int id);
    Pro add(Pro pro);
    boolean update(Pro pro);
    boolean delete(int id);
}
