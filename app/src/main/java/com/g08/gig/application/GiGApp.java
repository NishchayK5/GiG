package com.g08.gig.application;

import android.app.Application;

import com.g08.gig.business.services.AuthService;
import com.g08.gig.business.services.AuthServiceImpl;
import com.g08.gig.business.services.ClientUserService;
import com.g08.gig.business.services.ClientUserServiceImpl;
import com.g08.gig.business.services.ProUserService;
import com.g08.gig.business.services.ProUserServiceImpl;
import com.g08.gig.models.Client;
import com.g08.gig.persistence.ClientRepository;
import com.g08.gig.persistence.ProRepository;
import com.g08.gig.persistence.UserRepository;
import com.g08.gig.persistence.fake.FakeClientRepository;
import com.g08.gig.persistence.fake.FakeProRepository;
import com.g08.gig.persistence.fake.FakeUserRepository;
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
        UserRepository userRepo = new FakeUserRepository();
        ClientRepository clientRepo = new FakeClientRepository();
        ProRepository proRepo = new FakeProRepository();

        clientRepo.add(new Client(0, "testemailone@test.com", "Client", "One", "9999999999", "98 Street, Winnipeg, MB"));
        clientRepo.add(new Client(0, "testemailtwo@test.com", "Client", "Two", "9999999991", "97 Street, Winnipeg, MB"));
        clientRepo.add(new Client(0, "testemailthree@test.com", "Client", "Three", "9999999992", "96 Street, Winnipeg, MB"));

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
