package web.mini.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import web.mini.backend.exception.ResourceNotFoundException;
import web.mini.backend.model.Board;
import web.mini.backend.repository.BoardRepository;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/")
public class BoardController {

    @Autowired
    private BoardRepository boardRepository;

    /**
     * Get all boards list.
     *
     * @return the list
     */
    @GetMapping("/boards")
    public Iterable<Board> getAllBoards() {
        return boardRepository.findAll();
    }

    /**
     * Gets board by id.
     *
     * @param boardID the post id
     * @return the boards by id
     * @throws ResourceNotFoundException the resource not found exception
     */
    @GetMapping("/board/{id}")
    public ResponseEntity<Board> getPostsById(@PathVariable(value = "id") String boardID)
            throws ResourceNotFoundException {
        Board board =
                boardRepository
                        .findById(boardID)
                        .orElseThrow(() -> new ResourceNotFoundException("Post not found on :: " + boardID));
        return ResponseEntity.ok().body(board);
    }

    /**
     * Gets board by user id.
     *
     * @param userID the user id
     * @return the posts by user id
     */
    @GetMapping("/board/user/{userID}")
    public Page<Board> findByUser(@PathVariable(value = "userID") Long userID, PageRequest pageRequest) {
        return boardRepository.findByUserId(userID, pageRequest);
    }

    /**
     * Create post.
     *
     * @param board the post
     * @return the post
     */
    @PostMapping("/board")
    public Board createBoard(@Valid @RequestBody Board board) {
        return boardRepository.save(board);
    }

    /**
     * Delete board map.
     *
     * @param boardID the board
     * @return the map
     * @throws ResourceNotFoundException the exception
     */
    @DeleteMapping("/board/{id}")
    public Map<String, Boolean> deleteBoard(@PathVariable(value = "id") String boardID) throws ResourceNotFoundException {
        Board board =
                boardRepository
                        .findById(boardID)
                        .orElseThrow(() -> new ResourceNotFoundException("Post not found on :: " + boardID));

        boardRepository.delete(board);
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return response;
    }
}
