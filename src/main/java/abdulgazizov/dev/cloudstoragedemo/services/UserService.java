package abdulgazizov.dev.cloudstoragedemo.services;

import abdulgazizov.dev.cloudstoragedemo.dtos.UserDto;
import abdulgazizov.dev.cloudstoragedemo.entity.User;

public interface UserService {
    User save(UserDto user);
    User findById(Long id);
    User findByUsername(String username);
}
