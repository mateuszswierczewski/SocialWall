package pl.mswierczewski.socialwall.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

public interface JwtTokenService {

    String generateJwtToken(Authentication authentication, HttpServletRequest httpRequest);

    Optional<String> getTokenFromRequest(HttpServletRequest request);

    Authentication validateToken(String token, HttpServletRequest request) throws JwtException;

    void invalidateToken(String token);

    void invalidateAllUserTokens(String token);

    Claims getClaims(String token);
}
