package web.mini.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import web.mini.backend.model.User;
import web.mini.backend.repository.UserRepository;

import java.util.Date;

@Controller
public class WebController {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    /**
     * Return Landing Page
     *
     * @return rendered landing.html
     */
    @RequestMapping("/")
    public String index() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && !("anonymousUser").equals(auth.getName())) {
            return "home";
        }

        return "index";
    }

    /**
     * Return Login Page
     *
     * @return rendered login.html
     */
    @RequestMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * Return Register Page
     *
     * @return rendered signup.html
     */
    @RequestMapping("/signup")
    public String signup() {
        return "signup";
    }

    /**
     * Process Register Page
     *
     * @return signup.html if failed, else home
     */
    @PostMapping("/signup")
    public String signup(String username,
                         String password,
                         String repassword,
                         String first_name,
                         String last_name,
                         Date age,
                         String gender) {
        if(repassword.equals(password)){
            User user = new User();

            user.setUserName(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setFirstName(first_name);
            user.setLastName(last_name);
            user.setDob(age);
            user.setGender(gender);
            user.setEnabled(1);
            user.setRole("ROLE_USER");

            userRepository.save(user);

            return "home";
        }
        return "signup";
    }

    /**
     * Return Home Page post login
     *
     * @return rendered home.html
     */
    @RequestMapping("/home")
    public String authLogin() {
        return "home";
    }

    @RequestMapping("/profile")
    public String profile(){
        return "profile";
    }
}
