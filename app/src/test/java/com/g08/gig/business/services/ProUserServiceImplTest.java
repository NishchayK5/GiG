package com.g08.gig.business.services;

import com.g08.gig.business.validation.UserValidator;
import com.g08.gig.business.validation.ValidationException;
import com.g08.gig.models.Pro;
import com.g08.gig.persistence.fake.FakeProRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProUserServiceImplTest {

    private static final String VALID_FIRST = "ProTest";
    private static final String VALID_LAST = "Tester";
    private static final String VALID_EMAIL = "pro@example.com";
    private static final String VALID_PHONE = "1234567890";
    private static final String VALID_PASSWORD = "password123";
    private static final String VALID_CITY = "Winnipeg";
    private static final String[] VALID_SKILLS = new String[]{"CarWashing", "Tire Changing"};

    private FakeProRepository repo;
    private ProUserServiceImpl service;

    @BeforeEach
    void setUp() {
        repo = new FakeProRepository();
        service = new ProUserServiceImpl(repo, new UserValidator());
    }

    // --- addPro: success ---

    @Test
    void addPro_withValidData_returnsProAndStoresInRepo() {
        Pro added = service.addPro(VALID_EMAIL, VALID_FIRST, VALID_LAST, VALID_PHONE,
                VALID_PASSWORD, VALID_PASSWORD, VALID_SKILLS, VALID_CITY);

        assertNotNull(added);
        assertTrue(added.getId() > 0);
        assertEquals(VALID_EMAIL, added.getEmail());
        assertEquals(VALID_FIRST, added.getFirstName());
        assertEquals(VALID_LAST, added.getLastName());
        assertEquals(VALID_PHONE, added.getPhoneNumber());
        assertArrayEquals(VALID_SKILLS, added.getSkillSet());
        assertEquals(VALID_CITY, added.getCityOfOperation());
        assertNotNull(added.getPassword());
        assertNotEquals(VALID_PASSWORD, added.getPassword()); // stored hashed

        Pro fromRepo = repo.getById(added.getId());
        assertNotNull(fromRepo);
        assertEquals(added.getId(), fromRepo.getId());
        assertEquals(VALID_EMAIL, fromRepo.getEmail());
    }

    @Test
    void addPro_withNullSkillSet_succeeds() {
        Pro added = service.addPro(VALID_EMAIL, VALID_FIRST, VALID_LAST, VALID_PHONE,
                VALID_PASSWORD, VALID_PASSWORD, null, VALID_CITY);

        assertNotNull(added);
        assertNull(added.getSkillSet());
        assertEquals(VALID_CITY, added.getCityOfOperation());
    }

    @Test
    void addPro_trimsWhitespace() {
        Pro added = service.addPro("  " + VALID_EMAIL + "  ", "  " + VALID_FIRST + "  ",
                "  " + VALID_LAST + "  ", "  " + VALID_PHONE + "  ",
                "  " + VALID_PASSWORD + "  ", "  " + VALID_PASSWORD + "  ",
                VALID_SKILLS, "  " + VALID_CITY + "  ");

        assertNotNull(added);
        assertEquals(VALID_EMAIL, added.getEmail());
        assertEquals(VALID_FIRST, added.getFirstName());
        assertEquals(VALID_LAST, added.getLastName());
        assertEquals(VALID_PHONE, added.getPhoneNumber());
        assertEquals(VALID_CITY, added.getCityOfOperation());
    }

    // --- addPro: validation failures ---

    @Test
    void addPro_emptyFirstName_throwsValidationException() {
        try {

        Pro added = service.addPro(VALID_EMAIL, "", VALID_LAST, VALID_PHONE,
                        VALID_PASSWORD, VALID_PASSWORD, VALID_SKILLS, VALID_CITY);
            fail("Expected ValidationException");
        } catch (ValidationException e) {
            assertEquals("First name is required.", e.getMessage());
        }

    }

    @Test
    void addPro_emptyEmail_throwsValidationException() {
        assertThrows(ValidationException.class, () ->
                service.addPro("", VALID_FIRST, VALID_LAST, VALID_PHONE,
                        VALID_PASSWORD, VALID_PASSWORD, VALID_SKILLS, VALID_CITY));
    }

    @Test
    void addPro_invalidEmailFormat_throwsValidationException() {
        assertThrows(ValidationException.class, () ->
                service.addPro("no-at-sign", VALID_FIRST, VALID_LAST, VALID_PHONE,
                        VALID_PASSWORD, VALID_PASSWORD, VALID_SKILLS, VALID_CITY));
    }

    @Test
    void addPro_phoneNotTenDigits_throwsValidationException() {
        assertThrows(ValidationException.class, () ->
                service.addPro(VALID_EMAIL, VALID_FIRST, VALID_LAST, "123",
                        VALID_PASSWORD, VALID_PASSWORD, VALID_SKILLS, VALID_CITY));
    }

    @Test
    void addPro_passwordsDoNotMatch_throwsValidationException() {
        assertThrows(ValidationException.class, () ->
                service.addPro(VALID_EMAIL, VALID_FIRST, VALID_LAST, VALID_PHONE,
                        VALID_PASSWORD, "other", VALID_SKILLS, VALID_CITY));
    }

    @Test
    void addPro_emptyPassword_throwsValidationException() {
        assertThrows(ValidationException.class, () ->
                service.addPro(VALID_EMAIL, VALID_FIRST, VALID_LAST, VALID_PHONE,
                        "", VALID_PASSWORD, VALID_SKILLS, VALID_CITY));
    }

    // --- updatePro: success ---

    @Test
    void updatePro_existingPro_returnsTrueAndUpdatesRepo() {
        Pro added = service.addPro(VALID_EMAIL, VALID_FIRST, VALID_LAST, VALID_PHONE,
                VALID_PASSWORD, VALID_PASSWORD, VALID_SKILLS, VALID_CITY);
        int id = added.getId();

        String[] newSkills = new String[]{"Electrical"};
        String newCity = "Chicago";
        boolean result = service.updatePro(id, "new@example.com", "New", "Name", "9876543210", newSkills, newCity);

        assertTrue(result);
        Pro updated = repo.getById(id);
        assertNotNull(updated);
        assertEquals("new@example.com", updated.getEmail());
        assertEquals("New", updated.getFirstName());
        assertEquals("Name", updated.getLastName());
        assertEquals("9876543210", updated.getPhoneNumber());
        assertArrayEquals(newSkills, updated.getSkillSet());
        assertEquals(newCity, updated.getCityOfOperation());
        assertEquals(added.getPassword(), updated.getPassword()); // unchanged
    }

    @Test
    void updatePro_invalidEmail_throwsValidationException() {
        Pro added = service.addPro(VALID_EMAIL, VALID_FIRST, VALID_LAST, VALID_PHONE,
                VALID_PASSWORD, VALID_PASSWORD, VALID_SKILLS, VALID_CITY);

        assertThrows(ValidationException.class, () ->
                service.updatePro(added.getId(), "bad-email", VALID_FIRST, VALID_LAST, VALID_PHONE, VALID_SKILLS, VALID_CITY));
    }

    @Test
    void updatePro_idNotExists_returnsFalse() {
        boolean result = service.updatePro(999, VALID_EMAIL, VALID_FIRST, VALID_LAST, VALID_PHONE, VALID_SKILLS, VALID_CITY);
        assertFalse(result);
    }

    // --- deletePro ---

    @Test
    void deletePro_existingPro_returnsTrueAndRemovesFromRepo() {
        Pro added = service.addPro(VALID_EMAIL, VALID_FIRST, VALID_LAST, VALID_PHONE,
                VALID_PASSWORD, VALID_PASSWORD, VALID_SKILLS, VALID_CITY);
        int id = added.getId();

        boolean result = service.deletePro(id);

        assertTrue(result);
        assertNull(repo.getById(id));
    }

    @Test
    void deletePro_idNotExists_returnsFalse() {
        boolean result = service.deletePro(999);
        assertFalse(result);
    }
}
