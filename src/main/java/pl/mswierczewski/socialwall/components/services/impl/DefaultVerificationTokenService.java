package pl.mswierczewski.socialwall.components.services.impl;

import org.springframework.stereotype.Service;
import pl.mswierczewski.socialwall.components.models.SocialWallUser;
import pl.mswierczewski.socialwall.components.models.VerificationToken;
import pl.mswierczewski.socialwall.components.repositories.VerificationTokenRepository;
import pl.mswierczewski.socialwall.components.services.VerificationTokenService;
import pl.mswierczewski.socialwall.exceptions.ExpiredVerificationTokenException;
import pl.mswierczewski.socialwall.exceptions.SocialWallUserNotFoundException;

import java.util.Optional;
import java.util.UUID;

@Service
public class DefaultVerificationTokenService implements VerificationTokenService {

    private final VerificationTokenRepository verificationTokenRepository;

    public DefaultVerificationTokenService(VerificationTokenRepository verificationTokenRepository) {
        this.verificationTokenRepository = verificationTokenRepository;
    }

    @Override
    public VerificationToken generateVerificationToken(SocialWallUser user) {
        String token = UUID.randomUUID().toString() + "-" + UUID.randomUUID().toString();

        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);

        return verificationTokenRepository.save(verificationToken);
    }

    @Override
    public SocialWallUser getUserByVerificationTokenId(String token) {
        VerificationToken verificationToken = verificationTokenRepository
                .findById(token)
                .orElseThrow(() -> new SocialWallUserNotFoundException("xd"));

        if (verificationToken.isExpired()){
            throw new ExpiredVerificationTokenException("Token expired at " + verificationToken.getExpiryDate());
        }

        return verificationToken.getUser();
    }

    @Override
    public void remove(String token) {
        verificationTokenRepository.deleteById(token);
    }
}
