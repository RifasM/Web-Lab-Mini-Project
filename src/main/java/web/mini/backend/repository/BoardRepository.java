package web.mini.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import web.mini.backend.model.Board;

import java.util.List;

public interface BoardRepository extends ElasticsearchRepository<Board, String> {

    Page<Board> findByUserId(Long userID, Pageable pageable);

    List<Board> findByBoardDescription(String boardDescription);
}
