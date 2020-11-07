package web.mini.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import web.mini.backend.model.User;
import web.mini.backend.repository.UserRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class UserWebController {

    @Autowired
    private UserRepository userRepository;

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
}
