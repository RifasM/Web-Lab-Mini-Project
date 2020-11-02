package web.mini.backend.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import web.mini.backend.model.Board;

import java.util.List;

public interface BoardRepository extends ElasticsearchRepository<Board, String> {

    Iterable<Board> findByUserId(String userId);

    List<Board> findByBoardDescription(String boardDescription);
}
