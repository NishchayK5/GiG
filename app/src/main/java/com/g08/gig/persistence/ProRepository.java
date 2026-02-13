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
