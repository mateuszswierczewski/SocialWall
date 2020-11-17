package pl.mswierczewski.socialwall.components.models;

import org.hibernate.annotations.GenericGenerator;
import pl.mswierczewski.socialwall.components.enums.VoteType;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Vote implements Serializable {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(nullable = false, unique = true)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private SocialWallUser voter;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private VoteType voteType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public SocialWallUser getVoter() {
        return voter;
    }

    public void setVoter(SocialWallUser voter) {
        this.voter = voter;
    }

    public VoteType getVoteType() {
        return voteType;
    }

    public void setVoteType(VoteType voteType) {
        this.voteType = voteType;
    }
}
