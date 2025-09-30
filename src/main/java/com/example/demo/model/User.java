package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * User object for a user.
 */
public class User {

    private int user_id;
    private String username;
    private String email;
    private byte[] password;
    private byte[] salt;

    /**
     * Constructor for a user.
     * @param username username for the user
     * @param email email for the user
     * @param password password for the user
     */
    public User(String username, String email, byte[] password, byte[] salt) {
        this.username = username;
        this.email = email;
        this.password = password; //now hashed
        this.salt = salt;
    }

    public User(int user_id, String username, String email, byte[] password, byte[] salt) {
        this.username = username;
        this.user_id = user_id;
        this.email = email;
        this.password = password; //now hashed
        this.salt = salt;
    }

    public User(int user_id, String username, String email) {
        this.username = username;
        this.user_id = user_id;
        this.email = email;
        this.password = null; //now hashed
        this.salt = null;
    }

    /**
     * Gets the username of the user.
     * @return username as string
     */
    public String getUsername() {
        return username;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public boolean has_id() {
        return user_id != 0;
    }

    /**
     * Sets the username of the user.
     * @param username accepts a string and sets it as the username.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the email of the user.
     * @return email as string
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email of the user.
     * @param email accepts a string and sets it as the email.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the password of the user.
     *
     * @return password as string
     */
    public byte[] getPassword() {
        return password;
    }

    /**
     * Sets the password of the user.
     * @param password accepts a string and sets it as the password.
     */
    public void setPassword(byte[] password) {
        this.password = password;
    }

    /**
     * Check if username is valid,
     * @param username, accepts a string to verify
     * @return Optional.empty if valid otherwise list of error messages
     */
    public static Optional<List<String>> verifyUsername(String username) {
        // Username Validation
        // Username should be:
        // Not Null, more than 5 chars, less than 50 characters, Contain only letters, numbers and underscore. Unique!
        List<String> errors = new ArrayList<>();

        if (username == null) {
            errors.add("Username cannot be null");
            return Optional.of(errors);
        }

        int length = username.length();
        if (length <= 5) {
            errors.add("Username must be 6 or more characters");
        }
        if (length > 50) {
            errors.add("Username must be less than 50 characters");
        }

        if (!username.matches("^[A-Za-z0-9_]+$")) {
            errors.add("Username can only contain letters, numbers, and underscore");
        }
    
        return errors.isEmpty() ? Optional.empty() : Optional.of(errors);
    }
    /**
     * Check if password is valid,
     * @param password, accepts a string to verify
     * @return none if valid otherwise exception string
     */
    public static Optional<List<String>> verifyPassword(String password) {
        List<String> errors = new ArrayList<>();

        if (password == null) {
            errors.add("Password cannot be null");
            return Optional.of(errors);
        }

        int length = password.length();
        if (length < 6) {
            errors.add("Password must be at least 6 characters long");
        }
        if (length > 49) {
            errors.add("Password must be less than 50 characters");
        }

        boolean hasUpper = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        // allowed special characters
        String specials = "!@#$%^&*()_+-=[]{};':\"\\|,.<>/?";

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUpper = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (specials.indexOf(c) >= 0) {
                hasSpecial = true;
            } else if (!Character.isLetter(c)) {
                errors.add("Password contains invalid characters");
                return Optional.of(errors);
            }
        }

        if (!hasUpper) errors.add("Password must contain at least one uppercase letter");
        if (!hasDigit) errors.add("Password must contain at least one number");
        if (!hasSpecial) errors.add("Password must contain at least one special character");

        return errors.isEmpty() ? Optional.empty() : Optional.of(errors);
    }

    public byte[] getSalt() {
        return salt;
    }
}
