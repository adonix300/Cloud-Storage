package abdulgazizov.dev.cloudstoragedemo.services;

import abdulgazizov.dev.cloudstoragedemo.dtos.FileDto;
import org.apache.coyote.BadRequestException;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileStorageService {
    String upload(MultipartFile file, String fileName);
    Resource download(String filename) throws IOException;
    void delete(String fileName) throws IOException;
    void editFileName(String newFileName, String oldFileName) throws IOException;
    List<FileDto> getFiles(int limit) throws BadRequestException;
}
