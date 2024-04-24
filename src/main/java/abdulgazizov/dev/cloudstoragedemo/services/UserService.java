package abdulgazizov.dev.cloudstoragedemo.services;

import abdulgazizov.dev.cloudstoragedemo.entity.User;
import abdulgazizov.dev.cloudstoragedemo.responses.UserResponse;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    UserResponse create(User user);
    UserResponse getById(Long id);
    User getByUsername(String username);
    UserResponse update(User user);
    void delete(Long id);
    void uploadFile(Long id, MultipartFile file);
}
