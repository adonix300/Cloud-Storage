package abdulgazizov.dev.cloudstoragedemo.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FileNameDto {
    @NotBlank
    @Size(min = 1, max = 255)
    @JsonProperty("filename")
    private String fileName;
}
