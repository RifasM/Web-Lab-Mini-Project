package web.mini.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class webController {
    /**
     * Return Landing Page
     *
     * @return rendered landing.html
     */
    @RequestMapping("/")
    public String index() {
        return "landing";
    }
}
