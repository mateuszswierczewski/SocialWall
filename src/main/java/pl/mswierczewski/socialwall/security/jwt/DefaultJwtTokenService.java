package pl.mswierczewski.socialwall.security.jwt;

import io.jsonwebtoken.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.mswierczewski.socialwall.exceptions.SocialWallUserNotFoundException;
import pl.mswierczewski.socialwall.components.models.SocialWallUser;
import pl.mswierczewski.socialwall.components.services.SocialWallUserService;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static pl.mswierczewski.socialwall.security.jwt.JwtConfig.*;

/**
 * Default implementation of JwtTokenService
 */
@Service
public class DefaultJwtTokenService implements JwtTokenService {

    private final JwtTokenRepository jwtTokenRepository;
    private final SocialWallUserService userService;
    private final SecretKey secretKey;

    public DefaultJwtTokenService(JwtTokenRepository jwtTokenRepository, SocialWallUserService userService, SecretKey secretKey) {
        this.jwtTokenRepository = jwtTokenRepository;
        this.userService = userService;
        this.secretKey = secretKey;
    }

    /**
     * Generates JWT for given user. To generate a token, method use: user id, user authorities, user ip and user agent.
     * Token is valid for 1 day, from the moment it is generated. After being generate, the token is saved in repository.
     *
     * @param authentication - Authentication
     * @param httpRequest - HttpServletRequest
     * @return JWT token for given user
     */
    public String generateJwtToken(Authentication authentication, HttpServletRequest httpRequest) {
        SocialWallUser user = (SocialWallUser) authentication.getPrincipal();

        Map<String, Object> claims = new HashMap<>();

        // Gets user authorities
        Set<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        // Adds user ip, user agent and user authorities
        claims.put(IP, getIpAddressFromHttpRequest(httpRequest));
        claims.put(USER_AGENT, getUserAgentFromHttpRequest(httpRequest));
        claims.put(AUTHORITIES, authorities);

        // Sets expiration date
        Date expirationDate = java.sql.Date.valueOf(LocalDate.now().plusDays(1));

        // Builds token
        String token = Jwts.builder()
                .setSubject(user.getId())
                .addClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(expirationDate)
                .signWith(secretKey)
                .compact();

        JwtToken jwtToken = new JwtToken(
                token,
                user.getId(),
                expirationDate
        );

        // Saves token
        jwtTokenRepository.save(jwtToken);

        return token;
    }

    /**
     * Extracts user agent from request.
     *
     * @param httpRequest - HttpServletRequest
     * @return A string of the user agent or empty string if can't extract it
     */
    private String getUserAgentFromHttpRequest(HttpServletRequest httpRequest) {
        String userAgent = "";

        if (httpRequest != null) {
            userAgent = httpRequest.getHeader("User-Agent");

            if (userAgent == null || "".equals(userAgent)) {
                userAgent = "";
            }
        }

        return userAgent;
    }

    /**
     * Extracts user agent from claims.
     *
     * @param claims - Claims
     * @return A string of the user agent
     */
    private String getUserAgentFromJwtClaims(Claims claims){
        return claims.get(USER_AGENT, String.class);
    }

    /**
     * Extracts user ip address from request.
     *
     * @param httpRequest - HttpServletRequest
     * @return A string of the user ip address or empty string if can't extract it
     */
    private String getIpAddressFromHttpRequest(HttpServletRequest httpRequest) {
        String ipAddress = "";

        if (httpRequest != null) {
            ipAddress = httpRequest.getHeader("X-FORWARDED-FOR");

            if (ipAddress == null || "".equals(ipAddress)) {
                ipAddress = httpRequest.getRemoteAddr();
            }
        }

        return ipAddress;
    }

    /**
     * Extracts user ip address from claims.
     *
     * @param claims - Claims
     * @return A string of the user ip address
     */
    private String getIpAddressFromJwtClaims(Claims claims) {
        return claims.get(IP, String.class);
    }

