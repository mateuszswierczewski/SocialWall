package pl.mswierczewski.socialwall.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.mswierczewski.socialwall.components.enums.SocialWallUserRole;
import pl.mswierczewski.socialwall.components.models.VerificationToken;
import pl.mswierczewski.socialwall.components.services.VerificationTokenService;
import pl.mswierczewski.socialwall.dtos.SignInRequest;
import pl.mswierczewski.socialwall.dtos.SignOutRequest;
import pl.mswierczewski.socialwall.dtos.SignUpRequest;
import pl.mswierczewski.socialwall.exceptions.SocialWallBadCredentialsException;
import pl.mswierczewski.socialwall.exceptions.UserAlreadyExistException;
import pl.mswierczewski.socialwall.mappers.UserMapper;
import pl.mswierczewski.socialwall.security.jwt.JwtTokenService;
import pl.mswierczewski.socialwall.components.models.SocialWallUser;
import pl.mswierczewski.socialwall.components.services.SocialWallUserService;
import pl.mswierczewski.socialwall.utils.mail.MailService;


import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Service
public class AuthService {

    private final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final SocialWallUserService userService;
    private final JwtTokenService jwtTokenService;
    private final VerificationTokenService verificationTokenService;
    private final MailService mailService;

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthService(SocialWallUserService socialWallUserService, JwtTokenService jwtTokenService,
                       VerificationTokenService verificationTokenService, MailService mailService,
                       PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.userService = socialWallUserService;
        this.jwtTokenService = jwtTokenService;
        this.verificationTokenService = verificationTokenService;
        this.mailService = mailService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public void signUp(SignUpRequest request) {
        boolean isUsernameExists = userService.existsByUsername(request.getUsername());
        boolean isEmailExists = userService.existsByEmail(request.getEmail());

        if (isUsernameExists || isEmailExists){
            if (isUsernameExists)
                logger.trace(String.format("New user tried to sign up with username %s, but this username is already taken!", request.getUsername()));
            if (isEmailExists)
                logger.trace(String.format("New user tried to sign up with email %s, but this email is already exists!", request.getEmail()));

            throw new UserAlreadyExistException(isUsernameExists, isEmailExists);
        }

        SocialWallUser user = UserMapper.MAPPER.mapSignUpRequestToUser(request, SocialWallUserRole.DEFAULT_USER, passwordEncoder);

        user = userService.save(user);

        VerificationToken verificationToken = verificationTokenService.generateVerificationToken(user);

        mailService.sendVerificationEmail(user, verificationToken);

        logger.trace(String.format("User %s sign up! User ID: %s", user.getUsername(), user.getId()));
    }

    public String signIn(SignInRequest request, HttpServletRequest httpRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            String token = jwtTokenService.generateJwtToken(authentication, httpRequest);
            logger.trace(String.format("User %s sign in! User token: %s", request.getUsername(), token));

            return token;

        } catch (UsernameNotFoundException e){
            throw SocialWallBadCredentialsException.BAD_USERNAME_OR_EMAIL;
        } catch (BadCredentialsException e){
            throw SocialWallBadCredentialsException.BAD_PASSWORD;
        }
    }

    public void signOut(SignOutRequest request, HttpServletRequest httpRequest) {
        Optional<String> tokenOptional = jwtTokenService.getTokenFromRequest(httpRequest);

        tokenOptional.ifPresent(
                token -> {
                    String userId = jwtTokenService.getClaims(token).getSubject();

                    if (request.isOnAllDevices()) {
                        jwtTokenService.invalidateAllUserTokens(userId);
                        logger.trace(String.format("User %s sign out from all devices!", userId));
                    } else {
                        jwtTokenService.invalidateToken(token);
                        logger.trace(String.format("User %s sign out!", userId));
                    }
                }
        );
    }

    public SocialWallUser getCurrentUser(){
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.getUserById(userId);
    }

    @Transactional
    public void activateAccount(String token) {
        SocialWallUser user = verificationTokenService.getUserByVerificationTokenId(token);
        user.setEnabled(true);
        userService.save(user);

        verificationTokenService.remove(token);

        logger.trace(String.format("User %s (%s) verified account successfully!", user.getUsername(), user.getId()));
    }
}
