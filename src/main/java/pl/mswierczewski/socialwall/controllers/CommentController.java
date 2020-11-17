package pl.mswierczewski.socialwall.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.mswierczewski.socialwall.components.services.CommentService;
import pl.mswierczewski.socialwall.dtos.comment.CommentRequest;
import pl.mswierczewski.socialwall.dtos.comment.CommentResponse;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<CommentResponse> addNewComment(Principal principal, @Valid @RequestBody CommentRequest request) {
        CommentResponse response = commentService.addNewComment(principal.getName(), request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<CommentResponse> getCommentById(@PathVariable String commentId) {
        CommentResponse response = commentService.getCommentResponseById(commentId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("/byPost/{postId}")
    public ResponseEntity<List<CommentResponse>> getCommentsByPost(@PathVariable String postId) {
        List<CommentResponse> response = commentService.getAllCommentsByPost(postId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> deleteComment(Principal principal, @PathVariable String commentId) {
        commentService.deleteComment(principal.getName(), commentId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Comment deleted successfully!");
    }
}
