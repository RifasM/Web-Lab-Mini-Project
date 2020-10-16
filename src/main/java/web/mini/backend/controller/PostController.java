package web.mini.backend.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import web.mini.backend.exception.ResourceNotFoundException;
import web.mini.backend.model.Post;
import web.mini.backend.repository.PostRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/")
public class PostController {

    private final PostRepository postRepository;
    private AwsController awsController;

    public PostController(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    /**
     * Get all post list.
     *
     * @return the list
     */
    @GetMapping("/posts")
    public Iterable<Post> getAllPosts() {
        return postRepository.findAll();
    }

    /**
     * Gets post by id.
     *
     * @param postID the post id
     * @return the posts by id
     * @throws ResourceNotFoundException the resource not found exception
     */
    @GetMapping("/post/{id}")
    public ResponseEntity<Post> getPostsById(@PathVariable(value = "id") String postID)
            throws ResourceNotFoundException {
        Post post =
                postRepository
                        .findById(postID)
                        .orElseThrow(() -> new ResourceNotFoundException("Post not found on :: " + postID));
        return ResponseEntity.ok().body(post);
    }

    /**
     * Gets post by user id.
     *
     * @param userID the user id
     * @return the posts by user id
     */
    @GetMapping("/post/user/{userID}")
    public Page<Post> findByUser(@PathVariable(value = "userID") Long userID, PageRequest pageRequest) {
        return postRepository.findByPostUser(userID, pageRequest);
    }

    /**
     * Create post.
     *
     * @param post          the post
     * @param multipartFile the post file
     * @return the status
     */
    @PostMapping("/post")
    public ResponseEntity<String> createPost(@RequestBody Post post, @RequestParam MultipartFile multipartFile) {
        ResponseEntity<String> status = this.awsController.uploadFileToS3Bucket(multipartFile,
                "posts");
        if (status.getStatusCode().is2xxSuccessful()) {
            try {
                post.setPostUrl(status.getBody());
                System.out.println(post);
                postRepository.save(post);
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

        Map<String, Boolean> response = new HashMap<>();

        if (status != 500)
            response.put("deleted", Boolean.TRUE);
        else {
            response.put("deleted from database", Boolean.TRUE);
            response.put("deleted from s3", Boolean.FALSE);
        }

        return response;
    }

    /**
     * Create post.
     *
     * @param title the post
     * @return the post
     */
    @GetMapping("/post/{title}")
    public List<Post> findByTitle(@PathVariable(value = "title") String title) {
        return postRepository.findByPostTitle(title);
    }
}
