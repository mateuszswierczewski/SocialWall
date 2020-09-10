package pl.mswierczewski.socialwall.components.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import pl.mswierczewski.socialwall.components.models.SocialWallUser;
import pl.mswierczewski.socialwall.components.repositories.SocialWallUserRepository;
import pl.mswierczewski.socialwall.exceptions.SocialWallUserNotFoundException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;


class DefaultSocialWallUserServiceTest {

    private DefaultSocialWallUserService underTest;

    @Mock
    private SocialWallUserRepository socialWallUserRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        underTest = new DefaultSocialWallUserService(socialWallUserRepository);
    }

    @Test
    void itShouldReturnUserByUsername() {
        // Given
        // ... username
        String username = "User";

        // ... user
        given(socialWallUserRepository.findByUsername(username)).willReturn(Optional.of(new SocialWallUser()));

        // When
        UserDetails user = underTest.loadUserByUsername(username);

        // Then
        assertThat(user).isNotNull();
    }

    @Test
    void itShouldReturnUserByEmail() {
        // Given
        // ... username
        String username = "User";

        // ... optional empty after searching by username
        given(socialWallUserRepository.findByUsername(username)).willReturn(Optional.empty());

        // ... user
        given(socialWallUserRepository.findByEmail(username)).willReturn(Optional.of(new SocialWallUser()));

        // When
        UserDetails user = underTest.loadUserByUsername(username);

        // Then
        assertThat(user).isNotNull();
    }

    @Test
    void itShouldThrowExceptionWhenUserDoesNotExistByUsernameAndEmail() {
        // Given
        // ... username
        String username = "User";

        // ... optional empty after searching by username
        given(socialWallUserRepository.findByUsername(username)).willReturn(Optional.empty());

        // ... optional empty after searching by email
        given(socialWallUserRepository.findByEmail(username)).willReturn(Optional.empty());

        // Then should throw exception
        assertThatThrownBy(() -> underTest.loadUserByUsername(username))
            .isInstanceOf(UsernameNotFoundException.class)
            .hasMessageContaining(String.format("Username %s not found!", username));
    }

    @Test
    void itShouldReturnUserById() {
        // Given
        // ... user id
        String id = UUID.randomUUID().toString();

        // ... user
        given(socialWallUserRepository.findById(id)).willReturn(Optional.of(new SocialWallUser()));

        // When
        SocialWallUser user = underTest.getUserById(id);

        // Then
        assertThat(user).isNotNull();
    }

    @Test
    void itShouldThrowExceptionWhenUserDoesNotExistByUserId() {
        // Given
        // ... user id
        String userId = UUID.randomUUID().toString();

        // ... user
        given(socialWallUserRepository.findById(userId)).willReturn(Optional.empty());

        // Then should throw exception
        assertThatThrownBy(() -> underTest.getUserById(userId))
                .isInstanceOf(SocialWallUserNotFoundException.class)
                .hasMessageContaining(String.format("User %s not found!", userId));

    }

}