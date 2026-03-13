package com.g08.gig.business.profile;

import com.g08.gig.business.services.ProUserService;
import com.g08.gig.models.Pro;
import com.g08.gig.models.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProProfileLoaderTest {

    @Mock
    private ProUserService proUserService;

    private ProProfileLoader loader;

    @BeforeEach
    void setUp() {
        loader = new ProProfileLoader(proUserService);
    }

    @Test
    void getUserForEdit_delegatesToServiceAndReturnsPro() {
        Pro pro = new Pro(1, "p@test.com", "John", "Smith", "2045555678", "pwd",
                new String[]{"Oil Change"}, "Winnipeg");
        when(proUserService.getProById(1)).thenReturn(pro);

        User result = loader.getUserForEdit(1);

        assertNotNull(result);
        assertSame(pro, result);
        assertEquals(1, result.getId());
        assertEquals("p@test.com", result.getEmail());
        assertEquals("John", result.getFirstName());
        verify(proUserService).getProById(1);
    }

    @Test
    void getUserForEdit_whenServiceReturnsNull_returnsNull() {
        when(proUserService.getProById(999)).thenReturn(null);

        User result = loader.getUserForEdit(999);

        assertNull(result);
        verify(proUserService).getProById(999);
    }
}
