package com.example.eams_project_fall2024;

public class Administrator {
    private String adminUsername;
    private String adminPassword;

    public Administrator(String adminUsername, String adminPassword) {
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
    }

    public void approveUserRegistration() {}

    public void rejectUserRegistration() {}

    public String getAdminUsername() {
        return adminUsername;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    // New method to compare passwords
    public boolean checkPassword(String inputPassword) {
        return adminPassword.equals(inputPassword);
    }
}

