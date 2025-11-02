package com.example.beethere.EventClassesTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.beethere.User;
import com.example.beethere.eventclasses.UserListManager;

import org.junit.jupiter.api.Test;

public class UserListManagerTest {

    public User mockUser(){
        User user = new User("Nour");
        return user;
    }

    public UserListManager mockListManager(){
        UserListManager listManager = new UserListManager(2);
        listManager.addWaitlist(mockUser());
        return listManager;
    }

    @Test
    void testAddWaitlist(){
        UserListManager listManager = mockListManager();
        assertEquals(1, listManager.getWaitlist().size());

        User user = new User("Hana");
        listManager.addWaitlist(user);
        assertEquals(2, listManager.getWaitlist().size());
    }

    @Test
    void testRemoveWaitlist(){
        UserListManager listManager = mockListManager();

        User user = new User("Riya");
        listManager.addWaitlist(user);
        assertEquals(2, listManager.getWaitlist().size());

        listManager.removeWaitlist(user);
        assertEquals(1, listManager.getWaitlist().size());
    }

    @Test
    void testAddInvite(){
        UserListManager listManager = mockListManager();
        User user = new User("Chanelle");
        listManager.addWaitlist(user);
        listManager.addInvite(user);
        assertEquals(1, listManager.getWaitlist().size());
        assertEquals(1, listManager.getInviteList().size());
        assertTrue(listManager.getInviteList().get(user));
    }

    @Test
    void testRemoveInvite(){
        UserListManager listManager = mockListManager();
        User user = new User("Chanelle");
        listManager.addWaitlist(user);
        listManager.addInvite(user);
        assertEquals(1, listManager.getInviteList().size());

        listManager.removeInvite(user);
        assertEquals(0, listManager.getInviteList().size());
    }

    @Test
    void testAddRegistered(){
        UserListManager listManager = mockListManager();

        User user = new User("Monika");
        listManager.addRegistered(user);
        assertEquals(1, listManager.getRegistered().size());

        User user1 = new User("Hana");
        listManager.addRegistered(user1);
        assertEquals(2, listManager.getRegistered().size());
    }

    @Test
    void testRemoveRegistered(){
        UserListManager listManager = mockListManager();

        User user = new User("Riya");
        listManager.addRegistered(user);
        User user1 = new User("Chanelle");
        listManager.addRegistered(user1);
        assertEquals(2, listManager.getRegistered().size());

        listManager.removeRegistered(user);
        assertEquals(1, listManager.getRegistered().size());
    }

    @Test
    void testAcceptInvite(){
        UserListManager listManager = mockListManager();

        User user = new User("Monika");
        listManager.addInvite(user);
        listManager.acceptInvite(user);
        assertEquals(0, listManager.getInviteList().size());
        assertEquals(1, listManager.getRegistered().size());
    }

    @Test
    void testDeclineInvite(){
        UserListManager listManager = mockListManager();
        User user = new User("Hana");
        listManager.addWaitlist(user);
        listManager.addInvite(user);
        listManager.declineInvite(user);
        assertFalse(listManager.getInviteList().get(user));
    }

    @Test
    void testSelectNewInvite(){

    }

    @Test
    void testSelectInvitations(){

    }
}
