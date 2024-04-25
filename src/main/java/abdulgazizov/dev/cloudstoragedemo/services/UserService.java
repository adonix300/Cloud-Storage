package abdulgazizov.dev.cloudstoragedemo.services;

import abdulgazizov.dev.cloudstoragedemo.entity.User;
import abdulgazizov.dev.cloudstoragedemo.responses.UserResponse;

public interface UserService {
    UserResponse create(User user);

    UserResponse getById(Long id);

    UserResponse getByUsername(String username);

    UserResponse update(Long id, User user);

    void delete(Long id, String username);

    User getUserByUsername(String username);

    void saveFileForUser(Long id, String fileName);
}
