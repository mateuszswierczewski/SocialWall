package pl.mswierczewski.socialwall.components.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.mswierczewski.socialwall.components.enums.SocialWallUserRole;
import pl.mswierczewski.socialwall.components.models.SocialWallUser;
import pl.mswierczewski.socialwall.components.models.VerificationToken;
import pl.mswierczewski.socialwall.components.repositories.VerificationTokenRepository;
import pl.mswierczewski.socialwall.exceptions.ExpiredVerificationTokenException;
import pl.mswierczewski.socialwall.exceptions.SocialWallUserNotFoundException;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

class DefaultVerificationTokenServiceTest {

    private DefaultVerificationTokenService underTest;

    @Mock
    private VerificationTokenRepository verificationTokenRepository;

    @Captor
    private ArgumentCaptor<VerificationToken> verificationTokenArgumentCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        underTest = new DefaultVerificationTokenService(verificationTokenRepository);
    }

    @Test
    void itShouldGenerateVerificationToken() {
        // Given user
        SocialWallUser user = new SocialWallUser("User", "Password", "user@email.com", SocialWallUserRole.DEFAULT_USER);

        // When
        underTest.generateVerificationToken(user);

        // Then
        then(verificationTokenRepository).should().save(verificationTokenArgumentCaptor.capture());

        VerificationToken verificationToken = verificationTokenArgumentCaptor.getValue();

        assertThat(verificationToken).isNotNull();
        assertThat(verificationToken.getToken()).isNotNull();
        assertThat(verificationToken.getExpiryDate()).isNotNull().isAfter(LocalDate.now());
        assertThat(verificationToken.getUser()).isEqualTo(user);
    }

    @Test
    void itShouldReturnUserByVerificationToken() {
        // Given
        // ... verification token
        String token = UUID.randomUUID().toString() + "-" + UUID.randomUUID().toString();
        SocialWallUser user = new SocialWallUser("User", "Password", "user@email.com", SocialWallUserRole.DEFAULT_USER);
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setTokenExpirationLengthInDays(3);

        // ... optional of verification token
        given(verificationTokenRepository.findById(token)).willReturn(Optional.of(verificationToken));

        // When
        SocialWallUser userFromMethod = underTest.getUserByVerificationTokenId(token);

        // Then
        assertThat(userFromMethod).isEqualTo(user);
    }

    @Test
    void itShouldThrowExceptionWhenVerificationTokenDoesNotExists() {
        // Given
        // ... verification token
        String token = UUID.randomUUID().toString() + "-" + UUID.randomUUID().toString();

        // ... empty optional of verification token
        given(verificationTokenRepository.findById(token)).willReturn(Optional.empty());

        // Then should throw exception
        assertThatThrownBy(() -> underTest.getUserByVerificationTokenId(token))
                .isInstanceOf(SocialWallUserNotFoundException.class)
                .hasMessageContaining("User not found!");
    }

    @Test
    void itShouldThrowExceptionWhenVerificationTokenIsExpired() {
        // Given
        // ... verification token
        String token = UUID.randomUUID().toString() + "-" + UUID.randomUUID().toString();
        SocialWallUser user = new SocialWallUser("User", "Password", "user@email.com", SocialWallUserRole.DEFAULT_USER);
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setTokenExpirationLengthInDays(-1);

        // ... optional of verification token which is expired
        given(verificationTokenRepository.findById(token)).willReturn(Optional.of(verificationToken));

        // Then should throw exception
        assertThatThrownBy(() -> underTest.getUserByVerificationTokenId(token))
                .isInstanceOf(ExpiredVerificationTokenException.class)
                .hasMessageContaining("Token expired at " + verificationToken.getExpiryDate());
    }
}