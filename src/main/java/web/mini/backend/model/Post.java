package web.mini.backend.model;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.Map;

/**
 * The type Post.
 *
 * @author Mohammed Rifas S
 **/
@Document(indexName = "pixies-posts")
public class Post {
    @Id
    private String id;

    private String postTitle;

    private String postDescription;

    private String postType;

    private String postUrl;

    private String tags;

    private int enabled;

    private String postUser;

    private Map<String, Integer> postLikesUserIds; // Integer here is to specify type of like, thumbs, heart, etc

    private Map<String, String> postCommentsUserIds;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date postDate;

    public Post(String id,
                String postTitle,
                String postDescription,
                String postType,
                String postUrl,
                String tags,
                int enabled,
                String postUser,
                Map<String, Integer> postLikesUserIds,
                Map<String, String> postCommentsUserIds,
                Date postDate) {
        this.id = id;
        this.postTitle = postTitle;
        this.postDescription = postDescription;
        this.postType = postType;
        this.postUrl = postUrl;
        this.tags = tags;
        this.enabled = enabled;
        this.postUser = postUser;
        this.postLikesUserIds = postLikesUserIds;
        this.postCommentsUserIds = postCommentsUserIds;
        this.postDate = postDate;
    }

    public String getId() {
        return id;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostDescription() {
        return postDescription;
    }

    public void setPostDescription(String postDescription) {
        this.postDescription = postDescription;
    }

    public String getPostType() {
        return postType;
    }

    public void setPostType(String postType) {
        this.postType = postType;
    }

    public String getPostUrl() {
        return postUrl;
    }

    public void setPostUrl(String postUrl) {
        this.postUrl = postUrl;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public int getEnabled() {
        return enabled;
    }

    public void setEnabled(int enabled) {
        this.enabled = enabled;
    }

    public String getPostUser() {
        return postUser;
    }

    public void setPostUser(String postUser) {
        this.postUser = postUser;
    }

    public Date getPostDate() {
        return postDate;
    }

    public void setPostDate(Date postDate) {
        this.postDate = postDate;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Integer> getPostLikesUserIds() {
        return postLikesUserIds;
    }

    public void setPostLikesUserIds(Map<String, Integer> postLikesUserIds) {
        this.postLikesUserIds = postLikesUserIds;
    }

    public Map<String, String> getPostCommentsUserIds() {
        return postCommentsUserIds;
    }

    public void setPostCommentsUserIds(Map<String, String> postCommentsUserIds) {
        this.postCommentsUserIds = postCommentsUserIds;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", postTitle='" + postTitle + '\'' +
                ", postDescription='" + postDescription + '\'' +
                ", postType='" + postType + '\'' +
                ", postUrl='" + postUrl + '\'' +
                ", tags='" + tags + '\'' +
                ", enabled=" + enabled +
                ", postUser=" + postUser +
                ", postLikesUserIds=" + postLikesUserIds +
                ", postCommentsUserIds=" + postCommentsUserIds +
                ", postDate=" + postDate +
                '}';
    }
}
