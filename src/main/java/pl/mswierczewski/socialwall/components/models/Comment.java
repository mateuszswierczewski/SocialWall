package pl.mswierczewski.socialwall.components.models;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import pl.mswierczewski.socialwall.components.interfaces.Votable;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Table(name = "comments")
public class Comment implements Serializable, Votable {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(nullable = false, unique = true)
    private String id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private SocialWallUser author;

    @Column(length = 510, nullable = false)
    private String textContent;

    @Column(name = "created", nullable = false, columnDefinition = "TIMESTAMP")
    @UpdateTimestamp
    private ZonedDateTime createdDateTime;

    @OneToMany
    @JoinTable(
            name = "comment_votes",
            joinColumns = @JoinColumn(name = "comment_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "vote_id", nullable = false, unique = true)
    )
    private List<Vote> votes;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public SocialWallUser getAuthor() {
        return author;
    }

    public void setAuthor(SocialWallUser author) {
        this.author = author;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public ZonedDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(ZonedDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    @Override
    public List<Vote> getVotes() {
        return votes;
    }

    public void setVotes(List<Vote> votes) {
        this.votes = votes;
    }

    @Override
    public void addVote(Vote vote) {
        votes.add(vote);
    }
}
