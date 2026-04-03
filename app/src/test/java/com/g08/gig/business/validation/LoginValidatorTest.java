package com.g08.gig.business.validation;

import com.g08.gig.business.constants.ValidationMessages;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginValidatorTest {

    private static final String VALID_EMAIL = "user@example.com";
    private static final String VALID_PASSWORD = "SecurePass123";

    private LoginValidator validator;

    @BeforeEach
    void setUp() {
        validator = new LoginValidator();
    }

    @Test
    void validate_validEmailAndPassword_doesNotThrow() {
        assertDoesNotThrow(() -> validator.validate(VALID_EMAIL, VALID_PASSWORD));
    }

    @Test
    void validate_emailWithWhitespace_trimsAndSucceeds() {
        assertDoesNotThrow(() -> validator.validate("  " + VALID_EMAIL + "  ", "  " + VALID_PASSWORD + "  "));
    }

    // --- Email required ---

    @Test
    void validate_nullEmail_throwsEmailRequired() {
        ValidationException e = assertThrows(ValidationException.class, () ->
                validator.validate(null, VALID_PASSWORD));
        assertEquals(ValidationMessages.EMAIL_REQUIRED, e.getMessage());
        assertEquals("email", e.getField());
    }

    @Test
    void validate_emptyEmail_throwsEmailRequired() {
        ValidationException e = assertThrows(ValidationException.class, () ->
                validator.validate("", VALID_PASSWORD));
        assertEquals(ValidationMessages.EMAIL_REQUIRED, e.getMessage());
        assertEquals("email", e.getField());
    }

    @Test
    void validate_blankEmail_throwsEmailRequired() {
        ValidationException e = assertThrows(ValidationException.class, () ->
                validator.validate("   ", VALID_PASSWORD));
        assertEquals(ValidationMessages.EMAIL_REQUIRED, e.getMessage());
        assertEquals("email", e.getField());
    }

    // --- Password required ---

    @Test
    void validate_nullPassword_throwsPasswordRequired() {
        ValidationException e = assertThrows(ValidationException.class, () ->
                validator.validate(VALID_EMAIL, null));
        assertEquals(ValidationMessages.PASSWORD_REQUIRED, e.getMessage());
        assertEquals("password", e.getField());
    }

    @Test
    void validate_emptyPassword_throwsPasswordRequired() {
        ValidationException e = assertThrows(ValidationException.class, () ->
                validator.validate(VALID_EMAIL, ""));
        assertEquals(ValidationMessages.PASSWORD_REQUIRED, e.getMessage());
        assertEquals("password", e.getField());
    }

    @Test
    void validate_blankPassword_throwsPasswordRequired() {
        ValidationException e = assertThrows(ValidationException.class, () ->
                validator.validate(VALID_EMAIL, "   "));
        assertEquals(ValidationMessages.PASSWORD_REQUIRED, e.getMessage());
        assertEquals("password", e.getField());
    }

    // --- Email format ---

    @Test
    void validate_invalidEmailFormat_noAt_throwsValidAddress() {
        ValidationException e = assertThrows(ValidationException.class, () ->
                validator.validate("notanemail", VALID_PASSWORD));
        assertEquals(ValidationMessages.EMAIL_VALID_ADDRESS, e.getMessage());
        assertEquals("email", e.getField());
    }

    @Test
    void validate_invalidEmailFormat_missingDomain_throwsValidAddress() {
        ValidationException e = assertThrows(ValidationException.class, () ->
                validator.validate("user@", VALID_PASSWORD));
        assertEquals(ValidationMessages.EMAIL_VALID_ADDRESS, e.getMessage());
        assertEquals("email", e.getField());
    }

    @Test
    void validate_invalidEmailFormat_emptyLocalPart_throwsValidAddress() {
        ValidationException e = assertThrows(ValidationException.class, () ->
                validator.validate("@example.com", VALID_PASSWORD));
        assertEquals(ValidationMessages.EMAIL_VALID_ADDRESS, e.getMessage());
        assertEquals("email", e.getField());
    }

    @Test
    void validate_validEmailWithPlus_doesNotThrow() {
        assertDoesNotThrow(() -> validator.validate("user+tag@example.com", VALID_PASSWORD));
    }

    @Test
    void validate_validEmailWithDots_doesNotThrow() {
        assertDoesNotThrow(() -> validator.validate("first.last@company.co.uk", VALID_PASSWORD));
    }
}
