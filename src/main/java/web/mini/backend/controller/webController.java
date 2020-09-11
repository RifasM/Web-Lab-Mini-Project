package web.mini.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class webController {
    /**
     * Return Landing Page
     *
     * @return rendered landpage.html
     */
    @RequestMapping("/")
    public String index() {
        return "landpage";
    }

    @RequestMapping("/login")
    public String login() {
        return "login";
    }

    @RequestMapping("/signup")
    public String signup() {
        return "signup";
    }
}
