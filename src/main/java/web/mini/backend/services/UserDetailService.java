package web.mini.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import web.mini.backend.model.User;
import web.mini.backend.repository.UserRepository;

@Service
public class UserDetailService {

    @Autowired
    private UserRepository userRepository;

    public User loadUserByEmail(String userName) throws UsernameNotFoundException {
        User user = this.userRepository.findAllByEmail(userName);

        if (user == null) {
            System.out.println("User not found! " + userName);
            throw new UsernameNotFoundException("User " + userName + " was not found in the database");
        }

        System.out.println("Found User: " + user);

        return user;
    }

}