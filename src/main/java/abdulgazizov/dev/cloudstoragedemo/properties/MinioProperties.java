package abdulgazizov.dev.cloudstoragedemo.properties;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public record MinioProperties(
        @NotEmpty
        @Value("${minio.bucket}")
        String bucketName,

        @NotEmpty
        @Value("${minio.url}")
        String url,

        @NotEmpty
        @Value("${minio.accessKey}")
        String accessKey,

        @NotEmpty
        @Value("${minio.secretKey}")
        String secretKey) {
}
