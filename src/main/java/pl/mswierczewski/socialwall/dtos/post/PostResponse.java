package pl.mswierczewski.socialwall.dtos.post;

import com.fasterxml.jackson.annotation.JsonInclude;
import pl.mswierczewski.socialwall.components.enums.PostType;

import java.time.ZonedDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostResponse {

    // Obligatory fields
    private String postId;
    private String authorId;
    private PostType postType;
    private int numberOfComments;
    private int numberOfLikes;
    private int numberOfDislikes;
    private ZonedDateTime createdDateTime;

    // At least one field of them must be non null
    private String textContent;
    private List<String> imagesLinks;

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public PostType getPostType() {
        return postType;
    }

    public void setPostType(PostType postType) {
        this.postType = postType;
    }

    public int getNumberOfComments() {
        return numberOfComments;
    }

    public void setNumberOfComments(int numberOfComments) {
        this.numberOfComments = numberOfComments;
    }

    public int getNumberOfLikes() {
        return numberOfLikes;
    }

    public void setNumberOfLikes(int numberOfLikes) {
        this.numberOfLikes = numberOfLikes;
    }

    public int getNumberOfDislikes() {
        return numberOfDislikes;
    }

    public void setNumberOfDislikes(int numberOfDislikes) {
        this.numberOfDislikes = numberOfDislikes;
    }

    public ZonedDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(ZonedDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public List<String> getImages() {
        return imagesLinks;
    }

    public void setImagesLinks(List<String> imagesLinks) {
        this.imagesLinks = imagesLinks;
    }
}
