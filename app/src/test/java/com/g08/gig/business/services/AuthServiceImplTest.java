package com.g08.gig.business.services;
import com.g08.gig.business.validation.LoginValidator;
import com.g08.gig.business.validation.ValidationException;

import com.g08.gig.models.Client;
import com.g08.gig.models.Pro;
import com.g08.gig.models.User;
import com.g08.gig.persistence.fake.FakeClientRepository;
import com.g08.gig.persistence.fake.FakeProRepository;
import com.g08.gig.persistence.fake.FakeUserRepository;
import com.g08.gig.persistence.fake.FakeSkillRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import at.favre.lib.crypto.bcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceImplTest {

    private static final String CLIENT_EMAIL = "sarah.martinez@email.com";
    private static final String PRO_EMAIL = "marcus.johnson@autoservice.com";
    private static final String PASSWORD = "MyCar2024!";

    private FakeUserRepository userRepo;
    private FakeClientRepository clientRepo;
    private FakeProRepository proRepo;
    private AuthServiceImpl authService;
    private ClientUserServiceImpl clientService;
    private ProUserServiceImpl proService;
    private FakeSkillRepository skillRepo;

    @BeforeEach
    void setUp() {

        userRepo = new FakeUserRepository();
        clientRepo = new FakeClientRepository(userRepo);
        proRepo = new FakeProRepository(userRepo);
        skillRepo = new FakeSkillRepository();

        authService = new AuthServiceImpl(userRepo, clientRepo, proRepo);
        clientService = new ClientUserServiceImpl(clientRepo, userRepo);
        proService = new ProUserServiceImpl(proRepo, userRepo,skillRepo);
    }

    // --- login: invalid input ---

    @Test
    void login_nullEmail_returnsFailure() {
        LoginResult result = authService.login(null, PASSWORD);
        assertFalse(result.isSuccess());
    }

    @Test
    void login_emptyEmail_returnsFailure() {
        LoginResult result = authService.login("", PASSWORD);
        assertFalse(result.isSuccess());
    }

    @Test
    void login_blankEmail_returnsFailure() {
        LoginResult result = authService.login("   ", PASSWORD);
        assertFalse(result.isSuccess());
    }

    @Test
    void login_nullPassword_returnsFailure() {
        Client added = clientService.addClient("Sarah", "Martinez", CLIENT_EMAIL, PASSWORD, PASSWORD,
                "2045552233", "452 Oak Street, Winnipeg");
        assertNotNull(added);

        LoginResult result = authService.login(CLIENT_EMAIL, null);
        assertFalse(result.isSuccess());
    }

    @Test
    void login_emailNotInSystem_returnsFailure() {
        LoginResult result = authService.login("unknown.driver@email.com", PASSWORD);
        assertFalse(result.isSuccess());
    }

    @Test
    void login_invalidEmailFormat_returnsFailure() {
        LoginResult result = authService.login("notanemail", PASSWORD);
        assertFalse(result.isSuccess());
    }

    @Test
    void login_wrongPassword_returnsFailure() {
        Client added = clientService.addClient("Sarah", "Martinez", CLIENT_EMAIL, PASSWORD, PASSWORD,
                "2045552233", "452 Oak Street, Winnipeg");
        assertNotNull(added);

        LoginResult result = authService.login(CLIENT_EMAIL, "WrongPassword1!");
        assertFalse(result.isSuccess());
    }

    // --- login: client success ---

    @Test
    void login_validClientCredentials_returnsSuccessAsClient() {
        Client added = clientService.addClient("Sarah", "Martinez", CLIENT_EMAIL, PASSWORD, PASSWORD,
                "2045552233", "452 Oak Street, Winnipeg");
        assertNotNull(added);

        LoginResult result = authService.login(CLIENT_EMAIL, PASSWORD);

        assertTrue(result.isSuccess());
        assertEquals(added.getId(), result.getUserId());
        assertTrue(result.isClient());
        assertFalse(result.isPro());
    }

    @Test
    void login_clientEmailWithWhitespace_trimsAndSucceeds() {
        clientService.addClient("Sarah", "Martinez", CLIENT_EMAIL, PASSWORD, PASSWORD,
                "2045552233", "452 Oak Street, Winnipeg");

        LoginResult result = authService.login("  " + CLIENT_EMAIL + "  ", PASSWORD);

        assertTrue(result.isSuccess());
        assertTrue(result.isClient());
    }

    // --- login: pro success ---

    @Test
    void login_validProCredentials_returnsSuccessAsPro() {
        Pro added = proService.addPro("Marcus", "Johnson", PRO_EMAIL, "2045557788", PASSWORD, PASSWORD,
                new String[]{"Oil Change", "Brake Service"}, "Winnipeg");
        assertNotNull(added);

        LoginResult result = authService.login(PRO_EMAIL, PASSWORD);

        assertTrue(result.isSuccess());
        assertEquals(added.getId(), result.getUserId());
        assertFalse(result.isClient());
        assertTrue(result.isPro());
    }

    @Test
    void login_proEmailWithWhitespace_trimsAndSucceeds() {
        proService.addPro("Marcus", "Johnson", PRO_EMAIL, "2045557788", PASSWORD, PASSWORD,
                new String[]{"AC Repair", "Engine Diagnostics"}, "Winnipeg");

        LoginResult result = authService.login("  " + PRO_EMAIL + "  ", PASSWORD);

        assertTrue(result.isSuccess());
        assertTrue(result.isPro());
    }

    // --- login: user in userRepo but not in client or pro repo (edge case) ---

    @Test
    void login_userExistsButNotClientOrPro_returnsFailure() {
        String hashedPassword = BCrypt.withDefaults().hashToString(12, PASSWORD.toCharArray());
        User userOnly = new User(0, "old.account@email.com", "James", "Wilson", "2045551212", hashedPassword, "USER");
        User added = userRepo.add(userOnly);
        assertNotNull(added);
        // No corresponding Client or Pro with this id

        LoginResult result = authService.login("old.account@email.com", PASSWORD);

        assertFalse(result.isSuccess());
    }

    @Test
    void login_userWithNullPassword_returnsFailure() {
        User userWithNullPassword = new User(0, "nopass@test.com", "No", "Pass", "2045550000", null, "USER");
        User added = userRepo.add(userWithNullPassword);
        assertNotNull(added);
        assertNull(added.getPassword());

        LoginResult result = authService.login("nopass@test.com", "any");

        assertFalse(result.isSuccess());
    }

    @Test
    void login_userWithEmptyPassword_returnsFailure() {
        User userWithEmptyPassword = new User(0, "emptypass@test.com", "Empty", "Pass", "2045550001", "", "USER");
        User added = userRepo.add(userWithEmptyPassword);
        assertNotNull(added);

        LoginResult result = authService.login("emptypass@test.com", "any");

        assertFalse(result.isSuccess());
    }

    @Test
    void login_withCustomLoginValidator_usesValidatorAndReturnsFailureWhenValidationThrows() {
        LoginValidator failingValidator = new LoginValidator() {
            @Override
            public void validate(String email, String password) {
                throw new ValidationException("Invalid", "email");
            }
        };
        AuthServiceImpl authWithValidator = new AuthServiceImpl(userRepo, clientRepo, proRepo, failingValidator);
        clientService.addClient("Sarah", "Martinez", CLIENT_EMAIL, PASSWORD, PASSWORD,
                "2045552233", "452 Oak Street, Winnipeg");

        LoginResult result = authWithValidator.login(CLIENT_EMAIL, PASSWORD);

        assertFalse(result.isSuccess());
    }

    // --- LoginResult failure/success state ---

    @Test
    void loginResult_failure_hasNegativeUserId() {
        LoginResult result = LoginResult.failure();
        assertFalse(result.isSuccess());
        assertEquals(-1, result.getUserId());
        assertFalse(result.isClient());
        assertFalse(result.isPro());
    }

    @Test
    void loginResult_success_client_hasCorrectFlags() {
        LoginResult result = LoginResult.success(1, true, false);
        assertTrue(result.isSuccess());
        assertEquals(1, result.getUserId());
        assertTrue(result.isClient());
        assertFalse(result.isPro());
    }

    @Test
    void loginResult_success_pro_hasCorrectFlags() {
        LoginResult result = LoginResult.success(2, false, true);
        assertTrue(result.isSuccess());
        assertEquals(2, result.getUserId());
        assertFalse(result.isClient());
        assertTrue(result.isPro());
    }
}
