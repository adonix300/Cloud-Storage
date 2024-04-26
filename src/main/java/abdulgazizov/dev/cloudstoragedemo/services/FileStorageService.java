package abdulgazizov.dev.cloudstoragedemo.services;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileStorageService {
    String upload(MultipartFile file, Long id, String fileName);
    Resource download(String filename) throws IOException;
    void delete(String fileName, Long id) throws IOException;
    void editFileName(String oldFileName, String newFileName, Long id) throws IOException;
}
