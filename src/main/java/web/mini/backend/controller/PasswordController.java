package web.mini.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import web.mini.backend.model.ResetPassword;
import web.mini.backend.model.User;
import web.mini.backend.repository.ResetPasswordRepository;
import web.mini.backend.repository.UserRepository;
import web.mini.backend.service.EmailService;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Controller
@RequestMapping("/api/v1/security/")
public class PasswordController {
    @Autowired
    ResetPasswordRepository resetPasswordRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;

    /**
     * Send the email to the user's email upon validation of the user's username and email ID
     *
     * @param userName The username of the user requesting a password reset
     * @param email    The email of the user requesting a password reset
     * @return The response Entity of type reset password
     */
    @PostMapping("/resetPassword")
    public ResponseEntity<ResetPassword> getResetLink(@RequestParam String userName,
                                                      @RequestParam String email) {
        User user = userRepository.findByUsername(userName);
        ResetPassword resetPassword = new ResetPassword();

        if (user.getEmail().equals(email)) {
            String token = UUID.randomUUID().toString();

            resetPassword.setToken(token);
            resetPassword.setUser(user.getId());
            resetPassword.setExpiryDate(new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(ResetPassword.getEXPIRATION())));

            ResponseEntity<String> result = emailService.sendMail(user, token);

            resetPasswordRepository.save(resetPassword);

            if (result.getStatusCode().is2xxSuccessful())
                return ResponseEntity.ok().body(resetPassword);
        }
        return ResponseEntity.badRequest().body(null);
    }

    /**
     * Validate the token sent as the parameter
     *
     * @param token The String token to validate if it exists and if its valid
     * @return The Response Entity
     */
    @GetMapping("/validateToken")
    public ResponseEntity<ResetPassword> validateToken(@RequestParam String token) {
        ResetPassword resetPassword = resetPasswordRepository.findByToken(token);

        if (resetPassword.getExpiryDate().before(Calendar.getInstance().getTime())) {
            resetPasswordRepository.delete(resetPassword);
            return ResponseEntity.badRequest().body(null);
        }

        return ResponseEntity.ok().body(resetPassword);
    }
}
