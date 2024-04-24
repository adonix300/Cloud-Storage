package abdulgazizov.dev.cloudstoragedemo.services;

import abdulgazizov.dev.cloudstoragedemo.entity.User;
import abdulgazizov.dev.cloudstoragedemo.responses.UserResponse;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    UserResponse create(User user);
    UserResponse getById(Long id);
    UserResponse getByUsername(String username);
    UserResponse update(Long id, User user);
    void delete(Long id, String username);
    void uploadFile(Long id, MultipartFile file);
    User getUserByUsername(String username);
}
