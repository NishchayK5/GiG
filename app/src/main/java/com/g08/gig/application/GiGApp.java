package com.g08.gig.application;

import android.app.Application;

import com.g08.gig.business.services.AuthService;
import com.g08.gig.business.services.AuthServiceImpl;
import com.g08.gig.business.services.ClientUserService;
import com.g08.gig.business.services.ClientUserServiceImpl;
import com.g08.gig.business.services.ProUserService;
import com.g08.gig.business.services.ProUserServiceImpl;
import com.g08.gig.business.services.RequestService;
import com.g08.gig.business.services.RequestServiceImpl;
import com.g08.gig.models.Client;
import com.g08.gig.models.Pro;
import com.g08.gig.models.Request;
import com.g08.gig.persistence.ClientRepository;
import com.g08.gig.persistence.ProRepository;
import com.g08.gig.persistence.RequestRepository;
import com.g08.gig.persistence.UserRepository;
import com.g08.gig.persistence.fake.FakeClientRepository;
import com.g08.gig.persistence.fake.FakeProRepository;
import com.g08.gig.persistence.fake.FakeRequestRepository;
import com.g08.gig.persistence.fake.FakeUserRepository;
import com.g08.gig.persistence.real.AppDbHelper;
import com.g08.gig.persistence.real.Client.SqlClientRepository;
import com.g08.gig.persistence.real.Pro.SqlProRepository;
import com.g08.gig.persistence.real.Request.SqlRequestRepository;
import com.g08.gig.persistence.real.user.SqlUserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class GiGApp extends Application {
    private ClientUserService clientUserService;
    private ProUserService proUserService;
    private AuthService authService;

    private RequestService reqService;
    private AppDbHelper dbHelper;
    private UserRepository userRepo;
    private ClientRepository clientRepo;
    private ProRepository proRepo;
    private RequestRepository reqRepo;

    @Override
    public void onCreate() {
        super.onCreate();

        dbHelper = new AppDbHelper(getApplicationContext());
        //Sets up repositories



        final boolean FAKE_REPO = false;

        if(!FAKE_REPO) {
            userRepo = new SqlUserRepository(dbHelper);
            clientRepo = new SqlClientRepository(dbHelper);
            proRepo = new SqlProRepository(dbHelper);
            reqRepo = new SqlRequestRepository(dbHelper, clientRepo);
        }
        else {
            userRepo = new FakeUserRepository();
            clientRepo = new FakeClientRepository();
            proRepo = new FakeProRepository();
            reqRepo = new FakeRequestRepository();
        }

        //sets up all needed services
        clientUserService = new ClientUserServiceImpl(clientRepo, userRepo);
        proUserService = new ProUserServiceImpl(proRepo, userRepo);
        authService = new AuthServiceImpl(userRepo, clientRepo, proRepo);
        reqService = new RequestServiceImpl(reqRepo);

        if(FAKE_REPO) {
            populateRepo();
        }

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

    public RequestService getRequestService() { return reqService; }

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
        populateRequestRepo();
    }

    public void populateRequestRepo()
    {
        Client placeholderClient = clientRepo.getById(1);
        reqService.addRequest(placeholderClient, "Brake pad replacement",
                "My 2019 Honda Civic needs new front brake pads. There's a grinding noise when I stop.");
        reqService.addRequest(placeholderClient, "Oil change needed",
                "Due for synthetic oil change. Prefer weekend appointment if possible.");
        reqService.addRequest(placeholderClient, "AC not cooling",
                "Car AC blows warm air. 2020 Toyota Corolla. Would like a diagnosis.");
    }

}
