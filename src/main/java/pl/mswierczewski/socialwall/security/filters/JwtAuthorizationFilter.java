package pl.mswierczewski.socialwall.security.filters;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.mswierczewski.socialwall.exceptions.SocialWallUserNotFoundException;
import pl.mswierczewski.socialwall.exceptions.handlers.ErrorResponse;
import pl.mswierczewski.socialwall.security.jwt.JwtTokenService;
import pl.mswierczewski.socialwall.utils.JsonUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

/**
 * This class authenticates every single request, which requires authentication.
 * Authentication is based on JWT validation.
 */
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;
    private final JsonUtils jsonUtils;

    public JwtAuthorizationFilter(JwtTokenService jwtTokenService, JsonUtils jsonUtils) {
        this.jwtTokenService = jwtTokenService;
        this.jsonUtils = jsonUtils;
    }

    /**
     * Filters requests.
     *
     * @param request - HttpServletRequest
     * @param response - HttpServletResponse
     * @param filterChain - FilterChain
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // Extracts token from request
            Optional<String> tokenOptional = jwtTokenService.getTokenFromRequest(request);

            // If token is present, then validates it
            tokenOptional.ifPresent(
                    token -> {
                        Authentication authentication = jwtTokenService.validateToken(token, request);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
            );

            // Continue request
            filterChain.doFilter(request, response);

        } catch (JwtException | SocialWallUserNotFoundException e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    e.getMessage(),
                    HttpStatus.UNAUTHORIZED,
                    ZonedDateTime.now(ZoneId.of("UTC"))
            );

            if (e instanceof ExpiredJwtException){
                errorResponse.addAdditionalData("expiration", ((ExpiredJwtException) e).getClaims().getExpiration().toString());
            }

            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write(jsonUtils.convertObjectToJson(errorResponse));
        }
    }
}
