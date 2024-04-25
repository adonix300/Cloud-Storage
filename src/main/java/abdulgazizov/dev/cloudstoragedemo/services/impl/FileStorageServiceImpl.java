package abdulgazizov.dev.cloudstoragedemo.services.impl;

import abdulgazizov.dev.cloudstoragedemo.exceptions.FileUploadException;
import abdulgazizov.dev.cloudstoragedemo.properties.MinioProperties;
import abdulgazizov.dev.cloudstoragedemo.services.FileStorageService;
import abdulgazizov.dev.cloudstoragedemo.services.UserService;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {
    private final MinioClient minioClient;
    private final MinioProperties minioProperties;
    private final UserService userService;

    @Override
    public String upload(MultipartFile file, Long id) {
        try {
            createBucket();
        } catch (Exception e) {
            log.error("Error creating bucket: {}", e.getMessage());
            throw new FileUploadException("File upload failed: " + e.getMessage());
        }
        if (file.isEmpty() || file.getOriginalFilename() == null) {
            log.error("File is empty");
            throw new FileUploadException("File is empty");
        }
        String fileName = generateFileName(file);
        InputStream inputStream;
        try {
            inputStream = file.getInputStream();
        } catch (IOException e) {
            log.error("Error reading file input stream: {}", e.getMessage());
            throw new FileUploadException("File upload failed: " + e.getMessage());
        }
        saveFile(inputStream, fileName);
        userService.saveFileForUser(id, fileName);
        log.info("File uploaded successfully: {}", fileName);
        return fileName;
    }

    @Override
    public Resource download(String fileName) throws IOException {
        try {
            findObject(fileName);

            InputStream inputStream = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(minioProperties.bucketName())
                    .object(fileName)
                    .build());

            log.info("File downloaded successfully: {}", fileName);
            return new InputStreamResource(inputStream);
        } catch (Exception e) {
            log.error("Error downloading file: {}", e.getMessage());
            throw new IOException("Failed to download file: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(String fileName) throws IOException {
        try {
            findObject(fileName);
            removeFile(fileName);
            log.info("File deleted successfully: {}", fileName);
        } catch (Exception e) {
            log.error("Error deleting file: {}", e.getMessage());
            throw new IOException("Error deleting file: " + e.getMessage());
        }
    }

    @Override
    public void editFileName(String oldFileName, String newFileName) throws IOException {
        try {
            findObject(oldFileName);
            copyObject(oldFileName, newFileName);
            removeFile(oldFileName);

            log.info("File renamed successfully from {} to {}", oldFileName, newFileName);
        } catch (Exception e) {
            log.error("Error renaming file: {}", e.getMessage());
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


    @SneakyThrows
    private void createBucket() {
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(minioProperties.bucketName())
                .build());
        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(minioProperties.bucketName())
                    .build());
            log.info("Bucket created: {}", minioProperties.bucketName());
        }
    }


    @SneakyThrows
    private void saveFile(InputStream inputStream, String fileName) {
        minioClient.putObject(PutObjectArgs.builder()
                .stream(inputStream, inputStream.available(), -1)
                .bucket(minioProperties.bucketName())
                .object(fileName)
                .build());
    }

    private String generateFileName(MultipartFile file) {
        String extension = getExtension(file);
        return UUID.randomUUID() + "." + extension;
    }

    private String getExtension(MultipartFile file) {
        return Objects.requireNonNull(file.getOriginalFilename())
                .substring(file.getOriginalFilename().lastIndexOf(".") + 1);
    }


    @SneakyThrows
    private void removeFile(String fileName) {
        minioClient.removeObject(RemoveObjectArgs.builder()
                .bucket(minioProperties.bucketName())
                .object(fileName)
                .build());
    }

    @SneakyThrows
    private void findObject(String fileName) {
        StatObjectResponse response = minioClient.statObject(StatObjectArgs.builder()
                .bucket(minioProperties.bucketName())
                .object(fileName)
                .build());
        if (response == null) {
            log.error("File not found: {}", fileName);
            throw new FileNotFoundException("File not found: " + fileName);
        }
    }
}

