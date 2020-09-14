package web.mini.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import web.mini.backend.model.User;
import web.mini.backend.repository.PostRepository;
import web.mini.backend.repository.UserRepository;

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
                         int age,
                         String gender) {
        if(repassword.equals(password)){
            User user = new User();

            user.setUserName(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setFirstName(first_name);
            user.setLastName(last_name);
            user.setAge(age);
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
    @RequestMapping(value = "/home")
    public String authLogin() {
        return "home";
    }

}
