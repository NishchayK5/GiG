package e2e;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Context;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.g08.gig.R;
import com.g08.gig.persistence.real.AppDbHelper;
import com.g08.gig.presentation.StartUp;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class CreateClientUserE2ETest {

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        E2ETestHelpers.resetAppState(context);
    }

    @After
    public void tearDown() {
        Context context = ApplicationProvider.getApplicationContext();
        E2ETestHelpers.resetAppState(context);
    }

    @Test
    public void createClientAccount_navigatesToLoginScreen() {
        ActivityScenario.launch(StartUp.class);

        onView(withId(R.id.clientCreationButton)).perform(click());

        onView(withId(R.id.firstNameInput))
                .perform(replaceText("Daya"), closeSoftKeyboard());

        onView(withId(R.id.lastNameInput))
                .perform(replaceText("Gill"), closeSoftKeyboard());

        onView(withId(R.id.emailInput))
                .perform(replaceText("client_create@test.com"), closeSoftKeyboard());
        onView(withId(R.id.phoneInput))
                .perform(replaceText("2045551111"), closeSoftKeyboard());

        onView(withId(R.id.addressInput))
                .perform(replaceText("123 Main St"), closeSoftKeyboard());

        onView(withId(R.id.passwordInput))
                .perform(replaceText("Password1!"), closeSoftKeyboard());

        onView(withId(R.id.confirmPassword))
                .perform(replaceText("Password1!"), closeSoftKeyboard());

        //onView(withId(R.id.addCustomerUserButton)).perform(click());
        onView(withId(R.id.addCustomerUserButton)).perform(click());
        onView(withId(R.id.loginbutton)).check(matches(isDisplayed()));
    }
}