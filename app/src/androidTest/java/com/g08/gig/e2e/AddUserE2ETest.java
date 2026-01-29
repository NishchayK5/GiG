package com.g08.gig.e2e;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.g08.gig.R;
import com.g08.gig.persistence.real.AppDbHelper;
import com.g08.gig.presentation.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AddUserE2ETest {

    @Rule
    public ActivityScenarioRule<MainActivity> rule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void clearDb() {
        Context context = ApplicationProvider.getApplicationContext();
        context.deleteDatabase(AppDbHelper.DB_NAME);
    }

    @Test
    public void addUser_showsInList() {
        onView(withId(R.id.firstNameInput)).perform(replaceText("Jane"));
        onView(withId(R.id.lastNameInput)).perform(replaceText("Doe"));
        onView(withId(R.id.emailInput)).perform(replaceText("jane@example.com"));

        onView(withId(R.id.emailInput))
                .perform(closeSoftKeyboard(), pressImeActionButton());

        onView(withId(R.id.addButton)).perform(click());
    }

}
