package abdulgazizov.dev.cloudstoragedemo.services;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileStorageService {
    String upload(MultipartFile file, Long id);
    Resource download(String filename) throws IOException;
    void delete(String filename) throws IOException;
    void editFileName(String filename, String newFileName) throws IOException;
}
