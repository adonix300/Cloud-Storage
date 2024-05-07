package abdulgazizov.dev.cloudstoragedemo.filter;

import abdulgazizov.dev.cloudstoragedemo.jwt.JwtAuthentication;
import abdulgazizov.dev.cloudstoragedemo.jwt.JwtProvider;
import abdulgazizov.dev.cloudstoragedemo.jwt.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends GenericFilterBean {
    private static final String AUTHORIZATION = "Auth-Token";
    private final JwtProvider jwtProvider;
    private final JwtUtils jwtUtils;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        log.info("Processing request in JwtFilter");

        final String token = getTokenFromRequest((HttpServletRequest) servletRequest);
        log.debug("Extracted token from request: {}", token);

        if (token != null) {
            log.debug("Validating access token: {}", token);
            if (jwtProvider.validateAccessToken(token)) {
                log.info("Access token is valid");

                final Claims claims = jwtProvider.getAccessClaims(token);
                log.debug("Extracted claims from token: {}", claims);

                final JwtAuthentication jwtInfoToken = jwtUtils.generate(claims);
                jwtInfoToken.setAuthenticated(true);
                log.info("Authenticated user: {}", jwtInfoToken.getPrincipal());

                SecurityContextHolder.getContext().setAuthentication(jwtInfoToken);
            } else {
                log.warn("Invalid access token: {}", token);
            }
        } else {
            log.debug("No token found in request");
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        final String bearer = request.getHeader(AUTHORIZATION);
        log.debug("Extracting token from header: {}", bearer);

        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}
