package web.mini.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import web.mini.backend.repository.PostRepository;
import web.mini.backend.repository.UserRepository;

@Controller
public class WebController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    /**
     * Return Landing Page
     *
     * @return rendered landing.html
     */
    @RequestMapping("/")
    public String index() {
        return "index";
    }

    // TODO: Remove
    @RequestMapping("/old")
    public String old_index() {
        return "landing";
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
     * Return Home Page post login
     *
     * @return rendered home.html
     */
    @RequestMapping(value = "/home")
    public String authLogin() {
        return "home";
    }

    @RequestMapping("/blog")
    public String blog(){
        return "blog";
    }
}
