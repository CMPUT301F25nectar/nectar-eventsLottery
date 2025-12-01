package com.example.beethere.notifications_classes;

import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;
@RunWith(JUnit4.class)
public class NotificationTest {
    private Notification notification;
    private List<String> deviceIds;
    private List<String> respondedDeviceIds;

    @Before
    public void setUp() {
        deviceIds = new ArrayList<>();
        deviceIds.add("device123");
        deviceIds.add("device456");

        respondedDeviceIds = new ArrayList<>();
    }

    @Test
    public void testNotificationConstructorWithAllParameters() {
        notification = new Notification(
                "notif123",
                "event456",
                "Test Event",
                "This is a test notification message",
                1733024400000L,
                "winning",
                deviceIds,
                respondedDeviceIds
        );

        assertEquals("notif123", notification.getNotifId());
        assertEquals("event456", notification.getEventId());
        assertEquals("Test Event", notification.getEventName());
        assertEquals("This is a test notification message", notification.getMessage());
        assertEquals(1733024400000L, notification.getTimestamp());
        assertEquals("winning", notification.getType());
        assertEquals(2, notification.getDeviceIds().size());
        assertEquals("device123", notification.getDeviceIds().get(0));
        assertEquals(0, notification.getRespondedDeviceIds().size());
    }

    @Test
    public void testNotificationEmptyConstructor() {
        notification = new Notification();
        assertNotNull(notification);
    }

    @Test
    public void testNotificationSetters() {
        notification = new Notification();

        notification.setNotifId("notif789");
        notification.setEventId("event999");
        notification.setEventName("Updated Event");
        notification.setMessage("Updated message");
        notification.setTimestamp(1733024500000L);
        notification.setType("losing");

        List<String> newDeviceIds = new ArrayList<>();
        newDeviceIds.add("device789");
        notification.setDeviceIds(newDeviceIds);

        List<String> newRespondedIds = new ArrayList<>();
        newRespondedIds.add("device123");
        notification.setRespondedDeviceIds(newRespondedIds);

        assertEquals("notif789", notification.getNotifId());
        assertEquals("event999", notification.getEventId());
        assertEquals("Updated Event", notification.getEventName());
        assertEquals("Updated message", notification.getMessage());
        assertEquals(1733024500000L, notification.getTimestamp());
        assertEquals("losing", notification.getType());
        assertEquals(1, notification.getDeviceIds().size());
        assertEquals("device789", notification.getDeviceIds().get(0));
        assertEquals(1, notification.getRespondedDeviceIds().size());
        assertEquals("device123", notification.getRespondedDeviceIds().get(0));
    }

    @Test
    public void testNotificationTypes() {
        Notification winningNotif = new Notification();
        winningNotif.setType("winning");
        assertEquals("winning", winningNotif.getType());

        Notification losingNotif = new Notification();
        losingNotif.setType("losing");
        assertEquals("losing", losingNotif.getType());

        Notification adminNotif = new Notification();
        adminNotif.setType("admin");
        assertEquals("admin", adminNotif.getType());

        Notification organizerNotif = new Notification();
        organizerNotif.setType("organizer");
        assertEquals("organizer", organizerNotif.getType());

        Notification cancelledNotif = new Notification();
        cancelledNotif.setType("cancelled");
        assertEquals("cancelled", cancelledNotif.getType());
    }

    @Test
    public void testEmptyDeviceIds() {
        List<String> emptyList = new ArrayList<>();
        notification = new Notification(
                "notif100",
                "event200",
                "Empty Event",
                "No recipients",
                System.currentTimeMillis(),
                "admin",
                emptyList,
                new ArrayList<>()
        );

        assertNotNull(notification.getDeviceIds());
        assertTrue(notification.getDeviceIds().isEmpty());
        assertEquals(0, notification.getDeviceIds().size());
    }

    @Test
    public void testMultipleDeviceIds() {
        List<String> multipleDevices = new ArrayList<>();
        multipleDevices.add("device1");
        multipleDevices.add("device2");
        multipleDevices.add("device3");
        multipleDevices.add("device4");

        notification = new Notification(
                "notif300",
                "event400",
                "Multi-Device Event",
                "Sent to multiple devices",
                System.currentTimeMillis(),
                "winning",
                multipleDevices,
                new ArrayList<>()
        );

        assertEquals(4, notification.getDeviceIds().size());
        assertTrue(notification.getDeviceIds().contains("device1"));
        assertTrue(notification.getDeviceIds().contains("device4"));
    }

    @Test
    public void testRespondedDeviceIds() {
        List<String> responded = new ArrayList<>();
        responded.add("device123");
        responded.add("device456");

        notification = new Notification(
                "notif500",
                "event600",
                "Responded Event",
                "Some users responded",
                System.currentTimeMillis(),
                "winning",
                new ArrayList<>(),
                responded
        );

        assertEquals(2, notification.getRespondedDeviceIds().size());
        assertTrue(notification.getRespondedDeviceIds().contains("device123"));
        assertTrue(notification.getRespondedDeviceIds().contains("device456"));
    }

    @Test
    public void testNotificationWithNullNotifId() {
        // When creating notifications, notifId is often null (Firestore auto-generates)
        notification = new Notification(
                null,
                "event1000",
                "Auto-ID Event",
                "Firestore will generate ID",
                System.currentTimeMillis(),
                "losing",
                deviceIds,
                new ArrayList<>()
        );

        assertNull(notification.getNotifId());
        assertEquals("event1000", notification.getEventId());
    }
}
