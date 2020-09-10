package pl.mswierczewski.socialwall.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.mswierczewski.socialwall.components.enums.Gender;
import pl.mswierczewski.socialwall.components.enums.SocialWallUserRole;
import pl.mswierczewski.socialwall.components.models.SocialWallUser;
import pl.mswierczewski.socialwall.components.models.VerificationToken;
import pl.mswierczewski.socialwall.components.services.SocialWallUserService;
import pl.mswierczewski.socialwall.components.services.VerificationTokenService;
import pl.mswierczewski.socialwall.dtos.SignInRequest;
import pl.mswierczewski.socialwall.dtos.SignUpRequest;
import pl.mswierczewski.socialwall.exceptions.UserAlreadyExistException;
import pl.mswierczewski.socialwall.mappers.UserMapper;
import pl.mswierczewski.socialwall.security.jwt.JwtTokenService;
import pl.mswierczewski.socialwall.utils.mail.MailService;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

class AuthServiceTest {

    @Mock
    private SocialWallUserService userService;

    @Mock
    private JwtTokenService jwtTokenService;

    @Mock
    private VerificationTokenService verificationTokenService;

    @Mock
    private MailService mailService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    private AuthService underTest;

    @Captor
    private ArgumentCaptor<SocialWallUser> userArgumentCaptor;

    @Captor
    private ArgumentCaptor<VerificationToken> verificationTokenArgumentCaptor;

    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        underTest = new AuthService(userService, jwtTokenService, verificationTokenService, mailService, userMapper, passwordEncoder, authenticationManager);
    }

    @Test
    void itShouldSignUpNewUser() {
        // Given
        // ... signup request
        SignUpRequest signUpRequest = new SignUpRequest(
                "user", "pass", "user@email.com",
                "John", "Smith", LocalDate.of(1990, 1, 1), Gender.MALE);

        // ... false when it checks if username exists in db
        given(userService.existsByUsername(signUpRequest.getUsername())).willReturn(false);

        // ... false when it checks if email exists in db
        given(userService.existsByUsername(signUpRequest.getEmail())).willReturn(false);

        // ... user
        SocialWallUser user = new SocialWallUser("user", "pass", "user@email.com", SocialWallUserRole.DEFAULT_USER);
        user.setId(UUID.randomUUID().toString());
        given(userMapper.mapSignUpRequestToUser(signUpRequest, SocialWallUserRole.DEFAULT_USER, passwordEncoder))
                .willReturn(user);

        // ... verification token;
        VerificationToken verificationToken = new VerificationToken();
        given(verificationTokenService.generateVerificationToken(user))
                .willReturn(verificationToken);

        // When
        when(userService.save(user)).thenReturn(user);

        underTest.signUp(signUpRequest);

        // Then
        then(mailService).should().sendVerificationEmail(userArgumentCaptor.capture(), verificationTokenArgumentCaptor.capture());

        assertThat(userArgumentCaptor.getValue()).isEqualTo(user);
        assertThat(verificationTokenArgumentCaptor.getValue()).isEqualTo(verificationToken);
    }

    @Test
    void itShouldNotSignUpNewUserAndThrowExceptionWhenUsernameIsAlreadyTaken() {
        // Given
        // ... signup request
        SignUpRequest signUpRequest = new SignUpRequest(
                "user", "pass", "user@email.com",
                "John", "Smith", LocalDate.of(1990, 1, 1), Gender.MALE);

        // ... true when it checks if username exists in db
        given(userService.existsByUsername(signUpRequest.getUsername())).willReturn(true);

        // Then should throw exception and does not save user
        assertThatThrownBy(() -> underTest.signUp(signUpRequest))
                .isInstanceOf(UserAlreadyExistException.class)
                .hasMessageContaining("Username is already taken!");

        then(userMapper).shouldHaveNoInteractions();
        then(userService).should(never()).save(any());
        then(verificationTokenService).shouldHaveNoInteractions();
    }

    @Test
    void itShouldNotSignUpNewUserAndThrowExceptionWhenEmailIsAlreadyTaken() {
        // Given
        // ... signup request
        SignUpRequest signUpRequest = new SignUpRequest(
                "user", "pass", "user@email.com",
                "John", "Smith", LocalDate.of(1990, 1, 1), Gender.MALE);

        // ... false when it checks if username exists in db
        given(userService.existsByUsername(signUpRequest.getUsername())).willReturn(false);

        // ... true when it checks if email exists in db
        given(userService.existsByEmail(signUpRequest.getEmail())).willReturn(true);

        // Then should throw exception and does not save user
        assertThatThrownBy(() -> underTest.signUp(signUpRequest))
                .isInstanceOf(UserAlreadyExistException.class)
                .hasMessageContaining("Email is already taken!");

        then(userMapper).shouldHaveNoInteractions();
        then(userService).should(never()).save(any());
        then(verificationTokenService).shouldHaveNoInteractions();
    }

    @Test
    void itShouldSignIn() {
        // Given
        // ... SignIn request
        SignInRequest request = new SignInRequest("user", "pass");

        // ... successful authentication
        Authentication authentication = new UsernamePasswordAuthenticationToken("user", "pass");
        given(authenticationManager.authenticate(any())).willReturn(authentication);

        // ... fake jwt token
        given(jwtTokenService.generateJwtToken(any(), any())).willReturn("fake-token");

        // Then
        assertDoesNotThrow(() -> underTest.signIn(request, null));

    }

    //TODO: write the rest sign in and sign out tests


    @Test
    void itShouldActivateAccount() {
        // Given
        // ... token
        String token = UUID.randomUUID().toString() + "-" + UUID.randomUUID().toString();

        // ... user
        SocialWallUser user = new SocialWallUser("User", "pass", "user@email.com", SocialWallUserRole.DEFAULT_USER);
        given(verificationTokenService.getUserByVerificationTokenId(token)).willReturn(user);

        // When
        underTest.activateAccount(token);

        // Then
        then(userService).should().save(userArgumentCaptor.capture());
        then(verificationTokenService).should().remove(stringArgumentCaptor.capture());

        assertThat(userArgumentCaptor.getValue().isEnabled()).isTrue();
        assertThat(stringArgumentCaptor.getValue()).isEqualTo(token);
    }
}