package com.example.beethere;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.beethere.notifications_classes.NotificationHandler;
import com.example.beethere.notifications_classes.Notification;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.Tasks;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class NotificationHandlerInstrumentedTest {

    private NotificationHandler notificationHandler;

    @Before
    public void setup() {
        notificationHandler = new NotificationHandler();  // no need for db as FirebaseFirestore.getInstance() is used directly
    }

    @Test
    public void testRealTimeNotificationListener() throws InterruptedException {
        // Create a fake user with device ID
        User alice = new User("Alice", "alice@test.com");
        alice.setDeviceid("device_alice_123");

        // Set up the listener for this user
        notificationHandler.setupNotificationListener(alice.getDeviceid(), new NotificationHandler.NotificationCallback() {
            @Override
            public void onSuccess(List<Notification> notifications) {
                // Verify that the notifications list is not empty and contains the notification sent
                assertTrue(!notifications.isEmpty());
                assertEquals("Sample Event", notifications.get(0).getEventName());
                android.util.Log.d("NotificationTest", "Real-time notifications received");
            }

            @Override
            public void onError(String error) {
                // Fail the test if there is an error in fetching notifications
                fail("Error receiving notifications: " + error);
            }
        });

        // Send a new notification for this user
        HashMap<User, Boolean> inviteList = new HashMap<>();
        inviteList.put(alice, true);
        ArrayList<User> waitlist = new ArrayList<>();
        waitlist.add(new User("Bob", "bob@test.com"));  // Example waitlist
        notificationHandler.sendLotteryNotifications(
                "event_test_04",
                "Sample Event",
                inviteList,
                waitlist,
                "organizer_001"
        );

        // Wait a little for Firestore to update
        Thread.sleep(3000); // Wait for Firestore to trigger the real-time listener
    }
}
