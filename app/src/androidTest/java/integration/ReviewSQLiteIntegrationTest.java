package integration;

import static org.junit.Assert.*;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.g08.gig.business.validation.ValidationException;
import com.g08.gig.business.services.ClientUserServiceImpl;
import com.g08.gig.business.services.ProUserServiceImpl;
import com.g08.gig.business.services.ReviewServiceImpl;
import com.g08.gig.models.Client;
import com.g08.gig.models.Pro;
import com.g08.gig.models.Review;
import com.g08.gig.persistence.real.AppDbHelper;
import com.g08.gig.persistence.real.Client.SqlClientRepository;
import com.g08.gig.persistence.real.Pro.SqlProRepository;
import com.g08.gig.persistence.real.Review.SqlReviewRepository;
import com.g08.gig.persistence.real.Skills.SqlSkillRepository;
import com.g08.gig.persistence.real.user.SqlUserRepository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class ReviewSQLiteIntegrationTest {

    private static final String VALID_PASSWORD = "SecurePass1!";

    private AppDbHelper dbHelper;
    private SqlUserRepository userRepo;
    private SqlClientRepository clientRepo;
    private SqlProRepository proRepo;
    private SqlSkillRepository skillRepo;
    private SqlReviewRepository reviewRepo;

    private ClientUserServiceImpl clientService;
    private ProUserServiceImpl proService;
    private ReviewServiceImpl reviewService;

    @Before
    public void setUp() {
        Context ctx = InstrumentationRegistry.getInstrumentation().getTargetContext();
        ctx.deleteDatabase(AppDbHelper.DB_NAME);

        dbHelper = new AppDbHelper(ctx);
        userRepo = new SqlUserRepository(dbHelper);
        clientRepo = new SqlClientRepository(dbHelper);
        skillRepo = new SqlSkillRepository(dbHelper);
        proRepo = new SqlProRepository(dbHelper, skillRepo);
        reviewRepo = new SqlReviewRepository(dbHelper, clientRepo, proRepo);

        clientService = new ClientUserServiceImpl(clientRepo, userRepo);
        proService = new ProUserServiceImpl(proRepo, userRepo, skillRepo);
        reviewService = new ReviewServiceImpl(reviewRepo);
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
    public void addReview_persistsTrimmedTitleToSQLite() {
        Client client = addTestClient("review.client1@test.com", "2045000001");
        Pro pro = addTestPro("review.pro1@test.com", "2046000001");

        Review added = reviewService.addReview(
                client,
                pro,
                "  Great work  ",
                "Fixed the issue quickly",
                5
        );

        assertNotNull(added);
        assertTrue(added.getId() > 0);

        Review fromDb = reviewRepo.getById(added.getId());
        assertNotNull(fromDb);
        assertEquals(client.getId(), fromDb.getReviewer().getId());
        assertEquals(pro.getId(), fromDb.getPro().getId());
        assertEquals("Great work", fromDb.getTitle());
        assertEquals("Fixed the issue quickly", fromDb.getBody());
        assertEquals(5, fromDb.getStars());
    }

    @Test
    public void getAllReviewsForPro_returnsOnlyThatProsReviews() {
        Client client1 = addTestClient("review.client2@test.com", "2045000002");
        Client client2 = addTestClient("review.client3@test.com", "2045000003");
        Pro pro1 = addTestPro("review.pro2@test.com", "2046000002");
        Pro pro2 = addTestPro("review.pro3@test.com", "2046000003");

        reviewService.addReview(client1, pro1, "Excellent", "Very professional", 5);
        reviewService.addReview(client2, pro1, "Solid", "Arrived on time", 4);
        reviewService.addReview(client1, pro2, "Okay", "Job got done", 3);

        List<Review> pro1Reviews = reviewService.getAllReviewsForPro(pro1);
        List<Review> pro2Reviews = reviewService.getAllReviewsForPro(pro2);

        assertEquals(2, pro1Reviews.size());
        assertEquals(1, pro2Reviews.size());
        assertEquals("Okay", pro2Reviews.get(0).getTitle());
    }

    @Test
    public void getReviewById_returnsPersistedReview() {
        Client client = addTestClient("review.client4@test.com", "2045000004");
        Pro pro = addTestPro("review.pro4@test.com", "2046000004");

        Review added = reviewService.addReview(client, pro, "Fast service", "Problem solved in one visit", 5);
        Review fromService = reviewService.getReviewById(added.getId());

        assertNotNull(fromService);
        assertEquals(added.getId(), fromService.getId());
        assertEquals("Fast service", fromService.getTitle());
        assertEquals(5, fromService.getStars());
    }

    @Test
    public void deleteReview_removesRowFromSQLite() {
        Client client = addTestClient("review.client5@test.com", "2045000005");
        Pro pro = addTestPro("review.pro5@test.com", "2046000005");

        Review added = reviewService.addReview(client, pro, "Helpful", "Explained everything clearly", 5);
        assertNotNull(reviewRepo.getById(added.getId()));

        assertTrue(reviewService.deleteReview(added.getId()));
        assertNull(reviewRepo.getById(added.getId()));
    }

    @Test(expected = ValidationException.class)
    public void addReview_blankTitle_throwsValidationException() {
        Client client = addTestClient("review.client6@test.com", "2045000006");
        Pro pro = addTestPro("review.pro6@test.com", "2046000006");

        reviewService.addReview(client, pro, "   ", "Body still present", 4);
    }

    @Test
    public void deletePro_cascadesToDeleteAssociatedReviews() {
        Client client = addTestClient("review.client7@test.com", "2045000007");
        Pro pro = addTestPro("review.pro7@test.com", "2046000007");

        Review review = reviewService.addReview(client, pro, "Reliable", "Would hire again", 5);
        assertNotNull(reviewRepo.getById(review.getId()));

        proService.deletePro(pro.getId());

        assertNull(proRepo.getById(pro.getId()));
        assertNull(reviewRepo.getById(review.getId()));
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
                new String[]{"Brakes", "Diagnostics"},
                "Winnipeg"
        );
    }
}
