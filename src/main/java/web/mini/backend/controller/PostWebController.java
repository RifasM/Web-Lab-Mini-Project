package web.mini.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import web.mini.backend.exception.ResourceNotFoundException;
import web.mini.backend.model.Post;
import web.mini.backend.repository.PostRepository;

import java.util.Date;

@Controller
public class PostWebController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostController postController;

    @Autowired
    private BoardController boardController;

    /**
     * Return Post Creation Page
     * Get Mapping
     *
     * @return rendered createPost.html
     */
    @GetMapping("/createPost")
    public String createPostPage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("boards", boardController.findByUser(auth.getName()));
        return "postTemplates/createPost";
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
                postUser,
                null,
                null,
                new Date());

        ResponseEntity<String> result = postController.createPost(post, postFile);

        if (result.getStatusCode().is2xxSuccessful())
            return "postTemplates/createPost";
        else
            return "error";
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


}
