package abdulgazizov.dev.cloudstoragedemo.services;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String upload(MultipartFile file);
}
