package abdulgazizov.dev.cloudstoragedemo.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponse {
    private final String type = "Bearer";
    @JsonProperty("auth-token")
    private final String accessToken;
    @JsonProperty("refresh-token")
    private final String refreshToken;
}
