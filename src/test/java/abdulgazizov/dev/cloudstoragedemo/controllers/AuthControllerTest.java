package abdulgazizov.dev.cloudstoragedemo.controllers;

import abdulgazizov.dev.cloudstoragedemo.dtos.JwtRequest;
import abdulgazizov.dev.cloudstoragedemo.responses.JwtResponse;
import abdulgazizov.dev.cloudstoragedemo.services.AuthService;
import jakarta.security.auth.message.AuthException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {
    @Mock
    private AuthService authService;
    @InjectMocks
    private AuthController authController;

    @Test
    @DisplayName("Логин: успешно")
    void login_success() throws AuthException {
        //given
        JwtRequest jwtRequest = new JwtRequest("username", "password");
        JwtResponse jwtResponse = new JwtResponse("access_token", "refresh_token");

        when(authService.login(jwtRequest)).thenReturn(jwtResponse);
        //when
        var response = authController.login(jwtRequest);

        //then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(jwtResponse, response.getBody());
    }

    @Test
    @DisplayName("Логин: ошибка аутентификации")
    void login_AuthenticationFailed() throws AuthException {
        //given
        JwtRequest jwtRequest = new JwtRequest("username", "wrongpassword");
        when(authService.login(jwtRequest)).thenThrow(new AuthException("Authentication failed"));

        //when
        AuthException exception = assertThrows(AuthException.class,
                () -> authController.login(jwtRequest));

        //then
        assertNotNull(exception);
        assertEquals("Authentication failed", exception.getMessage());
    }

    @Test
    @DisplayName("Логаут: успешно")
    void logout_Success() throws Exception {
        //given
        doNothing().when(authService).logout();

        //when
        var response = authController.logout();

        //then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(authService).logout();
    }


    @Test
    @DisplayName("Получение нового access токена: успешно")
    void getNewAccessToken_Success() throws AuthException {
        //given
        String refreshToken = "refresh_token";
        JwtResponse jwtResponse = new JwtResponse("new_access_token", null);
        when(authService.getAccessToken(refreshToken)).thenReturn(jwtResponse);

        //when
        var response = authController.getNewAccessToken(refreshToken);

        //then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(jwtResponse, response.getBody());
    }

    @Test
    @DisplayName("Получение нового access токена: токен просрочен")
    void getNewAccessToken_TokenExpired() throws AuthException {
        //given
        String refreshToken = "expired_refresh_token";
        when(authService.getAccessToken(refreshToken)).thenThrow(new AuthException("Token expired"));

        //when
        AuthException exception = assertThrows(AuthException.class,
                () -> authController.getNewAccessToken(refreshToken));

        //then
        assertNotNull(exception);
        assertEquals("Token expired", exception.getMessage());
    }

    @Test
    @DisplayName("Обновление токенов: успешно")
    void refresh_Success() throws Exception {
        //given
        String refreshToken = "refresh_token";
        JwtResponse jwtResponse = new JwtResponse("new_access_token", "new_refresh_token");
        when(authService.refresh(refreshToken)).thenReturn(jwtResponse);

        //when
        var response = authController.refresh(refreshToken);

        //then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(jwtResponse, response.getBody());
    }

    @Test
    @DisplayName("Обновление токенов: ошибка обновления")
    void refresh_Failure() throws AuthException {
        //given
        String refreshToken = "refresh_token";
        when(authService.refresh(refreshToken)).thenThrow(new AuthException("JWT tokens is not valid"));

        //when
        AuthException exception = assertThrows(AuthException.class,
                () -> authController.refresh(refreshToken));

        //then
        assertNotNull(exception);
        assertEquals("JWT tokens is not valid", exception.getMessage());
    }

}