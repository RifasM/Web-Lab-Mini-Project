package web.mini.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import web.mini.backend.model.User;
import web.mini.backend.repository.UserRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
                         String name,
                         String dob,
                         String gender) throws ParseException {
        if (repassword.equals(password)) {
            User user = new User();

            user.setUserName(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setName(name);
            user.setDob(new SimpleDateFormat("yyyy-MM-dd").parse(dob));
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
    public ModelAndView profile(Authentication auth) {
        ModelAndView user_data = new ModelAndView("profile");
        user_data.addObject("username_exists", "no");
        user_data.addObject("basic_details", userRepository.findByUsername(auth.getName()));
        return user_data;
    }

    /**
     * Update Profile Page
     *
     * @return updated profile page
     */
    @PostMapping("/profile")
    public ModelAndView profile(String id,
                                String email,
                                String name,
                                String dob,
                                String gender) throws ParseException {
        ModelAndView errors = new ModelAndView("profile");

        User user = userRepository.findById(Long.parseLong(id)).orElseThrow();
        User check = userRepository.findByUsername(email);

        if (check != null && !user.getUserName().equals(email)) {
            errors.addObject("username_exists", "yes");
            return errors;
        }

        user.setUserName(email);
        user.setName(name);
        user.setDob(new SimpleDateFormat("yyyy-MM-dd").parse(dob));
        user.setGender(gender);
        user.setUpdatedAt(new Date());
        user.setUpdatedBy(email);

        userRepository.save(user);

        errors.addObject("basic_details", user);
        errors.addObject("username_exists", "no");

        return errors;

    }
}
