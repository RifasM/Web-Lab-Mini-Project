package web.mini.backend.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordUtil {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

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