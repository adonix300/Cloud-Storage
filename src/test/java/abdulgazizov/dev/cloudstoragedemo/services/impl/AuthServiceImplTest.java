package abdulgazizov.dev.cloudstoragedemo.services.impl;

import abdulgazizov.dev.cloudstoragedemo.dtos.JwtRequest;
import abdulgazizov.dev.cloudstoragedemo.entity.User;
import abdulgazizov.dev.cloudstoragedemo.jwt.JwtProvider;
import abdulgazizov.dev.cloudstoragedemo.responses.JwtResponse;
import abdulgazizov.dev.cloudstoragedemo.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.security.auth.message.AuthException;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {
    @Mock
    private UserService userService;
    @Mock
    private JwtProvider jwtProvider;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private AuthServiceImpl authService;

    private User user;
    private String refreshToken;
    private String accessToken;


    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("username");
        user.setPassword("password");

        refreshToken = "validRefreshToken";
        accessToken = "accessToken";
    }

    @Test
    @DisplayName("Аутентификация: Успешный сценарий")
    void login_testLoginSuccess() throws AuthException {
        //given
        JwtRequest jwtRequest = new JwtRequest(user.getUsername(), user.getPassword());

        when(userService.getByUsername(jwtRequest.getLogin())).thenReturn(user);
        when(passwordEncoder.matches(jwtRequest.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtProvider.generateAccessToken(user)).thenReturn(accessToken);
        when(jwtProvider.generateRefreshToken(user)).thenReturn(refreshToken);
        //when
        JwtResponse jwtResponse = authService.login(jwtRequest);
        //then
        assertNotNull(jwtResponse);
        assertEquals(accessToken, jwtResponse.getAccessToken());
        assertEquals(refreshToken, jwtResponse.getRefreshToken());
    }

    @Test
    @DisplayName("Аутентификация: Неверный пароль")
    void login_testWrongPasswordShouldThrowBadRequestException() throws AuthException {
        //given
        JwtRequest jwtRequest = new JwtRequest(user.getUsername(), "wrongPassword");

        when(userService.getByUsername(jwtRequest.getLogin())).thenReturn(user);
        when(passwordEncoder.matches(jwtRequest.getPassword(), user.getPassword())).thenReturn(false);

        //when
        AuthException thrown = assertThrows(AuthException.class, () -> authService.login(jwtRequest));

        //then
        assertTrue(thrown.getMessage().contains("Password is wrong"));
        verify(userService).getByUsername(jwtRequest.getLogin());
        verify(passwordEncoder).matches(jwtRequest.getPassword(), user.getPassword());
        verifyNoInteractions(jwtProvider);
    }

    @DisplayName("Аутентификация: Пользователя не существует")
    @Test
    void login_testUnExistsLoginShouldThrowEntityNotFoundException() {
        //given

        JwtRequest jwtRequest = new JwtRequest("UnExistsLogin", user.getPassword());
        when(userService.getByUsername(jwtRequest.getLogin())).thenThrow(new EntityNotFoundException("User with username " + jwtRequest.getLogin() + " not found"));

        //when
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> authService.login(jwtRequest));

        //then
        assertTrue(thrown.getMessage().contains("User with username " + jwtRequest.getLogin() + " not found"));
        verify(userService).getByUsername(jwtRequest.getLogin());
        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(jwtProvider);
    }
}