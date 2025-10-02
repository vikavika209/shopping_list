package com.auth.shopping_list.entity;

public enum Role {
    ROLE_USER("USER"),
    ROLE_ADMIN("ADMIN");

    private final String role;

    Role(String role) {
        this.role = role;
    }

    public String getRole() {
        return name();
    }
}
