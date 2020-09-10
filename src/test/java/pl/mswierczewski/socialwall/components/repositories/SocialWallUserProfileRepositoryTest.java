package pl.mswierczewski.socialwall.components.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import pl.mswierczewski.socialwall.components.enums.Gender;
import pl.mswierczewski.socialwall.components.models.SocialWallUserProfile;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@DataJpaTest
@ActiveProfiles("test")
class SocialWallUserProfileRepositoryTest {

    @Autowired
    private SocialWallUserProfileRepository underTest;

    @Test
    void itShouldSaveSocialWallUserProfile() {
        // Given
        // ... user profile object
        SocialWallUserProfile profile = new SocialWallUserProfile("John", "Smith", LocalDate.of(1990, 1, 1), Gender.MALE);
        profile.setCity("Los Angeles");
        profile.setCountry("US");

        // When
        underTest.save(profile);

        // Then
        Optional<SocialWallUserProfile> userProfileAfterSave = underTest.findById(profile.getId());

        assertThat(userProfileAfterSave)
                .isPresent()
                .hasValueSatisfying(
                        userProfile -> assertThat(userProfile).isEqualTo(profile)
                );
    }

    @Test
    void itShouldNotSaveSocialWallUserProfileWhenFirstNameIsNull() {
        // Given
        // ... user profile object
        SocialWallUserProfile profile = new SocialWallUserProfile(null, "Smith", LocalDate.of(1990, 1, 1), Gender.MALE);
        profile.setCity("Los Angeles");
        profile.setCountry("US");

        // While the user profile is saved, then it should throw an exception
        assertThatThrownBy(() -> underTest.save(profile))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("not-null property references a null or transient value : pl.mswierczewski.socialwall.components.models.SocialWallUserProfile.firstName");

    }

    @Test
    void itShouldNotSaveSocialWallUserProfileWhenLastNameIsNull() {
        // Given
        // ... user profile object
        SocialWallUserProfile profile = new SocialWallUserProfile("John", null, LocalDate.of(1990, 1, 1), Gender.MALE);
        profile.setCity("Los Angeles");
        profile.setCountry("US");

        // While the user profile is saved, then it should throw an exception
        assertThatThrownBy(() -> underTest.save(profile))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("not-null property references a null or transient value : pl.mswierczewski.socialwall.components.models.SocialWallUserProfile.lastName");

    }

    @Test
    void itShouldNotSaveSocialWallUserProfileWhenBirthDateIsNull() {
        // Given
        // ... user profile object
        SocialWallUserProfile profile = new SocialWallUserProfile("John", "Smith", null, Gender.MALE);
        profile.setCity("Los Angeles");
        profile.setCountry("US");

        // While the user profile is saved, then it should throw an exception
        assertThatThrownBy(() -> underTest.save(profile))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("not-null property references a null or transient value : pl.mswierczewski.socialwall.components.models.SocialWallUserProfile.birthDate");

    }

    @Test
    void itShouldNotSaveSocialWallUserProfileWhenGenderIsNull() {
        // Given
        // ... user profile object
        SocialWallUserProfile profile = new SocialWallUserProfile("John", "Smith", LocalDate.of(1990, 1, 1), null);
        profile.setCity("Los Angeles");
        profile.setCountry("US");

        // While the user profile is saved, then it should throw an exception
        assertThatThrownBy(() -> underTest.save(profile))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("not-null property references a null or transient value : pl.mswierczewski.socialwall.components.models.SocialWallUserProfile.gender");

    }
}