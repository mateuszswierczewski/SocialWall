package pl.mswierczewski.socialwall.security.jwt;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(
        name = "jwt_tokens",
        indexes = {
                @Index(name = "userIdIndex", columnList = "userId"),
                @Index(name = "expiryDateIndex", columnList = "expiryDate")
        })
public class JwtToken implements Serializable {

    @Id
    @Column(length = 600)
    private String token;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private Date expiryDate;

    private boolean isForbidden;

    public JwtToken(String token, String userId, Date expiryDate) {
        this.token = token;
        this.userId = userId;
        this.expiryDate = expiryDate;
        this.isForbidden = false;
    }

    public JwtToken() {

    }

    public String getToken() {
        return token;
    }

    public String getUserId(){
        return userId;
    }

    public boolean isTokenExpired(){
        return expiryDate.after(new Date());
    }

    public boolean isForbidden(){
        return isForbidden;
    }

    public void invalidate(){
        this.isForbidden = true;
    }

}
