package abdulgazizov.dev.cloudstoragedemo.controllers;

import abdulgazizov.dev.cloudstoragedemo.dtos.FileDto;
import abdulgazizov.dev.cloudstoragedemo.dtos.FileNameDto;
import abdulgazizov.dev.cloudstoragedemo.exceptions.FileUploadException;
import abdulgazizov.dev.cloudstoragedemo.services.FileStorageService;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class FileStorageControllerTest {
    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private FileStorageController fileStorageController;

    @Test
    @DisplayName("Загрузка файла: успешно")
    void upload_testSuccess() {
        //given
        MultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "Hello World".getBytes());
        String filename = "example.txt";

        when(fileStorageService.upload(file, filename)).thenReturn(filename);
        //when
        var response = fileStorageController.upload(file, filename);
        //then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("File uploaded successfully: " + filename, response.getBody());
    }

    @Test
    @DisplayName("Загрузка файла: файл пустой")
    void upload_EmptyFileShouldThrowException() {
        //given
        MultipartFile file = new MockMultipartFile("file", "example.txt", "text/plain", new byte[0]);
        String fileName = "example.txt";

        //when
        var response = fileStorageController.upload(file, fileName);

        //then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("No file provided", response.getBody());
    }

    @Test
    @DisplayName("Загрузка файла: ошибка сохранения")
    void upload_FileSaveError() throws Exception {
        //given
        MultipartFile file = new MockMultipartFile("file", "example.txt", "text/plain", "test data".getBytes());
        String fileName = "example.txt";

        when(fileStorageService.upload(file, fileName)).thenThrow(new FileUploadException("Unable to save file"));

        //when
        Exception exception = assertThrows(FileUploadException.class,
                () -> fileStorageController.upload(file, fileName));

        //then
        assertNotNull(exception);
        assertEquals("Unable to save file", exception.getMessage());
    }

    @Test
    @DisplayName("Получение списка файлов: успешно")
    void getFiles_Successfully() throws BadRequestException {
        //given
        int limit = 5;
        List<FileDto> fileDtos = List.of(new FileDto("file1.txt", "text/plain", 1234L, 100L));
        when(fileStorageService.getFiles(limit)).thenReturn(fileDtos);

        //when
        var response = fileStorageController.getFiles(limit);

        //then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(fileDtos, response.getBody());
    }

    @Test
    @DisplayName("Получение списка файлов: недопустимый лимит")
    void getFiles_InvalidLimit() throws BadRequestException {
        //given
        int limit = 0;

        when(fileStorageService.getFiles(limit)).thenThrow(new BadRequestException("Limit must be greater than 0"));

        //when
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> fileStorageController.getFiles(limit));

        //then
        assertNotNull(exception);
        assertEquals("Limit must be greater than 0", exception.getMessage());
    }

    @Test
    @DisplayName("Скачивание файла: успешно")
    void download_FileSuccessfully() throws Exception {
        //given
        String fileName = "example.txt";
        Resource file = new ByteArrayResource("Hello, world!".getBytes());

        when(fileStorageService.download(fileName)).thenReturn(file);

        //when
        var response = fileStorageController.download(fileName);

        //then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Скачивание файла: файл не найден")
    void download_FileNotFound() throws Exception {
        //given
        String fileName = "nonexistent.txt";

        when(fileStorageService.download(fileName)).thenThrow(new FileNotFoundException("File not found"));

        //when
        Exception exception = assertThrows(FileNotFoundException.class,
                () -> fileStorageController.download(fileName));

        //then
        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("File not found"));
    }

    @Test
    @DisplayName("Удаление файла: успешно")
    void delete_FileSuccessfully() throws Exception {
        //given
        String fileName = "example.txt";

        doNothing().when(fileStorageService).delete(fileName);

        //when
        var response = fileStorageController.delete(fileName);

        //then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("File deleted successfully", response.getBody());
    }

    @Test
    @DisplayName("Удаление файла: ошибка удаления")
    void delete_FileDeletionError() throws Exception {
        //given
        String fileName = "example.txt";

        doThrow(new IOException("Error deleting file: " + fileName)).when(fileStorageService).delete(fileName);

        //when
        Exception exception = assertThrows(IOException.class,
                () -> fileStorageController.delete(fileName));

        //then
        assertNotNull(exception);
        assertEquals("Error deleting file: " + fileName, exception.getMessage());
    }

    @Test
    @DisplayName("Переименование файла: успешно")
    void edit_FileSuccessfully() throws Exception {
        //given
        String oldFileName = "oldName.txt";
        FileNameDto fileNameDto = new FileNameDto();
        fileNameDto.setFileName("newFileName.txt");

        doNothing().when(fileStorageService).editFileName(fileNameDto.getFileName(), oldFileName);

        //when
        var response = fileStorageController.edit(oldFileName, fileNameDto);

        //then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("File edited successfully", response.getBody());
    }

    @Test
    @DisplayName("Переименование файла: файл не существует")
    void edit_NonExistingFile() throws Exception {
        //given
        String oldFileName = "oldName.txt";
        FileNameDto fileNameDto = new FileNameDto();
        fileNameDto.setFileName("newFileName.txt");

        doThrow(new FileNotFoundException("File does not exist")).when(fileStorageService).editFileName(fileNameDto.getFileName(), oldFileName);

        //when
        Exception exception = assertThrows(FileNotFoundException.class,
                () -> fileStorageController.edit(oldFileName, fileNameDto));

        //then
        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("File does not exist"));
    }
}