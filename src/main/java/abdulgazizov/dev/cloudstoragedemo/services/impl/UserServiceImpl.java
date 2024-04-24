package abdulgazizov.dev.cloudstoragedemo.services.impl;

import abdulgazizov.dev.cloudstoragedemo.entity.Role;
import abdulgazizov.dev.cloudstoragedemo.entity.User;
import abdulgazizov.dev.cloudstoragedemo.jwt.JwtAuthentication;
import abdulgazizov.dev.cloudstoragedemo.mappers.UserMapper;
import abdulgazizov.dev.cloudstoragedemo.repositories.UserRepository;
import abdulgazizov.dev.cloudstoragedemo.responses.UserResponse;
import abdulgazizov.dev.cloudstoragedemo.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public UserResponse save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.setRoles(Collections.singleton(Role.ROLE_USER));
        }
        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    @Override
    public UserResponse findById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User with userId " + userId + " not found"));
        return userMapper.toResponse(user);
    }


    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("User with username " + username + " not found"));
    }

    @Override
    public UserResponse myProfile() {
        JwtAuthentication authentication = (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
        return findById(authentication.getId());
    }

    @Override
    public void uploadFile(Long userId, MultipartFile file) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));

    }


}
