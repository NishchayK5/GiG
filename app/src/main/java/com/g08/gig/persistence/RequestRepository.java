package com.g08.gig.persistence;

import com.g08.gig.models.Request;
import java.util.List;
public interface RequestRepository {

    List<Request> getAll();

    Request getById(int id);

    Request add(Request request);

    boolean delete(int id);

    List<Request> getByClient(int clientId);

    List<Request> getAllWithStatus(boolean status);

    boolean update(Request request);
}
