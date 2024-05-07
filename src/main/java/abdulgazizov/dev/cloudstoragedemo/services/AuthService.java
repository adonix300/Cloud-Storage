package abdulgazizov.dev.cloudstoragedemo.services;

import abdulgazizov.dev.cloudstoragedemo.dtos.JwtRequest;
import abdulgazizov.dev.cloudstoragedemo.jwt.JwtAuthentication;
import abdulgazizov.dev.cloudstoragedemo.responses.JwtResponse;
import jakarta.security.auth.message.AuthException;

/**
 * Interface for managing authentication using JWT tokens.
 */
public interface AuthService {
    /**
     * Performs login and generates JWT tokens (access and refresh tokens).
     *
     * @param request an object containing the user's login and password
     * @return a JwtResponse object containing the generated access and refresh tokens
     * @throws AuthException if the password is incorrect
     */
    JwtResponse login(JwtRequest request) throws AuthException;

    /**
     * Performs logout by removing the refresh token from storage.
     *
     * @throws AuthException if the refresh token is invalid
     */
    void logout() throws AuthException;

    /**
     * Generates a new access token from the refresh token.
     *
     * @param refreshToken the refresh token to obtain a new access token
     * @return a JwtResponse object containing the new access token
     * @throws AuthException if the refresh token is invalid or expired
     */
    JwtResponse getAccessToken(String refreshToken) throws AuthException;

    /**
     * Refreshes the access and refresh tokens.
     *
     * @param refreshToken the current refresh token
     * @return a JwtResponse object containing the new access and refresh tokens
     * @throws AuthException if the refresh token is invalid
     */
    JwtResponse refresh(String refreshToken) throws AuthException;

    /**
     * Retrieves the JwtAuthentication from the security context.
     *
     * @return a JwtAuthentication object containing the JWT authentication details
     */
    JwtAuthentication getJwtAuthentication();
}
