package web.mini.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.UncategorizedElasticsearchException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import web.mini.backend.BackendApplication;
import web.mini.backend.exception.ResourceNotFoundException;
import web.mini.backend.model.Post;
import web.mini.backend.repository.PostRepository;
import web.mini.backend.repository.UserRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static web.mini.backend.controller.BoardController.getStringBooleanMap;

@RestController
@RequestMapping("/api/v1/")
public class PostController {
    private static final Logger LOGGER = LoggerFactory.getLogger(BackendApplication.class);


    private final PostRepository postRepository;
    private final AwsController awsController;

    public PostController(PostRepository postRepository,
                          UserRepository userRepository,
                          AwsController awsController) {
        this.postRepository = postRepository;
        this.awsController = awsController;
    }

    /**
     * Get all the enabled or active post list.
     *
     * @return the list
     */
    @GetMapping("/posts")
    public Iterable<Post> getAllEnabledPosts() {
        Iterable<Post> post = null;
        try {
            // get all the enabled posts
            post = postRepository.findAllByEnabledOrderByPostDateDesc(1);
        } catch (UncategorizedElasticsearchException e) {
            LOGGER.error(e.toString());
        }
        return post;
    }

    /**
     * Get all the disabled or inactive post list.
     *
     * @return the list
     */
    @GetMapping("/posts/disabled")
    public Iterable<Post> getAllDisabledPosts() {
        Iterable<Post> post = null;
        try {
            // get all the disabled posts
            post = postRepository.findAllByEnabledOrderByPostDateDesc(0);
        } catch (UncategorizedElasticsearchException e) {
            LOGGER.error(e.toString());
        }
        return post;
    }

    /**
     * Gets post by id.
     *
     * @param postID the post id
     * @return the posts by id
     */
    @GetMapping("/post/{id}")
    public ResponseEntity<Post> getPostsById(@PathVariable(value = "id") String postID) {
        Post post;
        try {
            post = postRepository.findById(postID)
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Post not found on :: " + postID));
            if (post.getEnabled() == 0)
                return ResponseEntity.status(203).body(post);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(500).body(null);
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
     * @param query the post
     * @return the post
     */
    @GetMapping("/post/search")
    public List<Post> findPost(@RequestParam String query) {
        return postRepository.findByPostTitleContainingOrPostDescriptionContaining(query, query);
    }

    /**
     * Method to like a post, store the user who liked the post
     *
     * @param postID   the post which the respective user has liked
     * @param likeType the type of like, heart, thumbs, curious
     * @param auth     the authentication details of the user
     * @return true if successful, false if failed
     */
    @RequestMapping("/post/like")
    public Boolean likePost(@RequestParam String postID,
                            @RequestParam int likeType,
                            Authentication auth) {
        if (!auth.getName().equals("anonymousUser")) {
            ResponseEntity<Post> result = getPostsById(postID);
            if (result.getStatusCode().is2xxSuccessful()) {
                Post post = result.getBody();

                assert post != null;
                if (post.getPostLikesUserIds() != null)
                    post.getPostLikesUserIds().put(auth.getName(), likeType);
                else {
                    Map<String, Integer> like = new HashMap<>();
                    like.put(auth.getName(), likeType);
                    post.setPostLikesUserIds(like);
                }
                postRepository.save(post);

                return true;
            }
        }

        return false;
    }

    /**
     * Method to comment on a post and map the user id of
     * the person commenting the post
     *
     * @param post    the post which the respective user has commented
     * @param comment the comment which the user has posted
     * @param auth    the authentication details of the request
     * @return true if successful, false if failed
     */
    public ResponseEntity<Post> comment(@RequestParam Post post,
                                        @RequestParam String comment,
                                        Authentication auth) {
        try {
            if (post.getPostCommentsUserIds() != null)
                post.getPostCommentsUserIds().put(auth.getName(), comment);
            else {
                Map<String, String> comments = new HashMap<>();
                comments.put(auth.getName(), comment);
                post.setPostCommentsUserIds(comments);
            }
            postRepository.save(post);

            return ResponseEntity.ok().body(post);
        } catch (NullPointerException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * Disable a given post
     *
     * @param postID post of the post to be disabled
     * @param auth   authentication parameter to check user is ADMIN
     * @return Boolean true if successful and false if failed
     */
    @PostMapping("/disable")
    public ResponseEntity<Boolean> disablePost(@RequestParam String postID,
                                               Authentication auth) {
        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            ResponseEntity<Post> result = getPostsById(postID);
            if (result.getStatusCode().is2xxSuccessful() && result.getBody() != null) {
                result.getBody().setEnabled(0); // disable the post
                postRepository.save(result.getBody());

                return ResponseEntity.ok().body(true);
            }
        }

        return ResponseEntity.badRequest().body(false);
    }

    /**
     * Enable a given post
     *
     * @param postID post of the post to be enabled
     * @param auth   authentication parameter to check user is ADMIN
     * @return Boolean true if successful and false if failed
     */
    @PostMapping("/enable")
    public ResponseEntity<Boolean> enablePost(@RequestParam String postID,
                                              Authentication auth) {
        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            ResponseEntity<Post> result = getPostsById(postID);
            if (result.getStatusCode().is2xxSuccessful() && result.getBody() != null) {
                result.getBody().setEnabled(1); // enable the post
                postRepository.save(result.getBody());

                return ResponseEntity.ok().body(true);
            }
        }

        return ResponseEntity.badRequest().body(false);
    }
}
