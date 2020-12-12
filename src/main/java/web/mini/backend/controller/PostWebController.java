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
import org.springframework.web.multipart.MultipartFile;
import web.mini.backend.BackendApplication;
import web.mini.backend.exception.ResourceNotFoundException;
import web.mini.backend.model.Board;
import web.mini.backend.model.Post;
import web.mini.backend.repository.BoardRepository;
import web.mini.backend.repository.PostRepository;
import web.mini.backend.repository.UserRepository;

import java.util.Date;
import java.util.List;

@Controller
public class PostWebController {
    private static final Logger LOGGER = LoggerFactory.getLogger(BackendApplication.class);

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private PostController postController;

    @Autowired
    private BoardController boardController;

    @Autowired
    private UserRepository userRepository;


    /**
     * Return Disabled post page if the user is admin
     *
     * @return rendered disabledPosts.html page
     */
    @RequestMapping("/home/disabled")
    public String viewDisabledPosts(Model model) {
        Iterable<Post> posts = postController.getAllDisabledPosts();
        model.addAttribute("posts", posts);
        return "home";
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
                                    @RequestParam String boardId,
                                    @RequestParam String tags,
                                    @RequestParam MultipartFile postFile,
                                    Model model,
                                    Authentication auth) {
        if (!auth.getName().equals("anonymousUser")) {
            Post post = new Post(
                    null,
                    postTitle,
                    postDescription,
                    postType,
                    null,
                    tags,
                    1,
                    auth.getName(),
                    null,
                    null,
                    new Date());
            ResponseEntity<String> result = postController.createPost(post, postFile);

            if (result.getStatusCode().is2xxSuccessful()) {
                ResponseEntity<Board> res = boardController.addPost(boardId, post.getId());
                if (res.getStatusCode().is2xxSuccessful())
                    model.addAttribute("success", post.getId());
                else
                    model.addAttribute("error", result.getBody());

                model.addAttribute("boards", boardController.findByUser(auth.getName()));
                return "postTemplates/createPost";

            }
        }
        return "error";
    }

    /**
     * Return Post Creation Page
     * Get Mapping
     *
     * @return rendered createPost.html
     */
    @GetMapping("/createPost")
    public String createPostPage(Model model,
                                 Authentication auth) {
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
                           Model model,
                           Authentication auth) {

        ResponseEntity<Post> request = postController.getPostsById(post_id);

        if (request.getStatusCode().is2xxSuccessful()) {
            Post post = request.getBody();

            int heart = 0, thumb = 0, wow = 0, haha = 0;
            assert post != null;
            if (post.getPostLikesUserIds() != null) {
                for (String user : post.getPostLikesUserIds().keySet()) {
                    switch (post.getPostLikesUserIds().get(user)) {
                        case 1:
                            heart++;
                            break;
                        case 2:
                            thumb++;
                            break;
                        case 3:
                            wow++;
                            break;
                        case 4:
                            haha++;
                            break;
                    }
                }
            }
            model.addAttribute("post_data", post);

            model.addAttribute("like_heart", heart);
            model.addAttribute("like_thumb", thumb);
            model.addAttribute("like_wow", wow);
            model.addAttribute("like_haha", haha);

            model.addAttribute("user_boards", boardController.findByUser(auth.getName()));

            if (post.getEnabled() == 0 && !auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")))
                return "errorPages/404";

        } else
            return "errorPages/404";


        return "postTemplates/viewPost";
    }

    /**
     * Add a given post id to the respective board id
     *
     * @param boardID the board id to add the post to
     * @param postID  the post id to be added
     * @param model   to return the success or failure state
     * @return the post page with success of error messages
     */
    @PostMapping("/addPost/{postID}")
    public String addPostToBoard(@RequestParam String boardID,
                                 @PathVariable(value = "postID") String postID) {
        ResponseEntity<Board> result = boardController.addPost(boardID, postID);
        if (result.getStatusCode().is2xxSuccessful() && result.getBody() != null)
            return "redirect:/viewBoard/" + boardID;

        return "redirect:/viewPost/" + postID;
    }

    /**
     * Return Home Page after deleting the post
     *
     * @param post_id postID to delete
     * @return redirect to home
     */
    @RequestMapping("/deletePost/{post_id}")
    public String deletePost(@PathVariable(value = "post_id") String post_id,
                             Authentication auth)
            throws ResourceNotFoundException {
        Post post = postController.getPostsById(post_id).getBody();

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
    public String editPost(@PathVariable(value = "post_id") String post_id, Model model) {
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
    public String editPost(@PathVariable String post_id,
                           @RequestParam String postTitle,
                           @RequestParam String postDescription,
                           @RequestParam String tags,
                           Model model,
                           Authentication auth) {
        ResponseEntity<Post> request = postController.getPostsById(post_id);
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

    /**
     * View all the posts of the current logged in user
     *
     * @param model Model to add the attributes to render onto the page
     * @param auth  Get the authentication details of the current user
     * @return the rendered view posts page
     */
    @RequestMapping("/viewPosts")
    public String viewAllUserPosts(Model model,
                                   Authentication auth) {
        List<Post> posts = postController.findByUser(auth.getName());
        model.addAttribute("firstName", userRepository.findByUsername(auth.getName()).getFirstName());
        model.addAttribute("posts", posts);

        return "postTemplates/viewPosts";
    }

    /**
     * Comment on a post based on logged in user
     *
     * @param postID  post ID of the post being commented on
     * @param comment the comment to be saved
     * @param auth    authentication details of current user
     * @return the post page with the updated comment
     */
    @PostMapping("/comment")
    public String commentPost(@RequestParam String postID,
                              @RequestParam String comment,
                              Authentication auth) {
        ResponseEntity<Post> result = postController.getPostsById(postID);
        if (result.getStatusCode().is2xxSuccessful() && result.getBody() != null) {
            result = postController.comment(result.getBody(), comment, auth);
            if (result.getStatusCode().is4xxClientError())
                LOGGER.error("Error creating comment on Post ID: " + postID + ". " +
                        "Provided comment: " + comment);
        }

        return "redirect:/viewPost/" + postID;
    }

    /**
     * Disable a post only if role is ADMIN
     *
     * @param postID post id to disable
     * @param auth   authentication details of logged in user
     * @return post page if successful, else 404 page
     */
    @PostMapping("/disable")
    public String disablePost(@RequestParam String postID,
                              Authentication auth) {
        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            ResponseEntity<Boolean> result = postController.disablePost(postID, auth);
            if (result.getStatusCode().is2xxSuccessful())
                return "redirect:/viewPost/" + postID;
        }

        return "errorPages/404";
    }

    /**
     * Enable a post only if role is ADMIN
     *
     * @param postID post id to enable
     * @param auth   authentication details of logged in user
     * @return post page if successful, else 404 page
     */
    @PostMapping("/enable")
    public String enablePost(@RequestParam String postID,
                             Authentication auth) {
        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            ResponseEntity<Boolean> result = postController.enablePost(postID, auth);
            if (result.getStatusCode().is2xxSuccessful())
                return "redirect:/viewPost/" + postID;
        }

        return "errorPages/404";
    }

    /**
     * Search for posts based on a query string.
     * Search the title and description for the provided string.
     *
     * @param query the query string to search
     * @param model model to add parameters to the template
     * @return rendered home page with search results
     */
    @PostMapping("/search")
    public String searchPosts(@RequestParam String query,
                              Model model) {
        List<Post> result = postController.findPost(query);
        model.addAttribute("search", query);
        model.addAttribute("posts", result);
        return "home";
    }
}
