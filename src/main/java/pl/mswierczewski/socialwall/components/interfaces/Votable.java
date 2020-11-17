package pl.mswierczewski.socialwall.components.interfaces;

import pl.mswierczewski.socialwall.components.models.Vote;

import java.util.List;

public interface Votable {

    void addVote(Vote vote);

    List<Vote> getVotes();
}
