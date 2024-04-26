package abdulgazizov.dev.cloudstoragedemo.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FileNameDto {
    @JsonProperty("filename")
    private String fileName;
}
