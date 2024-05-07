package abdulgazizov.dev.cloudstoragedemo.services.impl;

import abdulgazizov.dev.cloudstoragedemo.entity.Role;
import abdulgazizov.dev.cloudstoragedemo.entity.User;
import abdulgazizov.dev.cloudstoragedemo.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserFileServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserFileServiceImpl userFileService;

    private User user;
    private String fileName;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("username");
        user.setPassword("password");
        user.setId(1L);
        user.setRoles(Collections.singleton(Role.ROLE_USER));
        user.setFiles(new HashSet<>());
        fileName = "file.txt";
    }

    @Test
    @DisplayName("Добавление файла: Успешный сценарий")
    void addFileToUser_testSuccess() {
        //given
        Long id = 1L;
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        //when
        userFileService.addFileToUser(id, fileName);
        //then
        assertEquals(1, user.getFiles().size());
        assertEquals(fileName, user.getFiles().iterator().next());

        verify(userRepository).findById(id);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Добавление файла: пользователь не найден")
    void addFileToUser_testUserNotFound() {
        //given
        Long id = 2L;
        when(userRepository.findById(id)).thenThrow(new EntityNotFoundException("User with id " + id + " not found"));

        //when
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> userRepository.findById(id));

        //then
        assertTrue(thrown.getMessage().contains("User with id " + id + " not found"));
        verify(userRepository).findById(id);
        verify(userRepository, never()).save(user);
    }

    @Test
    @DisplayName("Удаление файла: успешный сценарий")
    void removeFileFromUser_testSuccess() {
        //given
        Long id = user.getId();
        user.getFiles().add(fileName);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        //when
        userFileService.removeFileFromUser(id, fileName);
        //then
        assertEquals(0, user.getFiles().size());

        verify(userRepository).findById(id);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Удаление файла: пользователь не найден")
    void removeFileFromUser_testUserNotFound() {
        //given
        Long id = 2L;
        when(userRepository.findById(id)).thenThrow(new EntityNotFoundException("User with id " + id + " not found"));

        //when
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> userRepository.findById(id));

        //then
        assertTrue(thrown.getMessage().contains("User with id " + id + " not found"));
        verify(userRepository).findById(id);
        verify(userRepository, never()).save(user);

    }
}