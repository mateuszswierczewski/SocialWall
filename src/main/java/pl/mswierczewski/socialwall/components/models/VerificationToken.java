package pl.mswierczewski.socialwall.components.models;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "verification_tokens")
public class VerificationToken implements Serializable {

    @Id
    private String token;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private SocialWallUser user;

    private LocalDate expiryDate;

    public VerificationToken() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public SocialWallUser getUser() {
        return user;
    }

    public void setUser(SocialWallUser user) {
        this.user = user;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public void setTokenExpirationLengthInDays(Integer days) {
        this.expiryDate = LocalDate.now().plusDays(days);
    }

    public boolean isExpired() {
        return expiryDate.isBefore(LocalDate.now());
    }
}
