package abdulgazizov.dev.cloudstoragedemo.services.impl;

import abdulgazizov.dev.cloudstoragedemo.entity.Role;
import abdulgazizov.dev.cloudstoragedemo.entity.User;
import abdulgazizov.dev.cloudstoragedemo.repositories.UserRepository;
import abdulgazizov.dev.cloudstoragedemo.services.UserService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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
    @CachePut(value = "users", key = "#result.username")
    public User create(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new EntityExistsException("Username already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.setRoles(Collections.singleton(Role.ROLE_USER));
        }
        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "#id")
    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User with id " + id + " not found"));
    }


    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "#username")
    public User getByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("User with username " + username + " not found"));

    }

    @Override
    @Transactional
    @Caching(
            put = {@CachePut(value = "users", key = "#result.username"),
                    @CachePut(value = "users", key = "#id")},
            evict = {@CacheEvict(value = "users", key = "#user.username", condition = "#user.username != #result.username"),
                    @CacheEvict(value = "users", key = "#id")}
    )
    public User update(Long id, User user) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + user.getId()));

        if (user.getUsername() != null && !user.getUsername().equals(existingUser.getUsername())) {
            if (userRepository.findByUsername(user.getUsername()).isPresent()) {
                throw new EntityExistsException("Username already exists");
            }
            existingUser.setUsername(user.getUsername());
        }

        if (user.getPassword() != null && !passwordEncoder.matches(user.getPassword(), existingUser.getPassword())) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        if (user.getRoles() != null && !existingUser.getRoles().equals(user.getRoles())) {
            existingUser.setRoles(user.getRoles());
        }

        if (user.getFiles() != null && !existingUser.getFiles().equals(user.getFiles())) {
            existingUser.setFiles(user.getFiles());
        }

        return userRepository.save(existingUser);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "users", key = "#id"),
            @CacheEvict(value = "users", key = "#username")
    })
    public void delete(Long id, String username) {
        userRepository.deleteById(id);
    }
}
