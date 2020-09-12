package web.mini.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import web.mini.backend.repository.PostRepository;
import web.mini.backend.repository.UserRepository;
import web.mini.backend.utils.PasswordUtil;

@Controller
public class WebController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    private PasswordUtil passwordUtil;

    /**
     * Return Landing Page
     *
     * @return rendered landpage.html
     */
    @RequestMapping("/")
    public String index() {
        return "landpage";
    }

    /**
     * Return Login Page
     *
     * @return rendered login.html
     */
    @RequestMapping("/login")
    public String login(Model model) {
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
     * Return Home Page post login
     *
     * @param userName
     * @param password
     * @return rendered home.html
     */
    @RequestMapping(value = "/auth-login", method = RequestMethod.POST)
    public String authLogin() {
        return "home";
    }
}
