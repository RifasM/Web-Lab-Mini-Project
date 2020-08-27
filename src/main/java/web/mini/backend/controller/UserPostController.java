package web.mini.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import web.mini.backend.repository.UserPostRepository;

@RestController
@RequestMapping("/api/v1/posts/")
public class UserPostController {

    @Id
    public String id;
    public String firstName;
    public String lastName;
    @Autowired
    private UserPostRepository userPostRepository;

    public UserPostController() {
    }

    public UserPostController(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return String.format(
                "Customer[id=%s, firstName='%s', lastName='%s']",
                id, firstName, lastName);
    }

}
