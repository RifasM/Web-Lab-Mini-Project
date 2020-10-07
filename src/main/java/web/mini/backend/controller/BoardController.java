package web.mini.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
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
     * Get all post list.
     *
     * @return the list
     */
    @GetMapping("/posts")
    public Iterable<Board> getAllBoards() {
        return boardRepository.findAll();
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
     * Delete post map.
     *
     * @param boardID the board
     * @return the map
     * @throws ResourceNotFoundException the exception
     */
    @DeleteMapping("/board/{id}")
    public Map<String, Boolean> deleteBoard(@PathVariable(value = "id") Long boardID) throws ResourceNotFoundException {
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
