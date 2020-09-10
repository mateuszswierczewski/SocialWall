package pl.mswierczewski.socialwall.utils.mail;

import pl.mswierczewski.socialwall.components.models.SocialWallUser;
import pl.mswierczewski.socialwall.components.models.VerificationToken;

public interface MailService {

    void sendVerificationEmail(SocialWallUser user, VerificationToken verificationToken);
}
