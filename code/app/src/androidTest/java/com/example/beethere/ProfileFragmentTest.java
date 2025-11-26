package com.example.beethere;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;
import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ProfileFragmentTest {
    @Rule
    public ActivityScenarioRule<MainActivity> rule =
            new ActivityScenarioRule<>(MainActivity.class);

    //wait for async Firestore updates
    private static ViewAction waitFor(long millis) {
        return new ViewAction() {
            @Override public Matcher<View> getConstraints() { return isRoot(); }
            @Override public String getDescription() { return "wait for " + millis + " ms"; }
            @Override public void perform(UiController ui, View v) { ui.loopMainThreadForAtLeast(millis); }
        };
    }
    //go to profile
    private void openprofile() {
        onView(withId(R.id.navigation_profile)).perform(click());
        try {
            onView(withText("Cancel")).check(matches(isDisplayed())).perform(click());
        } catch (NoMatchingViewException ignored) {
        }
    }

    @Test
    public void saveprofile() {
        openprofile();

        /*onView(withId(R.id.first_name)).perform(replaceText("abc"));
        onView(withId(R.id.last_name)).perform(replaceText("xyz"));
        onView(withId(R.id.email)).perform(replaceText("abc@gmail.com"));
        onView(withId(R.id.phone)).perform(replaceText("12362847"));

        onView(withId(R.id.savebtn)).perform(click());
        onView(isRoot()).perform(waitFor(1200)); // pause for firestore

        onView(withId(R.id.first_name)).check(matches(withText("abc")));
        onView(withId(R.id.last_name)).check(matches(withText("xyz")));
        onView(withId(R.id.email)).check(matches(withText("abc@gmail.com")));
        onView(withId(R.id.phone)).check(matches(withText("12362847")));*/
    }

    @Test
    public void deleteprofile() {
        openprofile();
        /*onView(withId(R.id.deletebtn)).perform(click());
        onView(isRoot()).perform(waitFor(1200)); // pause for firestore

        onView(withId(R.id.first_name)).check(matches(withText("")));
        onView(withId(R.id.last_name)).check(matches(withText("")));
        onView(withId(R.id.email)).check(matches(withText("")));
        onView(withId(R.id.phone)).check(matches(withText("")));*/
    }
}