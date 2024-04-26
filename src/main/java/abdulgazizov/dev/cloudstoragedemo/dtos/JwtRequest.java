package abdulgazizov.dev.cloudstoragedemo.dtos;

import lombok.Data;

import java.io.Serializable;

@Data
public class JwtRequest implements Serializable {
    private String login;
    private String password;
}
