package com.g08.gig.business.validation;

import com.g08.gig.business.constants.ValidationMessages;

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

    @Test
    void validate_firstNameContainsDigits_throwsLettersOnly() {
        ValidationException e = assertThrows(ValidationException.class, () ->
                validator.validate("Jennifer1", VALID_LAST, VALID_EMAIL, VALID_PHONE));
        assertEquals(ValidationMessages.FIRST_NAME_LETTERS_ONLY, e.getMessage());
    }

    @Test
    void validate_firstNameContainsInvalidSymbols_throwsLettersOnly() {
        assertThrows(ValidationException.class, () ->
                validator.validate("Jennifer@Lee", VALID_LAST, VALID_EMAIL, VALID_PHONE));
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

    @Test
    void validate_lastNameContainsDigits_throwsLettersOnly() {
        ValidationException e = assertThrows(ValidationException.class, () ->
                validator.validate(VALID_FIRST, "Lee99", VALID_EMAIL, VALID_PHONE));
        assertEquals(ValidationMessages.LAST_NAME_LETTERS_ONLY, e.getMessage());
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
        assertEquals("Email must be a valid format.", e.getMessage());
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
        assertEquals("Phone number must be 10 to 15 digits.", e.getMessage());
    }

    @Test
    void validate_phoneNotFifteenDigits_tooLong_throws() {
        assertThrows(ValidationException.class, () ->
                validator.validate(VALID_FIRST, VALID_LAST, VALID_EMAIL, "1234567890123456"));
    }

    @Test
    void validate_phoneExactlyTenDigits_doesNotThrow() {
        assertDoesNotThrow(() -> validator.validate(VALID_FIRST, VALID_LAST, VALID_EMAIL, "1234567890"));
    }

    @Test
    void validate_phoneContainsNonDigits_throwsDigitsOnly() {
        ValidationException e = assertThrows(ValidationException.class, () ->
                validator.validate(VALID_FIRST, VALID_LAST, VALID_EMAIL, "204-555-1234"));
        assertEquals(ValidationMessages.PHONE_DIGITS_ONLY, e.getMessage());
    }

    @Test
    void validate_phoneContainsLetters_throwsDigitsOnly() {
        assertThrows(ValidationException.class, () ->
                validator.validate(VALID_FIRST, VALID_LAST, VALID_EMAIL, "204abc5555"));
    }

    @Test
    void validate_phoneExactlyFifteenDigits_doesNotThrow() {
        assertDoesNotThrow(() -> validator.validate(VALID_FIRST, VALID_LAST, VALID_EMAIL, "123456789012345"));
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
                "  password1234  ", "  password1234  "));
    }

    @Test
    void validate_sixArg_passwordShorterThan8Chars_throwsMinLength() {
        ValidationException e = assertThrows(ValidationException.class, () ->
                validator.validate(VALID_FIRST, VALID_LAST, VALID_EMAIL, VALID_PHONE, "Short1!", "Short1!"));
        assertEquals(ValidationMessages.PASSWORD_MIN_LENGTH, e.getMessage());
    }

    @Test
    void validate_sixArg_passwordExactly8Chars_doesNotThrow() {
        assertDoesNotThrow(() -> validator.validate(VALID_FIRST, VALID_LAST, VALID_EMAIL, VALID_PHONE,
                "Exactly8", "Exactly8"));
    }
}
