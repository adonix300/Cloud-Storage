package abdulgazizov.dev.cloudstoragedemo.services;

import abdulgazizov.dev.cloudstoragedemo.dtos.UserDto;
import abdulgazizov.dev.cloudstoragedemo.entity.User;
import abdulgazizov.dev.cloudstoragedemo.responses.UserResponse;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    UserResponse save(User user);
    UserResponse findById(Long userId);
    User findByUsername(String username);
    UserResponse myProfile();
    void uploadFile(Long userId, MultipartFile file);
}
