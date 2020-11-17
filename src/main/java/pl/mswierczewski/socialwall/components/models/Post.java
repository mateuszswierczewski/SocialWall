package pl.mswierczewski.socialwall.components.models;

import org.hibernate.annotations.*;
import org.hibernate.annotations.CascadeType;
import pl.mswierczewski.socialwall.components.enums.PostType;
import pl.mswierczewski.socialwall.components.interfaces.Votable;

import javax.annotation.PostConstruct;
import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "posts")
public class Post implements Serializable, Votable {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(nullable = false, unique = true)
    private String id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private SocialWallUser author;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PostType postType;

    @Column(length = 510)
    private String textContent;

    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    @JoinTable(
            name = "posts_images_links",
            joinColumns = @JoinColumn(name = "post_id", nullable = false)
    )
    @Cascade(CascadeType.ALL)
    private List<String> imagesLinks = new ArrayList<>();


    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "post_comments",
            joinColumns = @JoinColumn(name = "post_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "comment_id", nullable = false, unique = true)
    )
    @LazyCollection(LazyCollectionOption.EXTRA)
    @Cascade(CascadeType.DELETE)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany
    @JoinTable(
            name = "post_votes",
            joinColumns = @JoinColumn(name = "post_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "vote_id", nullable = false, unique = true)
    )
    @Cascade(CascadeType.DELETE)
    private List<Vote> votes = new ArrayList<>();

    @Column(name = "created", nullable = false, columnDefinition = "TIMESTAMP")
    @UpdateTimestamp
    private ZonedDateTime createdDateTime;

    @PostConstruct
    private void setUp(){
        createdDateTime = ZonedDateTime.now();
    }

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

    public PostType getPostType() {
        return postType;
    }

    public void setPostType(PostType postType) {
        this.postType = postType;
    }

    public Optional<String> getTextContent() {
        return Optional.ofNullable(textContent);
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public Optional<List<String>> getImagesLinks() {
        return Optional.ofNullable(imagesLinks);
    }

    public void setImagesLinks(List<String> imagesLinks) {
        this.imagesLinks = imagesLinks;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    @Override
    public List<Vote> getVotes() {
        return votes;
    }

    public void setVotes(List<Vote> votes) {
        this.votes = votes;
    }

    public ZonedDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(ZonedDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public void addImageLink(String link) {
        imagesLinks.add(link);
    }

    @Override
    public void addVote(Vote vote) {
        votes.add(vote);
    }
}