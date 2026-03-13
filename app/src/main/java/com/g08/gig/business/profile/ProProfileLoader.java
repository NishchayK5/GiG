package com.g08.gig.business.profile;

import com.g08.gig.business.services.ProUserService;
import com.g08.gig.models.User;

// This class retrieves a pro's profile information when editing. This implementation retrieves the pro's data from the repository based on the provided userID.
public final class ProProfileLoader implements ProfileLoader {

    private final ProUserService proUserService;

    public ProProfileLoader(ProUserService proUserService) {
        this.proUserService = proUserService;
    }

    @Override
    public User getUserForEdit(int userId) {
        return proUserService.getProById(userId);
    }
}
