package web.mini.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import web.mini.backend.exception.ResourceNotFoundException;
import web.mini.backend.model.Board;
import web.mini.backend.model.Post;
import web.mini.backend.repository.BoardRepository;
import web.mini.backend.repository.PostRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class PostWebController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private PostController postController;

    @Autowired
    private BoardController boardController;


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
                                    @RequestParam String boardId,
                                    @RequestParam String tags,
                                    @RequestParam String postUser,
                                    @RequestParam MultipartFile postFile,
                                    Model model) throws ResourceNotFoundException {
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

        if (result.getStatusCode().is2xxSuccessful()) {
            Board board = boardController.getBoardById(boardId).getBody();
            if (board != null) {
                List<String> postIDs = new ArrayList<>();
                if (board.getPostID() != null)
                    postIDs = board.getPostID();

                postIDs.add(post.getId());
                board.setPostID(postIDs);
                boardRepository.save(board);
                model.addAttribute("success", post.getId());
            } else
                model.addAttribute("error", result.getBody());
            return "postTemplates/createPost";

        } else
            return "error";
    }

    /**
     * Return Post Creation Page
     * Get Mapping
     *
     * @return rendered createPost.html
     */
    @GetMapping("/createPost")
    public String createPostPage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<Board> board = boardController.findByUser(auth.getName());
        model.addAttribute("boards", board);
        return "postTemplates/createPost";
    }

    /**
     * Return Post Page
     *
     * @param post_id postID to view
     * @return rendered viewPost.html
     */
    @GetMapping("/viewPost/{post_id}")
    public String postPage(@PathVariable(value = "post_id") String post_id,
                           Model model) {
        try {
            ResponseEntity<Post> request = postController.getPostsById(post_id);
            if (request.getStatusCode().is2xxSuccessful())
                model.addAttribute("post_data", request.getBody());
        } catch (ResourceNotFoundException e) {
            return "errorPages/404";
        }

        return "postTemplates/viewPost";
    }

    /**
     * Return Home Page after deleting the post
     *
     * @param post_id postID to delete
     * @return redirect to home
     */
    @RequestMapping("/deletePost/{post_id}")
    public String deletePost(@PathVariable(value = "post_id") String post_id)
            throws ResourceNotFoundException {
        Post post = postController.getPostsById(post_id).getBody();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (post != null && post.getPostUser().equals(auth.getName())) {
            postController.deletePost(post_id);
            return "redirect:/";
        }

        return "errorPages/404";
    }

    /**
     * Handle Post Edit Page request
     * Get Mapping
     *
     * @return rendered editPost.html
     */
    @GetMapping("/editPost/{post_id}")
    public String editPost(@PathVariable(value = "post_id") String post_id, Model model)
            throws ResourceNotFoundException {
        ResponseEntity<Post> post = postController.getPostsById(post_id);
        if (post.getStatusCode().is2xxSuccessful())
            model.addAttribute("post_data", post.getBody());
        else
            return "errorPages/404";

        return "postTemplates/editPost";
    }

    /**
     * Handle Post Edit Page request
     * Post Mapping
     *
     * @return rendered editPost.html
     */
    @PostMapping("/editPost/{post_id}")
    public String createPostProcess(@PathVariable String post_id,
                                    @RequestParam String postTitle,
                                    @RequestParam String postDescription,
                                    @RequestParam String tags,
                                    Model model) throws ResourceNotFoundException {
        ResponseEntity<Post> request = postController.getPostsById(post_id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (request.getStatusCode().is2xxSuccessful() && request.getBody() != null) {
            Post post = request.getBody();
            if (post.getPostUser().equals(auth.getName())) {
                post.setPostTitle(postTitle);
                post.setPostDescription(postDescription);
                post.setTags(tags);
                postRepository.save(post);
                model.addAttribute("success", post_id);
            } else
                model.addAttribute("error", "unauthorised");

            model.addAttribute("post_data", post);
            return "postTemplates/editPost";
        }

        return "errorPages/404";
    }
}
