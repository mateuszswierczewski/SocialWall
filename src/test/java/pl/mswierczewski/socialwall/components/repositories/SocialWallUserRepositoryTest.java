package pl.mswierczewski.socialwall.components.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import pl.mswierczewski.socialwall.components.enums.Gender;
import pl.mswierczewski.socialwall.components.enums.SocialWallUserRole;
import pl.mswierczewski.socialwall.components.models.SocialWallUser;
import pl.mswierczewski.socialwall.components.models.SocialWallUserProfile;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
class SocialWallUserRepositoryTest {

    @Autowired
    private SocialWallUserRepository underTest;

    @Test
    void itShouldSaveSocialWallUser() {
        // Given
        // ... user object
        SocialWallUser user = new SocialWallUser("User", "user1234", "user@email.com", SocialWallUserRole.DEFAULT_USER);

        // ... user profile info
        SocialWallUserProfile userProfile = new SocialWallUserProfile("John", "Smith", LocalDate.of(1980, 6, 16), Gender.MALE);
        user.setUserProfile(userProfile);

        // When
        underTest.save(user);

        // Then
        Optional<SocialWallUser> userAfterSave = underTest.findById(user.getId());

        assertThat(userAfterSave)
                .isPresent()
                .hasValueSatisfying(
                        u -> assertThat(u).isEqualTo(user)
                );

        assertThat(userAfterSave.get().getUserProfile()).isEqualTo(userProfile);
    }

    @Test
    void itShouldNotSaveSocialWallUserWhenUsernameIsNull() {
        // Given
        // ... user object
        SocialWallUser user = new SocialWallUser(null, "user1234", "user@email.com", SocialWallUserRole.DEFAULT_USER);

        // ... user profile info
        SocialWallUserProfile userProfile = new SocialWallUserProfile("John", "Smith", LocalDate.of(1980, 6, 16), Gender.MALE);
        user.setUserProfile(userProfile);

        // While the user is saved, then it should throw an exception
        assertThatThrownBy(() -> underTest.save(user))
            .isInstanceOf(DataIntegrityViolationException.class)
            .hasMessageContaining("not-null property references a null or transient value : pl.mswierczewski.socialwall.components.models.SocialWallUser.username");
    }

    @Test
    void itShouldNotSaveSocialWallUserWhenPasswordIsNull() {
        // Given
        // ... user object
        SocialWallUser user = new SocialWallUser("User", null, "user@email.com", SocialWallUserRole.DEFAULT_USER);

        // ... user profile info
        SocialWallUserProfile userProfile = new SocialWallUserProfile("John", "Smith", LocalDate.of(1980, 6, 16), Gender.MALE);
        user.setUserProfile(userProfile);

        // While the user is saved, then it should throw an exception
        assertThatThrownBy(() -> underTest.save(user))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("not-null property references a null or transient value : pl.mswierczewski.socialwall.components.models.SocialWallUser.password");
    }

    @Test
    void itShouldNotSaveSocialWallUserWhenEmailIsNull() {
        // Given
        // ... user object
        SocialWallUser user = new SocialWallUser("User", "user1234", null, SocialWallUserRole.DEFAULT_USER);

        // ... user profile info
        SocialWallUserProfile userProfile = new SocialWallUserProfile("John", "Smith", LocalDate.of(1980, 6, 16), Gender.MALE);
        user.setUserProfile(userProfile);

        // While the user is saved, then it should throw an exception
        assertThatThrownBy(() -> underTest.save(user))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("not-null property references a null or transient value : pl.mswierczewski.socialwall.components.models.SocialWallUser.email");
    }

    @Test
    void itShouldNotSaveSocialWallUserWhenUserProfileIsNull() {
        // Given
        // ... user object
        SocialWallUser user = new SocialWallUser("User", "user1234", "user@email.com", SocialWallUserRole.DEFAULT_USER);

        // ... user profile info
        user.setUserProfile(null);

        // While the user is saved, then it should throw an exception
        assertThatThrownBy(() -> underTest.save(user))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("not-null property references a null or transient value : pl.mswierczewski.socialwall.components.models.SocialWallUser.userProfile");
    }

}