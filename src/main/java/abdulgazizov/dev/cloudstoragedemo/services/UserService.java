package abdulgazizov.dev.cloudstoragedemo.services;

import abdulgazizov.dev.cloudstoragedemo.dtos.UserDto;
import abdulgazizov.dev.cloudstoragedemo.entity.User;
import abdulgazizov.dev.cloudstoragedemo.responses.UserResponse;

public interface UserService {
    UserResponse save(User user);
    UserResponse findById(Long id);
    User findByUsername(String username);
    UserResponse myProfile();
}
