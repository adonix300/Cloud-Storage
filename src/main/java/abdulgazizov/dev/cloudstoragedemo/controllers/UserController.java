package abdulgazizov.dev.cloudstoragedemo.controllers;

import abdulgazizov.dev.cloudstoragedemo.entity.User;
import abdulgazizov.dev.cloudstoragedemo.mappers.UserMapper;
import abdulgazizov.dev.cloudstoragedemo.responses.UserResponse;
import abdulgazizov.dev.cloudstoragedemo.services.UserService;
import abdulgazizov.dev.cloudstoragedemo.services.impl.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
//@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final AuthService authService;
    private final UserMapper userMapper;

    @PostMapping("register")
    public ResponseEntity<UserResponse> register(@RequestBody User user) {
        User createdUser = userService.create(user);
        return ResponseEntity.ok(userMapper.toResponse(createdUser));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("get/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable Long id) {
        User user = userService.getById(id);
        return ResponseEntity.ok(userMapper.toResponse(user));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("myprofile")
    public ResponseEntity<UserResponse> myProfile() {
        Long id = authService.getJwtAuthentication().getId();
        User user = userService.getById(id);
        return ResponseEntity.ok(userMapper.toResponse(user));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @DeleteMapping("delete")
    public ResponseEntity<Void> deleteMyProfile() {
        Long id = authService.getJwtAuthentication().getId();
        String username = authService.getJwtAuthentication().getUsername();
        userService.delete(id, username);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("update")
    public ResponseEntity<UserResponse> update(@RequestBody User user) {
        Long id = authService.getJwtAuthentication().getId();
        User userToUpdate = userService.update(id, user);
        return ResponseEntity.ok(userMapper.toResponse(userToUpdate));
    }
}
