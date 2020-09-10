package pl.mswierczewski.socialwall.mappers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.mswierczewski.socialwall.components.enums.Gender;
import pl.mswierczewski.socialwall.components.enums.SocialWallUserRole;
import pl.mswierczewski.socialwall.components.models.SocialWallUser;
import pl.mswierczewski.socialwall.components.models.SocialWallUserProfile;
import pl.mswierczewski.socialwall.dtos.SignUpRequest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {
    
    UserMapper underTest;

    PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        underTest = new UserMapperImpl();
        passwordEncoder = new BCryptPasswordEncoder();
    }

    @Test
    void itShouldMapSignUpRequestToUser() {
        // Given
        // ... sign up request
        SignUpRequest request = new SignUpRequest("user", "pass", "user@email.com", "John", "Smith", LocalDate.of(1990, 1, 1), Gender.MALE);

        // ... user profile
        UserMapper spy = Mockito.spy(underTest);
        Mockito.doReturn(new SocialWallUserProfile()).when(spy).mapSignUpRequestToUserProfile(request);

        // When
        SocialWallUser user = underTest.mapSignUpRequestToUser(request, SocialWallUserRole.DEFAULT_USER, passwordEncoder);

        // Then
        assertThat(user).isNotNull();
        assertThat(user.getUsername()).isEqualTo(request.getUsername());
        assertThat(passwordEncoder.matches(request.getPassword(), user.getPassword())).isTrue();
        assertThat(user.getEmail()).isEqualTo(request.getEmail());
        assertThat(user.getAuthorities().toArray()).containsOnly(new SimpleGrantedAuthority(SocialWallUserRole.DEFAULT_USER.name()));
    }

    @Test
    void itShouldMapSignUpRequestToUserProfile() {
        // Given
        // ... sign up request
        SignUpRequest request = new SignUpRequest("user", "pass", "user@email.com", "John", "Smith", LocalDate.of(1990, 1, 1), Gender.MALE);

        // When
        SocialWallUserProfile userProfile = underTest.mapSignUpRequestToUserProfile(request);

        // Then
        assertThat(userProfile).isNotNull();
        assertThat(userProfile.getFirstName()).isEqualTo(request.getFirstName());
        assertThat(userProfile.getLastName()).isEqualTo(request.getLastName());
        assertThat(userProfile.getBirthDate()).isEqualTo(request.getBirthDate());
        assertThat(userProfile.getGender()).isEqualTo(request.getGender());
    }
}