package pl.mswierczewski.socialwall.components.services;

import org.springframework.security.core.userdetails.UserDetailsService;
import pl.mswierczewski.socialwall.components.models.SocialWallUser;

public interface SocialWallUserService extends UserDetailsService {

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByUserId(String userId);

    SocialWallUser save(SocialWallUser user);

    SocialWallUser getUserById(String userId);
}
