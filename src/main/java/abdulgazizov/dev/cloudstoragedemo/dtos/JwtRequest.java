package abdulgazizov.dev.cloudstoragedemo.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

@Data
public class JwtRequest implements Serializable {
    @NotBlank(message = "Login can't be null")
    private String login;
    @NotBlank(message = "Password can't be null")
    private String password;
}
