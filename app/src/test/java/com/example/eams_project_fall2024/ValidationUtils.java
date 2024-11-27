package com.example.eams_project_fall2024;

public class ValidationUtils {

    /**
     * Email validation utility method
     * @param email
     * @return
     */
    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
}

