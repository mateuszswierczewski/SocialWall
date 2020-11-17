package pl.mswierczewski.socialwall.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import pl.mswierczewski.socialwall.components.enums.VoteType;
import pl.mswierczewski.socialwall.components.models.Comment;
import pl.mswierczewski.socialwall.components.models.Vote;
import pl.mswierczewski.socialwall.dtos.comment.CommentResponse;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "userId", source = "author.id")
    @Mapping(target = "numberOfLikes", source = "votes", qualifiedByName = "likes")
    @Mapping(target = "numberOfDislikes", source = "votes", qualifiedByName = "dislikes")
    CommentResponse mapCommentToCommentResponse(Comment comment);


    @Named("dislikes")
    default int getNumberOfDislikes(List<Vote> votes) {
        return (int) votes.stream()
                .filter(vote -> vote.getVoteType().equals(VoteType.DISLIKE))
                .count();
    }

    @Named("likes")
    default int getNumberOfLikes(List<Vote> votes) {
        return (int) votes.stream()
                .filter(vote -> vote.getVoteType().equals(VoteType.LIKE))
                .count();
    }
}
