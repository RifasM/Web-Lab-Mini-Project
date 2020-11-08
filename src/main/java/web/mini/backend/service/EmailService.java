package web.mini.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import web.mini.backend.BackendApplication;
import web.mini.backend.model.User;

import javax.mail.MessagingException;

@Service
public class EmailService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BackendApplication.class);
    private final TemplateEngine templateEngine;
    private final JavaMailSender javaMailSender;
    @Value("application.server.url")
    private String serverUrl;

    public EmailService(TemplateEngine templateEngine, JavaMailSender javaMailSender) {
        this.templateEngine = templateEngine;
        this.javaMailSender = javaMailSender;
    }

    public ResponseEntity<String> sendMail(User user, String uuid) {
        try {
            Context context = new Context();
            context.setVariable("user", user);
            context.setVariable("serverUrl", serverUrl);
            context.setVariable("uuid", uuid);

            String process = templateEngine.process("emailTemplates/resetPassword", context);
            javax.mail.internet.MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
            helper.setSubject("Reset Password Link for " + user.getUserName());
            helper.setText(process, true);
            helper.setTo(user.getEmail());
            javaMailSender.send(mimeMessage);
            return ResponseEntity.ok().body("Sent");
        } catch (MessagingException e) {
            LOGGER.error("Failed To send Reset Password Email to " + user.getEmail() + ". Cause: " + e.getMessage());
        }
        return ResponseEntity.status(500).body("error");
    }
}
