package abdulgazizov.dev.cloudstoragedemo.services;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    void upload(Long id, MultipartFile file);
}
