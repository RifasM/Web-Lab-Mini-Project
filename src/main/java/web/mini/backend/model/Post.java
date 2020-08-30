package web.mini.backend.model;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.http.ResponseEntity;
import web.mini.backend.controller.UserController;
import web.mini.backend.exception.ResourceNotFoundException;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "posts")
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

    @Column(name = "post_location", nullable = false)
    private String postLocation;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "post_created_at", nullable = false)
    private Date postDate;

    //@Column(name = "post_created_by", nullable = false)
    @OneToOne(targetEntity = User.class, cascade = CascadeType.REMOVE)
    private long createdBy;

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

    public String getPostLocation() {
        return postLocation;
    }

    public void setPostLocation(String postLocation) {
        this.postLocation = postLocation;
    }

    public Date getPostDate() {
        return postDate;
    }

    public void setPostDate(Date postDate) {
        this.postDate = postDate;
    }

    public long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(long createdBy) throws ResourceNotFoundException {
        ResponseEntity<User> user = new UserController().getUsersById(createdBy);
        System.out.println(user);
        this.createdBy = Objects.requireNonNull(user.getBody()).getId();
    }

    @Override
    public String toString() {
        return "Post{" +
                "id='" + id + '\'' +
                ", postTitle='" + postTitle + '\'' +
                ", postDescription='" + postDescription + '\'' +
                ", postType='" + postType + '\'' +
                ", postLocation='" + postLocation + '\'' +
                ", postDate='" + postDate + '\'' +
                ", createdBy='" + createdBy + '\'' +
                '}';
    }
}
