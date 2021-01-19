package pl.mswierczewski.socialwall.components.services;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;
import pl.mswierczewski.socialwall.components.models.SocialWallUser;
import pl.mswierczewski.socialwall.dtos.FileDto;
import pl.mswierczewski.socialwall.dtos.user.EditUserProfileRequest;
import pl.mswierczewski.socialwall.dtos.user.UserBasicInfo;
import pl.mswierczewski.socialwall.dtos.user.UserInfo;

import java.security.Principal;
import java.util.List;

public interface SocialWallUserService extends UserDetailsService {

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByUserId(String userId);

    SocialWallUser save(SocialWallUser user);

    SocialWallUser getUserById(String userId);

    UserInfo getUserInfo(String userId);

    void uploadUserProfileImage(String userId, MultipartFile file);

    FileDto downloadUserProfileImage(String userId);

    UserBasicInfo getUserBasicInfo(String userId);

    Boolean followUser(String userId, String followingUserId);

    Boolean unfollowUser(String principalId, String unfollowingUserId);

    List<UserBasicInfo> getUserFollowers(String principalId, String userId);

    Boolean isFollowing(String userId1, String userId2);

    List<UserBasicInfo> getUserFollowing(String principalId, String userId);

    List<UserBasicInfo> findUsersByName(String name, int page, int pageSize);

    UserInfo editUserProfile(String userId, EditUserProfileRequest editUserProfileRequest);
}
