package web.mini.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import web.mini.backend.model.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select u from User " +
            "u where u.userName = ?1")
    User findByUsername(String username);

    List<User> findByEnabled(int enabled);
}
