package pl.mswierczewski.socialwall.tools.mail;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pl.mswierczewski.socialwall.components.models.SocialWallUser;
import pl.mswierczewski.socialwall.components.models.VerificationToken;

@Service
public class MailService {

    private final JavaMailSender mailSender;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendVerificationEmail(SocialWallUser user, VerificationToken verificationToken){
        String link = "http://localhost:8080/api/auth/activateAccount/" + verificationToken.getToken();

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(user.getEmail());
        mail.setSubject("SocialWall registration confirmation");
        mail.setText("To activate account click link: " + link);

        mailSender.send(mail);
    }
}
