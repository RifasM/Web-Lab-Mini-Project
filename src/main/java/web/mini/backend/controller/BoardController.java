package web.mini.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import web.mini.backend.exception.ResourceNotFoundException;
import web.mini.backend.model.Board;
import web.mini.backend.repository.BoardRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/")
public class BoardController {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private AwsController awsController;

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
    public ResponseEntity<Board> getBoardById(@PathVariable(value = "id") String boardID)
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
     * @return the boards by user id
     */
    @GetMapping("/board/user/{userID}")
    public List<Board> findByUser(@PathVariable(value = "userID") String userID) {
        return boardRepository.findByUserId(userID);
    }

    public static Map<String, Boolean> getStringBooleanMap(int status) {
        Map<String, Boolean> response = new HashMap<>();

        if (status != 500)
            response.put("deleted", Boolean.TRUE);
        else {
            response.put("deleted from database", Boolean.TRUE);
            response.put("deleted from s3", Boolean.FALSE);
        }

        return response;
    }

    /**
     * Gets board by user id and board name.
     *
     * @param userID  the user id
     * @param boardId the name of the board to find
     * @return the boards by user id
     */
    @GetMapping("/board/{boardId}/{userID}")
    public Board findByUserAndBoard(@PathVariable(value = "userID") String userID, @PathVariable(value = "boardId") String boardId) {
        return boardRepository.findBoardByIdAndUserId(boardId, userID);
    }

    /**
     * Create board.
     *
     * @param board the post
     * @return the post
     */
    @PostMapping("/board")
    public ResponseEntity<String> createBoard(@RequestParam Board board,
                                              @RequestParam MultipartFile boardCoverFile) {
        if (!boardCoverFile.isEmpty()) {
            ResponseEntity<String> status = awsController.uploadFileToS3Bucket(boardCoverFile,
                    "boards");
            if (status.getStatusCode().is2xxSuccessful()) {
                try {
                    board.setBoardCoverUrl(status.getBody());
                    boardRepository.save(board);
                    return ResponseEntity.ok().body("created successfully");
                } catch (Exception e) {
                    return ResponseEntity.status(500).body("Error: " + e.getMessage());
                }
            }
        } else {
            boardRepository.save(board);
            return ResponseEntity.ok().body("created successfully");
        }
        return ResponseEntity.badRequest().body("Error");
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

        int status = this.awsController.deleteFileFromS3Bucket(board.getBoardCoverUrl(),
                "boards").getStatusCodeValue();

        return getStringBooleanMap(status);
    }
}
