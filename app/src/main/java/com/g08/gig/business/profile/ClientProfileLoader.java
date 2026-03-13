package com.g08.gig.business.profile;

import com.g08.gig.business.services.ClientUserService;
import com.g08.gig.models.User;

// This class retrieves a client's profile information when editing. This implementation retrieves the client's data from the repository based on the provided userID.
public final class ClientProfileLoader implements ProfileLoader {

    private final ClientUserService clientUserService;

    public ClientProfileLoader(ClientUserService clientUserService) {
        this.clientUserService = clientUserService;
    }

    @Override
    public User getUserForEdit(int userId) {
        return clientUserService.getClientById(userId);
    }
}
