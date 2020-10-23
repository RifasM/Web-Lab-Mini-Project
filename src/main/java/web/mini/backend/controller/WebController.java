package web.mini.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import web.mini.backend.exception.ResourceNotFoundException;
import web.mini.backend.model.Post;
import web.mini.backend.model.User;
import web.mini.backend.repository.PostRepository;
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

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostController postController;

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
     * Return Post Creation Page
     * Get Mapping
     *
     * @return rendered createPost.html
     */
    @GetMapping("/createPost")
    public String createPostPage() {
        return "createPost";
    }


    /**
     * Handle Post Creation Page request
     * Post Mapping
     *
     * @return rendered createPost.html
     */
    @PostMapping("/createPost")
    public String createPostProcess(@RequestParam String postTitle,
                                    @RequestParam String postDescription,
                                    @RequestParam String postType,
                                    @RequestParam String tags,
                                    @RequestParam String postUser,
                                    @RequestParam MultipartFile postFile) {
        Post post = new Post(
                null,
                postTitle,
                postDescription,
                postType,
                null,
                tags,
                1,
                userRepository.findByUsername(postUser).getId(),
                null,
                null,
                new Date());

        ResponseEntity<String> result = postController.createPost(post, postFile);

        if (result.getStatusCode().is2xxSuccessful())
            return "postTemplates/createPost";
        else
            return "postTemplates/createPost?error=true";
    }

    /**
     * Return Post Page
     *
     * @param post_id postID to view
     * @return rendered viewPost.html
     */
    @GetMapping("/viewPost/{post_id}")
    public ModelAndView createPostPage(@PathVariable(value = "post_id") String post_id)
            throws ResourceNotFoundException {
        ModelAndView post_data = new ModelAndView("postTemplates/viewPost");
        Post post = postRepository.findById(post_id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found on :: " + post_id));
        post_data.addObject("post_data", post);
        return post_data;
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
    public String profile(String id,
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
            return "profile";
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
