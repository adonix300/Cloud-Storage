package abdulgazizov.dev.cloudstoragedemo.controllers;

import abdulgazizov.dev.cloudstoragedemo.services.FileStorageService;
import abdulgazizov.dev.cloudstoragedemo.services.impl.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileStorageController {
    private final FileStorageService fileStorageService;
    private final AuthService authService;

    @PostMapping("upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            log.error("No file provided");
            return ResponseEntity.badRequest().body("No file provided");
        }
        try {
            Long userId = authService.getJwtAuthentication().getId();
            String fileName = fileStorageService.upload(file, userId);
            log.info("File uploaded successfully: {}", fileName);
            return ResponseEntity.ok("File uploaded successfully: " + fileName);
        } catch (Exception e) {
            log.error("Upload failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed");
        }
    }

    @GetMapping("download/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        try {
            Resource file = fileStorageService.download(fileName);
            log.info("File downloaded successfully: {}", fileName);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .body(file);
        } catch (IOException e) {
            log.error("Error downloading file: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("delete/{fileName}")
    public ResponseEntity<String> deleteFile(@PathVariable String fileName) {
        Long userId = authService.getJwtAuthentication().getId();
        try {
            fileStorageService.delete(fileName, userId);
            return ResponseEntity.ok("File deleted successfully");
        } catch (IOException e) {
            log.error("Failed to delete file: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Failed to delete file: " + e.getMessage());
        }
    }

    @PostMapping("edit/")
    public ResponseEntity<String> editFileName(@RequestParam("oldFileName") String oldFileName, @RequestParam("newFileName") String newFileName){
        Long userId = authService.getJwtAuthentication().getId();
        try {
            fileStorageService.editFileName(oldFileName, newFileName, userId);
            return ResponseEntity.ok("File edited successfully");
        } catch (IOException e) {
            log.error("Failed to edit file: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Failed to edit file: " + e.getMessage());
        }
    }
}
