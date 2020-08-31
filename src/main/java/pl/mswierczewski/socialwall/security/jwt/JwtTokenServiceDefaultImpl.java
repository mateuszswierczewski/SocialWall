package pl.mswierczewski.socialwall.security.jwt;

import io.jsonwebtoken.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import pl.mswierczewski.socialwall.exceptions.SocialWallUserNotFoundException;
import pl.mswierczewski.socialwall.components.models.SocialWallUser;
import pl.mswierczewski.socialwall.components.services.SocialWallUserService;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static pl.mswierczewski.socialwall.security.jwt.JwtConfig.*;

@Service
public class JwtTokenServiceDefaultImpl implements JwtTokenService {

    private final JwtTokenRepository jwtTokenRepository;
    private final SocialWallUserService userService;
    private final SecretKey secretKey;

    public JwtTokenServiceDefaultImpl(JwtTokenRepository jwtTokenRepository, SocialWallUserService userService, SecretKey secretKey) {
        this.jwtTokenRepository = jwtTokenRepository;
        this.userService = userService;
        this.secretKey = secretKey;
    }

    public String generateJwtToken(Authentication authentication, HttpServletRequest httpRequest) {
        SocialWallUser user = (SocialWallUser) authentication.getPrincipal();

        Map<String, Object> claims = new HashMap<>();

        Set<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        claims.put(IP, getIpAddressFromHttpRequest(httpRequest));
        claims.put(USER_AGENT, getUserAgentFromHttpRequest(httpRequest));
        claims.put(AUTHORITIES, authorities);

        Date expirationDate = java.sql.Date.valueOf(LocalDate.now().plusDays(1));

        String token = Jwts.builder()
                .setSubject(user.getId())
                .addClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(expirationDate)
                .signWith(secretKey)
                .compact();

        JwtToken jwtToken = new JwtToken(
                token,
                authentication.getName(),
                expirationDate
        );

        jwtTokenRepository.save(jwtToken);

        return token;
    }

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

    private String getUserAgentFromJwtClaims(Claims claims){
        return claims.get(USER_AGENT, String.class);
    }

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

    private String getIpAddressFromJwtClaims(Claims claims) {
        return claims.get(IP, String.class);
    }

    @SuppressWarnings("unchecked")
    private Set<SimpleGrantedAuthority> getAuthorities(Claims claims){
        List<String> authorities = (List<String>) claims.get(AUTHORITIES);

        return authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

    public Optional<String> getTokenFromRequest(HttpServletRequest request) {
        String tokenString = request.getHeader(AUTHORIZATION_HEADER);

        if (tokenString == null || !tokenString.startsWith(AUTHORIZATION_PREFIX))
            return Optional.empty();
        else
            return Optional.of(tokenString.replace(AUTHORIZATION_PREFIX, ""));
    }

    public Authentication validateToken(String token, HttpServletRequest request) throws JwtException{
        Claims claims = getClaims(token);

        String userId = claims.getSubject();

        if (!userService.existsByUserId(userId)){
            throw new SocialWallUserNotFoundException(userId);
        }

        if (!getIpAddressFromHttpRequest(request).equals(getIpAddressFromJwtClaims(claims))){
            throw new JwtException("Wrong ip address!");
        }

        if (!getUserAgentFromHttpRequest(request).equals(getUserAgentFromJwtClaims(claims))){
            throw new JwtException("Wrong user agent!");
        }

        if (!isTokenValid(token)){
            throw new JwtException("Token is invalid!");
        }

        Set<SimpleGrantedAuthority> authorities = getAuthorities(claims);

        return new UsernamePasswordAuthenticationToken(
                userId,
                null,
                authorities
        );
    }

    private boolean isTokenValid(String token) {
        JwtToken jwtToken = jwtTokenRepository.findById(token).orElse(null);

        if (jwtToken != null){
            return jwtToken.isValid();
        } else {
            return false;
        }
    }

    public Claims getClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public void invalidateToken(String token) {
       JwtToken jwtToken = jwtTokenRepository.getOne(token);
       jwtToken.invalidate();
       jwtTokenRepository.save(jwtToken);

    }

    @Override
    public void invalidateAllUserTokens(String userId) {
        Optional<List<JwtToken>> jwtTokenList = jwtTokenRepository.findAllByUserId(userId);

        jwtTokenList.ifPresent(
                jwtTokens -> {
                    List<JwtToken> tokens = jwtTokens.stream()
                            .filter(JwtToken::isValid)
                            .collect(Collectors.toList());

                    tokens.forEach(JwtToken::invalidate);

                    jwtTokenRepository.saveAll(tokens);
                }
        );
    }
}
