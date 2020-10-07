package pl.mswierczewski.socialwall.mappers;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.mswierczewski.socialwall.components.enums.SocialWallUserRole;
import pl.mswierczewski.socialwall.components.models.SocialWallUser;
import pl.mswierczewski.socialwall.components.models.SocialWallUserProfile;
import pl.mswierczewski.socialwall.dtos.user.UserBasicInfo;
import pl.mswierczewski.socialwall.dtos.user.UserInfo;
import pl.mswierczewski.socialwall.dtos.auth.SignUpRequest;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", source = "request.username")
    @Mapping(target = "password", expression = "java(passwordEncoder.encode(request.getPassword()))")
    @Mapping(target = "email", source = "request.email")
    @Mapping(target = "credentialsNonExpired", expression = "java(true)")
    @Mapping(target = "accountNonLocked", expression = "java(true)")
    @Mapping(target = "accountNonExpired", expression = "java(true)")
    @Mapping(target = "enabled", expression = "java(false)")
    @Mapping(target = "userProfile", expression = "java(mapSignUpRequestToUserProfile(request))")
    SocialWallUser mapSignUpRequestToUser(SignUpRequest request, SocialWallUserRole role, PasswordEncoder passwordEncoder);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "country", ignore = true)
    @Mapping(target = "city", ignore = true)
    @Mapping(target = "profileImageLink", ignore = true)
    SocialWallUserProfile mapSignUpRequestToUserProfile(SignUpRequest request);

    @Mapping(target = "userId", source = "id")
    @Mapping(target = "firstName", expression = "java(user.getUserProfile().getFirstName())")
    @Mapping(target = "lastName", expression = "java(user.getUserProfile().getLastName())")
    @Mapping(target = "gender", expression = "java(user.getUserProfile().getGender())")
    @Mapping(target = "description", expression = "java(user.getUserProfile().getDescription().orElse(null))")
    @Mapping(target = "country", expression = "java(user.getUserProfile().getCountry().orElse(null))")
    @Mapping(target = "city", expression = "java(user.getUserProfile().getCity().orElse(null))")
    @Mapping(target = "birthDate", expression = "java(user.getUserProfile().getBirthDate())")
    @Mapping(target = "numberOfFollowers", expression = "java(user.getFollowers().size())")
    @Mapping(target = "numberOfFollowing", expression = "java(user.getFollowing().size())")
    UserInfo mapUserToUserInfo(SocialWallUser user);

    @Mapping(target = "userId", source = "id")
    @Mapping(target = "firstName", expression = "java(user.getUserProfile().getFirstName())")
    @Mapping(target = "lastName", expression = "java(user.getUserProfile().getLastName())")
    UserBasicInfo mapUserToBasicUserInfo(SocialWallUser user);

    @AfterMapping
    default void afterMapSignUpRequestToUser(SocialWallUserRole role, @MappingTarget SocialWallUser user) {
        user.addRole(role);
    }


}
