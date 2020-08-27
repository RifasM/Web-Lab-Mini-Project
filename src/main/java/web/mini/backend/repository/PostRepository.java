package web.mini.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import web.mini.backend.model.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
}
