package abdulgazizov.dev.cloudstoragedemo.services;

import abdulgazizov.dev.cloudstoragedemo.dtos.JwtRequest;
import abdulgazizov.dev.cloudstoragedemo.jwt.JwtAuthentication;
import abdulgazizov.dev.cloudstoragedemo.responses.JwtResponse;
import jakarta.security.auth.message.AuthException;
import org.apache.coyote.BadRequestException;

public interface AuthService {
    JwtResponse login(JwtRequest request) throws BadRequestException;

    void logout(String refreshToken);

    JwtResponse getAccessToken(String refreshToken);

    JwtResponse refresh(String refreshToken) throws AuthException;

    JwtAuthentication getJwtAuthentication();
}
