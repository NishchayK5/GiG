package com.g08.gig.business.services;

import com.g08.gig.models.Pro;

public interface ProUserService {
    Pro addPro(String firstName, String lastName, String email, String phone, String password, String confirmPassword, String[] skillSet, String cityOfOperation);
    boolean updatePro(int id, String email, String firstName, String lastName, String phone, String[] skillSet, String cityOfOperation);
    boolean deletePro(int id);
    Pro getProById(int id);
}
