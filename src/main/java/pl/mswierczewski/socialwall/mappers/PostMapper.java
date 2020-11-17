package pl.mswierczewski.socialwall.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import pl.mswierczewski.socialwall.components.enums.VoteType;
import pl.mswierczewski.socialwall.components.models.Post;
import pl.mswierczewski.socialwall.components.models.SocialWallUser;
import pl.mswierczewski.socialwall.components.models.Vote;
import pl.mswierczewski.socialwall.dtos.post.PostRequest;
import pl.mswierczewski.socialwall.dtos.post.PostResponse;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PostMapper {

    @Mapping(target = "votes", ignore = true)
    @Mapping(target = "textContent", expression = "java(request.getTextContent().orElse(null))")
    @Mapping(target = "postType", source = "request.postType")
    @Mapping(target = "imagesLinks", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDateTime", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "author", source = "user")
    Post mapPostRequestToPost(SocialWallUser user, PostRequest request);

    @Mapping(target = "postId", source = "id")
    @Mapping(target = "numberOfLikes", source = "votes", qualifiedByName = "likes")
    @Mapping(target = "numberOfDislikes", source = "votes", qualifiedByName = "dislikes")
    @Mapping(target = "numberOfComments", expression = "java(post.getComments().size())")
    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "textContent", expression = "java(post.getTextContent().orElse(null))")
    @Mapping(target = "imagesLinks", expression = "java(post.getImagesLinks().orElse(null))")
    PostResponse mapPostToPostResponse(Post post);

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
