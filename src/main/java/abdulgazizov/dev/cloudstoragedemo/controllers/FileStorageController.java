package abdulgazizov.dev.cloudstoragedemo.controllers;

import abdulgazizov.dev.cloudstoragedemo.services.FileStorageService;
import abdulgazizov.dev.cloudstoragedemo.services.impl.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping("upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("fileName") String customFileName) {
        if (file.isEmpty()) {
            log.error("No file provided");
            return ResponseEntity.badRequest().body("No file provided");
        }
        Long userId = authService.getJwtAuthentication().getId();
        String fileName = fileStorageService.upload(file, userId, customFileName);
        log.info("File uploaded successfully: {}", fileName);
        return ResponseEntity.ok("File uploaded successfully: " + fileName);
    }


    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("download/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) throws IOException {
        Resource file = fileStorageService.download(fileName);
        log.info("File downloaded successfully: {}", fileName);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(file);
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @DeleteMapping("delete/{fileName}")
    public ResponseEntity<String> deleteFile(@PathVariable String fileName) throws IOException {
        Long id = authService.getJwtAuthentication().getId();
        fileStorageService.delete(fileName, id);
        return ResponseEntity.ok("File deleted successfully");
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping("edit/")
    public ResponseEntity<String> editFileName(@RequestParam("oldFileName") String oldFileName, @RequestParam("newFileName") String newFileName) throws IOException {
        Long id = authService.getJwtAuthentication().getId();

        fileStorageService.editFileName(oldFileName, newFileName, id);
        return ResponseEntity.ok("File edited successfully");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("{id}/edit/")
    public ResponseEntity<String> editFileName(@PathVariable Long id, @RequestParam("oldFileName") String oldFileName, @RequestParam("newFileName") String newFileName) throws IOException {
        fileStorageService.editFileName(oldFileName, newFileName, id);
        return ResponseEntity.ok("File edited successfully");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("{id}/delete/{fileName}")
    public ResponseEntity<String> deleteFile(@PathVariable Long id, @PathVariable String fileName) throws IOException {
        fileStorageService.delete(fileName, id);
        return ResponseEntity.ok("File deleted successfully");
    }
}
