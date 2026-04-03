package integration;

import static org.junit.Assert.*;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.g08.gig.business.services.AppointmentServiceImpl;
import com.g08.gig.business.services.ClientUserServiceImpl;
import com.g08.gig.business.services.ProUserServiceImpl;
import com.g08.gig.business.services.RequestServiceImpl;
import com.g08.gig.models.Appointment;
import com.g08.gig.models.Client;
import com.g08.gig.models.Pro;
import com.g08.gig.models.Request;
import com.g08.gig.persistence.real.AppDbHelper;
import com.g08.gig.persistence.real.Appointment.SqlAppointmentRepository;
import com.g08.gig.persistence.real.Client.SqlClientRepository;
import com.g08.gig.persistence.real.Pro.SqlProRepository;
import com.g08.gig.persistence.real.Request.SqlRequestRepository;
import com.g08.gig.persistence.real.Skills.SqlSkillRepository;
import com.g08.gig.persistence.real.user.SqlUserRepository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.LocalDateTime;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class AppointmentSQLiteIntegrationTest {

    private static final String VALID_PASSWORD = "SecurePass1!";

    private AppDbHelper dbHelper;
    private SqlUserRepository userRepo;
    private SqlClientRepository clientRepo;
    private SqlProRepository proRepo;
    private SqlSkillRepository skillRepo;
    private SqlRequestRepository requestRepo;
    private SqlAppointmentRepository appointmentRepo;

    private ClientUserServiceImpl clientService;
    private ProUserServiceImpl proService;
    private RequestServiceImpl requestService;
    private AppointmentServiceImpl appointmentService;

    @Before
    public void setUp() {
        Context ctx = InstrumentationRegistry.getInstrumentation().getTargetContext();
        ctx.deleteDatabase(AppDbHelper.DB_NAME);

        dbHelper = new AppDbHelper(ctx);
        userRepo = new SqlUserRepository(dbHelper);
        clientRepo = new SqlClientRepository(dbHelper);
        skillRepo = new SqlSkillRepository(dbHelper);
        requestRepo = new SqlRequestRepository(dbHelper, clientRepo);
        proRepo = new SqlProRepository(dbHelper, skillRepo);
        appointmentRepo = new SqlAppointmentRepository(dbHelper, clientRepo, proRepo, requestRepo);

        clientService = new ClientUserServiceImpl(clientRepo, userRepo);
        proService = new ProUserServiceImpl(proRepo, userRepo, skillRepo);
        requestService = new RequestServiceImpl(requestRepo);
        appointmentService = new AppointmentServiceImpl(appointmentRepo);
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
    public void addAppt_persistsToSQLiteAndCanBeRetrievedById() {
        Client client = addTestClient("appt.client1@test.com", "2043000001");
        Pro pro = addTestPro("appt.pro1@test.com", "2044000001");
        Request request = requestService.addRequest(client, "Brake Repair", "Need new pads");

        LocalDateTime start = LocalDateTime.of(2026, 4, 10, 9, 0);
        LocalDateTime end = LocalDateTime.of(2026, 4, 10, 10, 30);

        Appointment added = appointmentService.addAppt(start, end, pro, client, request);

        assertNotNull(added);
        assertTrue(added.getId() > 0);

        Appointment fromDb = appointmentService.getApptById(added.getId());
        assertNotNull(fromDb);
        assertEquals(pro.getId(), fromDb.getPro().getId());
        assertEquals(client.getId(), fromDb.getClient().getId());
        assertEquals(request.getId(), fromDb.getRequest().getId());
        assertEquals(start, fromDb.getAppointmentStart());
        assertEquals(end, fromDb.getAppointmentEnd());
    }

    @Test
    public void getAllApptsByPro_returnsOnlyAppointmentsForThatPro() {
        Client client1 = addTestClient("appt.client2@test.com", "2043000002");
        Client client2 = addTestClient("appt.client3@test.com", "2043000003");
        Pro pro1 = addTestPro("appt.pro2@test.com", "2044000002");
        Pro pro2 = addTestPro("appt.pro3@test.com", "2044000003");

        Request req1 = requestService.addRequest(client1, "Oil Change", "Need oil");
        Request req2 = requestService.addRequest(client2, "Tires", "Need swap");
        Request req3 = requestService.addRequest(client1, "Battery", "Need replacement");

        appointmentService.addAppt(LocalDateTime.of(2026, 4, 11, 8, 0), LocalDateTime.of(2026, 4, 11, 9, 0), pro1, client1, req1);
        appointmentService.addAppt(LocalDateTime.of(2026, 4, 11, 10, 0), LocalDateTime.of(2026, 4, 11, 11, 0), pro1, client2, req2);
        appointmentService.addAppt(LocalDateTime.of(2026, 4, 11, 12, 0), LocalDateTime.of(2026, 4, 11, 13, 0), pro2, client1, req3);

        List<Appointment> pro1Appointments = appointmentService.getAllApptsByPro(pro1);
        List<Appointment> pro2Appointments = appointmentService.getAllApptsByPro(pro2);

        assertEquals(2, pro1Appointments.size());
        assertEquals(1, pro2Appointments.size());
        assertEquals(req3.getId(), pro2Appointments.get(0).getRequest().getId());
    }

    @Test
    public void getAllApptsByClient_returnsOnlyAppointmentsForThatClient() {
        Client client1 = addTestClient("appt.client4@test.com", "2043000004");
        Client client2 = addTestClient("appt.client5@test.com", "2043000005");
        Pro pro = addTestPro("appt.pro4@test.com", "2044000004");

        Request req1 = requestService.addRequest(client1, "Starter", "Car will not start");
        Request req2 = requestService.addRequest(client1, "AC Repair", "AC not cold");
        Request req3 = requestService.addRequest(client2, "Brake Repair", "Brake noise");

        appointmentService.addAppt(LocalDateTime.of(2026, 4, 12, 9, 0), LocalDateTime.of(2026, 4, 12, 10, 0), pro, client1, req1);
        appointmentService.addAppt(LocalDateTime.of(2026, 4, 12, 11, 0), LocalDateTime.of(2026, 4, 12, 12, 0), pro, client1, req2);
        appointmentService.addAppt(LocalDateTime.of(2026, 4, 12, 13, 0), LocalDateTime.of(2026, 4, 12, 14, 0), pro, client2, req3);

        List<Appointment> client1Appointments = appointmentService.getAllApptsByClient(client1);
        List<Appointment> client2Appointments = appointmentService.getAllApptsByClient(client2);

        assertEquals(2, client1Appointments.size());
        assertEquals(1, client2Appointments.size());
        assertEquals(req3.getId(), client2Appointments.get(0).getRequest().getId());
    }

    @Test
    public void getAppointmentOfRequest_returnsMatchingAppointment() {
        Client client = addTestClient("appt.client6@test.com", "2043000006");
        Pro pro = addTestPro("appt.pro5@test.com", "2044000005");
        Request request = requestService.addRequest(client, "Diagnostics", "Check engine light on");

        Appointment added = appointmentService.addAppt(
                LocalDateTime.of(2026, 4, 13, 15, 0),
                LocalDateTime.of(2026, 4, 13, 16, 0),
                pro,
                client,
                request
        );

        Appointment fromRepo = appointmentRepo.getAppointmentOfRequest(request);

        assertNotNull(fromRepo);
        assertEquals(added.getId(), fromRepo.getId());
        assertEquals(request.getId(), fromRepo.getRequest().getId());
    }

    @Test
    public void deleteRequest_cascadesToDeleteAppointment() {
        Client client = addTestClient("appt.client7@test.com", "2043000007");
        Pro pro = addTestPro("appt.pro6@test.com", "2044000006");
        Request request = requestService.addRequest(client, "Tire Rotation", "Need tires rotated");

        Appointment appointment = appointmentService.addAppt(
                LocalDateTime.of(2026, 4, 14, 10, 0),
                LocalDateTime.of(2026, 4, 14, 11, 0),
                pro,
                client,
                request
        );

        assertNotNull(appointmentRepo.getById(appointment.getId()));

        requestService.removeRequest(request.getId());

        assertNull(appointmentRepo.getById(appointment.getId()));
        assertNull(requestRepo.getById(request.getId()));
    }

    private Client addTestClient(String email, String phone) {
        return clientService.addClient(
                "Dayaveer",
                "Gill",
                email,
                VALID_PASSWORD,
                VALID_PASSWORD,
                phone,
                "123 Main St"
        );
    }

    private Pro addTestPro(String email, String phone) {
        return proService.addPro(
                "Izzy",
                "Elskamp",
                email,
                phone,
                VALID_PASSWORD,
                VALID_PASSWORD,
                new String[]{"Brakes", "Electrical"},
                "Winnipeg"
        );
    }
}
