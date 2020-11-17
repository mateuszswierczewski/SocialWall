package pl.mswierczewski.socialwall.components.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.mswierczewski.socialwall.components.interfaces.Votable;
import pl.mswierczewski.socialwall.components.models.Post;
import pl.mswierczewski.socialwall.components.models.SocialWallUser;
import pl.mswierczewski.socialwall.components.models.Vote;
import pl.mswierczewski.socialwall.components.repositories.VoteRepository;
import pl.mswierczewski.socialwall.components.services.CommentService;
import pl.mswierczewski.socialwall.components.services.PostService;
import pl.mswierczewski.socialwall.components.services.SocialWallUserService;
import pl.mswierczewski.socialwall.components.services.VoteService;
import pl.mswierczewski.socialwall.dtos.vote.VoteRequest;
import pl.mswierczewski.socialwall.dtos.vote.VoteResponse;
import pl.mswierczewski.socialwall.exceptions.api.BadRequestException;
import pl.mswierczewski.socialwall.exceptions.api.EntityNotFoundException;
import pl.mswierczewski.socialwall.exceptions.api.RequestForbiddenException;
import pl.mswierczewski.socialwall.mappers.VoteMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DefaultVoteService implements VoteService {

    private final VoteRepository voteRepository;

    private final PostService postService;
    private final CommentService commentService;
    private final SocialWallUserService userService;

    private final VoteMapper voteMapper;

    @Autowired
    public DefaultVoteService(VoteRepository voteRepository, PostService postService,
                              CommentService commentService, SocialWallUserService userService, VoteMapper voteMapper) {
        this.voteRepository = voteRepository;
        this.postService = postService;
        this.commentService = commentService;
        this.userService = userService;
        this.voteMapper = voteMapper;
    }

    public Vote getVoteById(String voteId) {
        return voteRepository
                .findById(voteId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Vote %s not found!", voteId)));
    }

    @Override
    @Transactional
    public VoteResponse addVote(String userId, VoteRequest request) {
        Votable votable;

        if (request.getPostId() != null) {
            votable = postService.getPostById(request.getPostId());
        } else if (request.getCommentId() != null) {
            votable = commentService.getCommentById(request.getCommentId());
        } else {
            throw new BadRequestException("Vote must be assigned to post or comment!");
        }

        SocialWallUser user = userService.getUserById(userId);

        Optional<Vote> voteOptional = votable.getVotes().stream()
                .filter(vote -> vote.getVoter().equals(user))
                .findAny();

        if (voteOptional.isPresent()) {
            Vote vote = voteOptional.get();

            if (vote.getVoteType().equals(request.getVoteType())) {
                throw new BadRequestException("User has already voted!");
            } else {
                vote.setVoteType(request.getVoteType());
                voteRepository.save(vote);
                return voteMapper.mapVoteToVoteResponse(vote);
            }

        } else {
            Vote vote = new Vote();
            vote.setVoter(user);
            vote.setVoteType(request.getVoteType());

            voteRepository.save(vote);

            votable.addVote(vote);

            return voteMapper.mapVoteToVoteResponse(vote);
        }
    }

    @Override
    public void deleteVote(String userId, String voteId) {
        Vote vote = getVoteById(voteId);

        if (vote.getVoter().getId().equals(userId)) {
            voteRepository.deleteById(voteId);
        } else {
            String msg = String.format("User %s does not have permission to delete vote %s", userId, voteId);
            throw new RequestForbiddenException(msg);
        }
    }

    @Override
    public List<VoteResponse> getVotesByPost(String postId) {
        Post post = postService.getPostById(postId);

        List<Vote> votes = post.getVotes();

        return votes.stream()
            .map(voteMapper::mapVoteToVoteResponse)
            .collect(Collectors.toList());
    }
}
