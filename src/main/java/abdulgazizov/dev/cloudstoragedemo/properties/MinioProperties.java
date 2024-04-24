package abdulgazizov.dev.cloudstoragedemo.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public record MinioProperties(

    @Value("${minio.bucket}")
    String bucketName,

    @Value("${minio.url}")
    String url,

    @Value("${minio.accessKey}")
    String accessKey,

    @Value("${minio.secretKey}")
    String secretKey){
}
