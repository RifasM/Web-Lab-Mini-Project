package web.mini.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import web.mini.backend.model.Post;
import web.mini.backend.model.User;
import web.mini.backend.repository.UserRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;

@Controller
public class WebController {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostController postController;

    /**
     * Return Landing Page
     *
     * @return rendered landing.html
     */
    @RequestMapping("/")
    public String index(Authentication auth) {
        if (auth != null && !("anonymousUser").equals(auth.getName()))
            return "redirect:/home";

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
    public String signup(Model model) {
        model.addAttribute("username_exists", "no");
        return "signup";
    }

    /**
     * Process Register Page
     *
     * @return signup.html if failed, else home
     */
    @PostMapping("/signup")
    public String signup(String first_name,
                         String last_name,
                         String email,
                         String username,
                         String password,
                         String phone,
                         String dob,
                         String gender,
                         Model model) throws ParseException {

        User check = userRepository.findByUsername(username);

        if (check != null) {
            model.addAttribute("username_exists", "yes");
            return "signup";
        }

        User user = new User();
        user.setUserName(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setFirstName(first_name);
        user.setLastName(last_name);
        user.setEmail(email);
        user.setPhone(phone);
        user.setDob(new SimpleDateFormat("yyyy-MM-dd").parse(dob));
        user.setGender(gender);
        user.setEnabled(1);
        user.setRole("ROLE_USER");

        userRepository.save(user);

        return "login";
    }

    /**
     * Return Home Page post login
     *
     * @return rendered home.html
     */
    @RequestMapping("/home")
    public String authLogin(Model model) {
        Iterable<Post> posts = postController.getAllEnabledPosts();
        model.addAttribute("posts", posts);
        model.addAttribute("recent_posts", postController.recentPosts());

        return "home";
    }
}
