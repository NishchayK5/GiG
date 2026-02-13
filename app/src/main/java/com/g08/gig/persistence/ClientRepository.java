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
