package pl.mswierczewski.socialwall.dtos.vote;

import pl.mswierczewski.socialwall.components.enums.VoteType;

import javax.validation.constraints.NotNull;

public class VoteRequest {

    @NotNull
    private VoteType voteType;

    private String postId;
    private String commentId;

    public VoteType getVoteType() {
        return voteType;
    }

    public void setVoteType(VoteType voteType) {
        this.voteType = voteType;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }
}
