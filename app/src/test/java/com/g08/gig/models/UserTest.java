package com.g08.gig.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void constructor_sixArg_setsFieldsAndPasswordIsNull() {
        User user = new User(1, "maria.garcia@email.com", "Maria", "Garcia", "2045550123", "CLIENT");
        assertEquals(1, user.getId());
        assertEquals("maria.garcia@email.com", user.getEmail());
        assertEquals("Maria", user.getFirstName());
        assertEquals("Garcia", user.getLastName());
        assertEquals("2045550123", user.getPhoneNumber());
        assertNull(user.getPassword());
        assertEquals("CLIENT", user.getRole());
    }

    @Test
    void constructor_sevenArg_setsAllFieldsIncludingPassword() {
        User user = new User(2, "david.chen@outlook.com", "David", "Chen", "2045559876", "SecurePass1!", "PRO");
        assertEquals(2, user.getId());
        assertEquals("david.chen@outlook.com", user.getEmail());
        assertEquals("David", user.getFirstName());
        assertEquals("Chen", user.getLastName());
        assertEquals("2045559876", user.getPhoneNumber());
        assertEquals("SecurePass1!", user.getPassword());
        assertEquals("PRO", user.getRole());
    }

    @Test
    void constructor_allowsZeroId() {
        User user = new User(0, "james.wilson@gmail.com", "James", "Wilson", "4315553344", "USER");
        assertEquals(0, user.getId());
    }
}
