package web.mini.backend.model;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;

/**
 * The type Board.
 *
 * @author Mohammed Rifas S
 **/
@Document(indexName = "pixies-boards")
public class Board {
    @Id
    private String id;

    private String userId;

    private String boardName;

    private List<Long> postID;

    private String boardDescription;

    private String boardCoverUrl;

    private Boolean privateBoard;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    public Board(String id,
                 String userId,
                 String boardName,
                 List<Long> postID,
                 String boardDescription,
                 String boardCoverUrl,
                 Boolean privateBoard,
                 Date createdAt) {
        this.id = id;
        this.userId = userId;
        this.boardName = boardName;
        this.postID = postID;
        this.boardDescription = boardDescription;
        this.boardCoverUrl = boardCoverUrl;
        this.privateBoard = privateBoard;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
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
                ", createdAt=" + createdAt +
                '}';
    }
}
