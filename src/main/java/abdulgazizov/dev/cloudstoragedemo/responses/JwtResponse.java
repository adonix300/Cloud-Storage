package abdulgazizov.dev.cloudstoragedemo.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class JwtResponse implements Serializable{
    private final String type = "Bearer";
    private final String accessToken;
    private final String refreshToken;
}
