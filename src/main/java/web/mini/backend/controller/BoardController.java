package web.mini.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import web.mini.backend.exception.ResourceNotFoundException;
import web.mini.backend.model.Board;
import web.mini.backend.repository.BoardRepository;

import java.util.ArrayList;
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
     */
    @GetMapping("/board/{id}")
    public ResponseEntity<Board> getBoardById(@PathVariable(value = "id") String boardID) {
        Board board;
        try {
            board =
                    boardRepository
                            .findById(boardID)
                            .orElseThrow(() -> new ResourceNotFoundException("Post not found on :: " + boardID));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(500).body(null);
        }
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
    public Board findByUserAndBoard(@PathVariable(value = "userID") String userID,
                                    @PathVariable(value = "boardId") String boardId) {
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

    /**
     * Update a board based on the new board content and a cover file if present
     *
     * @param newBoard       the board entity to update
     * @param boardCoverFile the cove file to upload to the s3 bucket
     * @return a response entity with the updated board as the body
     * @throws ResourceNotFoundException
     */
    @PutMapping("/board")
    public ResponseEntity<Board> updateBoard(Board newBoard,
                                             MultipartFile boardCoverFile)
            throws ResourceNotFoundException {
        Board board =
                boardRepository
                        .findById(newBoard.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Post not found on :: " + newBoard.getId()));
        board.setBoardName(newBoard.getBoardName());
        board.setBoardDescription(newBoard.getBoardDescription());
        board.setPrivateBoard(newBoard.getPrivateBoard());

        if (!boardCoverFile.isEmpty()) {
            ResponseEntity<String> status = awsController.uploadFileToS3Bucket(boardCoverFile,
                    "boards");
            if (status.getStatusCode().is2xxSuccessful()) {
                this.awsController.deleteFileFromS3Bucket(newBoard.getBoardCoverUrl(),
                        "boards");
                board.setBoardCoverUrl(status.getBody());
            }

            return ResponseEntity.status(500).body(board);
        }

        boardRepository.save(board);

        return ResponseEntity.ok().body(board);
    }

    /**
     * Add a given post id to the requested board
     *
     * @param boardID the board id to add the post
     * @param postID  the post id of the post to be added
     * @return the board after addition of the post
     */
    @RequestMapping("/board/addPost/{post_id}")
    public ResponseEntity<Board> addPost(@RequestParam String boardID,
                                         @PathVariable(value = "post_id") String postID) {
        ResponseEntity<Board> result = getBoardById(boardID);
        if (result.getStatusCode().is2xxSuccessful() && result.getBody() != null) {
            Board board = result.getBody();

            List<String> postIDs = new ArrayList<>();
            if (board.getPostID() != null)
                postIDs = board.getPostID();

            // if board already contains the post, skip it
            if (!postIDs.contains(postID)) {
                postIDs.add(postID);
                board.setPostID(postIDs);
                boardRepository.save(board);
            }
            return ResponseEntity.ok().body(board);
        }

        return ResponseEntity.status(500).body(null);
    }
}
