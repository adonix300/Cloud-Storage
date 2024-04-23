package abdulgazizov.dev.cloudstoragedemo.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponse {
    private final String type = "Bearer";
    private final String accessToken;
    private final String refreshToken;
}
