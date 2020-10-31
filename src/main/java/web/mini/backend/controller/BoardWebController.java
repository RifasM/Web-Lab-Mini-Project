package web.mini.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import web.mini.backend.model.Board;
import web.mini.backend.repository.PostRepository;

import java.util.Date;

@Controller
public class BoardWebController {
    @Autowired
    private PostRepository postRepository;

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
                                    @RequestParam String privateBoard,
                                    @RequestParam MultipartFile boardCoverUrl) {
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
}
