package abdulgazizov.dev.cloudstoragedemo.services.impl;

import abdulgazizov.dev.cloudstoragedemo.entity.Role;
import abdulgazizov.dev.cloudstoragedemo.entity.User;
import abdulgazizov.dev.cloudstoragedemo.repositories.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(Role.ROLE_USER));
        user.setId(1L);
    }

    @Test
    @DisplayName("Создание пользователя: успешный сценарий")
    void create_testCreateUserSuccess() {
        //given
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(user)).thenReturn(user);

        //then
        User createdUser = userServiceImpl.create(user);

        //when
        assertNotNull(createdUser);
        assertEquals("encodedPassword", createdUser.getPassword());
        assertEquals("username", createdUser.getUsername());
        assertEquals(Collections.singleton(Role.ROLE_USER), createdUser.getRoles());
    }

    @Test
    @DisplayName("Создание пользователя: пользователь уже существует")
    void create_testCreateUserAlreadyExist() {
        //given
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        //when
        EntityExistsException thrown = assertThrows(EntityExistsException.class, () -> {
            User newUser = new User();
            newUser.setUsername("username");
            newUser.setPassword("password");
            newUser.setRoles(Collections.singleton(Role.ROLE_USER));
            userServiceImpl.create(newUser);
        });

        //then
        assertTrue(thrown.getMessage().contains("Username already exists"));
        verify(userRepository).findByUsername(user.getUsername());
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    @DisplayName("Получение пользователя по ID: успешный сценарий")
    void getById_testGetUserSuccess() {
        //given
        Long id = 1L;
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        //when
        User newUser = userServiceImpl.getById(1L);
        //then
        assertNotNull(newUser);
        assertEquals("username", newUser.getUsername());
        assertEquals("password", newUser.getPassword());
        assertEquals(Collections.singleton(Role.ROLE_USER), newUser.getRoles());

        verify(userRepository).findById(id);
    }

    @Test
    @DisplayName("Получение пользователя по ID: пользователь не найден")
    void getById_testGetUserNotFound() {
        //given
        Long id = 2L;
        when(userRepository.findById(id)).thenThrow(new EntityNotFoundException("User with id " + id + " not found"));

        //when
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> userRepository.findById(id));

        //then
        assertTrue(thrown.getMessage().contains("User with id " + id + " not found"));
        verify(userRepository).findById(id);
    }

    @Test
    @DisplayName("Получение пользователя по имени пользователя: успешный сценарий")
    void getByUsername_testGetUserSuccess() {
        //given
        String username = "username";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        //when
        User newUser = userServiceImpl.getByUsername(username);
        //then
        assertNotNull(newUser);
        assertEquals("username", newUser.getUsername());
        assertEquals("password", newUser.getPassword());
        assertEquals(Collections.singleton(Role.ROLE_USER), newUser.getRoles());

        verify(userRepository).findByUsername(username);
    }

    @Test
    @DisplayName("Получение пользователя по имени пользователя: пользователь не найден")
    void getByUsername_testGetUserNotFound() {
        //given
        String username = "username";
        when(userRepository.findByUsername(username)).thenThrow(new EntityNotFoundException("User with username " + username + " not found"));

        //when
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> userRepository.findByUsername(username));

        //then
        assertTrue(thrown.getMessage().contains("User with username " + username + " not found"));

        verify(userRepository).findByUsername(username);
    }
}