package web.mini.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import web.mini.backend.model.ResetPassword;
import web.mini.backend.model.User;
import web.mini.backend.repository.UserRepository;
import web.mini.backend.service.EmailService;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Controller
@RequestMapping("/api/v1/security/")
public class passwordController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @PostMapping("/resetPassword")
    public ResponseEntity<ResetPassword> getResetLink(@RequestParam String userName,
                                                      @RequestParam String email) {
        User user = userRepository.findByUsername(userName);
        ResetPassword resetPassword = new ResetPassword();

        if (user.getEmail().equals(email)) {
            String token = UUID.randomUUID().toString();

            resetPassword.setToken(token);
            resetPassword.setUser(user);
            resetPassword.setExpiryDate(new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(ResetPassword.getEXPIRATION())));

            ResponseEntity<String> result = emailService.sendMail(user, token);

            if (result.getStatusCode().is2xxSuccessful())
                return ResponseEntity.ok().body(resetPassword);
        }
        return ResponseEntity.badRequest().body(null);
    }

}
