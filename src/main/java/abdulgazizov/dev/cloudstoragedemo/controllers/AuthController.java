package abdulgazizov.dev.cloudstoragedemo.controllers;

import abdulgazizov.dev.cloudstoragedemo.dtos.JwtRequest;
import abdulgazizov.dev.cloudstoragedemo.responses.JwtResponse;
import abdulgazizov.dev.cloudstoragedemo.services.AuthService;
import jakarta.security.auth.message.AuthException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("login")
    public ResponseEntity<JwtResponse> login(@RequestBody @NonNull @Valid JwtRequest jwtRequest) throws BadRequestException {
        log.debug("Received login request: {}", jwtRequest);
        final JwtResponse token = authService.login(jwtRequest);
        log.info("User logged in successfully");
        return ResponseEntity.ok(token);
    }

    @PostMapping("logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Auth-Token") @NonNull String refreshToken) throws AuthException {
        log.debug("Received logout request with auth token: {}", refreshToken);
        authService.logout(refreshToken);
        log.info("User logged out successfully");
        return ResponseEntity.ok().build();
    }

    @PostMapping("token")
    public ResponseEntity<JwtResponse> getNewAccessToken(@RequestHeader(value = "Auth-Token") @NonNull String refreshToken) {
        log.debug("Received request for new access token: {}", refreshToken);
        final JwtResponse token = authService.getAccessToken(refreshToken);
        log.info("New access token issued");
        return ResponseEntity.ok(token);
    }

    @PostMapping("refresh")
    public ResponseEntity<JwtResponse> refresh(@RequestHeader(value = "Auth-Token") @NonNull String refreshToken) throws AuthException {
        log.debug("Received refresh token request: {}", refreshToken);
        final JwtResponse token = authService.refresh(refreshToken);
        log.info("Refresh token issued");
        return ResponseEntity.ok(token);
    }
}
