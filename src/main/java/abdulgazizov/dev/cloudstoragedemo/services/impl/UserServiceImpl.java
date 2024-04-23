package abdulgazizov.dev.cloudstoragedemo.services.impl;

import abdulgazizov.dev.cloudstoragedemo.dtos.UserDto;
import abdulgazizov.dev.cloudstoragedemo.entity.Role;
import abdulgazizov.dev.cloudstoragedemo.entity.User;
import abdulgazizov.dev.cloudstoragedemo.repositories.UserRepository;
import abdulgazizov.dev.cloudstoragedemo.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public User save(UserDto userDto) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword());
        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);
        user.setRoles(roles);
        return userRepository.save(user);
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User with id " + id + " not found"));
    }
}
