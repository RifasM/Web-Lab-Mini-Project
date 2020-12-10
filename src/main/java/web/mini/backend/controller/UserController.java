package web.mini.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import web.mini.backend.exception.ResourceNotFoundException;
import web.mini.backend.model.User;
import web.mini.backend.repository.UserRepository;

import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The type Post.
 *
 * @author Mohammed Rifas S
 **/
@RestController
@RequestMapping("/api/v1")
public class UserController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Get all users list.
     *
     * @return the list
     */
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Gets users by id.
     *
     * @param userId the user id
     * @return the users by id
     * @throws ResourceNotFoundException the resource not found exception
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUsersById(@PathVariable(value = "id") Long userId)
            throws ResourceNotFoundException {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found on :: " + userId));
        return ResponseEntity.ok().body(user);
    }

    /**
     * Gets the enabled users by username.
     *
     * @param username the username
     * @return the user
     */
    @GetMapping("/users/{username}")
    public ResponseEntity<User> getEnabledUsersByUsername(@PathVariable(value = "username") String username) {
        User user = userRepository.findByUsername(username);
        assert user != null;
        if (user.getEnabled() == 0)
            return ResponseEntity.badRequest().body(null);

        return ResponseEntity.ok().body(user);
    }

    /**
     * Gets the diabled users by username.
     *
     * @param username the username
     * @return the user
     */
    @GetMapping("/users/disabled/{username}")
    public ResponseEntity<User> getDisabledUsersByUsername(@PathVariable(value = "username") String username) {
        User user = userRepository.findByUsername(username);
        assert user != null;
        if (user.getEnabled() == 1)
            return ResponseEntity.badRequest().body(null);

        return ResponseEntity.ok().body(user);
    }

    /**
     * Create user user.
     *
     * @param user the user
     * @return the user
     */
    @PostMapping("/users")
    public User createUser(@Valid @RequestBody User user) {
        String encryptedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPassword);

        return userRepository.save(user);
    }

    /**
     * Update user response entity.
     *
     * @param userId      the user id
     * @param userDetails the user details
     * @return the response entity
     * @throws ResourceNotFoundException the resource not found exception
     */
    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable(value = "id") Long userId, @Valid @RequestBody User userDetails)
            throws ResourceNotFoundException {

        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found on :: " + userId));

        user.setUserName(userDetails.getUserName());
        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setUpdatedAt(new Date());
        final User updatedUser = userRepository.save(user);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Delete user map.
     *
     * @param userId the user id
     * @return the map
     * @throws Exception the exception
     */
    @DeleteMapping("/user/{id}")
    public Map<String, Boolean> deleteUser(@PathVariable(value = "id") Long userId) throws Exception {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found on :: " + userId));

        userRepository.delete(user);
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return response;
    }

    /**
     * Deactivate a user based on the user name
     *
     * @param username  the username of the user to be deactivated
     * @param adminName the name of the administrator disabling the user
     * @return true if successful, false if failed
     */
    @RequestMapping("/user/deactivate")
    public Boolean deactivateUser(@RequestParam String username,
                                  @RequestParam String adminName) {
        ResponseEntity<User> result = getEnabledUsersByUsername(username);
        if (result.getStatusCode().is2xxSuccessful() && result.getBody() != null) {
            result.getBody().setEnabled(0); // disable the user
            result.getBody().setUpdatedAt(new Date());
            result.getBody().setUpdatedBy(adminName);
            userRepository.save(result.getBody());
            return true;
        }

        return false;
    }

    /**
     * Activate a user based on the user name
     *
     * @param username  the username of the user to be activated
     * @param adminName the name of the administrator enabling the user
     * @return true if successful, false if failed
     */
    @RequestMapping("/user/activate")
    public Boolean activateUser(@RequestParam String username,
                                @RequestParam String adminName) {
        ResponseEntity<User> result = getDisabledUsersByUsername(username);
        if (result.getStatusCode().is2xxSuccessful() && result.getBody() != null) {
            result.getBody().setEnabled(1); // activate the user
            result.getBody().setUpdatedAt(new Date());
            result.getBody().setUpdatedBy(adminName);
            userRepository.save(result.getBody());
            return true;
        }

        return false;
    }
}