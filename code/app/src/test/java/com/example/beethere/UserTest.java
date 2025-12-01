package com.example.beethere;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class UserTest {
    @Test
    public void user_notnull(){
        User u = new User();
        assertNotNull(u);
        assertNull(u.getName());
        assertNull(u.getEmail());
        assertNull(u.getPhone());
        assertNull(u.getDeviceid());
        assertEquals(Boolean.FALSE, u.getAdmin());
        assertEquals(Boolean.FALSE, u.getOrganizer());
    }
    @Test
    public void user_nameemail(){
        User u = new User ("abc xyz", "abc@gmail.com");
        assertEquals("abc xyz", u.getName());
        assertEquals("abc@gmail.com", u.getEmail());
        assertNull(u.getPhone());
        assertNull(u.getDeviceid());
        assertEquals(Boolean.FALSE, u.getAdmin());
        assertEquals(Boolean.FALSE, u.getOrganizer());
    }
    @Test
    public void user_setter(){
        User u = new User();
        u.setName("John Doe");
        u.setEmail("john@gmail.com");
        u.setPhone("1234567890");
        u.setDeviceid("abc292428309");
        u.setAdmin(Boolean.FALSE);
        u.setOrganizer(Boolean.TRUE);
        assertEquals("John Doe", u.getName());
        assertEquals("john@gmail.com", u.getEmail());
        assertEquals("1234567890", u.getPhone());
        assertEquals("abc292428309", u.getDeviceid());
        assertEquals(Boolean.FALSE, u.getAdmin());
        assertEquals(Boolean.TRUE, u.getOrganizer());
    }
}
