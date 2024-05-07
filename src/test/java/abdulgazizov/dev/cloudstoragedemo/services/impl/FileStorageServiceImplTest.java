package abdulgazizov.dev.cloudstoragedemo.services.impl;

import abdulgazizov.dev.cloudstoragedemo.dtos.FileDto;
import abdulgazizov.dev.cloudstoragedemo.entity.Role;
import abdulgazizov.dev.cloudstoragedemo.entity.User;
import abdulgazizov.dev.cloudstoragedemo.exceptions.FileUploadException;
import abdulgazizov.dev.cloudstoragedemo.jwt.JwtAuthentication;
import abdulgazizov.dev.cloudstoragedemo.properties.MinioProperties;
import abdulgazizov.dev.cloudstoragedemo.services.AuthService;
import abdulgazizov.dev.cloudstoragedemo.services.UserFileService;
import abdulgazizov.dev.cloudstoragedemo.services.UserService;
import io.minio.*;
import io.minio.messages.Item;
import lombok.SneakyThrows;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileStorageServiceImplTest {
    @InjectMocks
    private FileStorageServiceImpl fileStorageService;

    @Mock
    private MinioClient minioClient;

    @Mock
    private MinioProperties minioProperties;

    @Mock
    private UserService userService;

    @Mock
    private UserFileService userFileService;

    @Mock
    private AuthService authService;

    @Mock
    private JwtAuthentication jwtAuthentication;

    @Captor
    private ArgumentCaptor<PutObjectArgs> putObjectArgsCaptor;

    private User user;

    @BeforeEach
    public void setup() {
        user = new User();
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(Role.ROLE_USER));
        user.setId(1L);


        Set<String> files = new HashSet<>();
        files.add("file1.txt");
        files.add("file2.txt");

        user.setFiles(files);
    }

    @SneakyThrows
    @Test
    @DisplayName("Загрузка файла: успешная загрузка")
    void upload_testUploadFileSuccessfully() {
        //given

        MultipartFile file = new MockMultipartFile("file", "example.txt", "text/plain", "Hello, World!".getBytes());
        String fileName = "example.txt";

        when(authService.getJwtAuthentication()).thenReturn(jwtAuthentication);
        when(jwtAuthentication.getId()).thenReturn(user.getId());
        when(minioProperties.bucketName()).thenReturn("bucket");
        //when
        String customFileName = fileStorageService.upload(file, fileName);

        //then
        verify(minioClient).putObject(putObjectArgsCaptor.capture());
        PutObjectArgs capturedArgs = putObjectArgsCaptor.getValue();

        assertNotNull(capturedArgs);
        assertNotNull(customFileName);
        assertEquals(fileName, capturedArgs.object());
        assertEquals(fileName, customFileName);
        assertEquals(file.getSize(), capturedArgs.stream().available());

        verify(minioClient).bucketExists(BucketExistsArgs.builder()
                .bucket(minioProperties.bucketName())
                .build());
        verify(userFileService).addFileToUser(user.getId(), customFileName);

    }

    @SneakyThrows
    @Test
    @DisplayName("Загрузка файла: файл пуст")
    void upload_testUploadFileNullShouldThrowException() {
        //given
        MultipartFile file = new MockMultipartFile("file", "example.txt", "text/plain", "".getBytes());
        String fileName = "";

        when(authService.getJwtAuthentication()).thenReturn(jwtAuthentication);
        when(jwtAuthentication.getId()).thenReturn(user.getId());
        when(minioProperties.bucketName()).thenReturn("bucket");
        //when
        FileUploadException thrown = assertThrows(FileUploadException.class, () -> fileStorageService.upload(file, fileName));

        //then
        assertNotNull(thrown);
        assertEquals("File is empty", thrown.getMessage());
    }

    @Test
    @DisplayName("Загрузка файла: название файла пустое")
    void upload_testUploadFileWithNullOriginalNameShouldThrowException() throws Exception {
        //given
        MultipartFile file = new MockMultipartFile("file", null, "text/plain", "Hello world".getBytes());
        String fileName = "";

        when(authService.getJwtAuthentication()).thenReturn(jwtAuthentication);
        when(jwtAuthentication.getId()).thenReturn(user.getId());
        when(minioProperties.bucketName()).thenReturn("bucket");

        //when & then
        FileUploadException thrown = assertThrows(FileUploadException.class, () -> fileStorageService.upload(file, fileName));
        assertEquals("File is empty", thrown.getMessage());
    }

    @Test
    @DisplayName("Скачивание файла: успешно")
    void download_ExistingFile_Success() throws Exception {
        //given
        String fileName = "existing.txt";
        InputStream fakeStream = new ByteArrayInputStream("File content".getBytes());

        GetObjectResponse getObjectResponse = mock(GetObjectResponse.class);
        when(getObjectResponse.readAllBytes()).thenReturn(fakeStream.readAllBytes());
        StatObjectResponse statObjectResponse = mock(StatObjectResponse.class);

        when(minioProperties.bucketName()).thenReturn("bucket");
        when(minioClient.getObject(any(GetObjectArgs.class))).thenReturn(getObjectResponse);
        when(minioClient.statObject(any(StatObjectArgs.class))).thenReturn(statObjectResponse);

        //when
        Resource resource = fileStorageService.download(fileName);

        //then
        assertNotNull(resource);
        assertInstanceOf(InputStreamResource.class, resource);
        byte[] actualData = resource.getInputStream().readAllBytes();
        assertArrayEquals("File content".getBytes(), actualData);
    }

    @Test
    @DisplayName("Скачивание файла: файл не найден")
    void download_ExistingFile_Fail() throws Exception {
        //given
        String fileName = "nonExisting.txt";
        when(minioProperties.bucketName()).thenReturn("bucket");
        when(minioClient.statObject(any(StatObjectArgs.class))).thenThrow(new FileNotFoundException("File not found: " + fileName));
        //when

        IOException thrown = assertThrows(IOException.class, () -> fileStorageService.download(fileName));
        //then
        assertNotNull(thrown);
        assertTrue(thrown.getMessage().contains("File not found: " + fileName));
    }

    @Test
    @DisplayName("Переименование файла: успешно")
    void editFileName_Success() throws Exception {
        //given
        String oldFileName = "file1.txt";
        String newFileName = "newName.txt";

        when(authService.getJwtAuthentication()).thenReturn(jwtAuthentication);
        when(jwtAuthentication.getId()).thenReturn(user.getId());
        when(minioProperties.bucketName()).thenReturn("bucket");
        when(userService.getById(user.getId())).thenReturn(user);

        when(minioClient.statObject(any(StatObjectArgs.class))).thenReturn(mock(StatObjectResponse.class));
        when(minioClient.copyObject(any(CopyObjectArgs.class))).thenReturn(mock(ObjectWriteResponse.class));
        doNothing().when(minioClient).removeObject(any(RemoveObjectArgs.class));

        //when
        fileStorageService.editFileName(newFileName, oldFileName);

        //then
        verify(userFileService).removeFileFromUser(user.getId(), oldFileName);
        verify(userFileService).addFileToUser(user.getId(), newFileName);
    }

    @Test
    @DisplayName("Переименовывание файла: несуществующий файл")
    void editFileName_testFileNotFound() throws Exception {
        //given
        String oldFileName = "nonExisting.txt";
        String newFileName = "newName.txt";
        when(authService.getJwtAuthentication()).thenReturn(jwtAuthentication);
        when(jwtAuthentication.getId()).thenReturn(user.getId());
        when(userService.getById(user.getId())).thenReturn(user);

        //when
        FileNotFoundException exception = assertThrows(FileNotFoundException.class, () -> fileStorageService.editFileName(newFileName, oldFileName));

        //then
        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("File not found: "));
    }

    @Test
    @DisplayName("Удаление файла: успешно")
    void delete_testDeleteFileSuccessfully() throws Exception {
        //given
        String fileName = "file1.txt";

        when(authService.getJwtAuthentication()).thenReturn(jwtAuthentication);
        when(jwtAuthentication.getId()).thenReturn(user.getId());
        when(minioProperties.bucketName()).thenReturn("bucket");
        when(userService.getById(user.getId())).thenReturn(user);

        when(minioClient.statObject(any(StatObjectArgs.class))).thenReturn(mock(StatObjectResponse.class));
        doNothing().when(minioClient).removeObject(any(RemoveObjectArgs.class));

        //when
        fileStorageService.delete(fileName);

        //then
        verify(userFileService).removeFileFromUser(user.getId(), fileName);
    }

    @Test
    @DisplayName("Переименовывание файла: несуществующий файл")
    void delete_testFileNotFound() throws Exception {
        //given
        String fileName = "nonExisting.txt";

        when(authService.getJwtAuthentication()).thenReturn(jwtAuthentication);
        when(jwtAuthentication.getId()).thenReturn(user.getId());
        when(userService.getById(user.getId())).thenReturn(user);

        //when
        FileNotFoundException exception = assertThrows(FileNotFoundException.class, () -> fileStorageService.delete(fileName));

        //then
        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("File not found: "));
    }

    @Test
    @DisplayName("Получение файлов: успешно")
    void getFiles_ValidLimit_Success() throws Exception {
        //given
        int limit = 2;
        Set<String> userFiles = Set.of("file1.txt", "file2.txt");

        User user = mock(User.class);
        when(user.getFiles()).thenReturn(userFiles);
        when(authService.getJwtAuthentication()).thenReturn(jwtAuthentication);
        when(userService.getById(user.getId())).thenReturn(user);
        when(minioProperties.bucketName()).thenReturn("bucket");

        List<Result<Item>> results = List.of(
                createMockItemResult("file1.txt", 123, ZonedDateTime.now()),
                createMockItemResult("file2.txt", 456, ZonedDateTime.now()),
                createMockItemResult("file3.txt", 789, ZonedDateTime.now())
        );
        when(minioClient.listObjects(any(ListObjectsArgs.class))).thenReturn(results);

        // when
        List<FileDto> files = fileStorageService.getFiles(limit);

        // then
        assertNotNull(files);
        assertEquals(2, files.size());
        assertTrue(files.stream().anyMatch(f -> f.getFileName().equals("file1.txt")));
        assertTrue(files.stream().anyMatch(f -> f.getFileName().equals("file2.txt")));
    }

    @Test
    @DisplayName("Получение файлов: неверный лимит")
    void getFiles_InvalidLimit_ThrowsBadRequestException() {
        // given
        int invalidLimit = 0;
        when(authService.getJwtAuthentication()).thenReturn(jwtAuthentication);
        when(jwtAuthentication.getId()).thenReturn(user.getId());
        when(userService.getById(user.getId())).thenReturn(user);

        // when
        BadRequestException exception = assertThrows(BadRequestException.class, () -> fileStorageService.getFiles(invalidLimit));

        //then
        assertEquals("Limit must be greater than 0", exception.getMessage());
    }

    @Test
    @DisplayName("Получение файлов: ошибка получения")
    void getFiles_ErrorListingFiles_ThrowsRuntimeException() {
        // given
        int limit = 2;
        when(authService.getJwtAuthentication()).thenReturn(jwtAuthentication);
        when(jwtAuthentication.getId()).thenReturn(user.getId());
        when(minioProperties.bucketName()).thenReturn("bucket");
        when(minioClient.listObjects(any(ListObjectsArgs.class))).thenThrow(new RuntimeException("MinIO error"));

        //when
        Exception exception = assertThrows(RuntimeException.class, () -> fileStorageService.getFiles(limit));

        //then
        assertEquals("Failed to retrieve files: MinIO error", exception.getMessage());
    }

    private Result<Item> createMockItemResult(String fileName, long size, ZonedDateTime lastModified) {
        Item item = mock(Item.class);
        lenient().when(item.objectName()).thenReturn(fileName);
        lenient().when(item.size()).thenReturn(size);
        lenient().when(item.lastModified()).thenReturn(lastModified);

        Result<Item> result = mock(Result.class);
        try {
            lenient().when(result.get()).thenReturn(item);
        } catch (Exception e) {
            fail("Failed to create mock item result");
        }
        return result;
    }
}
