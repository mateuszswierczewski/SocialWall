package pl.mswierczewski.socialwall.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.mswierczewski.socialwall.dtos.SignInRequest;
import pl.mswierczewski.socialwall.dtos.SignOutRequest;
import pl.mswierczewski.socialwall.dtos.SignUpRequest;
import pl.mswierczewski.socialwall.security.AuthService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

import static pl.mswierczewski.socialwall.security.jwt.JwtConfig.AUTHORIZATION_HEADER;
import static pl.mswierczewski.socialwall.security.jwt.JwtConfig.AUTHORIZATION_PREFIX;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signUp")
    public ResponseEntity<String> signUp(@Valid @RequestBody SignUpRequest request){
        authService.signUp(request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("User Registration Successful");
    }

    @PostMapping("/signIn")
    public ResponseEntity<String> signIn(@Valid @RequestBody SignInRequest request, HttpServletRequest httpRequest){
        String token = authService.signIn(request, httpRequest);

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(AUTHORIZATION_HEADER, AUTHORIZATION_PREFIX + token)
                .body("Authorization successful");
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
        System.out.println("tut");
        authService.activateAccount(token);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Account activated!");
    }
}
