package pl.mswierczewski.socialwall.components.services;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;
import pl.mswierczewski.socialwall.components.models.SocialWallUser;
import pl.mswierczewski.socialwall.dtos.FileDto;
import pl.mswierczewski.socialwall.dtos.user.UserBasicInfo;
import pl.mswierczewski.socialwall.dtos.user.UserInfo;

import java.security.Principal;

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

    void followUser(String userId, String followingUserId);
}
