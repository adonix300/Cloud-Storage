package abdulgazizov.dev.cloudstoragedemo.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileDto {
    @NotBlank(message = "Filename can't be null")
    @Size(min = 1, max = 255, message = "Filename length must be between 1 and 255 characters.")
    @JsonProperty("filename")
    private String fileName;

    @NotBlank(message = "File type cannot be null.")
    @Size(min = 1, max = 50, message = "File type must be between 1 and 50 characters.")
    private String fileType;

    private Long editedAt;

    @NotNull(message = "Size cannot be null.")
    @Min(value = 1, message = "Size must be at least 1 byte.")
    private Long size;
}
