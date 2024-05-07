package abdulgazizov.dev.cloudstoragedemo.filter;

import abdulgazizov.dev.cloudstoragedemo.jwt.JwtAuthentication;
import abdulgazizov.dev.cloudstoragedemo.jwt.JwtProvider;
import abdulgazizov.dev.cloudstoragedemo.jwt.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {

    @Mock
    private JwtProvider jwtProvider;


//    @Mock
//    private JwtAuthentication jwtAuthentication;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private JwtFilter jwtFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = mock(FilterChain.class);
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Аутентификация и обработка запроса с действительным токеном")
    void doFilter_testShouldAuthenticateWithValidToken() throws IOException, ServletException {
        //given
        String token = "validToken";
        String username = "testUser";
        Claims claims = Jwts.claims().setSubject(username);

        List<String> roles = List.of("ROLE_ADMIN");
        claims.put("roles", roles);

        JwtAuthentication jwtInfoToken = new JwtAuthentication();
        jwtInfoToken.setAuthenticated(true);
        jwtInfoToken.setUsername(username);

        request.addHeader("Auth-Token", "Bearer " + token);

        when(jwtProvider.validateAccessToken(token)).thenReturn(true);
        when(jwtProvider.getAccessClaims(token)).thenReturn(claims);
        when(jwtUtils.generate(claims)).thenReturn(jwtInfoToken);

        //when
        jwtFilter.doFilter(request, response, filterChain);

        //then
        verify(jwtUtils).generate(claims);
        verify(filterChain).doFilter(request, response);
        assertTrue(jwtInfoToken.isAuthenticated());
    }

    @Test
    @DisplayName("Продолжение фильтрации без аутентификации при недействительном токене")
    void doFilter_testShouldNotAuthenticateWithInvalidToken() throws IOException, ServletException {
        //given
        String token = "invalidToken";

        request.addHeader("Auth-Token", "Bearer " + token);

        when(jwtProvider.validateAccessToken(token)).thenReturn(false);

        jwtFilter.doFilter(request, response, filterChain);

        verify(jwtProvider).validateAccessToken(token);
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Пропуск фильтра без аутентификации, если токен отсутствует")
    void shouldIgnoreRequestWithoutToken() throws IOException, ServletException {
        jwtFilter.doFilter(request, response, filterChain);

        verify(jwtProvider, never()).validateAccessToken(anyString());
        verify(filterChain).doFilter(request, response);
        assert(SecurityContextHolder.getContext().getAuthentication() == null);
    }
}