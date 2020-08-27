package pl.mswierczewski.socialwall.components.user;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface SocialWallUserService extends UserDetailsService {

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByUserId(String userId);

    SocialWallUser save(SocialWallUser user);

    SocialWallUser getUserById(String userId);
}
