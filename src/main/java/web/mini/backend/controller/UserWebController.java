package web.mini.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import web.mini.backend.model.ResetPassword;
import web.mini.backend.model.User;
import web.mini.backend.repository.UserRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class UserWebController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordController passwordController;


    /**
     * Return the profile page for that user
     *
     * @param model Model to add the attributes to render onto the page
     * @param auth  Get the authentication details of the current user
     * @return The rendered profile page
     */
    @RequestMapping("/profile")
    public String profile(Model model, Authentication auth) {
        model.addAttribute("firstName", userRepository.findByUsername(auth.getName()).getFirstName());
        return "userTemplates/profile";
    }

    /**
     * Return the Profile Edit Page
     * Get Mapping
     *
     * @param auth Get the authentication details of the current user
     * @return The rendered edit profile page
     */
    @RequestMapping("/editProfile")
    public ModelAndView editProfile(Authentication auth) {
        ModelAndView user_data = new ModelAndView("userTemplates/editProfile");
        user_data.addObject("username_exists", "no");
        user_data.addObject("basic_details", userRepository.findByUsername(auth.getName()));
        return user_data;
    }

    /**
     * Update Profile Page
     *
     * @return updated profile page
     */
    @PostMapping("/editProfile")
    public String editProfile(String id,
                              String username,
                              String email,
                              String first_name,
                              String last_name,
                              String phone,
                              String dob,
                              String gender,
                              Model errors) throws ParseException {

        User user = userRepository.findById(Long.parseLong(id)).orElseThrow();
        User check = userRepository.findByUsername(username);

        if (check != null && !user.getUserName().equals(username)) {
            errors.addAttribute("username_exists", "yes");
            return "userTemplates/editProfile";
        }

        user.setUserName(username);
        user.setEmail(email);
        user.setFirstName(first_name);
        user.setLastName(last_name);
        user.setPhone(phone);
        user.setDob(new SimpleDateFormat("yyyy-MM-dd").parse(dob));
        user.setGender(gender);
        user.setUpdatedAt(new Date());
        user.setUpdatedBy(email);

        userRepository.save(user);

        errors.addAttribute("basic_details", user);
        errors.addAttribute("username_exists", "no");

        return "redirect:/profile";
    }

    /**
     * Get Mapping to render the forgot password page
     *
     * @return The rendered forgot password page
     */
    @GetMapping("/forgotPassword")
    public String forgotPassword() {
        return "passwordTemplates/forgotPassword";
    }

    /**
     * Post Mapping to render the forgot password page
     *
     * @param username Username of the requesting user
     * @param email    Email of the requesting user
     * @param model    Model to add the attributes to render on the html page
     * @return rendered HTML page
     */
    @PostMapping("/forgotPassword")
    public String forgotPassword(@RequestParam String username,
                                 @RequestParam String email,
                                 Model model) {
        ResponseEntity<ResetPassword> reset = passwordController.getResetLink(username, email);
        if (reset.getStatusCode().is2xxSuccessful())
            model.addAttribute("success", true);
        else
            model.addAttribute("error", reset.getBody());
        return "passwordTemplates/forgotPassword";
    }

    /**
     * Get mapping for rest password page
     *
     * @param token The token received in the mail
     * @param model Model to add the attributes
     * @return Rendered reset password page
     */
    @GetMapping("/resetPassword/{token}")
    public String resetPassword(@PathVariable(value = "token") String token,
                                Model model) {
        ResponseEntity<ResetPassword> reset = passwordController.validateToken(token);

        if (reset.getStatusCode().is2xxSuccessful())
            model.addAttribute("valid", true);
        else
            model.addAttribute("error", true);

        return "passwordTemplates/resetPassword";


    }
}
