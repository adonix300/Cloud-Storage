package abdulgazizov.dev.cloudstoragedemo.services.impl;

import abdulgazizov.dev.cloudstoragedemo.entity.Role;
import abdulgazizov.dev.cloudstoragedemo.entity.User;
import abdulgazizov.dev.cloudstoragedemo.repositories.UserRepository;
import abdulgazizov.dev.cloudstoragedemo.services.UserService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User create(User user) {
        log.debug("Creating user: {}", user);
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            log.warn("User already exists: {}", user);
            throw new EntityExistsException("Username already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.setRoles(Collections.singleton(Role.ROLE_USER));
        }

        User createdUser = userRepository.save(user);
        log.info("User created successfully: {}", createdUser);
        return createdUser;
    }

    @Override
    @Transactional(readOnly = true)
    public User getById(Long id) {
        log.debug("Getting user by id: {}", id);
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User with id " + id + " not found"));
    }


    @Override
    @Transactional(readOnly = true)
    public User getByUsername(String username) {
        log.debug("Getting user by username: {}", username);
        return userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("User with username " + username + " not found"));
    }
}
