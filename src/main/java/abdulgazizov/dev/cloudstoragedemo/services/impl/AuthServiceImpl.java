package abdulgazizov.dev.cloudstoragedemo.services.impl;

import abdulgazizov.dev.cloudstoragedemo.dtos.JwtRequest;
import abdulgazizov.dev.cloudstoragedemo.entity.User;
import abdulgazizov.dev.cloudstoragedemo.jwt.JwtAuthentication;
import abdulgazizov.dev.cloudstoragedemo.jwt.JwtProvider;
import abdulgazizov.dev.cloudstoragedemo.responses.JwtResponse;
import abdulgazizov.dev.cloudstoragedemo.services.AuthService;
import abdulgazizov.dev.cloudstoragedemo.services.UserService;
import io.jsonwebtoken.Claims;
import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserService userService;
    private final Map<String, String> refreshStorage = new ConcurrentHashMap<>();
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    public JwtResponse login(JwtRequest request) throws AuthException {
        log.debug("Received login request: {}", request);
        final User user = userService.getByUsername(request.getLogin());

        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.info("User authenticated successfully");
            final String accessToken = jwtProvider.generateAccessToken(user);
            final String refreshToken = jwtProvider.generateRefreshToken(user);
            refreshStorage.put(user.getUsername(), refreshToken);
            log.debug("Generated access token: {}", accessToken);
            log.debug("Generated refresh token: {}", refreshToken);
            return new JwtResponse(accessToken, refreshToken);
        } else {
            log.warn("Invalid password for user {}", request.getLogin());
            throw new AuthException("Password is wrong");
        }
    }

    public void logout() throws AuthException {
        log.debug("Received logout request");
        String username = getJwtAuthentication().getUsername();
        refreshStorage.remove(username);
        log.info("Refresh token removed from storage");
    }

    public JwtResponse getAccessToken(String refreshToken) throws AuthException {
        log.debug("Received request for new access token: {}", refreshToken);
        refreshToken = refreshToken.substring(7);
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            log.info("Refresh token is valid");
            final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            final String username = claims.getSubject();
            final String saveRefreshToken = refreshStorage.get(username);
            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                log.info("Generating new access token for user {}", username);
                final User user = userService.getByUsername(username);
                final String accessToken = jwtProvider.generateAccessToken(user);
                log.debug("Generated new access token: {}", accessToken);
                return new JwtResponse(accessToken, null);
            }
        } else {
            log.warn("Token expired {}", refreshToken);
            throw new AuthException("Token expired");
        }
        log.warn("Invalid refresh token: {}", refreshToken);
        return new JwtResponse(null, null);
    }

    public JwtResponse refresh(String refreshToken) throws AuthException {
        log.debug("Received refresh token request: {}", refreshToken);
        refreshToken = refreshToken.substring(7);
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            log.info("Refresh token is valid");
            final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            final String username = claims.getSubject();
            final String saveRefreshToken = refreshStorage.get(username);
            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                log.info("Generating new access token and refresh token for user {}", username);
                final User user = userService.getByUsername(username);
                final String accessToken = jwtProvider.generateAccessToken(user);
                final String newRefreshToken = jwtProvider.generateRefreshToken(user);
                refreshStorage.put(username, newRefreshToken);
                log.debug("Generated new access token: {}", accessToken);
                log.debug("Generated new refresh token: {}", newRefreshToken);
                return new JwtResponse(accessToken, newRefreshToken);
            }
        }
        log.warn("Invalid refresh token: {}", refreshToken);
        throw new AuthException("JWT tokens is not valid");
    }

    public JwtAuthentication getJwtAuthentication() {
        log.debug("Getting JWT authentication");
        return (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
    }
}
