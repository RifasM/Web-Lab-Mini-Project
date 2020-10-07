package web.mini.backend.model;

import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.List;

/**
 * The type Post.
 *
 * @author Mohammed Rifas S
 **/
@Document(indexName = "pixies-boards")
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private long userId;

    private String boardName;

    private List<Long> postID;

    private String boardDescription;

    private String boardCoverUrl;

    private Boolean privateBoard;

    public Board(long id,
                 long userId,
                 String boardName,
                 List<Long> postID,
                 String boardDescription,
                 String boardCoverUrl,
                 Boolean privateBoard) {
        this.id = id;
        this.userId = userId;
        this.boardName = boardName;
        this.postID = postID;
        this.boardDescription = boardDescription;
        this.boardCoverUrl = boardCoverUrl;
        this.privateBoard = privateBoard;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getBoardName() {
        return boardName;
    }

    public void setBoardName(String boardName) {
        this.boardName = boardName;
    }

    public List<Long> getPostID() {
        return postID;
    }

    public void setPostID(List<Long> postID) {
        this.postID = postID;
    }

    public String getBoardDescription() {
        return boardDescription;
    }

    public void setBoardDescription(String boardDescription) {
        this.boardDescription = boardDescription;
    }

    public String getBoardCoverUrl() {
        return boardCoverUrl;
    }

    public void setBoardCoverUrl(String boardCoverUrl) {
        this.boardCoverUrl = boardCoverUrl;
    }

    public Boolean getPrivateBoard() {
        return privateBoard;
    }

    public void setPrivateBoard(Boolean privateBoard) {
        this.privateBoard = privateBoard;
    }

    @Override
    public String toString() {
        return "Board{" +
                "id=" + id +
                ", userId=" + userId +
                ", boardName='" + boardName + '\'' +
                ", postID=" + postID +
                ", boardDescription='" + boardDescription + '\'' +
                ", boardCoverUrl='" + boardCoverUrl + '\'' +
                ", privateBoard=" + privateBoard +
                '}';
    }
}
