package org.acme.domain;

public class User {

    private final String id;
    private final String username;
    private final String email;
    private final String fullName;

    public User(String id, String username, String email, String fullName) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("User ID must not be blank");
        }
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username must not be blank");
        }
        this.id = id;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }
}
