package e2e;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.g08.gig.R;
import com.g08.gig.models.Client;
import com.g08.gig.models.Request;
import com.g08.gig.presentation.Login;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertEquals;


import java.time.LocalDate;

@RunWith(AndroidJUnit4.class)
public class SubmitBidFromProE2ETest {
    private Request seededRequest;

    @Before
    public void setUp() {
        E2ETestHelpers.clearDb();
        Client client = E2ETestHelpers.seedClient("Dayaveer", "Gill", "client@gmail.com");
        E2ETestHelpers.seedPro("Izzy", "Elskamp", "pro@gmail.com");
         seededRequest = E2ETestHelpers.seedRequest(client, "Oil change", "Need synthetic oil change");
    }

    @Test
    public void submitBidFromPro_withProposedTime_showsInBidList() {
        ActivityScenario.launch(Login.class);

        onView(withId(R.id.emailInput)).perform(replaceText("pro@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.passwordInput)).perform(replaceText("Password1!"), closeSoftKeyboard());
        onView(withId(R.id.loginbutton)).perform(click());

        onView(withText("Oil change")).perform(click());
        onView(withId(R.id.dialogBidAmountInput)).perform(replaceText("150"), closeSoftKeyboard());
        onView(withId(R.id.dialogPickTimeButton)).perform(click());

        LocalDate tomorrow = LocalDate.now().plusDays(1);
        onView(withClassName(Matchers.equalTo(android.widget.DatePicker.class.getName())))
                .inRoot(isDialog())
                .perform(PickerActions.setDate(tomorrow.getYear(), tomorrow.getMonthValue(), tomorrow.getDayOfMonth()));
        onView(withId(android.R.id.button1)).inRoot(isDialog()).perform(click());

        onView(withClassName(Matchers.equalTo(android.widget.TimePicker.class.getName())))
                .inRoot(isDialog())
                .perform(PickerActions.setTime(10, 30));
        onView(withId(android.R.id.button1)).inRoot(isDialog()).perform(click());

        onView(withId(R.id.dialogBidButton)).perform(click());
        onView(withId(R.id.nav_home)).perform(click());

        onView(withText("Oil change")).check(matches(isDisplayed()));
        assertEquals(
                1,
                E2ETestHelpers.app()
                        .getBidService()
                        .getAllByRequest(seededRequest)
                        .size()
        );

    }
}
