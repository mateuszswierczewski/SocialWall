package pl.mswierczewski.socialwall.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.mswierczewski.socialwall.components.services.SocialWallUserService;
import pl.mswierczewski.socialwall.dtos.TextResponse;
import pl.mswierczewski.socialwall.dtos.auth.SignInRequest;
import pl.mswierczewski.socialwall.dtos.auth.SignOutRequest;
import pl.mswierczewski.socialwall.dtos.auth.SignUpRequest;
import pl.mswierczewski.socialwall.security.AuthService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import static pl.mswierczewski.socialwall.security.jwt.JwtConfig.AUTHORIZATION_HEADER;
import static pl.mswierczewski.socialwall.security.jwt.JwtConfig.AUTHORIZATION_PREFIX;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final SocialWallUserService userService;

    public AuthController(AuthService authService, SocialWallUserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/signUp")
    public ResponseEntity<TextResponse> signUp(@Valid @RequestBody SignUpRequest request){
        authService.signUp(request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new TextResponse("User Registration Successful"));
    }

    @PostMapping("/signIn")
    public ResponseEntity<TextResponse> signIn(@Valid @RequestBody SignInRequest request, HttpServletRequest httpRequest){
        String token = authService.signIn(request, httpRequest);

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(AUTHORIZATION_HEADER, AUTHORIZATION_PREFIX + token)
                .body(new TextResponse("Authorization successful"));
    }

    @PostMapping("/signOut")
    public ResponseEntity<String> signOut(@RequestBody SignOutRequest request, HttpServletRequest httpRequest){
        authService.signOut(request, httpRequest);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Signed out successfully!");
    }

    @GetMapping("/activateAccount/{token}")
    public ResponseEntity<String> activateAccount(@PathVariable String token){
        authService.activateAccount(token);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Account activated!");
    }

    @GetMapping("/existsByUsername/{username}")
    public ResponseEntity<TextResponse> existsByUsername(@PathVariable("username") String username){
        boolean result = userService.existsByUsername(username);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new TextResponse(Boolean.toString(result)));
    }
}
