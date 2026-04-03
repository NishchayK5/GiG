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
import com.g08.gig.presentation.Login;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class CreateRequestFromClientE2ETest {

    @Before
    public void setUp() {
        E2ETestHelpers.clearDb();
        E2ETestHelpers.seedClient("Create", "Request", "request.client@test.com");
    }

    @Test
    public void createRequest_fromClientHome_showsInList() {
        ActivityScenario.launch(Login.class);

        onView(withId(R.id.emailInput)).perform(replaceText("request.client@test.com"), closeSoftKeyboard());
        onView(withId(R.id.passwordInput)).perform(replaceText("Password1!"), closeSoftKeyboard());
        onView(withId(R.id.loginbutton)).perform(click());

        onView(withId(R.id.nav_new_request)).perform(click());
        onView(withId(R.id.requestTitleInput)).perform(replaceText("Brake repair"), closeSoftKeyboard());
        onView(withId(R.id.requestDescriptionInput)).perform(replaceText("Need front pads replaced"), closeSoftKeyboard());
        onView(withId(R.id.submitRequestButton)).perform(click());

        onView(withText("Brake repair")).check(matches(isDisplayed()));
    }
}
