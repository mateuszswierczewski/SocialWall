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
import pl.mswierczewski.socialwall.dtos.SignInRequest;
import pl.mswierczewski.socialwall.dtos.SignOutRequest;
import pl.mswierczewski.socialwall.dtos.SignUpRequest;
import pl.mswierczewski.socialwall.exceptions.SocialWallBadCredentialsException;
import pl.mswierczewski.socialwall.exceptions.UserAlreadyExistException;
import pl.mswierczewski.socialwall.security.jwt.JwtTokenService;
import pl.mswierczewski.socialwall.components.user.SocialWallUser;
import pl.mswierczewski.socialwall.components.user.SocialWallUserRole;
import pl.mswierczewski.socialwall.components.user.SocialWallUserService;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Service
public class AuthService {

    private final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final SocialWallUserService userService;
    private final JwtTokenService jwtTokenService;

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public AuthService(SocialWallUserService socialWallUserService, JwtTokenService jwtTokenService, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder) {
        this.userService = socialWallUserService;
        this.jwtTokenService = jwtTokenService;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
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

        SocialWallUser user = new SocialWallUser(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                request.getEmail(),
                SocialWallUserRole.DEFAULT_USER
        );

        user = userService.save(user);

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

                    if (request.isSignOutOnAllDevices()) {
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
}
