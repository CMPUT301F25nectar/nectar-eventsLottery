package com.example.beethere;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.view.View;

import androidx.test.espresso.NoMatchingViewException;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ProfileFragmentTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);
    private void openProfile() {
        // open Profile  from bottom navigation
        onView(withId(R.id.navigation_profile)).perform(click());
        try {
            onView(withText("Cancel"))
                    .check(matches(isDisplayed()))
                    .perform(click());
        } catch (NoMatchingViewException ignored) {

        }
    }
    @Test
    public void profileScreen() {
        openProfile();
        onView(withId(R.id.firstname)).check(matches(isDisplayed()));
    }
    @Test
    public void saveProfile() {
        openProfile();

        onView(withId(R.id.firstname))
                .perform(replaceText("Userfirst"), closeSoftKeyboard());
        onView(withId(R.id.lastname))
                .perform(replaceText("Userlast"), closeSoftKeyboard());
        onView(withId(R.id.edit_email))
                .perform(replaceText("user@example.com"), closeSoftKeyboard());
        onView(withId(R.id.edit_phone))
                .perform(replaceText("1234567890"), closeSoftKeyboard());

        onView(withId(R.id.button_save_profile)).perform(click());

        onView(withId(R.id.firstname))
                .check(matches(withText("Userfirst")));
        onView(withId(R.id.lastname))
                .check(matches(withText("Userlast")));
        onView(withId(R.id.edit_email))
                .check(matches(withText("user@example.com")));
        onView(withId(R.id.edit_phone))
                .check(matches(withText("1234567890")));
    }
}
