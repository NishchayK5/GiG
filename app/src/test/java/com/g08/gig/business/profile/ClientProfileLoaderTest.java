package com.g08.gig.business.profile;

import com.g08.gig.business.services.ClientUserService;
import com.g08.gig.models.Client;
import com.g08.gig.models.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientProfileLoaderTest {

    @Mock
    private ClientUserService clientUserService;

    private ClientProfileLoader loader;

    @BeforeEach
    void setUp() {
        loader = new ClientProfileLoader(clientUserService);
    }

    @Test
    void getUserForEdit_delegatesToServiceAndReturnsClient() {
        Client client = new Client(1, "c@test.com", "Jane", "Doe", "2045551234", "123 Main St", "hash");
        when(clientUserService.getClientById(1)).thenReturn(client);

        User result = loader.getUserForEdit(1);

        assertNotNull(result);
        assertSame(client, result);
        assertEquals(1, result.getId());
        assertEquals("c@test.com", result.getEmail());
        assertEquals("Jane", result.getFirstName());
        verify(clientUserService).getClientById(1);
    }

    @Test
    void getUserForEdit_whenServiceReturnsNull_returnsNull() {
        when(clientUserService.getClientById(999)).thenReturn(null);

        User result = loader.getUserForEdit(999);

        assertNull(result);
        verify(clientUserService).getClientById(999);
    }
}
