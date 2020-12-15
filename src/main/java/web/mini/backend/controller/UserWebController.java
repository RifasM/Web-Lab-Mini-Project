package web.mini.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import web.mini.backend.BackendApplication;
import web.mini.backend.exception.ResourceNotFoundException;
import web.mini.backend.model.ResetPassword;
import web.mini.backend.model.User;
import web.mini.backend.repository.ResetPasswordRepository;
import web.mini.backend.repository.UserRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

@Controller
public class UserWebController {
    private static final Logger LOGGER = LoggerFactory.getLogger(BackendApplication.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordController passwordController;

    @Autowired
    private PostController postController;

    @Autowired
    private BoardController boardController;

    @Autowired
    private UserController userController;

    @Autowired
    private ResetPasswordRepository resetPasswordRepository;


    /**
     * Return the Dashboard for that user
     *
     * @param model Model to add the attributes to render onto the page
     * @param auth  Get the authentication details of the current user
     * @return The rendered profile page
     */
    @RequestMapping("/dashboard")
    public String profile(Model model,
                          Authentication auth) {
        model.addAttribute("firstName", userRepository.findByUsername(auth.getName()).getFirstName());
        return "userTemplates/dashboard";
    }

    /**
     * Return the profile of the user if active
     *
     * @param username the username of the user whose profile has been requested
     * @param model    Model to add the attributes to render onto the page
     * @return The rendered profile page
     */
    @RequestMapping("/profile/{username}")
    public String profile(@PathVariable(value = "username") String username,
                          Model model) {
        ResponseEntity<User> result = userController.getEnabledUsersByUsername(username);
        return getString(username, model, result);
    }

    /**
     * Return the profile of the disabled user
     *
     * @param username the username of the user whose profile has been requested
     * @param model    Model to add the attributes to render onto the page
     * @return The rendered profile page
     */
    @RequestMapping("/profile/disabled/{username}")
    public String disabledProfile(@PathVariable(value = "username") String username,
                                  Model model,
                                  Authentication auth) {
        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            ResponseEntity<User> result = userController.getDisabledUsersByUsername(username);
            return getString(username, model, result);
        }

        return "errorPages/403";
    }

    /**
     * Get the page to be returned based on the given parameters
     * @param username name of the user logged in
     * @param model to send back data to the templates
     * @param result the ResponseEntity Variable to fetch data from
     * @return the relevent template name to be returned
     */
    private String getString(String username, Model model, ResponseEntity<User> result) {
        if (result.getStatusCode().is2xxSuccessful() && result.getBody() != null) {
            model.addAttribute("user", result.getBody());
            model.addAttribute("posts", postController.findByUser(username));
            model.addAttribute("boards", boardController.findByUser(username));

            return "userTemplates/profile";
        }

        return "errorPages/404";
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
     * Get mapping for reset password page
     *
     * @param token The token received in the mail
     * @param model Model to add the attributes
     * @return Rendered reset password page
     */
    @GetMapping("/resetPassword/{token}")
    public String resetPasswordGet(@PathVariable(value = "token") String token,
                                   Model model) {
        ResponseEntity<ResetPassword> reset = passwordController.validateToken(token);

        if (reset.getStatusCode().is2xxSuccessful())
            model.addAttribute("valid", token);
        else
            model.addAttribute("error", true);

        return "passwordTemplates/resetPassword";
    }

    /**
     * Post mapping for reset password page
     *
     * @param token The token received in the mail
     * @param model Model to add the attributes
     * @return Rendered reset password page after resting the password
     */
    @PostMapping("/resetPassword/{token}")
    public String resetPasswordPost(@PathVariable(value = "token") String token,
                                    @RequestParam String password1,
                                    Model model) throws ResourceNotFoundException {
        ResponseEntity<ResetPassword> reset = passwordController.validateToken(token);

        if (reset.getStatusCode().is2xxSuccessful()) {
            User user = userRepository.findById(
                    Objects.requireNonNull(reset.getBody()).getUser())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found for token "
                            + token + "with User ID: " + reset.getBody().getUser()));

            String encryptedPassword = userController.passwordEncoder().encode(password1);
            user.setPassword(encryptedPassword);

            userRepository.save(user);
            resetPasswordRepository.delete(Objects.requireNonNull(reset.getBody()));
            model.addAttribute("success", true);
        } else
            model.addAttribute("error", true);

        return "passwordTemplates/resetPassword";
    }

    /**
     * Deactivate the user profile of a given user
     *
     * @param username the username to deactivate
     * @param auth     authentication details to check if current user is an admin
     * @return the user profile page if successful, else return 404
     */
    @RequestMapping("/deactivate/{username}")
    public String deactivateUser(@PathVariable(value = "username") String username,
                                 Authentication auth) {
        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            Boolean result = userController.deactivateUser(username, auth.getName());
            if (result)
                return "redirect:/profile/disabled/" + username;
            else {
                LOGGER.error("Could not deactivate user: " + username + ", with admin user authentication: " + auth.getName());
                return "errorPages/500";
            }
        }

        return "errorPages/403";
    }

    /**
     * Activate the user profile of a given user
     *
     * @param username the username to activate
     * @param auth     authentication details to check if current user is an admin
     * @return the user profile page if successful, else return 404
     */
    @RequestMapping("/activate/{username}")
    public String activateUser(@PathVariable(value = "username") String username,
                               Authentication auth) {
        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            Boolean result = userController.activateUser(username, auth.getName());
            if (result)
                return "redirect:/profile/" + username;
            else
                LOGGER.error("Could not activate user: " + username + ", with admin user authentication: " + auth.getName());
        }

        return "errorPages/403";
    }

    @RequestMapping("/disabledUsers")
    public String disabledUsers(Authentication auth,
                                Model model) {
        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            model.addAttribute("users", userController.getAllDisabledUsers());

            return "userTemplates/disabledUsers";
        }

        return "errorPages/404";
    }
}
