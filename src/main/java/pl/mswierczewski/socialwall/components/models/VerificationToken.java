package pl.mswierczewski.socialwall.components.models;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "verification_tokens")
public class VerificationToken implements Serializable {

    @Id
    private String token;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private SocialWallUser user;

    private Date expiryDate;

    public VerificationToken() {
    }

    @PrePersist
    private void prePersist(){
        if (expiryDate == null)
            expiryDate = java.sql.Date.valueOf(LocalDate.now().plusDays(3));
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

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public boolean isExpired() {
        return expiryDate.after(new Date());
    }
}
