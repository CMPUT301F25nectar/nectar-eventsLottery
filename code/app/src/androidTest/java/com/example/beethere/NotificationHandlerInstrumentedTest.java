package com.example.beethere;
import com.example.beethere.notifications_classes.NotificationHandler;
import android.content.Context;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.beethere.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class NotificationHandlerInstrumentedTest {

    private NotificationHandler handler;
    private Context context;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        handler = new NotificationHandler();
    }

    @Test
    public void testCreateWinningNotificationForNinetiesMusicFest() {
        // Real event from Firestore
        String eventId = "LTQkbmeVPa2CpT7HUdRZ";
        String eventName = "Nineties Music Fest";

        // Your device ID
        String yourDeviceId = "7cba254d21e73a14";

        Map<String, Boolean> inviteList = new HashMap<>();
        inviteList.put(yourDeviceId, true);

        // Create winning notification for real event
        handler.sendLotteryNotifications(
                eventId,
                eventName,
                inviteList,
                new ArrayList<>(),
                "6ddad4fa0421ed0e"  // Organizer device ID from event
        );

        // Wait for Firebase
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Check Firestore - should see notification for Nineties Music Fest
    }

    @Test
    public void testCreateLosingNotificationForWaitlistUsers() {
        // Real event from Firestore
        String eventId = "VX5F3yj0L5gj7uZbZ931";
        String eventName = "Pool Party";

        // Users from the waitlist
        ArrayList<User> waitlist = new ArrayList<>();

        // User 1: test joined (94c8a07fe685be05)
        User user1 = new User();
        user1.setDeviceid("7cba254d21e73a14");
        user1.setName("Riya P");
        user1.setEmail("ri@gmail.com");
        // Note: This user doesn't have notification preferences set,
        // so it will default to true
        waitlist.add(user1);

        // User 2: android phone (1072bef23afe2c8c)
        User user2 = new User();
        user2.setDeviceid("1072bef23afe2c8c");
        user2.setName("android phone");
        user2.setEmail("some@email.phone");
        user2.setReceiveLosingNotifs(true);  // From Firestore data
        waitlist.add(user2);

        // Create losing notifications for waitlist users
        handler.sendLotteryNotifications(
                eventId,
                eventName,
                new HashMap<>(),  // Empty invite list
                waitlist,
                "6ddad4fa0421ed0e"
        );

        // Wait for Firebase
        try {
            Thread.sleep(4000);  // 4 seconds for 2 notifications
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Check Firestore - should see 2 losing notifications
    }

    @Test
    public void testOrganizerMessageToWaitlist() {
        String eventId = "LTQkbmeVPa2CpT7HUdRZ";
        String eventName = "Nineties Music Fest";

        ArrayList<User> waitlist = new ArrayList<>();

        // User 1: Has receiveOrganizerNotifs = false
        User user1 = new User();
        user1.setDeviceid("94c8a07fe685be05");
        user1.setReceiveOrganizerNotifs(false);  // Won't receive
        waitlist.add(user1);

        // User 2: Has receiveOrganizerNotifs = false
        User user2 = new User();
        user2.setDeviceid("1072bef23afe2c8c");
        user2.setReceiveOrganizerNotifs(false);  // Won't receive
        waitlist.add(user2);

        // Send organizer message
        handler.sendOrganizerMessage(
                eventId,
                eventName,
                waitlist,
                "Don't forget to bring your 90s outfit to the music fest!",
                "6ddad4fa0421ed0e"
        );

        // Wait for Firebase
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Check Firestore - NO notification should be created
        // because both users have receiveOrganizerNotifs = false
        // Check Logcat for: "No users opted in to receive organizer messages"
    }

    @Test
    public void testYourOwnWinningNotification() {
        // Send yourself a winning notification for the real event
        String eventId = "LTQkbmeVPa2CpT7HUdRZ";
        String eventName = "Nineties Music Fest";
        String yourDeviceId = "7cba254d21e73a14";

        Map<String, Boolean> inviteList = new HashMap<>();
        inviteList.put(yourDeviceId, true);

        handler.sendLotteryNotifications(
                eventId,
                eventName,
                inviteList,
                new ArrayList<>(),
                "6ddad4fa0421ed0e"
        );

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // After test runs:
        // 1. Check Firestore notifications collection
        // 2. Open BeeThere app → Notifications tab
        // 3. Should see "Nineties Music Fest" winning notification!
        // 4. Click it → should navigate to event details
    }
}