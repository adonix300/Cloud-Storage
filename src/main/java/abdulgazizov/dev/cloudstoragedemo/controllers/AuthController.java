package abdulgazizov.dev.cloudstoragedemo.controllers;

import abdulgazizov.dev.cloudstoragedemo.dtos.JwtRequest;
import abdulgazizov.dev.cloudstoragedemo.dtos.RefreshJwtRequest;
import abdulgazizov.dev.cloudstoragedemo.responses.JwtResponse;
import abdulgazizov.dev.cloudstoragedemo.services.impl.AuthService;
import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("login")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest jwtRequest) throws AuthException {
        final JwtResponse token = authService.login(jwtRequest);
        return ResponseEntity.ok(token);
    }

    @PostMapping("token")
    public ResponseEntity<JwtResponse> getNewAccessToken(@RequestBody RefreshJwtRequest jwtRequest) {
        final JwtResponse token = authService.getAccessToken(jwtRequest.getRefreshToken());
        return ResponseEntity.ok(token);
    }

    @PostMapping("refresh")
    public ResponseEntity<JwtResponse> refresh(@RequestBody RefreshJwtRequest jwtRequest) throws AuthException {
        final JwtResponse token = authService.refresh(jwtRequest.getRefreshToken());
        return ResponseEntity.ok(token);
    }
}
