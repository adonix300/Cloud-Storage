package abdulgazizov.dev.cloudstoragedemo.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class JwtResponse implements Serializable{
    private final String type = "Bearer";
    @JsonProperty("auth-token")
    private final String accessToken;
    @JsonProperty("refresh-token")
    private final String refreshToken;
}
