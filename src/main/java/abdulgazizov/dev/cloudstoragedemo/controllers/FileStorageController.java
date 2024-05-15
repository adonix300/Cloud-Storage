package abdulgazizov.dev.cloudstoragedemo.controllers;

import abdulgazizov.dev.cloudstoragedemo.dtos.FileDto;
import abdulgazizov.dev.cloudstoragedemo.dtos.FileNameDto;
import abdulgazizov.dev.cloudstoragedemo.mappers.FileMapper;
import abdulgazizov.dev.cloudstoragedemo.services.FileStorageService;
import io.minio.messages.Item;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FileStorageController {
    private final FileStorageService fileStorageService;
    private final FileMapper fileMapper;

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping("file")
    public ResponseEntity<String> upload(@RequestParam("file") @NonNull MultipartFile file, @RequestParam("filename") @NonNull String fileName) {
        log.debug("Received file upload request: file={}, filename={}", file.getOriginalFilename(), fileName);
        if (file.isEmpty()) {
            log.debug("No file provided");
            return ResponseEntity.badRequest().body("No file provided");
        }
        String customFileName = fileStorageService.upload(file, fileName);
        log.info("File uploaded successfully: {}", customFileName);
        return ResponseEntity.ok("File uploaded successfully: " + customFileName);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("list")
    public ResponseEntity<List<FileDto>> getFiles(@RequestParam("limit") @NonNull int limit) throws BadRequestException {
        log.debug("Received request to get files with limit={}", limit);
        List<FileDto> resources = fileStorageService.getFiles(limit).stream().map(FileMapper::toFileDto).collect(Collectors.toList());
        log.info("Files retrieved successfully");
        return ResponseEntity.ok().body(resources);
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("file")
    public ResponseEntity<Resource> download(@RequestParam("filename") @NonNull String fileName) throws IOException {
        log.debug("Received request to download file: {}", fileName);
        Resource file = fileStorageService.download(fileName);
        log.info("File downloaded successfully: {}", fileName);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(file);
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @DeleteMapping("file")
    public ResponseEntity<String> delete(@RequestParam("filename") @NonNull String fileName) throws IOException {
        log.debug("Received request to delete file: {}", fileName);
        fileStorageService.delete(fileName);
        log.info("File deleted successfully: {}", fileName);
        return ResponseEntity.ok("File deleted successfully");
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PutMapping("file")
    public ResponseEntity<String> edit(@RequestParam("filename") @NonNull String oldFileName, @RequestBody @Valid FileNameDto fileNameDto) throws IOException {
        log.debug("Received request to edit file: oldFilename={}, newFilename={}", oldFileName, fileNameDto.getFileName());
        fileStorageService.editFileName(fileNameDto.getFileName(), oldFileName);
        log.info("File edited successfully: oldFilename={}, newFilename={}", oldFileName, fileNameDto.getFileName());
        return ResponseEntity.ok("File edited successfully");
    }
}
