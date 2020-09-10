package pl.mswierczewski.socialwall.components.services.impl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.mswierczewski.socialwall.components.models.SocialWallUser;
import pl.mswierczewski.socialwall.components.repositories.SocialWallUserRepository;
import pl.mswierczewski.socialwall.components.services.SocialWallUserService;
import pl.mswierczewski.socialwall.exceptions.SocialWallUserNotFoundException;

/**
 * Default implementation of SocialWallUserService.
 */
@Service
public class DefaultSocialWallUserService implements SocialWallUserService {

    private final SocialWallUserRepository userRepository;

    public DefaultSocialWallUserService(SocialWallUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Searches for a user in the database based on the given username. If it doesn't find it then it looks in the email
     * column. If it fails again, it throws an exception.
     *
     * @param username - String
     * @return UserDetails
     * @throws UsernameNotFoundException - throws when user not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository
                .findByUsername(username)
                .or(() -> userRepository.findByEmail(username))
                .orElseThrow(() -> new UsernameNotFoundException(String.format("Username %s not found!", username)));
    }

    /**
     * Searches the database for a user based on the given user id.
     *
     * @param userId - String
     * @return SocialWallUser - the user that was found based on the user id
     * @throws SocialWallUserNotFoundException - throws when user not found
     */
    @Override
    public SocialWallUser getUserById(String userId) throws SocialWallUserNotFoundException {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new SocialWallUserNotFoundException(userId));
    }

    /**
     * Searches the database to see if the user exists by given email.
     *
     * @param email - String
     * @return Boolean - true if exists, else false
     */
    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Searches the database to see if the user exists by given username.
     *
     * @param username - String
     * @return Boolean - true if exists, else false
     */
    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Searches the database to see if the user exists by given user id.
     *
     * @param userId - String
     * @return Boolean - true if exists, else false
     */
    @Override
    public boolean existsByUserId(String userId){
        return userRepository.existsById(userId);
    }

    /**
     * Saves given user in database.
     *
     * @param user - SocialWallUser to save
     * @return SocialWallUser after save with generated id
     */
    @Override
    public SocialWallUser save(SocialWallUser user) {
        return userRepository.save(user);
    }
}