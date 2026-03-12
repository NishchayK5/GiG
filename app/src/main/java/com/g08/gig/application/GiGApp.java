package com.g08.gig.application;

import android.app.Application;

import com.g08.gig.business.services.AuthService;
import com.g08.gig.business.services.AuthServiceImpl;
import com.g08.gig.business.services.BidService;
import com.g08.gig.business.services.BidServiceImpl;
import com.g08.gig.business.services.ClientUserService;
import com.g08.gig.business.services.ClientUserServiceImpl;
import com.g08.gig.business.services.ProUserService;
import com.g08.gig.business.services.ProUserServiceImpl;
import com.g08.gig.business.profile.ClientProfileLoader;
import com.g08.gig.business.profile.ProProfileLoader;
import com.g08.gig.business.profile.ProfileLoader;
import com.g08.gig.business.services.RequestService;
import com.g08.gig.business.services.RequestServiceImpl;
import com.g08.gig.persistence.BidRepository;
import com.g08.gig.persistence.ClientRepository;
import com.g08.gig.persistence.ProRepository;
import com.g08.gig.persistence.RequestRepository;
import com.g08.gig.persistence.UserRepository;
import com.g08.gig.persistence.fake.FakeBidRepository;
import com.g08.gig.persistence.fake.FakeClientRepository;
import com.g08.gig.persistence.fake.FakeProRepository;
import com.g08.gig.persistence.fake.FakeRequestRepository;
import com.g08.gig.persistence.fake.FakeUserRepository;
import com.g08.gig.persistence.real.AppDbHelper;
import com.g08.gig.persistence.real.Bid.SqlBidRepository;
import com.g08.gig.persistence.real.Client.SqlClientRepository;
import com.g08.gig.persistence.real.Pro.SqlProRepository;
import com.g08.gig.persistence.real.Request.SqlRequestRepository;
import com.g08.gig.persistence.real.user.SqlUserRepository;

public class GiGApp extends Application {
    private ClientUserService clientUserService;
    private ProUserService proUserService;
    private AuthService authService;
    private BidService bidService;
    private RequestService reqService;
    private AppDbHelper dbHelper;
    private UserRepository userRepo;
    private ClientRepository clientRepo;
    private ProRepository proRepo;
    private RequestRepository reqRepo;
    private BidRepository bidRepo;

    @Override
    public void onCreate() {
        super.onCreate();

        dbHelper = new AppDbHelper(getApplicationContext());

        final boolean FAKE_REPO = false;

        if(!FAKE_REPO) {
            userRepo = new SqlUserRepository(dbHelper);
            clientRepo = new SqlClientRepository(dbHelper);
            proRepo = new SqlProRepository(dbHelper);
            reqRepo = new SqlRequestRepository(dbHelper, clientRepo);
            bidRepo = new SqlBidRepository(dbHelper, proRepo, reqRepo);
        }
        else {
            userRepo = new FakeUserRepository();
            clientRepo = new FakeClientRepository(userRepo);
            proRepo = new FakeProRepository(userRepo);
            bidRepo = new FakeBidRepository();
            reqRepo = new FakeRequestRepository(bidRepo);
        }

        clientUserService = new ClientUserServiceImpl(clientRepo, userRepo);
        proUserService = new ProUserServiceImpl(proRepo, userRepo);
        authService = new AuthServiceImpl(userRepo, clientRepo, proRepo);
        reqService = new RequestServiceImpl(reqRepo);
        bidService = new BidServiceImpl(bidRepo);

        if (FAKE_REPO) {
            DatabaseSeeder.seed(clientUserService, proUserService, reqService, clientRepo);
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

    public BidService getBidService() { return bidService; }

    public ProfileLoader getProfileLoader(boolean isClient) {
        return isClient
                ? new ClientProfileLoader(clientUserService)
                : new ProProfileLoader(proUserService);
    }
}
