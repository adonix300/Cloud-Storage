package abdulgazizov.dev.cloudstoragedemo.jwt;

import abdulgazizov.dev.cloudstoragedemo.entity.Role;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public final class JwtUtils {

    public JwtAuthentication generate(Claims claims) {
        log.debug("Generating JwtAuthentication from claims {}", claims);
        final JwtAuthentication jwtInfoToken = new JwtAuthentication();
        jwtInfoToken.setRoles(getRoles(claims));
        jwtInfoToken.setId(claims.get("id", Long.class));
        jwtInfoToken.setUsername(claims.getSubject());
        log.debug("Generated JwtAuthentication: {}", jwtInfoToken);
        return jwtInfoToken;
    }

    private static Set<Role> getRoles(Claims claims) {
        final List<String> roles = claims.get("roles", List.class);
        return roles.stream()
                .map(Role::valueOf)
                .collect(Collectors.toSet());
    }
}