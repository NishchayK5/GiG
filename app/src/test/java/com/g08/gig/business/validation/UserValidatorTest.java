package com.g08.gig.business.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserValidatorTest {

    private static final String VALID_FIRST = "Jennifer";
    private static final String VALID_LAST = "Lee";
    private static final String VALID_EMAIL = "jennifer.lee@gmail.com";
    private static final String VALID_PHONE = "2045556677";
    private static final String VALID_PASSWORD = "SecurePass1!";

    private UserValidator validator;

    @BeforeEach
    void setUp() {
        validator = new UserValidator();
    }

    // --- 4-arg validate: valid ---

    @Test
    void validate_fourArg_withValidData_doesNotThrow() {
        assertDoesNotThrow(() -> validator.validate(VALID_FIRST, VALID_LAST, VALID_EMAIL, VALID_PHONE));
    }

    // --- 6-arg validate: valid ---

    @Test
    void validate_sixArg_withValidData_doesNotThrow() {
        assertDoesNotThrow(() -> validator.validate(VALID_FIRST, VALID_LAST, VALID_EMAIL, VALID_PHONE,
                VALID_PASSWORD, VALID_PASSWORD));
    }

    // --- First name ---

    @Test
    void validate_firstNameNull_throwsFirstNameRequired() {
        ValidationException e = assertThrows(ValidationException.class, () ->
                validator.validate(null, VALID_LAST, VALID_EMAIL, VALID_PHONE));
        assertEquals("First name is required.", e.getMessage());
    }

    @Test
    void validate_firstNameEmpty_throwsFirstNameRequired() {
        ValidationException e = assertThrows(ValidationException.class, () ->
                validator.validate("", VALID_LAST, VALID_EMAIL, VALID_PHONE));
        assertEquals("First name is required.", e.getMessage());
    }

    @Test
    void validate_firstNameWhitespaceOnly_throwsFirstNameRequired() {
        ValidationException e = assertThrows(ValidationException.class, () ->
                validator.validate("   ", VALID_LAST, VALID_EMAIL, VALID_PHONE));
        assertEquals("First name is required.", e.getMessage());
    }

    @Test
    void validate_firstNameOver60Chars_throwsLengthError() {
        String longName = "a".repeat(61);
        ValidationException e = assertThrows(ValidationException.class, () ->
                validator.validate(longName, VALID_LAST, VALID_EMAIL, VALID_PHONE));
        assertEquals("First name must be 60 characters or less.", e.getMessage());
    }

    @Test
    void validate_firstNameExactly60Chars_doesNotThrow() {
        String name60 = "a".repeat(60);
        assertDoesNotThrow(() -> validator.validate(name60, VALID_LAST, VALID_EMAIL, VALID_PHONE));
    }

    // --- Last name ---

    @Test
    void validate_lastNameNull_throwsLastNameRequired() {
        ValidationException e = assertThrows(ValidationException.class, () ->
                validator.validate(VALID_FIRST, null, VALID_EMAIL, VALID_PHONE));
        assertEquals("Last name is required.", e.getMessage());
    }

    @Test
    void validate_lastNameEmpty_throwsLastNameRequired() {
        ValidationException e = assertThrows(ValidationException.class, () ->
                validator.validate(VALID_FIRST, "", VALID_EMAIL, VALID_PHONE));
        assertEquals("Last name is required.", e.getMessage());
    }

    @Test
    void validate_lastNameWhitespaceOnly_throwsLastNameRequired() {
        assertThrows(ValidationException.class, () ->
                validator.validate(VALID_FIRST, "   ", VALID_EMAIL, VALID_PHONE));
    }

    @Test
    void validate_lastNameOver60Chars_throwsLengthError() {
        String longName = "b".repeat(61);
        ValidationException e = assertThrows(ValidationException.class, () ->
                validator.validate(VALID_FIRST, longName, VALID_EMAIL, VALID_PHONE));
        assertEquals("Last name must be 60 characters or less.", e.getMessage());
    }

    @Test
    void validate_lastNameExactly60Chars_doesNotThrow() {
        String name60 = "b".repeat(60);
        assertDoesNotThrow(() -> validator.validate(VALID_FIRST, name60, VALID_EMAIL, VALID_PHONE));
    }

    // --- Email ---

    @Test
    void validate_emailNull_throwsEmailRequired() {
        ValidationException e = assertThrows(ValidationException.class, () ->
                validator.validate(VALID_FIRST, VALID_LAST, null, VALID_PHONE));
        assertEquals("Email is required.", e.getMessage());
    }

    @Test
    void validate_emailEmpty_throwsEmailRequired() {
        ValidationException e = assertThrows(ValidationException.class, () ->
                validator.validate(VALID_FIRST, VALID_LAST, "", VALID_PHONE));
        assertEquals("Email is required.", e.getMessage());
    }

    @Test
    void validate_emailWhitespaceOnly_throwsEmailRequired() {
        assertThrows(ValidationException.class, () ->
                validator.validate(VALID_FIRST, VALID_LAST, "   ", VALID_PHONE));
    }

    @Test
    void validate_emailNoAtSign_throwsInvalidFormat() {
        ValidationException e = assertThrows(ValidationException.class, () ->
                validator.validate(VALID_FIRST, VALID_LAST, "invalid-email", VALID_PHONE));
        assertEquals("Please enter a valid email address.", e.getMessage());
    }

    @Test
    void validate_emailOver255Chars_throwsLengthError() {
        String longEmail = "a".repeat(250) + "@x.com"; // 256 chars
        ValidationException e = assertThrows(ValidationException.class, () ->
                validator.validate(VALID_FIRST, VALID_LAST, longEmail, VALID_PHONE));
        assertEquals("Email must be 255 characters or less.", e.getMessage());
    }

    @Test
    void validate_emailExactly255Chars_doesNotThrow() {
        String email255 = "a".repeat(250) + "@x.co"; // 255 chars
        System.out.println(email255.length());
        assertDoesNotThrow(() -> validator.validate(VALID_FIRST, VALID_LAST, email255, VALID_PHONE));
    }

    // --- Phone ---

    @Test
    void validate_phoneNull_throwsPhoneRequired() {
        ValidationException e = assertThrows(ValidationException.class, () ->
                validator.validate(VALID_FIRST, VALID_LAST, VALID_EMAIL, null));
        assertEquals("Phone number is required.", e.getMessage());
    }

    @Test
    void validate_phoneEmpty_throwsPhoneRequired() {
        ValidationException e = assertThrows(ValidationException.class, () ->
                validator.validate(VALID_FIRST, VALID_LAST, VALID_EMAIL, ""));
        assertEquals("Phone number is required.", e.getMessage());
    }

    @Test
    void validate_phoneNotTenDigits_tooShort_throws() {
        ValidationException e = assertThrows(ValidationException.class, () ->
                validator.validate(VALID_FIRST, VALID_LAST, VALID_EMAIL, "123"));
        assertEquals("Phone number must be exactly 10 digits long.", e.getMessage());
    }

    @Test
    void validate_phoneNotTenDigits_tooLong_throws() {
        assertThrows(ValidationException.class, () ->
                validator.validate(VALID_FIRST, VALID_LAST, VALID_EMAIL, "12345678901"));
    }

    @Test
    void validate_phoneExactlyTenDigits_doesNotThrow() {
        assertDoesNotThrow(() -> validator.validate(VALID_FIRST, VALID_LAST, VALID_EMAIL, "1234567890"));
    }

    // --- Password (6-arg only) ---

    @Test
    void validate_sixArg_passwordNull_throwsPasswordRequired() {
        ValidationException e = assertThrows(ValidationException.class, () ->
                validator.validate(VALID_FIRST, VALID_LAST, VALID_EMAIL, VALID_PHONE, null, VALID_PASSWORD));
        assertEquals("Password is required.", e.getMessage());
    }

    @Test
    void validate_sixArg_passwordEmpty_throwsPasswordRequired() {
        ValidationException e = assertThrows(ValidationException.class, () ->
                validator.validate(VALID_FIRST, VALID_LAST, VALID_EMAIL, VALID_PHONE, "", VALID_PASSWORD));
        assertEquals("Password is required.", e.getMessage());
    }

    @Test
    void validate_sixArg_confirmPasswordNull_throwsConfirmRequired() {
        ValidationException e = assertThrows(ValidationException.class, () ->
                validator.validate(VALID_FIRST, VALID_LAST, VALID_EMAIL, VALID_PHONE, VALID_PASSWORD, null));
        assertEquals("Confirm password is required.", e.getMessage());
    }

    @Test
    void validate_sixArg_confirmPasswordEmpty_throwsConfirmRequired() {
        ValidationException e = assertThrows(ValidationException.class, () ->
                validator.validate(VALID_FIRST, VALID_LAST, VALID_EMAIL, VALID_PHONE, VALID_PASSWORD, ""));
        assertEquals("Confirm password is required.", e.getMessage());
    }

    @Test
    void validate_sixArg_passwordsDoNotMatch_throws() {
        ValidationException e = assertThrows(ValidationException.class, () ->
                validator.validate(VALID_FIRST, VALID_LAST, VALID_EMAIL, VALID_PHONE, VALID_PASSWORD, "other"));
        assertEquals("Passwords must match.", e.getMessage());
    }

    @Test
    void validate_sixArg_passwordsMatchWithWhitespace_doesNotThrow() {
        assertDoesNotThrow(() -> validator.validate(VALID_FIRST, VALID_LAST, VALID_EMAIL, VALID_PHONE,
                "  pass  ", "  pass  "));
    }
}
