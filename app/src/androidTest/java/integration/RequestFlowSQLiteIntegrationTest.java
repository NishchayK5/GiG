package integration;

import static org.junit.Assert.*;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.g08.gig.business.services.AuthServiceImpl;
import com.g08.gig.business.services.BidServiceImpl;
import com.g08.gig.business.services.ClientUserServiceImpl;
import com.g08.gig.business.services.LoginResult;
import com.g08.gig.business.services.ProUserServiceImpl;
import com.g08.gig.business.services.RequestServiceImpl;
import com.g08.gig.models.Bid;
import com.g08.gig.models.Client;
import com.g08.gig.models.Pro;
import com.g08.gig.models.Request;
import com.g08.gig.persistence.real.AppDbHelper;
import com.g08.gig.persistence.real.Bid.SqlBidRepository;
import com.g08.gig.persistence.real.Client.SqlClientRepository;
import com.g08.gig.persistence.real.Pro.SqlProRepository;
import com.g08.gig.persistence.real.Request.SqlRequestRepository;
import com.g08.gig.persistence.real.Skills.SqlSkillRepository;
import com.g08.gig.persistence.real.user.SqlUserRepository;
import at.favre.lib.crypto.bcrypt.BCrypt;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.LocalDateTime;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class RequestFlowSQLiteIntegrationTest {

    private AppDbHelper dbHelper;
    private SqlUserRepository userRepo;
    private SqlClientRepository clientRepo;
    private SqlSkillRepository skillRepo;
    private SqlProRepository proRepo;
    private SqlRequestRepository requestRepo;
    private SqlBidRepository bidRepo;

    private ClientUserServiceImpl clientService;
    private ProUserServiceImpl proService;
    private RequestServiceImpl requestService;
    private BidServiceImpl bidService;
    private AuthServiceImpl authService;

    private static final String CLIENT_EMAIL = "client@gmail.com";
    private static final String PRO_EMAIL = "pro@test.com";
    private static final String VALID_PASSWORD = "SecurePass1!";
    private static final String CLIENT_PHONE = "2041111111";
    private static final String PRO_PHONE = "2042222222";

    @Before
    public void setUp() {
        Context ctx = InstrumentationRegistry.getInstrumentation().getTargetContext();
        ctx.deleteDatabase(AppDbHelper.DB_NAME);

        dbHelper = new AppDbHelper(ctx);
        userRepo = new SqlUserRepository(dbHelper);
        clientRepo = new SqlClientRepository(dbHelper);
        skillRepo = new SqlSkillRepository(dbHelper);
        proRepo = new SqlProRepository(dbHelper, skillRepo);
        requestRepo = new SqlRequestRepository(dbHelper, clientRepo);
        bidRepo = new SqlBidRepository(dbHelper, proRepo, requestRepo);

        clientService = new ClientUserServiceImpl(clientRepo, userRepo);
        proService = new ProUserServiceImpl(proRepo, userRepo, skillRepo);
        requestService = new RequestServiceImpl(requestRepo);
        bidService = new BidServiceImpl(bidRepo);
        authService = new AuthServiceImpl(userRepo, clientRepo, proRepo);
    }

    @After
    public void tearDown() {
        if (dbHelper != null) {
            dbHelper.close();
        }
        Context ctx = InstrumentationRegistry.getInstrumentation().getTargetContext();
        ctx.deleteDatabase(AppDbHelper.DB_NAME);
    }

    @Test
    public void addClient_persistsToSQLiteAndCanBeRetrieved() {
        Client added = clientService.addClient(
                "Dayaveer", "Gill", CLIENT_EMAIL, VALID_PASSWORD, VALID_PASSWORD,
                CLIENT_PHONE, "123 Main St, Winnipeg");

        assertNotNull(added);
        assertTrue(added.getId() > 0);

        Client fromDb = clientRepo.getById(added.getId());
        assertNotNull(fromDb);
        assertEquals(CLIENT_EMAIL, fromDb.getEmail());
        assertEquals("Dayaveer", fromDb.getFirstName());
        assertEquals("Gill", fromDb.getLastName());
        assertEquals(CLIENT_PHONE, fromDb.getPhoneNumber());
        assertEquals("123 Main St, Winnipeg", fromDb.getAddress());
    }

    @Test
    public void addClient_passwordIsHashedInDatabase() {
        Client added = clientService.addClient(
                "Dayaveer", "Gill", CLIENT_EMAIL, VALID_PASSWORD, VALID_PASSWORD,
                CLIENT_PHONE, "123 Main St");

        Client fromDb = clientRepo.getById(added.getId());
        assertNotNull(fromDb);
        assertNotEquals(VALID_PASSWORD, fromDb.getPassword());
        assertTrue(
                BCrypt.verifyer()
                        .verify(VALID_PASSWORD.toCharArray(), fromDb.getPassword())
                        .verified
        );
    }

    @Test
    public void updateClient_changesArePersisted() {
        Client added = clientService.addClient(
                "Dayaveer", "Gill", CLIENT_EMAIL, VALID_PASSWORD, VALID_PASSWORD,
                CLIENT_PHONE, "Old Address");

        boolean result = clientService.updateClient(
                added.getId(), "Dayaveer", "Gill-New",
                "client1@gmail.com", "2041111112", "New Address");

        assertTrue(result);
        Client fromDb = clientRepo.getById(added.getId());
        assertNotNull(fromDb);
        assertEquals("client1@gmail.com", fromDb.getEmail());
        assertEquals("Gill-New", fromDb.getLastName());
        assertEquals("New Address", fromDb.getAddress());
        assertEquals("2041111112", fromDb.getPhoneNumber());
    }

    @Test
    public void deleteClient_removesFromSQLite() {
        Client added = clientService.addClient(
                "Dayaveer", "Gill", CLIENT_EMAIL, VALID_PASSWORD, VALID_PASSWORD,
                CLIENT_PHONE, "123 Main St");
        int id = added.getId();

        assertTrue(clientService.deleteClient(id));
        assertNull(clientRepo.getById(id));
    }

    @Test
    public void addPro_persistsToSQLiteAndCanBeRetrieved() {
        Pro added = proService.addPro(
                "Izzy", "Elskamp", PRO_EMAIL, PRO_PHONE,
                VALID_PASSWORD, VALID_PASSWORD,
                new String[]{"Oil Change", "Brakes"}, "Winnipeg");

        assertNotNull(added);
        assertTrue(added.getId() > 0);

        Pro fromDb = proRepo.getById(added.getId());
        assertNotNull(fromDb);
        assertEquals(PRO_EMAIL, fromDb.getEmail());
        assertEquals("Izzy", fromDb.getFirstName());
        assertEquals("Winnipeg", fromDb.getCityOfOperation());
    }

    @Test
    public void addPro_passwordIsHashedInDatabase() {
        Pro added = proService.addPro(
                "Izzy", "Elskamp", PRO_EMAIL, PRO_PHONE,
                VALID_PASSWORD, VALID_PASSWORD,
                new String[]{"Oil Change"}, "Winnipeg");

        Pro fromDb = proRepo.getById(added.getId());
        assertNotNull(fromDb);
        assertNotEquals(VALID_PASSWORD, fromDb.getPassword());
        assertTrue(
                BCrypt.verifyer()
                        .verify(VALID_PASSWORD.toCharArray(), fromDb.getPassword())
                        .verified
        );
    }

    @Test
    public void updatePro_changesArePersisted() {
        Pro added = proService.addPro(
                "Izzy", "Elskamp", PRO_EMAIL, PRO_PHONE,
                VALID_PASSWORD, VALID_PASSWORD,
                new String[]{"Oil Change"}, "Winnipeg");

        Pro beforeUpdate = proRepo.getById(added.getId());
        assertNotNull(beforeUpdate);
        assertEquals(PRO_EMAIL, beforeUpdate.getEmail());
        assertEquals("Elskamp", beforeUpdate.getLastName());
        assertEquals(PRO_PHONE, beforeUpdate.getPhoneNumber());
        assertArrayEquals(new String[]{"Oil Change"}, beforeUpdate.getSkillSet());
        assertEquals("Winnipeg", beforeUpdate.getCityOfOperation());

        boolean result = proService.updatePro(
                added.getId(), "pro1@gmail.com",
                "Izzy", "Elskamp-New", "2044444444",
                new String[]{"Brakes", "AC"}, "Brandon");

        assertTrue(result);
        Pro fromDb = proRepo.getById(added.getId());
        assertNotNull(fromDb);
        assertEquals("pro1@gmail.com", fromDb.getEmail());
        assertEquals("Elskamp-New", fromDb.getLastName());
        assertEquals("2044444444", fromDb.getPhoneNumber());
        assertArrayEquals(new String[]{"Brakes", "AC"}, fromDb.getSkillSet());
        assertEquals("Brandon", fromDb.getCityOfOperation());
    }

    @Test
    public void addRequest_persistsToSQLiteAndCanBeRetrieved() {
        Client client = addTestClient();
        Request added = requestService.addRequest(client, "Brake Repair", "Need new pads");

        assertNotNull(added);
        assertTrue(added.getId() > 0);

        Request fromDb = requestRepo.getById(added.getId());
        assertNotNull(fromDb);
        assertEquals("Brake Repair", fromDb.getTitle());
        assertEquals("Need new pads", fromDb.getBody());
        assertTrue(fromDb.getRequestStatus());
        assertNotNull(fromDb.getClient());
        assertEquals(client.getId(), fromDb.getClient().getId());
    }

    @Test
    public void getAllRequests_returnsAllPersistedRequests() {
        Client client = addTestClient();
        requestService.addRequest(client, "Oil Change", "Due for oil change");
        requestService.addRequest(client, "Tire Rotation", "Need rotation");

        assertEquals(2, requestService.getAllRequests().size());
    }

    @Test
    public void getRequestsByClient_returnsOnlyThatClientsRequests() {
        Client client1 = addTestClient();
        Client client2 = clientService.addClient(
                "Adejare", "Taiwo", "client2@gmail.com", VALID_PASSWORD, VALID_PASSWORD,
                "2045555555", "200 Oak Ave");

        requestService.addRequest(client1, "Client1 Request A", "Body A");
        requestService.addRequest(client1, "Client1 Request B", "Body B");
        requestService.addRequest(client2, "Client2 Request", "Body C");

        assertEquals(2, requestService.getRequestsByClient(client1.getId()).size());

        List<Request> c2 = requestService.getRequestsByClient(client2.getId());
        assertEquals(1, c2.size());
        assertEquals("Client2 Request", c2.get(0).getTitle());
    }

    @Test
    public void completeRequest_setsStatusToInactiveInSQLite() {
        Client client = addTestClient();
        Request added = requestService.addRequest(client, "Oil Change", "Need oil");

        requestService.completeRequest(added.getId());

        Request fromDb = requestRepo.getById(added.getId());
        assertNotNull(fromDb);
        assertFalse(fromDb.getRequestStatus());
    }

    @Test
    public void getRequestsByStatus_returnsCorrectSubset() {
        Client client = addTestClient();
        requestService.addRequest(client, "Active A", "Body");
        requestService.addRequest(client, "Active B", "Body");
        Request toClose = requestService.addRequest(client, "Will Close", "Body");
        requestService.completeRequest(toClose.getId());

        assertEquals(2, requestService.getRequestsByStatus(true).size());
        assertEquals(1, requestService.getRequestsByStatus(false).size());
    }

    @Test
    public void removeRequest_deletesFromSQLite() {
        Client client = addTestClient();
        Request added = requestService.addRequest(client, "Temp Request", "Body");
        int id = added.getId();

        requestService.removeRequest(id);

        assertNull(requestRepo.getById(id));
    }

    @Test
    public void requestService_trimsWhitespaceBeforePersisting() {
        Client client = addTestClient();
        requestService.addRequest(client, "  Engine Diagnostic  ", "  Check engine light on  ");

        List<Request> all = requestRepo.getAll();
        assertEquals(1, all.size());
        assertEquals("Engine Diagnostic", all.get(0).getTitle());
        assertEquals("Check engine light on", all.get(0).getBody());
    }

    @Test
    public void addBid_persistsToSQLiteAndCanBeRetrieved() {
        Client client = addTestClient();
        Pro pro = addTestPro();
        Request request = requestService.addRequest(client, "Brake Repair", "Need pads");

        LocalDateTime bidTime = LocalDateTime.of(2026, 4, 2, 10, 0);
        Bid added = bidService.addBid(pro, request, 150.00, bidTime);

        assertNotNull(added);
        assertTrue(added.getId() > 0);

        Bid fromDb = bidRepo.getById(added.getId());
        assertNotNull(fromDb);
        assertEquals(150.00, fromDb.getCost(), 0.001);
        assertEquals(pro.getId(), fromDb.getPro().getId());
        assertEquals(request.getId(), fromDb.getRequest().getId());
        assertEquals(bidTime, fromDb.getTimeOfBid());
    }

    @Test
    public void getAllByRequest_returnsAllBidsForThatRequest() {
        Client client = addTestClient();
        Pro pro = addTestPro();
        Request request = requestService.addRequest(client, "Oil Change", "Need oil");
        Pro pro2 = proService.addPro(
                "Nishchay", "Kathuria", "pro2@gmail.com", "2046666666",
                VALID_PASSWORD, VALID_PASSWORD, new String[]{"Oil Change"}, "Winnipeg");

        bidService.addBid(pro, request, 80.00, LocalDateTime.of(2026, 4, 2, 11, 0));
        bidService.addBid(pro2, request, 95.00, LocalDateTime.of(2026, 4, 2, 12, 0));

        assertEquals(2, bidService.getAllByRequest(request).size());
    }

    @Test
    public void getAllByPro_returnsOnlyThatProsBids() {
        Client client = addTestClient();
        Pro pro1 = addTestPro();
        Pro pro2 = proService.addPro(
                "Other", "Pro", "other.pro@gmail.com", "2047777777",
                VALID_PASSWORD, VALID_PASSWORD, new String[]{"AC Repair"}, "Winnipeg");

        Request req1 = requestService.addRequest(client, "Brakes", "Need pads");
        Request req2 = requestService.addRequest(client, "Oil Change", "Need oil");

        bidService.addBid(pro1, req1, 120.00, LocalDateTime.of(2026, 4, 2, 13, 0));
        bidService.addBid(pro1, req2, 75.00, LocalDateTime.of(2026, 4, 2, 14, 0));
        bidService.addBid(pro2, req1, 99.00, LocalDateTime.of(2026, 4, 2, 15, 0));

        assertEquals(2, bidService.getAllByPro(pro1).size());

        List<Bid> pro2Bids = bidService.getAllByPro(pro2);
        assertEquals(1, pro2Bids.size());
        assertEquals(99.00, pro2Bids.get(0).getCost(), 0.001);
    }

    @Test
    public void bid_costStoredAsCentsRoundTripsCorrectly() {
        Client client = addTestClient();
        Pro pro = addTestPro();
        Request request = requestService.addRequest(client, "AC Repair", "AC broken");

        bidService.addBid(pro, request, 199.99, LocalDateTime.of(2026, 4, 2, 16, 0));

        List<Bid> bids = bidService.getAllByRequest(request);
        assertEquals(1, bids.size());
        assertEquals(199.99, bids.get(0).getCost(), 0.001);
    }

    @Test
    public void acceptBid_updatesAcceptedStatusInSQLite() {
        Client client = addTestClient();
        Pro pro = addTestPro();
        Request request = requestService.addRequest(client, "Alternator", "Battery light is on");

        Bid bid = bidService.addBid(pro, request, 175.00, LocalDateTime.of(2026, 4, 2, 17, 0));
        bidService.acceptBid(bid);

        Bid fromDb = bidRepo.getById(bid.getId());
        assertNotNull(fromDb);
        assertTrue(fromDb.getBidStatus());

        Bid accepted = bidService.getAcceptedBidByRequest(request);
        assertNotNull(accepted);
        assertEquals(bid.getId(), accepted.getId());
    }

    @Test
    public void login_clientWithCorrectCredentials_succeeds() {
        clientService.addClient(
                "Dayaveer", "Gill", CLIENT_EMAIL, VALID_PASSWORD, VALID_PASSWORD,
                CLIENT_PHONE, "123 Main St");

        LoginResult result = authService.login(CLIENT_EMAIL, VALID_PASSWORD);

        assertTrue(result.isSuccess());
        assertTrue(result.isClient());
        assertFalse(result.isPro());
    }

    @Test
    public void login_proWithCorrectCredentials_succeeds() {
        proService.addPro(
                "Izzy", "Elskamp", PRO_EMAIL, PRO_PHONE,
                VALID_PASSWORD, VALID_PASSWORD, new String[]{"Brakes"}, "Winnipeg");

        LoginResult result = authService.login(PRO_EMAIL, VALID_PASSWORD);

        assertTrue(result.isSuccess());
        assertTrue(result.isPro());
        assertFalse(result.isClient());
    }

    @Test
    public void login_wrongPassword_fails() {
        clientService.addClient(
                "Dayaveer", "Gill", CLIENT_EMAIL, VALID_PASSWORD, VALID_PASSWORD,
                CLIENT_PHONE, "123 Main St");

        assertFalse(authService.login(CLIENT_EMAIL, "WrongPassword1!").isSuccess());
    }

    @Test
    public void login_unknownEmail_fails() {
        assertFalse(authService.login("nobody@nowhere.com", VALID_PASSWORD).isSuccess());
    }

    @Test
    public void login_emailIsCaseInsensitive() {
        clientService.addClient(
                "Dayaveer", "Gill", CLIENT_EMAIL, VALID_PASSWORD, VALID_PASSWORD,
                CLIENT_PHONE, "123 Main St");

        assertTrue(authService.login(CLIENT_EMAIL.toUpperCase(), VALID_PASSWORD).isSuccess());
    }

    @Test
    public void login_returnsCorrectUserId() {
        Client added = clientService.addClient(
                "Dayaveer", "Gill", CLIENT_EMAIL, VALID_PASSWORD, VALID_PASSWORD,
                CLIENT_PHONE, "123 Main St");

        LoginResult result = authService.login(CLIENT_EMAIL, VALID_PASSWORD);

        assertTrue(result.isSuccess());
        assertEquals(added.getId(), result.getUserId());
    }

    @Test
    public void deleteRequest_cascadesAndRemovesAssociatedBids() {
        Client client = addTestClient();
        Pro pro = addTestPro();
        Request request = requestService.addRequest(client, "Oil Change", "Need oil");
        Bid bid = bidService.addBid(pro, request, 75.00, LocalDateTime.of(2026, 4, 2, 18, 0));

        assertNotNull(bidRepo.getById(bid.getId()));

        requestService.removeRequest(request.getId());

        assertNull(bidRepo.getById(bid.getId()));
        assertNull(requestRepo.getById(request.getId()));
    }

    @Test
    public void deleteClient_cascadesAndRemovesRequestsAndBids() {
        Client client = addTestClient();
        Pro pro = addTestPro();
        Request request = requestService.addRequest(client, "Brake Repair", "Need pads");
        Bid bid = bidService.addBid(pro, request, 120.00, LocalDateTime.of(2026, 4, 2, 19, 0));
        int requestId = request.getId();
        int bidId = bid.getId();

        clientService.deleteClient(client.getId());

        assertNull(clientRepo.getById(client.getId()));
        assertNull(requestRepo.getById(requestId));
        assertNull(bidRepo.getById(bidId));
    }

    @Test
    public void freshDatabase_hasNoRecords() {
        assertTrue(clientRepo.getAll().isEmpty());
        assertTrue(proRepo.getAll().isEmpty());
        assertTrue(requestRepo.getAll().isEmpty());
        assertTrue(bidRepo.getAll().isEmpty());
    }

    @Test
    public void multipleClients_areStoredAndRetrievedCorrectly() {
        clientService.addClient("Alice", "A", "alice@test.com", VALID_PASSWORD, VALID_PASSWORD, "2041000001", "1 Alice St");
        clientService.addClient("Bob", "B", "bob@test.com", VALID_PASSWORD, VALID_PASSWORD, "2041000002", "2 Bob Ave");
        clientService.addClient("Carol", "C", "carol@test.com", VALID_PASSWORD, VALID_PASSWORD, "2041000003", "3 Carol Rd");

        assertEquals(3, clientRepo.getAll().size());
    }

    private Client addTestClient() {
        return clientService.addClient(
                "Sarah", "Jones", CLIENT_EMAIL, VALID_PASSWORD, VALID_PASSWORD,
                CLIENT_PHONE, "123 Main St, Winnipeg");
    }

    private Pro addTestPro() {
        return proService.addPro(
                "Marcus", "Lee", PRO_EMAIL, PRO_PHONE,
                VALID_PASSWORD, VALID_PASSWORD,
                new String[]{"Oil Change", "Brakes"}, "Winnipeg");
    }
}
