package com.example.beethere;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.anything;

import android.content.Context;
import android.view.View;
import android.widget.ListView;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.beethere.notifications_classes.Notification;
import com.example.beethere.ui.notifications.NotificationsAdapter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * UI tests for NotificationsAdapter with mock data
 * Tests that the adapter correctly displays notification items
 */
@RunWith(AndroidJUnit4.class)
public class NotificationAdapterUITest {

    private NotificationsAdapter adapter;
    private ArrayList<Notification> testNotifications;
    private Context context;

    /**
     * Create mock notification data for testing
     */
    @Before
    public void setup() {
        context = ApplicationProvider.getApplicationContext();
        testNotifications = new ArrayList<>();

        // Create test notification 1 - Winner
        Notification notif1 = new Notification(
                "event_pool_party",
                "Pool Party",
                "Congratulations! You've been selected for Pool Party!",
                System.currentTimeMillis() - 3600000, // 1 hour ago
                "lotteryWon",
                Arrays.asList("device_123"),
                "organizer_001"
        );

        // Create test notification 2 - Loser
        Notification notif2 = new Notification(
                "event_concert",
                "Rock Concert",
                "Sorry! You weren't selected for Rock Concert this time.",
                System.currentTimeMillis() - 7200000, // 2 hours ago
                "lotteryLost",
                Arrays.asList("device_123"),
                "organizer_002"
        );

        // Create test notification 3 - Organizer message
        Notification notif3 = new Notification(
                "event_bbq",
                "BBQ Event",
                "Don't forget to bring your own drinks!",
                System.currentTimeMillis() - 86400000, // 1 day ago
                "organizerMessage",
                Arrays.asList("device_123"),
                "organizer_003"
        );

        testNotifications.add(notif1);
        testNotifications.add(notif2);
        testNotifications.add(notif3);
    }

    /**
     * Test that the adapter creates the correct number of views
     */
    @Test
    public void testAdapterItemCount() {
        adapter = new NotificationsAdapter(testNotifications, eventId -> {});

        // Check that adapter has 3 items
        assert(adapter.getCount() == 3);
    }

    /**
     * Test that notification data is correctly bound to views
     */
    @Test
    public void testNotificationDataBinding() {
        adapter = new NotificationsAdapter(testNotifications, eventId -> {});

        // Get the first notification
        Notification firstNotif = (Notification) adapter.getItem(0);

        // Verify data
        assert(firstNotif.getEventName().equals("Pool Party"));
        assert(firstNotif.getMessage().contains("Congratulations"));
        assert(firstNotif.getType().equals("lotteryWon"));
    }

    /**
     * Test that different notification types are handled correctly
     */
    @Test
    public void testDifferentNotificationTypes() {
        adapter = new NotificationsAdapter(testNotifications, eventId -> {});

        Notification winner = (Notification) adapter.getItem(0);
        Notification loser = (Notification) adapter.getItem(1);
        Notification message = (Notification) adapter.getItem(2);

        assert(winner.getType().equals("lotteryWon"));
        assert(loser.getType().equals("lotteryLost"));
        assert(message.getType().equals("organizerMessage"));
    }
}
