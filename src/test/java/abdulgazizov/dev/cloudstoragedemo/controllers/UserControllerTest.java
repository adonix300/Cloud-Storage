package abdulgazizov.dev.cloudstoragedemo.controllers;

import abdulgazizov.dev.cloudstoragedemo.entity.Role;
import abdulgazizov.dev.cloudstoragedemo.entity.User;
import abdulgazizov.dev.cloudstoragedemo.mappers.UserMapper;
import abdulgazizov.dev.cloudstoragedemo.responses.UserResponse;
import abdulgazizov.dev.cloudstoragedemo.services.UserService;
import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserController userController;

    @Test
    @DisplayName("Регистрация: успешно")
    void register_testRegisterSuccess() {
        //given
        User inputUser = new User();
        inputUser.setUsername("username");
        inputUser.setPassword("password");

        User registredUser = new User();
        registredUser.setUsername("username");
        registredUser.setPassword("password");
        registredUser.setRoles(Collections.singleton(Role.ROLE_USER));

        UserResponse response = new UserResponse();
        response.setUsername("username");
        response.setRoles(Collections.singleton(Role.ROLE_USER));

        when(userService.create(inputUser)).thenReturn(registredUser);
        when(userMapper.toResponse(registredUser)).thenReturn(response);
        //when
        var responseEntity = userController.register(inputUser);

        //then
        assertNotNull(responseEntity);
        assertEquals(response, responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Регистрация: пользователь уже существует")
    void register_testRegisterFail() {
        //given
        User inputUser = new User();
        inputUser.setUsername("username");
        inputUser.setPassword("password");

        when(userService.create(inputUser)).thenThrow(new EntityExistsException("Username already exists"));
        //when
        EntityExistsException thrown = assertThrows(EntityExistsException.class, () -> userController.register(inputUser));

        //then
        assertNotNull(thrown);
        assertTrue(thrown.getMessage().contains("Username already exists"));
        verify(userService).create(inputUser);
    }

    @Test
    @DisplayName("Регистрация: невалидные данные")
    void register_testInvalidUsername() {
        //given
        User emptyUser = new User();
        emptyUser.setUsername("");
        emptyUser.setPassword("password");

        when(userService.create(emptyUser)).thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Required data missing"));
        //when
        ResponseStatusException thrown = assertThrows(ResponseStatusException.class, () -> userController.register(emptyUser));

        // then
        assertNotNull(thrown);
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatusCode());
        assertEquals("Required data missing", thrown.getReason());
    }
}