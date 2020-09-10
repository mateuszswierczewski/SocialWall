package pl.mswierczewski.socialwall.security.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.mswierczewski.socialwall.components.services.SocialWallUserService;

import javax.crypto.SecretKey;

class DefaultJwtTokenServiceTest {

    private DefaultJwtTokenService underTest;

    @Mock
    private JwtTokenRepository jwtTokenRepository;

    @Mock
    private SocialWallUserService userService;

    @Mock
    private SecretKey secretKey;

    @Captor
    ArgumentCaptor<JwtToken> jwtTokenArgumentCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        underTest = new DefaultJwtTokenService(jwtTokenRepository, userService, secretKey);
    }

    @Test
    void itShouldGenerateJwtToken() {
        // Given

        // When
        // Then
    }
}