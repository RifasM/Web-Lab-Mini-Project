package web.mini.backend.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtil {
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // Encrypt Password with BCryptPasswordEncoder
    public String encryptPassword(String password) {

        return this.encoder.encode(password);
    }

    // Validate Password with BCryptPasswordEncoder
    public boolean validatePassword(String rawPassword, String encodedPassword) {
        return this.encoder.matches(rawPassword, encodedPassword);
    }

}