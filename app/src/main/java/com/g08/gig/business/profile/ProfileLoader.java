package com.g08.gig.business.profile;

import com.g08.gig.models.User;

//Contract for loading user information for profile editing
public interface ProfileLoader {

    User getUserForEdit(int userId);
}
