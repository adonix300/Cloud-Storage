package abdulgazizov.dev.cloudstoragedemo.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtRequest {
    @NotBlank(message = "Login can't be null")
    private String login;
    @NotBlank(message = "Password can't be null")
    private String password;
}
