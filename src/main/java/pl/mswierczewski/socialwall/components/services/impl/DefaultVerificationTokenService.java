package pl.mswierczewski.socialwall.components.services.impl;

import org.springframework.stereotype.Service;
import pl.mswierczewski.socialwall.components.models.SocialWallUser;
import pl.mswierczewski.socialwall.components.models.VerificationToken;
import pl.mswierczewski.socialwall.components.repositories.VerificationTokenRepository;
import pl.mswierczewski.socialwall.components.services.VerificationTokenService;
import pl.mswierczewski.socialwall.exceptions.ExpiredVerificationTokenException;
import pl.mswierczewski.socialwall.exceptions.NotFoundException;

import java.util.UUID;

/**
 * Default implementation of VerificationTokenService.
 */
@Service
public class DefaultVerificationTokenService implements VerificationTokenService {

    private final VerificationTokenRepository verificationTokenRepository;

    public DefaultVerificationTokenService(VerificationTokenRepository verificationTokenRepository) {
        this.verificationTokenRepository = verificationTokenRepository;
    }

    /**
     * Generates verification token for given user. Then it saves the generated token in the database.
     *
     * @param user - SocialWallUser for which the token will be generated
     * @return VerificationToken which is needed to activate the account
     */
    @Override
    public VerificationToken generateVerificationToken(SocialWallUser user) {
        // Generate token based on two UUID's
        String token = UUID.randomUUID().toString() + "-" + UUID.randomUUID().toString();

        // Sets the token properties
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setTokenExpirationLengthInDays(3);

        // Saves and return verification token
        return verificationTokenRepository.save(verificationToken);
    }

    /**
     * Finds the user to which the given token belongs.
     *
     * @param token - String
     * @return SocialWallUser
     * @throws NotFoundException - when token not find
     * @throws ExpiredVerificationTokenException - when token is expired
     */
    @Override
    public SocialWallUser getUserByVerificationTokenId(String token) {
        // Searches for verification token
        VerificationToken verificationToken = verificationTokenRepository
                .findById(token)
                .orElseThrow(() -> new NotFoundException(String.format("Token %s not found!", token)));

        // Checks if token is expired
        if (verificationToken.isExpired()){
            throw new ExpiredVerificationTokenException("Token expired at " + verificationToken.getExpiryDate());
        }

        // Returns user
        return verificationToken.getUser();
    }

    /**
     * Removes token from database.
     *
     * @param token - String
     */
    @Override
    public void remove(String token) {
        verificationTokenRepository.deleteById(token);
    }
}
