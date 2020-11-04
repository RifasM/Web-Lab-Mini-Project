package web.mini.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
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
    public ModelAndView postPage(@PathVariable(value = "post_id") String post_id)
            throws ResourceNotFoundException {
        ModelAndView post_data = new ModelAndView("postTemplates/viewPost");
        Post post = postRepository.findById(post_id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found on :: " + post_id));
        post_data.addObject("post_data", post);
        return post_data;
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

        return "error";
    }
}
