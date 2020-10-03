package web.mini.backend.model;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.util.Date;

/**
 * The type Post.
 *
 * @author Mohammed Rifas S
 **/
@Document(indexName = "pixies-posts", type = "post")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "post_title", nullable = false)
    private String postTitle;

    @Column(name = "post_description", nullable = false)
    private String postDescription;

    @Column(name = "post_type", nullable = false)
    private String postType;

    @Column(name = "post_url", nullable = false)
    private String postUrl;

    @Column(name = "post_tags", nullable = false)
    private String tags;

    @Column(name = "post_enabled", nullable = false)
    private int enabled;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "post_created_at", nullable = false)
    private Date postDate;

    @Column(name = "post_user")
    private long postUser;

    public long getPostUser() {
        return postUser;
    }

    public void setPostUser(long postUser) {
        this.postUser = postUser;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public Date getPostDate() {
        return postDate;
    }

    public void setPostDate(Date postDate) {
        this.postDate = postDate;
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
                ", postDate=" + postDate +
                ", postUser=" + postUser +
                '}';
    }

    public Post(long id,
                String postTitle,
                String postDescription,
                String postType,
                String postUrl,
                String tags,
                int enabled,
                Date postDate,
                long postUser) {
        this.id = id;
        this.postTitle = postTitle;
        this.postDescription = postDescription;
        this.postType = postType;
        this.postUrl = postUrl;
        this.tags = tags;
        this.enabled = enabled;
        this.postDate = postDate;
        this.postUser = postUser;
    }
}
