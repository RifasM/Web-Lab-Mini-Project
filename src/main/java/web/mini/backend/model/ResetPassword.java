package web.mini.backend.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "reset_password")
public class ResetPassword {
    // Set expiration to be 30 minutes,
    // else use 60 * 2 for 2 hours
    private static final int EXPIRATION = 30;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String token;

    private long userId;

    private Date expiryDate;

    public static int getEXPIRATION() {
        return EXPIRATION;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getUser() {
        return userId;
    }

    public void setUser(long userId) {
        this.userId = userId;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    @Override
    public String toString() {
        return "ResetPassword{" +
                "id=" + id +
                ", token='" + token + '\'' +
                ", user=" + userId +
                ", expiryDate=" + expiryDate +
                '}';
    }
}
