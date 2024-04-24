package abdulgazizov.dev.cloudstoragedemo.entity;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class MyFile {
    private MultipartFile multipartFile;
}
