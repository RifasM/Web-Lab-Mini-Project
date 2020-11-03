package web.mini.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import web.mini.backend.exception.ResourceNotFoundException;
import web.mini.backend.model.Board;
import web.mini.backend.model.Post;
import web.mini.backend.repository.BoardRepository;
import web.mini.backend.repository.PostRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Controller
public class BoardWebController {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private PostController postController;

    @Autowired
    private BoardController boardController;

    /**
     * Return Board Creation Page
     * Get Mapping
     *
     * @return rendered createBoard.html
     */
    @GetMapping("/createBoard")
    public String createPostPage() {
        return "boardTemplates/createBoard";
    }

    /**
     * Handle Board Creation Page request
     * Post Mapping
     *
     * @return rendered createBoard.html
     */
    @PostMapping("/createBoard")
    public String createPostProcess(@RequestParam String boardName,
                                    @RequestParam String boardDescription,
                                    @RequestParam String userId,
                                    @RequestParam(required = false, defaultValue = "true") String privateBoard,
                                    @RequestParam(required = false) MultipartFile boardCoverUrl) {

        Board board = new Board(
                null,
                userId,
                boardName,
                null,
                boardDescription,
                null,
                Boolean.parseBoolean(privateBoard),
                new Date());

        ResponseEntity<String> result = boardController.createBoard(board,
                boardCoverUrl);

        if (result.getStatusCode().is2xxSuccessful())
            return "boardTemplates/createBoard";
        else
            return "error";
    }

    /**
     * Return Posts under a given Board
     * Get Mapping
     *
     * @param board_id the board id of which posts have to be fetched
     * @return rendered viewBoard.html
     */
    @GetMapping("/viewBoard/{board_id}")
    public String createPostPage(@PathVariable(value = "board_id") String board_id, Model model)
            throws ResourceNotFoundException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        ResponseEntity<Board> board = boardController.getBoardById(board_id);

        // Check if board is private and requesting User is not the board owner, then raise error
        if (Objects.requireNonNull(board.getBody()).getPrivateBoard() && !auth.getName().equals(board.getBody().getUserId())) {
            return "error";
        }

        List<Post> post_list = new ArrayList<>();
        for (String post_id : board.getBody().getPostID()) {
            ResponseEntity<Post> result = postController.getPostsById(post_id);
            if (result.getStatusCode().is2xxSuccessful())
                post_list.add(result.getBody());
            else
                board.getBody().getPostID().remove(post_id);
        }
        boardRepository.save(board.getBody());

        model.addAttribute("posts", post_list);
        model.addAttribute("board_data", board.getBody());


        return "boardTemplates/viewBoard";
    }
}
