package pl.mswierczewski.socialwall.security.jwt;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(
        name = "tokens",
        indexes = {@Index(name = "userIdIndex", columnList = "userId"),
                   @Index(name = "tokenExpiringDateIndex", columnList = "tokenExpiringDate")
        })
public class JwtToken implements Serializable {

    @Id
    @Column(length = 600)
    private String token;

    @Column(nullable = false, length = 30)
    private String userId;

    @Column(nullable = false)
    private Date tokenExpiringDate;

    private boolean isValid;

    public JwtToken(String token, String userId, Date tokenExpiringDate) {
        this.token = token;
        this.userId = userId;
        this.tokenExpiringDate = tokenExpiringDate;
        this.isValid = true;
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
        return tokenExpiringDate.after(new Date());
    }

    public boolean isValid(){
        return isValid;
    }

    public void invalidate(){
        this.isValid = false;
    }

}
