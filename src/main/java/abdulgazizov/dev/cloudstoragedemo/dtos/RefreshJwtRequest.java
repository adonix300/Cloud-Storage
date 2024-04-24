package abdulgazizov.dev.cloudstoragedemo.dtos;

import lombok.Data;

import java.io.Serializable;

@Data
public class RefreshJwtRequest implements Serializable {
    public String refreshToken;
}
