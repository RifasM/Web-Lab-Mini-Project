package web.mini.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import web.mini.backend.exception.ResourceNotFoundException;
import web.mini.backend.model.Post;
import web.mini.backend.repository.PostRepository;
import web.mini.backend.repository.UserRepository;

import java.util.List;
import java.util.Map;

import static web.mini.backend.controller.BoardController.getStringBooleanMap;

@RestController
@RequestMapping("/api/v1/")
public class PostController {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final AwsController awsController;

    public PostController(PostRepository postRepository,
                          UserRepository userRepository,
                          AwsController awsController) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.awsController = awsController;
    }

    /**
     * Get all post list.
     *
     * @return the list
     */
    @GetMapping("/posts")
    public Iterable<Post> getAllPosts() {
        return postRepository.findAllByOrderByPostDateDesc();
    }

    /**
     * Gets post by id.
     *
     * @param postID the post id
     * @return the posts by id
     */
    @GetMapping("/post/{id}")
    public ResponseEntity<Post> getPostsById(@PathVariable(value = "id") String postID) {
        Post post = null;
        try {
            post = postRepository.findById(postID)
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Post not found on :: " + postID));
        } catch (ResourceNotFoundException e) {
            ResponseEntity.status(500).body(null);
        }
        return ResponseEntity.ok().body(post);
    }

    /**
     * Gets post by user id.
     *
     * @param userName the user's name for which posts are to be fetched
     * @return the posts by user id
     */
    @GetMapping("/post/user/{userID}")
    public List<Post> findByUser(@PathVariable(value = "userID") String userName) {
        return postRepository.findByPostUser(userName);
    }

    /**
     * Create post.
     *
     * @param postFile the post file
     * @return the status
     */
    @PostMapping("/post")
    public ResponseEntity<String> createPost(@RequestParam Post post,
                                             @RequestParam MultipartFile postFile) {

        ResponseEntity<String> status = awsController.uploadFileToS3Bucket(postFile,
                "posts");

        if (status.getStatusCode().is2xxSuccessful()) {
            try {
                post.setPostUrl(status.getBody());
                postRepository.save(post);
                return ResponseEntity.ok().body("created successfully");
            } catch (Exception e) {
                return ResponseEntity.status(500).body("Error: " + e.getMessage());
            }
        }
        return ResponseEntity.badRequest().body("Error");
    }

    /**
     * Delete post map.
     *
     * @param postID the post id
     * @return the map
     * @throws ResourceNotFoundException the exception
     */
    @DeleteMapping("/post/{id}")
    public Map<String, Boolean> deletePost(@PathVariable(value = "id") String postID) throws ResourceNotFoundException {
        Post post =
                postRepository
                        .findById(postID)
                        .orElseThrow(() -> new ResourceNotFoundException("Post not found on :: " + postID));

        postRepository.delete(post);
        int status = this.awsController.deleteFileFromS3Bucket(post.getPostUrl(), "posts").getStatusCodeValue();

        return getStringBooleanMap(status);
    }

    /**
     * Find post.
     *
     * @param title the post
     * @return the post
     */
    @GetMapping("/post/{title}")
    public List<Post> findByTitle(@PathVariable(value = "title") String title) {
        return postRepository.findByPostTitle(title);
    }
}
