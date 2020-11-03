package web.mini.backend.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import web.mini.backend.model.Board;

import java.util.List;

public interface BoardRepository extends ElasticsearchRepository<Board, String> {

    List<Board> findByUserId(String userId);

    Board findBoardByIdAndUserId(String boardId, String userId);

    List<Board> findByBoardDescription(String boardDescription);
}
