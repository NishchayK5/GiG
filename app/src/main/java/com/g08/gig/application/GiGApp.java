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
import com.g08.gig.persistence.fake.FakeClientRepository;
import com.g08.gig.persistence.fake.FakeProRepository;
import com.g08.gig.persistence.fake.FakeUserRepository;
import com.g08.gig.persistence.real.AppDbHelper;
import com.g08.gig.persistence.real.Client.SqlClientRepository;
import com.g08.gig.persistence.real.Pro.SqlProRepository;
import com.g08.gig.persistence.real.user.SqlUserRepository;

public class GiGApp extends Application {
    private ClientUserService clientUserService;
    private ProUserService proUserService;
    private AuthService authService;
    private AppDbHelper dbHelper;

    @Override
    public void onCreate() {
        super.onCreate();

        dbHelper = new AppDbHelper(getApplicationContext());
        //Sets up repositories
        UserRepository userRepo = new SqlUserRepository(dbHelper);
        ClientRepository clientRepo = new SqlClientRepository(dbHelper);
        ProRepository proRepo = new SqlProRepository(dbHelper);

        //sets up all needed services
        clientUserService = new ClientUserServiceImpl(clientRepo, userRepo);
        proUserService = new ProUserServiceImpl(proRepo, userRepo);
        authService = new AuthServiceImpl(userRepo, clientRepo, proRepo);
        //populateRepo();
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

    /**
     * Populates the repository with fake "stub" data for both clients and professionals
     */
    public void populateRepo(){
        clientUserService.addClient("Client", "One","testemailone@test.com", "TestPassOne", "TestPassOne", "9999999999", "98 Street, Winnipeg, MB");
        clientUserService.addClient("Client", "Two","testemailtwo@test.com", "TestPassTwo", "TestPassTwo", "9999999998", "97 Street, Winnipeg, MB");
        clientUserService.addClient("Client", "Three","testemailthree@test.com", "TestPassThree", "TestPassThree",  "9999999997", "96 Street Winnipeg MB");
        proUserService.addPro("Pro", "One", "testemailfour@test.com", "9999999996", "TestPassFour", "TestPassFour", new String[]{"SkillOne", "SkillTwo"}, "Winnipeg");
        proUserService.addPro("Pro", "Two", "testemailfive@test.com", "9999999995", "TestPassFive", "TestPassFive", new String[]{"SkillOne"}, "Winnipeg");
        proUserService.addPro("Pro", "Three", "testemailsix@test.com", "9999999994", "TestPassSix", "TestPassSix", new String[]{"SkillOne", "SkillTwo", "SkillThree"}, "Edmonton");

    }
}