    /**
     * Extracts user authorities from claims.
     *
     * @param claims - Claims
     * @return A set of the user authorities
     */
    @SuppressWarnings("unchecked")
    private Set<SimpleGrantedAuthority> getAuthorities(Claims claims){
        List<String> authorities = (List<String>) claims.get(AUTHORITIES);

        return authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

    /**
     * Extracts token from request.
     *
     * @param request - HttpServletRequest
     * @return A Optional of token string
     */
    public Optional<String> getTokenFromRequest(HttpServletRequest request) {
        // Gets header from request
        String tokenString = request.getHeader(AUTHORIZATION_HEADER);

        // Checks if token is not null and if it has valid prefix
        if (tokenString == null || !tokenString.startsWith(AUTHORIZATION_PREFIX))
            return Optional.empty();
        else
            return Optional.of(tokenString.replace(AUTHORIZATION_PREFIX, ""));
    }

    /**
     * Validates given token.
     *
     * @param token - String
     * @param request - HttpServletRequest
     * @return Authentication
     * @throws JwtException when token is invalid
     * @throws SocialWallUserNotFoundException when user doesn't exist
     */
    public Authentication validateToken(String token, HttpServletRequest request) {
        // Extracts claims from token
        // It also checks if token is expired
        Claims claims = getClaims(token);

        String userId = claims.getSubject();

        // Checks if token is not forbidden
        if (isTokenForbidden(token)){
            throw new JwtException("Token is invalid!");
        }

        // Checks if user exists
        if (!userService.existsByUserId(userId)){
            throw new SocialWallUserNotFoundException(userId);
        }

        // Checks if user ip address is correct
        if (!getIpAddressFromHttpRequest(request).equals(getIpAddressFromJwtClaims(claims))){
            throw new JwtException("Wrong ip address!");
        }

        // Checks if user agent is correct
        if (!getUserAgentFromHttpRequest(request).equals(getUserAgentFromJwtClaims(claims))){
            throw new JwtException("Wrong user agent!");
        }

        // Gets authorities from claims
        Set<SimpleGrantedAuthority> authorities = getAuthorities(claims);

        // Returns authentication
        return new UsernamePasswordAuthenticationToken(
                userId,
                null,
                authorities
        );
    }

    /**
     * Checks if token is forbidden.
     *
     * @param token - String
     * @return True if token is forbidden or doesn't exist, otherwise false
     */
    private boolean isTokenForbidden(String token) {
        JwtToken jwtToken = jwtTokenRepository.findById(token).orElse(null);

        return jwtToken == null || jwtToken.isForbidden();
    }

    /**
     * Extracts claims from token. Also checks if token is expired.
     *
     * @param token - String
     * @return Claims extracted from token
     */
    public Claims getClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Invalidates token by setting isForbidden variable true.
     *
     * @param token - String
     */
    @Transactional
    public void invalidateToken(String token) {
        // Finds token
        Optional<JwtToken> tokenOptional = jwtTokenRepository.findById(token);

        // If present, invalidate it
        tokenOptional.ifPresent(
               jwt -> {
                   jwt.invalidate();
                   jwtTokenRepository.save(jwt);
               }
        );
    }

    /**
     * Invalidates all user tokens by setting isForbidden variable true.
     *
     * @param userId - String
     */
    @Override
    public void invalidateAllUserTokens(String userId) {
        // Finds all user tokens
        Optional<List<JwtToken>> jwtTokenList = jwtTokenRepository.findAllByUserId(userId);

        // Invalidates all founded user tokens
        jwtTokenList.ifPresent(
                jwtTokens -> {
                    List<JwtToken> tokens = jwtTokens.stream()
                            .filter(token -> !token.isForbidden())
                            .collect(Collectors.toList());

                    tokens.forEach(JwtToken::invalidate);

                    jwtTokenRepository.saveAll(tokens);
                }
        );
    }
}
