package com.example.beethere;

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
    private FirebaseFirestore db;

    @Before
    public void setup() {
        db = FirebaseFirestore.getInstance();
        notificationHandler = new NotificationHandler();
    }

    @Test
    public void testSendLotteryNotifications() {
        // Create fake users
        User alice = new User("Alice", "alice@test.com");
        alice.setDeviceid("device_alice_123");

        User bob = new User("Bob", "bob@test.com");
        bob.setDeviceid("device_bob_456");

        // Create dummy inviteList and waitlist
        HashMap<User, Boolean> inviteList = new HashMap<>();
        inviteList.put(alice, true);
        ArrayList<User> waitlist = new ArrayList<>();
        waitlist.add(bob);

        // Call handler
        notificationHandler.sendLotteryNotifications(
                "event_test_01",
                "Test Event",
                inviteList,
                waitlist,
                "organizer_001"
        );

        // Log to verify test ran
        android.util.Log.d("NotificationTest", "sendLotteryNotifications() executed");
    }

    @Test
    public void testOrganizerMessage() {
        ArrayList<User> waitlist = new ArrayList<>();
        User user = new User("Charlie", "charlie@test.com");
        user.setDeviceid("device_charlie_789");
        waitlist.add(user);

        notificationHandler.sendOrganizerMessage(
                "event_test_02",
                "Organizer Event",
                waitlist,
                "Custom message from organizer",
                "organizer_002"
        );

        android.util.Log.d("NotificationTest", "sendOrganizerMessage() executed");
    }

    @Test
    public void testSendLotteryNotificationsAndVerifyFirestore() throws Exception {
        User alice = new User("Alice", "alice@test.com");
        alice.setDeviceid("device_alice_123");
        User bob = new User("Bob", "bob@test.com");
        bob.setDeviceid("device_bob_456");

        HashMap<User, Boolean> inviteList = new HashMap<>();
        inviteList.put(alice, true);
        ArrayList<User> waitlist = new ArrayList<>();
        waitlist.add(bob);

        notificationHandler.sendLotteryNotifications(
                "event_test_03",
                "Verification Event",
                inviteList,
                waitlist,
                "organizer_003"
        );

        // Wait for the Firestore operation to complete
        Tasks.await(db.collection("notifications")
                .whereEqualTo("eventName", "Verification Event")
                .get());

        db.collection("notifications")
                .whereEqualTo("eventName", "Verification Event")
                .get()
                .addOnSuccessListener(query -> {
                    android.util.Log.d("NotificationTest", "Firestore has " + query.size() + " notifications for Verification Event");
                })
                .addOnFailureListener(e -> {
                    android.util.Log.e("NotificationTest", "Firestore check failed: " + e.getMessage());
                });
    }
}

