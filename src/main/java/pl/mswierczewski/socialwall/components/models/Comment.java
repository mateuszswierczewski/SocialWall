package pl.mswierczewski.socialwall.components.models;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import pl.mswierczewski.socialwall.components.interfaces.Votable;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    private ZonedDateTime createdDateTime;

    @OneToMany
    @JoinTable(
            name = "comment_votes",
            joinColumns = @JoinColumn(name = "comment_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "vote_id", nullable = false, unique = true)
    )
    @Cascade(org.hibernate.annotations.CascadeType.DELETE)
    private List<Vote> votes = new ArrayList<>();

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Comment)) return false;
        Comment comment = (Comment) o;
        return getId().equals(comment.getId()) &&
                getAuthor().equals(comment.getAuthor()) &&
                getTextContent().equals(comment.getTextContent()) &&
                getCreatedDateTime().equals(comment.getCreatedDateTime());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getAuthor(), getTextContent(), getCreatedDateTime());
    }
}
