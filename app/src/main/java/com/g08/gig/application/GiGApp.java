package com.g08.gig.application;

import android.app.Application;

import com.g08.gig.business.services.ClientUserService;
import com.g08.gig.business.services.ClientUserServiceImpl;
import com.g08.gig.business.services.ProUserService;
import com.g08.gig.persistence.ClientRepository;
import com.g08.gig.persistence.ProRepository;
import com.g08.gig.persistence.UserRepository;
import com.g08.gig.persistence.real.AppDbHelper;
import com.g08.gig.persistence.real.Client.SqlClientRepository;
import com.g08.gig.persistence.real.user.SqlUserRepository;
import com.g08.gig.persistence.real.Pro.SqlProRepository;
import com.g08.gig.business.services.ProUserServiceImpl;

public class GiGApp extends Application {

    private ClientUserService clientUserService;
    private ProUserService proUserService;

    @Override
    public void onCreate() {
        super.onCreate();

        ClientRepository clientRepo = new SqlClientRepository(new AppDbHelper(getApplicationContext()));
        clientUserService = new ClientUserServiceImpl(clientRepo);

        ProRepository proRepo = new SqlProRepository(new AppDbHelper(getApplicationContext()));
        proUserService = new ProUserServiceImpl(proRepo);
    }

    public ClientUserService getClientUserService() {
        return clientUserService;
    }
    public ProUserService getProUserService() { return proUserService; }
}
