package com.g08.gig.application;

import android.app.Application;

import com.g08.gig.business.services.AuthService;
import com.g08.gig.business.services.AuthServiceImpl;
import com.g08.gig.business.services.ClientUserService;
import com.g08.gig.business.services.ClientUserServiceImpl;
import com.g08.gig.business.services.ProUserService;
import com.g08.gig.business.services.ProUserServiceImpl;
import com.g08.gig.models.Client;
import com.g08.gig.models.Pro;
import com.g08.gig.persistence.ClientRepository;
import com.g08.gig.persistence.ProRepository;
import com.g08.gig.persistence.UserRepository;
import com.g08.gig.persistence.fake.FakeClientRepository;
import com.g08.gig.persistence.fake.FakeProRepository;
import com.g08.gig.persistence.fake.FakeUserRepository;
import com.g08.gig.persistence.real.AppDbHelper;

public class GiGApp extends Application {

    private AppDbHelper dbHelper;
    private ClientUserService clientUserService;
    private ProUserService proUserService;
    private AuthService authService;

    @Override
    public void onCreate() {
        super.onCreate();
        UserRepository userRepo = new FakeUserRepository();
        ClientRepository clientRepo = new FakeClientRepository();
        ProRepository proRepo = new FakeProRepository();

        populateRepo(clientRepo, proRepo, userRepo);

        clientUserService = new ClientUserServiceImpl(clientRepo, userRepo);
        proUserService = new ProUserServiceImpl(proRepo, userRepo);
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

    public void populateRepo(ClientRepository clientRepo, ProRepository proRepo, UserRepository userRepo){
        Client clientOne = new Client(0, "testemailone@test.com", "Client", "One", "9999999999", "98 Street, Winnipeg, MB", "TestPassOne");
        Client clientTwo = new Client(0, "testemailtwo@test.com", "Client", "Two", "9999999991", "97 Street, Winnipeg, MB", "TestPassTwo");
        Client clientThree = new Client(0, "testemailthree@test.com", "Client", "Three", "9999999992", "96 Street, Winnipeg, MB", "TestPassThree");

        Pro proOne = new Pro(0, "testEmailthree@test.com", "Pro", "One", "9999999993", "TestPassFour", null, "Winnipeg");
        Pro proTwo = new Pro(0, "testEmailthree@test.com", "Pro", "Two", "9999999994", "TestPassFive", null, "Winnipeg");
        Pro proThree = new Pro(0, "testEmailthree@test.com", "Pro", "Three", "9999999995", "TestPassSix", null, "Winnipeg");

        clientRepo.add(clientOne);
        clientRepo.add(clientTwo);
        clientRepo.add(clientThree);
        userRepo.add(clientOne);
        userRepo.add(clientOne);
        userRepo.add(clientOne);

        proRepo.add(proOne);
        proRepo.add(proTwo);
        proRepo.add(proThree);
        userRepo.add(proOne);
        userRepo.add(proTwo);
        userRepo.add(proThree);
    }
}
