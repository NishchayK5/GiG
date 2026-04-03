package e2e;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
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
public class ViewAppointmentE2ETest {

    @Before
    public void setUp() {
        E2ETestHelpers.clearDb();
        Client client = E2ETestHelpers.seedClient("Appt", "Client", "appt.client@test.com");
        Pro pro = E2ETestHelpers.seedPro("Appt", "Pro", "appt.pro@test.com");
        Request request = E2ETestHelpers.seedRequest(client, "Alternator replacement", "Need alternator checked");
        LocalDateTime start = LocalDateTime.now().plusDays(1).withHour(9).withMinute(0);
        LocalDateTime end = start.plusHours(1);
        GiGApp app = E2ETestHelpers.app();
        app.getApptService().addAppt(start, end, pro, client, request);
    }

    @Test
    public void appointmentForPro_showsInAppointmentsTab() {
        ActivityScenario.launch(Login.class);

        onView(withId(R.id.emailInput)).perform(replaceText("appt.pro@test.com"), closeSoftKeyboard());
        onView(withId(R.id.passwordInput)).perform(replaceText("Password1!"), closeSoftKeyboard());
        onView(withId(R.id.loginbutton)).perform(click());

        onView(withId(R.id.nav_appointment)).perform(click());
        onView(withId(R.id.appointmentsList)).check(matches(isDisplayed()));
    }
}
