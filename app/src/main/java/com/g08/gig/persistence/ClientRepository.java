/**
 * File: ClientRepository.java

 * Purpose:
 * This class is responsible for all the retrieval and storage of Client-specific information to the database.
 *
 */
package com.g08.gig.persistence;

import com.g08.gig.models.Client;
import java.util.List;

public interface ClientRepository {
    List<Client> getAll();
    Client getById(int id);
    Client add(Client client);
    boolean update(Client client);
    boolean delete(int id);
}
