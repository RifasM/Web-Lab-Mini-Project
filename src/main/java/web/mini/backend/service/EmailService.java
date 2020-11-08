package web.mini.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import web.mini.backend.BackendApplication;
import web.mini.backend.model.User;

import javax.mail.MessagingException;
import java.net.InetAddress;
import java.util.Objects;

@Service
public class EmailService {
    @Autowired
    private Environment environment;

    private static final Logger LOGGER = LoggerFactory.getLogger(BackendApplication.class);
    private final TemplateEngine templateEngine;
    private final JavaMailSender javaMailSender;

    public EmailService(TemplateEngine templateEngine, JavaMailSender javaMailSender) {
        this.templateEngine = templateEngine;
        this.javaMailSender = javaMailSender;
    }

    public ResponseEntity<String> sendMail(User user, String uuid) {
        try {
            String server = InetAddress.getLoopbackAddress().getHostName();

            int port = Integer.parseInt(Objects.requireNonNull(environment.getProperty("server.port")));
            if (port != 80)
                server += ":" + port;

            Context context = new Context();
            context.setVariable("user", user);
            context.setVariable("uuid", uuid);
            context.setVariable("server", server);

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
