package com.g08.gig.models;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class RequestTest {

    @Test
    void constructor_setsAllFields() {
        Client client = new Client(1, "kevin.moore@email.com", "Kevin", "Moore", "2045556677",
                "320 Broadway, Winnipeg", null);
        LocalDateTime time = LocalDateTime.of(2025, 6, 15, 10, 30);
        Request request = new Request(5, client, "Brake pad replacement",
                "My 2019 Honda Civic needs new front brake pads. There's a grinding noise when I stop.", time, true);

        assertEquals(5, request.getId());
        assertSame(client, request.getClient());
        assertEquals("Brake pad replacement", request.getTitle());
        assertEquals("My 2019 Honda Civic needs new front brake pads. There's a grinding noise when I stop.", request.getBody());
        assertEquals(time, request.getTimeOfRequest());
    }

    @Test
    void setters_updateFields() {
        Client client = new Client(1, "rachel.green@email.com", "Rachel", "Green", "2045557788",
                "55 Pembina Hwy, Winnipeg", null);
        LocalDateTime time = LocalDateTime.now();
        Request request = new Request(1, client, "Oil change needed", "Due for synthetic oil change.", time, true);

        request.setId(2);
        assertEquals(2, request.getId());

        Client newClient = new Client(2, "daniel.wright@outlook.com", "Daniel", "Wright", "2045558899",
                "200 Portage Ave, Winnipeg", null);
        request.setClient(newClient);
        assertSame(newClient, request.getClient());

        request.setTitle("AC not cooling");
        assertEquals("AC not cooling", request.getTitle());

        request.setBody("Car AC blows warm air. 2020 Toyota Corolla.");
        assertEquals("Car AC blows warm air. 2020 Toyota Corolla.", request.getBody());

        LocalDateTime newTime = LocalDateTime.of(2025, 7, 1, 14, 0);
        request.setTimeOfRequest(newTime);
        assertEquals(newTime, request.getTimeOfRequest());
    }
}
