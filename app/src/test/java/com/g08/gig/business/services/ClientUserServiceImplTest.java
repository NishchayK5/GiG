package com.g08.gig.business.services;

import com.g08.gig.business.validation.ValidationException;
import com.g08.gig.models.Client;
import com.g08.gig.models.Pro;
import com.g08.gig.persistence.fake.FakeClientRepository;
import com.g08.gig.persistence.fake.FakeUserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClientUserServiceImplTest {

    private static final String VALID_FIRST = "Sarah";
    private static final String VALID_LAST = "Martinez";
    private static final String VALID_EMAIL = "sarah.martinez@email.com";
    private static final String VALID_PHONE = "2045552233";
    private static final String VALID_PASSWORD = "MyCar2024!";
    private static final String VALID_ADDRESS = "452 Oak Street, Winnipeg";

    private FakeClientRepository repo;
    private FakeUserRepository userRepo;
    private ClientUserServiceImpl service;

    @BeforeEach
    void setUp() {
        userRepo = new FakeUserRepository();
        repo = new FakeClientRepository(userRepo);
        service = new ClientUserServiceImpl(repo, userRepo);
    }

    // --- addClient: success ---

    @Test
    void addClient_withValidData_returnsClientAndStoresInRepo() {
        Client added = service.addClient(VALID_FIRST, VALID_LAST, VALID_EMAIL, VALID_PASSWORD,
                VALID_PASSWORD, VALID_PHONE, VALID_ADDRESS);

        assertNotNull(added);
        assertTrue(added.getId() > 0);
        assertEquals(VALID_EMAIL, added.getEmail());
        assertEquals(VALID_FIRST, added.getFirstName());
        assertEquals(VALID_LAST, added.getLastName());
        assertEquals(VALID_PHONE, added.getPhoneNumber());
        assertEquals(VALID_ADDRESS, added.getAddress());
        assertNotNull(added.getPassword());
        assertNotEquals(VALID_PASSWORD, added.getPassword()); // stored hashed

        Client fromRepo = repo.getById(added.getId());
        assertNotNull(fromRepo);
        assertEquals(added.getId(), fromRepo.getId());
        assertEquals(VALID_EMAIL, fromRepo.getEmail());
        assertEquals(VALID_ADDRESS, fromRepo.getAddress());
    }

    @Test
    void addClient_trimsWhitespace() {
        Client added = service.addClient("  " + VALID_FIRST + "  ", "  " + VALID_LAST + "  ",
                "  " + VALID_EMAIL + "  ", "  " + VALID_PASSWORD + "  ", "  " + VALID_PASSWORD + "  ",
                "  " + VALID_PHONE + "  ", "  " + VALID_ADDRESS + "  ");

        assertNotNull(added);
        assertEquals(VALID_EMAIL, added.getEmail());
        assertEquals(VALID_FIRST, added.getFirstName());
        assertEquals(VALID_LAST, added.getLastName());
        assertEquals(VALID_PHONE, added.getPhoneNumber());
        assertEquals(VALID_ADDRESS, added.getAddress());
    }

    // --- addClient: validation failures ---

    @Test
    void addClient_emptyFirstName_throwsValidationException() {
        assertThrows(ValidationException.class, () ->
                service.addClient("", VALID_LAST, VALID_EMAIL, VALID_PASSWORD, VALID_PASSWORD,
                        VALID_PHONE, VALID_ADDRESS));
    }

    @Test
    void addClient_emptyEmail_throwsValidationException() {
        assertThrows(ValidationException.class, () ->
                service.addClient(VALID_FIRST, VALID_LAST, "", VALID_PASSWORD, VALID_PASSWORD,
                        VALID_PHONE, VALID_ADDRESS));
    }

    @Test
    void addClient_invalidEmailFormat_throwsValidationException() {
        assertThrows(ValidationException.class, () ->
                service.addClient(VALID_FIRST, VALID_LAST, "no-at-sign", VALID_PASSWORD, VALID_PASSWORD,
                        VALID_PHONE, VALID_ADDRESS));
    }

    @Test
    void addClient_phoneNotTenDigits_throwsValidationException() {
        assertThrows(ValidationException.class, () ->
                service.addClient(VALID_FIRST, VALID_LAST, VALID_EMAIL, VALID_PASSWORD, VALID_PASSWORD,
                        "123", VALID_ADDRESS));
    }

    @Test
    void addClient_passwordsDoNotMatch_throwsValidationException() {
        assertThrows(ValidationException.class, () ->
                service.addClient(VALID_FIRST, VALID_LAST, VALID_EMAIL, VALID_PASSWORD, "other",
                        VALID_PHONE, VALID_ADDRESS));
    }

    @Test
    void addClient_emptyPassword_throwsValidationException() {
        assertThrows(ValidationException.class, () ->
                service.addClient(VALID_FIRST, VALID_LAST, VALID_EMAIL, "", VALID_PASSWORD,
                        VALID_PHONE, VALID_ADDRESS));
    }

    @Test
    void addClient_duplicateEmail_throwsValidationException() {
        service.addClient(VALID_FIRST, VALID_LAST, VALID_EMAIL, VALID_PASSWORD, VALID_PASSWORD,
                VALID_PHONE, VALID_ADDRESS);

        assertThrows(ValidationException.class, () ->
                service.addClient("Michael", "Thompson", VALID_EMAIL, "AnotherPass1!", "AnotherPass1!",
                        "2045554455", "200 Portage Ave, Winnipeg"));
    }

    // --- updateClient: success ---

    @Test
    void updateClient_existingClient_returnsTrueAndUpdatesRepo() {
        Client added = service.addClient(VALID_FIRST, VALID_LAST, VALID_EMAIL, VALID_PASSWORD,
                VALID_PASSWORD, VALID_PHONE, VALID_ADDRESS);
        int id = added.getId();

        String newAddress = "88 Riverside Drive, Winnipeg";
        boolean result = service.updateClient(id, "Sarah", "Martinez-White", "sarah.updated@email.com", "2045559988", newAddress);

        assertTrue(result);
        Client updated = repo.getById(id);
        assertNotNull(updated);
        assertEquals("sarah.updated@email.com", updated.getEmail());
        assertEquals("Sarah", updated.getFirstName());
        assertEquals("Martinez-White", updated.getLastName());
        assertEquals("2045559988", updated.getPhoneNumber());
        assertEquals(newAddress, updated.getAddress());
        assertEquals(added.getPassword(), updated.getPassword()); // unchanged
    }

    @Test
    void updateClient_invalidEmail_throwsValidationException() {
        Client added = service.addClient(VALID_FIRST, VALID_LAST, VALID_EMAIL, VALID_PASSWORD,
                VALID_PASSWORD, VALID_PHONE, VALID_ADDRESS);

        assertThrows(ValidationException.class, () ->
                service.updateClient(added.getId(), VALID_FIRST, VALID_LAST, "not-an-email", VALID_PHONE, VALID_ADDRESS));
    }

    @Test
    void updateClient_idNotExists_returnsFalse() {
        boolean result = service.updateClient(999, VALID_FIRST, VALID_LAST, VALID_EMAIL, VALID_PHONE, VALID_ADDRESS);
        assertFalse(result);
    }

    @Test
    void updateClient_clientExistsButUserRemoved_returnsFalse() {
        Client added = service.addClient(VALID_FIRST, VALID_LAST, VALID_EMAIL, VALID_PASSWORD,
                VALID_PASSWORD, VALID_PHONE, VALID_ADDRESS);
        int id = added.getId();
        userRepo.delete(id);
        boolean result = service.updateClient(id, VALID_FIRST, VALID_LAST, VALID_EMAIL, VALID_PHONE, VALID_ADDRESS);
        assertFalse(result);
    }

    // --- deleteClient ---

    @Test
    void deleteClient_existingClient_returnsTrueAndRemovesFromRepo() {
        Client added = service.addClient(VALID_FIRST, VALID_LAST, VALID_EMAIL, VALID_PASSWORD,
                VALID_PASSWORD, VALID_PHONE, VALID_ADDRESS);
        int id = added.getId();

        boolean result = service.deleteClient(id);

        assertTrue(result);
        assertNull(repo.getById(id));
    }

    @Test
    void deleteClient_idNotExists_returnsFalse() {
        boolean result = service.deleteClient(999);
        assertFalse(result);
    }

    @Test
    void clientByID() {
        Client added = service.addClient("Alex", "Nguyen", "tech@quicklube.ca", "SecurePass1!",
                "SecurePass1!", "2045551010", "Winnipeg");


        Client newClient = service.getClientById(added.getId());
        assertEquals(added.getFirstName(), newClient.getFirstName());

    }

    @Test
    void getClientById_nonexistentId_returnsNull() {
        Client result = service.getClientById(999);
        assertNull(result);
    }
}
