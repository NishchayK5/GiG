package com.g08.gig.application;

import android.app.Application;

import com.g08.gig.business.services.ClientUserService;
import com.g08.gig.business.services.ClientUserServiceImpl;
import com.g08.gig.business.services.CustomerUserServiceImpl;
import com.g08.gig.persistence.ClientRepository;
import com.g08.gig.persistence.UserRepository;
import com.g08.gig.persistence.real.AppDbHelper;
import com.g08.gig.persistence.real.Client.SqlClientRepository;
import com.g08.gig.persistence.real.user.SqlUserRepository;

public class GiGApp extends Application {

    private ClientUserService clientUserService;

    @Override
    public void onCreate() {
        super.onCreate();

        ClientRepository repo = new SqlClientRepository(new AppDbHelper(getApplicationContext()));
        clientUserService = new ClientUserServiceImpl(repo);
    }

    public ClientUserService getClientUserService() {
        return clientUserService;
    }
}
