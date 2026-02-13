package com.g08.gig.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProTest {

    @Test
    void constructor_sevenArg_setsSkillSetAndCityWithoutPassword() {
        String[] skills = new String[]{"Oil Change", "Brake Service", "Tire Rotation"};
        Pro pro = new Pro(1, "marcus.johnson@autoservice.com", "Marcus", "Johnson", "2045557788",
                skills, "Winnipeg");
        assertEquals(1, pro.getId());
        assertEquals("marcus.johnson@autoservice.com", pro.getEmail());
        assertEquals("Marcus", pro.getFirstName());
        assertEquals("Johnson", pro.getLastName());
        assertEquals("2045557788", pro.getPhoneNumber());
        assertNull(pro.getPassword());
        assertEquals("PRO", pro.getRole());
        assertArrayEquals(skills, pro.getSkillSet());
        assertEquals("Winnipeg", pro.getCityOfOperation());
    }

    @Test
    void constructor_eightArg_setsPassword() {
        String[] skills = new String[]{"AC Repair", "Engine Diagnostics"};
        Pro pro = new Pro(2, "emily.rodriguez@mechanics.ca", "Emily", "Rodriguez", "2045558899",
                "HashedPwd123", skills, "Brandon");
        assertEquals(2, pro.getId());
        assertEquals("HashedPwd123", pro.getPassword());
        assertEquals("PRO", pro.getRole());
        assertArrayEquals(skills, pro.getSkillSet());
        assertEquals("Brandon", pro.getCityOfOperation());
    }

    @Test
    void constructor_allowsNullSkillSet() {
        Pro pro = new Pro(3, "robert.kim@garage.ca", "Robert", "Kim", "2045559900", null, "Winnipeg");
        assertNull(pro.getSkillSet());
        assertEquals("Winnipeg", pro.getCityOfOperation());
    }

    @Test
    void proIsInstanceOfUser() {
        Pro pro = new Pro(1, "tech@quicklube.ca", "Alex", "Nguyen", "2045551010",
                new String[]{"Oil Change"}, "Winnipeg");
        assertTrue(pro instanceof User);
    }
}
