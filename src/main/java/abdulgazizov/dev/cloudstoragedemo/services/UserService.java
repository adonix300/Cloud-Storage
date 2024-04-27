package abdulgazizov.dev.cloudstoragedemo.services;

import abdulgazizov.dev.cloudstoragedemo.entity.User;

public interface UserService {
    User create(User user);

    User getById(Long id);

    User getByUsername(String username);

    User update(User user);

    void delete(String username);
}
