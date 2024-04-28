package abdulgazizov.dev.cloudstoragedemo.properties;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public record JwtProperties(
        @NotEmpty
        @Value("${jwt.secret.access}")
        String jwtAccessSecret,

        @NotEmpty
        @Value("${jwt.secret.refresh}")
        String jwtRefreshSecret) {
}
