package e2e;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.g08.gig.R;
import com.g08.gig.presentation.Login;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LoginClientE2ETest {

    @Before
    public void setUp() {
        E2ETestHelpers.clearDb();
        E2ETestHelpers.seedClient("Dayaveer", "Gill", "client@gmail.com");
    }

    @Test
    public void loginClient_opensClientHome() {
        ActivityScenario.launch(Login.class);

        onView(withId(R.id.emailInput)).perform(replaceText("client@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.passwordInput)).perform(replaceText("Password1!"), closeSoftKeyboard());
        onView(withId(R.id.loginbutton)).perform(click());

        onView(withId(R.id.requestsList)).check(matches(isDisplayed()));
        onView(withId(R.id.bottomNav)).check(matches(isDisplayed()));
    }
}
