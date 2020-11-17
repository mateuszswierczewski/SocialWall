package pl.mswierczewski.socialwall.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.mswierczewski.socialwall.components.models.Vote;
import pl.mswierczewski.socialwall.dtos.vote.VoteResponse;

@Mapper(componentModel = "spring")
public interface VoteMapper {

    @Mapping(target = "voterId", source = "voter.id")
    VoteResponse mapVoteToVoteResponse(Vote vote);
}
