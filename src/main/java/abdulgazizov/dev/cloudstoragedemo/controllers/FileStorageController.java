package abdulgazizov.dev.cloudstoragedemo.controllers;

import abdulgazizov.dev.cloudstoragedemo.dtos.FileDto;
import abdulgazizov.dev.cloudstoragedemo.dtos.FileNameDto;
import abdulgazizov.dev.cloudstoragedemo.services.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FileStorageController {
    private final FileStorageService fileStorageService;

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping("file")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("filename") String fileName) {
        if (file.isEmpty()) {
            log.error("No file provided");
            return ResponseEntity.badRequest().body("No file provided");
        }
        String customFileName = fileStorageService.upload(file, fileName);
        log.info("File uploaded successfully: {}", fileName);
        return ResponseEntity.ok("File uploaded successfully: " + customFileName);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("list")
    public ResponseEntity<List<FileDto>> listFiles(@RequestParam("limit") int limit) throws BadRequestException {
        List<FileDto> resources = fileStorageService.getFiles(limit);
        return ResponseEntity.ok().body(resources);
    }


    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("file")
    public ResponseEntity<Resource> downloadFile(@RequestParam("filename") String fileName) throws IOException {
        Resource file = fileStorageService.download(fileName);
        log.info("File downloaded successfully: {}", fileName);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(file);
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @DeleteMapping("file")
    public ResponseEntity<String> deleteFile(@RequestParam("filename") String fileName) throws IOException {
        fileStorageService.delete(fileName);
        return ResponseEntity.ok("File deleted successfully");
    }


    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PutMapping("file")
    public ResponseEntity<String> editFileName(@RequestParam("filename") String oldfileName, @RequestBody FileNameDto fileNameDto) throws IOException {
        fileStorageService.editFileName(fileNameDto.getFileName(), oldfileName);
        return ResponseEntity.ok("File edited successfully");
    }
}
