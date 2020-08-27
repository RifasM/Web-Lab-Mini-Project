package web.mini.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import web.mini.backend.model.Post;

public interface UserPostRepository extends JpaRepository<Post, Long> {
}
