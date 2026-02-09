package com.g08.gig.application;

import android.app.Application;

import com.g08.gig.business.services.AuthService;
import com.g08.gig.business.services.AuthServiceImpl;
import com.g08.gig.business.services.ClientUserService;
import com.g08.gig.business.services.ClientUserServiceImpl;
import com.g08.gig.business.services.ProUserService;
import com.g08.gig.business.services.ProUserServiceImpl;
import com.g08.gig.persistence.ClientRepository;
import com.g08.gig.persistence.ProRepository;
import com.g08.gig.persistence.UserRepository;
import com.g08.gig.persistence.real.AppDbHelper;
import com.g08.gig.persistence.real.Client.SqlClientRepository;
import com.g08.gig.persistence.real.Pro.SqlProRepository;
import com.g08.gig.persistence.real.user.SqlUserRepository;

public class GiGApp extends Application {

    private AppDbHelper dbHelper;
    private ClientUserService clientUserService;
    private ProUserService proUserService;
    private AuthService authService;

    @Override
    public void onCreate() {
        super.onCreate();

        dbHelper = new AppDbHelper(getApplicationContext());
        UserRepository userRepo = new SqlUserRepository(dbHelper);
        ClientRepository clientRepo = new SqlClientRepository(dbHelper);
        ProRepository proRepo = new SqlProRepository(dbHelper);

        clientUserService = new ClientUserServiceImpl(clientRepo);
        proUserService = new ProUserServiceImpl(proRepo);
        authService = new AuthServiceImpl(userRepo, clientRepo, proRepo);
    }

    public ClientUserService getClientUserService() {
        return clientUserService;
    }

    public ProUserService getProUserService() {
        return proUserService;
    }

    public AuthService getAuthService() {
        return authService;
    }
}
