package pl.mswierczewski.socialwall.components.services;

import pl.mswierczewski.socialwall.components.models.SocialWallUser;
import pl.mswierczewski.socialwall.components.models.VerificationToken;

public interface VerificationTokenService {

    VerificationToken generateVerificationToken(SocialWallUser user);

    SocialWallUser getUserByVerificationTokenId(String token);

    void remove(String token);
}
