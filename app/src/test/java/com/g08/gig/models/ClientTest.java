package com.g08.gig.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClientTest {

    @Test
    void constructor_sixArg_setsAddressAndInheritsUserFieldsWithoutPassword() {
        Client client = new Client(1, "sarah.martinez@email.com", "Sarah", "Martinez", "2045552233",
                "452 Oak Street, Winnipeg");
        assertEquals(1, client.getId());
        assertEquals("sarah.martinez@email.com", client.getEmail());
        assertEquals("Sarah", client.getFirstName());
        assertEquals("Martinez", client.getLastName());
        assertEquals("2045552233", client.getPhoneNumber());
        assertNull(client.getPassword());
        assertEquals("CLIENT", client.getRole());
        assertEquals("452 Oak Street, Winnipeg", client.getAddress());
    }

    @Test
    void constructor_sevenArg_setsAddressAndPassword() {
        Client client = new Client(2, "michael.thompson@outlook.com", "Michael", "Thompson", "2045554455",
                "88 Riverside Drive, Winnipeg", "MyCar2024!");
        assertEquals(2, client.getId());
        assertEquals("michael.thompson@outlook.com", client.getEmail());
        assertEquals("88 Riverside Drive, Winnipeg", client.getAddress());
        assertEquals("MyCar2024!", client.getPassword());
        assertEquals("CLIENT", client.getRole());
    }

    @Test
    void clientIsInstanceOfUser() {
        Client client = new Client(1, "jennifer.lee@gmail.com", "Jennifer", "Lee", "2045556677",
                "1200 Portage Avenue, Winnipeg", "pwd");
        assertTrue(client instanceof User);
    }
}
