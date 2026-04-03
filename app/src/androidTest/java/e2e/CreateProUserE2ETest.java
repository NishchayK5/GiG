package e2e;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.g08.gig.R;
import com.g08.gig.presentation.StartUp;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class CreateProUserE2ETest {

    @Before
    public void clearDb() {
        E2ETestHelpers.clearDb();
    }

    @Test
    public void createProAccount_navigatesToLoginScreen() {
        ActivityScenario.launch(StartUp.class);

        onView(withId(R.id.proCreationButton)).perform(click());
        onView(withId(R.id.firstNameInput)).perform(replaceText("Marcus"), closeSoftKeyboard());
        onView(withId(R.id.lastNameInput)).perform(replaceText("Johnson"), closeSoftKeyboard());
        onView(withId(R.id.emailInput)).perform(replaceText("pro.e2e@test.com"), closeSoftKeyboard());
        onView(withId(R.id.phoneInput)).perform(replaceText("2045550002"), closeSoftKeyboard());
        onView(withId(R.id.passwordInput)).perform(scrollTo(), replaceText("Password1!"), closeSoftKeyboard());
        onView(withId(R.id.confirmPassword)).perform(scrollTo(), replaceText("Password1!"), closeSoftKeyboard());
        onView(withId(R.id.cityInput)).perform(scrollTo(), replaceText("Winnipeg"), closeSoftKeyboard());
        onView(withId(R.id.qualificationsInput)).perform(scrollTo(), replaceText("Brakes, Diagnostics"), closeSoftKeyboard());
        onView(withId(R.id.addProUserButton)).perform(scrollTo(), click());

        onView(withId(R.id.loginTitle)).check(matches(isDisplayed()));
        onView(withId(R.id.loginbutton)).check(matches(isDisplayed()));
    }
}
