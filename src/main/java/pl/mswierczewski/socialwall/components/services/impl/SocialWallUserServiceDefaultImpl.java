package pl.mswierczewski.socialwall.components.services.impl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.mswierczewski.socialwall.components.models.SocialWallUser;
import pl.mswierczewski.socialwall.components.repositories.SocialWallUserRepository;
import pl.mswierczewski.socialwall.components.services.SocialWallUserService;
import pl.mswierczewski.socialwall.exceptions.SocialWallUserNotFoundException;



@Service
public class SocialWallUserServiceDefaultImpl implements SocialWallUserService {

    private final SocialWallUserRepository userRepository;

    public SocialWallUserServiceDefaultImpl(SocialWallUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository
                .findByUsername(username)
                .or(() -> userRepository.findByEmail(username))
                .orElseThrow(() -> new UsernameNotFoundException(String.format("Username %s not found!", username)));
    }

    @Override
    public SocialWallUser getUserById(String userId) throws SocialWallUserNotFoundException {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new SocialWallUserNotFoundException(userId));
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByUserId(String userId){
        return userRepository.existsById(userId);
    }

    @Override
    public SocialWallUser save(SocialWallUser user) {
        return userRepository.save(user);
    }



}
