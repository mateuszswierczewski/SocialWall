package pl.mswierczewski.socialwall.components.services;

import pl.mswierczewski.socialwall.dtos.vote.VoteRequest;
import pl.mswierczewski.socialwall.dtos.vote.VoteResponse;

import java.util.List;

public interface VoteService {
    VoteResponse addVote(String userId, VoteRequest request);

    void deleteVote(String userId, String voteId);

    List<VoteResponse> getVotesByPost(String postId);
}
