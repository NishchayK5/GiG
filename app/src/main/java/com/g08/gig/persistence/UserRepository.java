/**
 * File: UserRepository.java

 * Purpose:
 * This class is responsible for all the retrieval and storage of all generic information of pros and client
 * which is not specific to the role to the database.
 *
 */
package com.g08.gig.persistence;

import com.g08.gig.models.User;
import java.util.List;

public interface UserRepository {
    List<User> getAll();
    User getById(int id);
    User getByEmail(String email);
    User add(User user);
    boolean update(User user);
    boolean delete(int id);
}
