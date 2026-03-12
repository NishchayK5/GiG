package com.g08.gig.business.profile;

import com.g08.gig.business.services.ClientUserService;
import com.g08.gig.models.User;

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
