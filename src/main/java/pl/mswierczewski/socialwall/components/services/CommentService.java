package pl.mswierczewski.socialwall.components.services;

import pl.mswierczewski.socialwall.components.models.Comment;
import pl.mswierczewski.socialwall.dtos.comment.CommentRequest;
import pl.mswierczewski.socialwall.dtos.comment.CommentResponse;

import java.util.List;

public interface CommentService {
    CommentResponse addNewComment(String userId, CommentRequest request);

    void deleteComment(String userId, String commentId);

    Comment getCommentById(String commentId);

    CommentResponse getCommentResponseById(String commentId);

    List<CommentResponse> getAllCommentsByPost(String postId);
}
