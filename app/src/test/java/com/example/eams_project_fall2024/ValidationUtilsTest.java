package com.example.eams_project_fall2024;

import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class ValidationUtilsTest {

    @Test
    public void testValidEmails() {
        assertTrue("Valid email should pass", ValidationUtils.isValidEmail("user@example.com"));
        assertTrue("Valid email with subdomain should pass", ValidationUtils.isValidEmail("user@mail.example.com"));
    }

    @Test
    public void testEmptyAndNullEmails() {
        assertFalse("Empty email should fail", ValidationUtils.isValidEmail(""));
        assertFalse("Null email should fail", ValidationUtils.isValidEmail(null));
    }

    @Test
    public void testInvalidEmails() {
        assertFalse("Email without @ should fail", ValidationUtils.isValidEmail("userexample.com"));
    }

    @Test
    public void testEmailsWithInvalidCharacters() {
        assertFalse("Email with spaces should fail", ValidationUtils.isValidEmail("user @example.com"));
    }
}




