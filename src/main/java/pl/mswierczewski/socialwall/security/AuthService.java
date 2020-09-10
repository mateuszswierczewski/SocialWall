package pl.mswierczewski.socialwall.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

/**
 * Class which handles the requests related to user registration, authorization, authentication
 */
@Service
public class AuthService {

    private final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final SocialWallUserService userService;
    private final JwtTokenService jwtTokenService;
    private final VerificationTokenService verificationTokenService;
    private final MailService mailService;

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthService(SocialWallUserService socialWallUserService, JwtTokenService jwtTokenService,
                       VerificationTokenService verificationTokenService, MailService mailService,
                       UserMapper userMapper, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.userService = socialWallUserService;
        this.jwtTokenService = jwtTokenService;
        this.verificationTokenService = verificationTokenService;
        this.mailService = mailService;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Registers users in the system. Checks if request has unique username and email.
     * After save, sends email with verification link to given email in request.
     *
     * @param request SignUpRequest
     * @throws UserAlreadyExistException when username or email already exists
     */
    @Transactional
    public void signUp(SignUpRequest request) {
        // Checks if username and email are unique
        boolean isUsernameExists = userService.existsByUsername(request.getUsername());
        boolean isEmailExists = userService.existsByEmail(request.getEmail());

        if (isUsernameExists || isEmailExists){
            if (isUsernameExists)
                logger.trace(String.format("New user tried to sign up with username %s, but this username is already taken!", request.getUsername()));
            if (isEmailExists)
                logger.trace(String.format("New user tried to sign up with email %s, but this email is already exists!", request.getEmail()));

            throw new UserAlreadyExistException(isUsernameExists, isEmailExists);
        }

        // Maps request to user and encodes password
        SocialWallUser user = userMapper.mapSignUpRequestToUser(request, SocialWallUserRole.DEFAULT_USER, passwordEncoder);

        // Saves user
        user = userService.save(user);

        // Generates verification token and sends it via email
        VerificationToken verificationToken = verificationTokenService.generateVerificationToken(user);
        mailService.sendVerificationEmail(user, verificationToken);

        logger.trace(String.format("User %s sign up! User ID: %s", user.getUsername(), user.getId()));
    }

    /**
     * Validates user login request. Generates and returns user JWT token.
     *
     * @param request SignInRequest
     * @param httpRequest HttpServletRequest
     * @return A string of generated token
     */
    public String signIn(SignInRequest request, HttpServletRequest httpRequest) {
        try {
            // Authenticates request
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            // Generates JWT
            String token = jwtTokenService.generateJwtToken(authentication, httpRequest);
            logger.trace(String.format("User %s sign in! User token: %s", ((SocialWallUser) authentication.getPrincipal()).getId(), token));

            return token;

        } catch (UsernameNotFoundException e){
            throw SocialWallBadCredentialsException.BAD_USERNAME_OR_EMAIL;
        } catch (BadCredentialsException e){
            throw SocialWallBadCredentialsException.BAD_PASSWORD;
        }
    }

    /**
     * Invalidates the token provides in HttpServletRequest object, or also invalidates the rest of tokens belongs to user
     * according to SignOutRequest
     *
     * @param request SignOutRequest
     * @param httpRequest HttpServletRequest
     */
    public void signOut(SignOutRequest request, HttpServletRequest httpRequest) {
        // Extract token from http request
        Optional<String> tokenOptional = jwtTokenService.getTokenFromRequest(httpRequest);

        // Invalidates token or tokens
        tokenOptional.ifPresent(
                token -> {
                    String userId = jwtTokenService.getClaims(token).getSubject();

                    if (request.isOnAllDevices()) {
                        // If logout on all devices is requested
                        // This is equivalent to invalidating all tokens belonging to the user
                        jwtTokenService.invalidateAllUserTokens(userId);
                        logger.trace(String.format("User %s sign out from all devices!", userId));
                    } else {
                        // If it is requested to logout only on the device from which the request comes
                        jwtTokenService.invalidateToken(token);
                        logger.trace(String.format("User %s sign out!", userId));
                    }
                }
        );
    }

    /**
     * Returns object of current user who executes request.
     *
     * @return A SocialWallUser who executes request
     */
    public SocialWallUser getCurrentUser(){
        // Retrieves a user id from context
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.getUserById(userId);
    }

    /**
     * Activates user account by given verification token.
     *
     * @param token a String of verification token
     */
    @Transactional
    public void activateAccount(String token) {
        // Gets user from database verification token and enable user account
        SocialWallUser user = verificationTokenService.getUserByVerificationTokenId(token);
        user.setEnabled(true);
        userService.save(user);

        // Removes token from database
        verificationTokenService.remove(token);

        logger.trace(String.format("User %s (%s) verified account successfully!", user.getUsername(), user.getId()));
    }
}