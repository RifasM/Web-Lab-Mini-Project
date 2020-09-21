package web.mini.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import web.mini.backend.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select u from User " +
            "u where u.userName = ?1")
    /*
    select u.userName as user_name, " +
            "u.dob as dob, u.gender as gender, " +
            "u.role as role, u.createdAt as created_at," +
            " u.enabled as enabled, u.id as id, " +
            "u.name as name, u.updatedAt as updated_at, " +
            "u.updatedBy as updated_by from User " +
            "u where u.userName = ?1
     */
    User findByUsername(String username);
}
