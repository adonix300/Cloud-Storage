package abdulgazizov.dev.cloudstoragedemo.services.impl;

import abdulgazizov.dev.cloudstoragedemo.dtos.FileDto;
import abdulgazizov.dev.cloudstoragedemo.entity.User;
import abdulgazizov.dev.cloudstoragedemo.exceptions.FileUploadException;
import abdulgazizov.dev.cloudstoragedemo.properties.MinioProperties;
import abdulgazizov.dev.cloudstoragedemo.services.AuthService;
import abdulgazizov.dev.cloudstoragedemo.services.FileStorageService;
import abdulgazizov.dev.cloudstoragedemo.services.UserFileService;
import abdulgazizov.dev.cloudstoragedemo.services.UserService;
import io.minio.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.AccessDeniedException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {
    private final MinioClient minioClient;
    private final MinioProperties minioProperties;
    private final UserService userService;
    private final UserFileService userFileService;
    private final AuthService authService;

    @Override
    @Transactional
    public String upload(MultipartFile file, String fileName) {
        log.debug("Uploading file: {}, filename: {}", file.getOriginalFilename(), fileName);
        Long id = authService.getJwtAuthentication().getId();
        createBucket();

        if (file.isEmpty() || file.getOriginalFilename() == null) {
            log.warn("File is empty");
            throw new FileUploadException("File is empty");
        }

        if (Objects.isNull(fileName) || fileName.isEmpty()) {
            fileName = generateFileName(file);
        }

        InputStream inputStream;

        try {
            inputStream = file.getInputStream();
        } catch (IOException e) {
            log.error("Error reading file input stream: {}", e.getMessage(), e);
            throw new FileUploadException("File upload failed: " + e.getMessage());
        }

        saveFile(inputStream, fileName);
        userFileService.addFileToUser(id, fileName);
        log.info("File uploaded successfully: {}", fileName);
        return fileName;
    }

    @Override
    @Transactional(readOnly = true)
    @SneakyThrows
    public List<FileDto> getFiles(int limit) throws BadRequestException {
        log.debug("Getting files with limit: {}", limit);
        Long id = authService.getJwtAuthentication().getId();
        User user = userService.getById(id);

        if (limit <= 0) {
            log.warn("Limit must be greater than 0, received: {}", limit);
            throw new BadRequestException("Limit must be greater than 0");
        }
        List<FileDto> files = new ArrayList<>();
        try {
            Iterable<Result<Item>> items = minioClient.listObjects(ListObjectsArgs.builder()
                    .bucket(minioProperties.bucketName())
                    .build());

            int count = 0;

            for (Result<Item> item : items) {
                if (count >= limit) {
                    break;
                }
                Item fileItem = item.get();
                ZonedDateTime lastModified = fileItem.lastModified();

                FileDto fileDto = FileDto.builder()
                        .fileName(fileItem.objectName())
                        .size(fileItem.size())
                        .fileType(determineFileType(fileItem.objectName()))
                        .editedAt(lastModified.toInstant().toEpochMilli())
                        .build();


                if (user.getFiles().contains(fileDto.getFileName())) {
                    files.add(fileDto);
                    count++;
                }
            }
        } catch (Exception e) {
            log.error("Error retrieving file list: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve files: " + e.getMessage(), e);
        }
        log.info("Files retrieved successfully, count: {}", files.size());
        return files;
    }

    private String determineFileType(String fileName) {
        if (fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        }
        return "unknown"; // Или можно вернуть null, если необходимо
    }

    @Override
    @Transactional
    public Resource download(String fileName) throws IOException {
        log.debug("Downloading file: {}", fileName);
        try {
            findObject(fileName);

            InputStream inputStream = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(minioProperties.bucketName())
                    .object(fileName)
                    .build());

            log.info("File downloaded successfully: {}", fileName);
            return new InputStreamResource(inputStream);
        } catch (Exception e) {
            log.error("Error downloading file: {}", e.getMessage(), e);
            throw new IOException("Failed to download file: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void delete(String fileName) throws IOException {
        log.debug("Deleting file: {}", fileName);
        Long id = authService.getJwtAuthentication().getId();
        User user = userService.getById(id);
        checkUserHasFile(user, fileName);

        try {
            findObject(fileName);
            removeFile(fileName);

            userFileService.removeFileFromUser(id, fileName);
            log.info("File deleted successfully: {}", fileName);
        } catch (Exception e) {
            log.error("Error deleting file: {}", e.getMessage(), e);
            throw new IOException("Error deleting file: " + e.getMessage());
        }
    }


    @Override
    @Transactional
    public void editFileName(String newFileName, String oldFileName) throws IOException {
        log.debug("Renaming file: oldFilename={}, newFilename={}", oldFileName, newFileName);
        Long id = authService.getJwtAuthentication().getId();
        User user = userService.getById(id);
        checkUserHasFile(user, oldFileName);
        try {
            findObject(oldFileName);
            copyObject(oldFileName, newFileName);
            removeFile(oldFileName);

            userFileService.removeFileFromUser(id, oldFileName);
            userFileService.addFileToUser(id, newFileName);

            log.info("File renamed successfully from {} to {}", oldFileName, newFileName);
        } catch (Exception e) {
            log.error("Error renaming file: {}", e.getMessage(), e);
            throw new IOException("Failed to rename file: " + e.getMessage(), e);
        }
    }


    @SneakyThrows
    private void copyObject(String oldFileName, String newFileName) {
        minioClient.copyObject(CopyObjectArgs.builder()
                .bucket(minioProperties.bucketName())
                .source(CopySource.builder()
                        .bucket(minioProperties.bucketName())
                        .object(oldFileName)
                        .build())
                .object(newFileName)
                .build());
    }

    private void checkUserHasFile(User user, String fileName) throws AccessDeniedException {
        if (!user.getFiles().contains(fileName)) {
            log.warn("File access denied: User {} does not own the file {}", user.getUsername(), fileName);
            throw new AccessDeniedException("You do not have permission to access this file");
        }
    }


    @SneakyThrows
    private void createBucket() {
        log.debug("Creating bucket: {}", minioProperties.bucketName());
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(minioProperties.bucketName())
                    .build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(minioProperties.bucketName())
                        .build());
                log.info("Bucket created: {}", minioProperties.bucketName());
            }
        } catch (Exception e) {
            log.error("Error creating bucket: {}", e.getMessage(), e);
            throw new FileUploadException("File upload failed: " + e.getMessage());
        }
    }


    @SneakyThrows
    private void saveFile(InputStream inputStream, String fileName) {
        log.debug("Saving file: {}", fileName);
        minioClient.putObject(PutObjectArgs.builder()
                .stream(inputStream, inputStream.available(), -1)
                .bucket(minioProperties.bucketName())
                .object(fileName)
                .build());
    }

    private String generateFileName(MultipartFile file) {
        log.debug("Generating file name: {}", file.getOriginalFilename());
        String extension = getExtension(file);
        return UUID.randomUUID() + "." + extension;
    }

    private String getExtension(MultipartFile file) {
        log.debug("Getting extension for file: {}", file.getOriginalFilename());
        return Objects.requireNonNull(file.getOriginalFilename())
                .substring(file.getOriginalFilename().lastIndexOf(".") + 1);
    }


    @SneakyThrows
    private void removeFile(String fileName) {
        log.debug("Removing file: {}", fileName);
        minioClient.removeObject(RemoveObjectArgs.builder()
                .bucket(minioProperties.bucketName())
                .object(fileName)
                .build());
    }

    @SneakyThrows
    private void findObject(String fileName) {
        log.debug("Checking if file exists: {}", fileName);
        StatObjectResponse response = minioClient.statObject(StatObjectArgs.builder()
                .bucket(minioProperties.bucketName())
                .object(fileName)
                .build());
        if (response == null) {
            log.warn("File not found: {}", fileName);
            throw new FileNotFoundException("File not found: " + fileName);
        }
    }
}