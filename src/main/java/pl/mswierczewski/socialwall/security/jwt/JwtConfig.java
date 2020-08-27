package pl.mswierczewski.socialwall.security.jwt;

import io.jsonwebtoken.security.Keys;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import javax.crypto.SecretKey;

@Configuration
public class JwtConfig {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String AUTHORIZATION_PREFIX = "Bearer ";
    public static final String AUTHORIZATION_SECRET = "aQbaEr0cKe8WF3m6UTtysY0qM2HappyConf2tWhoAEL78NJ0EdKd2ZzLBygoneMQZ368TPBefallQ55Ti4oAU2nWi4Y7q7smurfXC";

    public static final String IP = "ip";
    public static final String USER_AGENT = "ua";
    public static final String AUTHORITIES = "authorities";

    @Bean
    public SecretKey secretKey() {
        return Keys.hmacShaKeyFor(AUTHORIZATION_SECRET.getBytes());
    }

}
