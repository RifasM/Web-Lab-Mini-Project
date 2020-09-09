package web.mini.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private PostRepository postRepository;

    /**
     * Get all post list.
     *
     * @return the list
     */
    @GetMapping("/posts")
    public List<Post> getAllPosts() {
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
     * @throws ResourceNotFoundException the resource not found exception
     */
    @GetMapping("/post/user/{user_id}")
    public List<Post> getPostsByUserId(@PathVariable(value = "user_id") Long userID)
            throws ResourceNotFoundException {
        return postRepository.findAllByUserId(userID);
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
     * @throws Exception the exception
     */
    @DeleteMapping("/post/{id}")
    public Map<String, Boolean> deletePost(@PathVariable(value = "id") Long postID) throws Exception {
        Post post =
                postRepository
                        .findById(postID)
                        .orElseThrow(() -> new ResourceNotFoundException("Post not found on :: " + postID));

        postRepository.delete(post);
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return response;
    }
}
