package pl.mswierczewski.socialwall.components.services.impl;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.mswierczewski.socialwall.components.models.Comment;
import pl.mswierczewski.socialwall.components.models.Post;
import pl.mswierczewski.socialwall.components.models.SocialWallUser;
import pl.mswierczewski.socialwall.components.repositories.CommentRepository;
import pl.mswierczewski.socialwall.components.services.CommentService;
import pl.mswierczewski.socialwall.components.services.PostService;
import pl.mswierczewski.socialwall.components.services.SocialWallUserService;
import pl.mswierczewski.socialwall.dtos.comment.CommentRequest;
import pl.mswierczewski.socialwall.dtos.comment.CommentResponse;
import pl.mswierczewski.socialwall.exceptions.api.EntityNotFoundException;
import pl.mswierczewski.socialwall.exceptions.api.RequestForbiddenException;
import pl.mswierczewski.socialwall.mappers.CommentMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DefaultCommentService implements CommentService {

    private final CommentRepository commentRepository;

    private final SocialWallUserService userService;
    private final PostService postService;

    private final CommentMapper commentMapper;

    public DefaultCommentService(CommentRepository commentRepository, SocialWallUserService userService, PostService postService, CommentMapper commentMapper) {
        this.commentRepository = commentRepository;
        this.userService = userService;
        this.postService = postService;
        this.commentMapper = commentMapper;
    }

    @Override
    @Transactional
    public CommentResponse addNewComment(String userId, CommentRequest request) {
        SocialWallUser user = userService.getUserById(userId); // Throws own exception if not exists
        Post post = postService.getPostById(request.getPostId()); // Throws own exception if not exists

        Comment comment = new Comment();
        comment.setAuthor(user);
        comment.setTextContent(request.getTextContent());

        comment = commentRepository.save(comment);
        postService.save(post);

        return commentMapper.mapCommentToCommentResponse(comment);
    }

    @Override
    public void deleteComment(String userId, String commentId) {
        Comment comment = getCommentById(commentId);

        if (!comment.getAuthor().getId().equals(userId)) {
            String msg = String.format("User %s does not have permission to delete comment %s", userId, commentId);
            throw new RequestForbiddenException(msg);
        }

    }

    @Override
    public Comment getCommentById(String commentId) {
        return commentRepository
                .findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Comment %s not found!", commentId)));
    }

    @Override
    public CommentResponse getCommentResponseById(String commentId) {
        return commentMapper.mapCommentToCommentResponse(getCommentById(commentId));
    }

    @Override
    public List<CommentResponse> getAllCommentsByPost(String postId) {
        Post post = postService.getPostById(postId);

        List<Comment> comments = post.getComments();

        return comments.stream()
                .map(commentMapper::mapCommentToCommentResponse)
                .collect(Collectors.toList());
    }
}
