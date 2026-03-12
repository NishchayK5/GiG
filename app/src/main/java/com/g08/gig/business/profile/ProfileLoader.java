package com.g08.gig.business.profile;

import com.g08.gig.models.User;

public interface ProfileLoader {

    User getUserForEdit(int userId);
}
