package web.mini.backend.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import web.mini.backend.exception.ResourceNotFoundException;
import web.mini.backend.model.Post;
import web.mini.backend.repository.PostRepository;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/")
public class PostController {

    private final PostRepository postRepository;

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
    public ResponseEntity<Post> getPostsById(@PathVariable(value = "id") Long postID)
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
    public Page<Post> findByAuthor(@PathVariable(value = "userID") Long userID, PageRequest pageRequest) {
        return postRepository.findByPostUser(userID, pageRequest);
    }

    /**
     * Create post.
     *
     * @param post the post
     * @return the post
     */
    @PostMapping("/post")
    public Post createPost(@Valid @RequestBody Post post) {
        return postRepository.save(post);
    }

    /**
     * Delete post map.
     *
     * @param postID the post id
     * @return the map
     * @throws ResourceNotFoundException the exception
     */
    @DeleteMapping("/post/{id}")
    public Map<String, Boolean> deletePost(@PathVariable(value = "id") Long postID) throws ResourceNotFoundException {
        Post post =
                postRepository
                        .findById(postID)
                        .orElseThrow(() -> new ResourceNotFoundException("Post not found on :: " + postID));

        postRepository.delete(post);
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
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
