package e2e;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.g08.gig.R;
import com.g08.gig.application.GiGApp;
import com.g08.gig.models.Client;
import com.g08.gig.models.Pro;
import com.g08.gig.models.Request;
import com.g08.gig.presentation.Login;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.LocalDateTime;

@RunWith(AndroidJUnit4.class)
public class AcceptBidAndLeaveReviewE2ETest {

    private Pro seededPro;

    @Before
    public void setUp() {
        E2ETestHelpers.clearDb();
        Client client = E2ETestHelpers.seedClient("Review", "Client", "review.client@test.com");
        seededPro = E2ETestHelpers.seedPro("Review", "Pro", "review.pro@test.com");
        Request request = E2ETestHelpers.seedRequest(client, "Battery replacement", "Need battery installed");
        E2ETestHelpers.seedBid(seededPro, request, 95.0, LocalDateTime.now().plusDays(1).withHour(11).withMinute(0));
    }

    @Test
    public void acceptBid_thenLeaveReview_movesRequestToPastAndPersistsReview() {
        ActivityScenario.launch(Login.class);

        onView(withId(R.id.emailInput)).perform(replaceText("review.client@test.com"), closeSoftKeyboard());
        onView(withId(R.id.passwordInput)).perform(replaceText("Password1!"), closeSoftKeyboard());
        onView(withId(R.id.loginbutton)).perform(click());

        onView(withText("Battery replacement")).perform(click());
        onView(withId(R.id.bidRowAcceptButton)).perform(click());
        onView(withId(R.id.dialogCompleteButton)).perform(click());
        onView(withText("Continue")).perform(click());

        onView(withId(R.id.completeReviewTitleInput)).perform(replaceText("Great work"), closeSoftKeyboard());
        onView(withId(R.id.completeReviewBodyInput)).perform(replaceText("Fast and professional"), closeSoftKeyboard());
        onView(withId(R.id.completeReviewRating)).perform(scrollTo(), E2ETestHelpers.setRating(4.0f));
        onView(withId(R.id.completeReviewSubmitButton)).perform(scrollTo(), click());

        onView(withText("Past requests")).perform(click());
        onView(withText("Battery replacement")).check(matches(isDisplayed()));

        GiGApp app = E2ETestHelpers.app();
        int reviewCount = app.getReviewService().getAllReviewsForPro(seededPro).size();
        if (reviewCount <= 0) {
            throw new AssertionError("Expected at least one review for the accepted pro");
        }
    }
}
