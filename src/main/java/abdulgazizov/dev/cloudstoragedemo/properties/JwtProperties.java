package abdulgazizov.dev.cloudstoragedemo.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public record JwtProperties(@Value("${jwt.secret.access}") String jwtAccessSecret,
                            @Value("${jwt.secret.refresh}") String jwtRefreshSecret) {
}
