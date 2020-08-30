package web.mini.backend.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "UserPostMapping")
public class PostUserMapper implements Serializable {
    @Id
    @Column(name = "user_id")
    private long userID;

    @Id
    @Column(name = "post_ID")
    private long postID;

    public long getUserID() {
        return userID;
    }

    public void setUserID(long userID) {
        this.userID = userID;
    }

    public long getPostID() {
        return postID;
    }

    public void setPostID(long postID) {
        this.postID = postID;
    }
}
