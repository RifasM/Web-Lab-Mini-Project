package web.mini.backend.repository;

import web.mini.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * The interface User repository.
 *
 * @author Mohammed Rifas S
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
