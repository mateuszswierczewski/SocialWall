package pl.mswierczewski.socialwall.dtos.vote;

import pl.mswierczewski.socialwall.components.enums.VoteType;

public class VoteResponse {

    private String id;
    private String voterId;
    private VoteType voteType;

    public VoteResponse(String id, String voterId, VoteType voteType) {
        this.id = id;
        this.voterId = voterId;
        this.voteType = voteType;
    }

    public VoteResponse(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVoterId() {
        return voterId;
    }

    public void setVoterId(String voterId) {
        this.voterId = voterId;
    }

    public VoteType getVoteType() {
        return voteType;
    }

    public void setVoteType(VoteType voteType) {
        this.voteType = voteType;
    }
}
