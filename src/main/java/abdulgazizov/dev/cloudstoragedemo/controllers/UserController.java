package abdulgazizov.dev.cloudstoragedemo.controllers;

import abdulgazizov.dev.cloudstoragedemo.entity.User;
import abdulgazizov.dev.cloudstoragedemo.mappers.UserMapper;
import abdulgazizov.dev.cloudstoragedemo.responses.UserResponse;
import abdulgazizov.dev.cloudstoragedemo.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping("register")
    public ResponseEntity<UserResponse> register(@RequestBody @NonNull @Valid User user) {
        log.debug("Received registration request: {}", user);
        User createdUser = userService.create(user);
        log.info("User registered successfully: {}", createdUser);
        return ResponseEntity.ok(userMapper.toResponse(createdUser));
    }
}
