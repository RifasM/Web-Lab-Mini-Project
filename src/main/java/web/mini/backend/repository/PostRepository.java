package web.mini.backend.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import web.mini.backend.model.Post;

import java.util.List;

@Repository
public interface PostRepository extends ElasticsearchRepository<Post, String> {

    List<Post> findByPostUser(String userID);

    List<Post> findByPostTitleContainingOrPostDescriptionContaining(String title, String desc);

    List<Post> findAllByEnabledOrderByPostDateDesc(int enabled);

    List<Post> findTop5ByEnabledAndPostTypeOrderByPostDateDesc(int enabled, String post_type);

    /*
    @Query("{\"bool\": {\"must\": [{\"match\": {\"authors.name\": \"?0\"}}]}}")
    Page<Article> findByAuthorsNameUsingCustomQuery(String name, Pageable pageable);
    */
}
