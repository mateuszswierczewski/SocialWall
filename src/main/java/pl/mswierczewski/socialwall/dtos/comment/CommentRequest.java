package pl.mswierczewski.socialwall.dtos.comment;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CommentRequest {

    @NotNull(message = "Post id cannot be null!")
    private String postId;

    @NotNull(message = "Comment cannot be null!")
    @Size(min = 1, max = 510, message = "Text cannot be empty or longer than {max} characters!")
    private String textContent;

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }
}
