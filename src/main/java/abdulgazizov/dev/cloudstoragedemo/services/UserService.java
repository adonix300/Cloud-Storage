package abdulgazizov.dev.cloudstoragedemo.services;

import abdulgazizov.dev.cloudstoragedemo.entity.User;

/**
 * Service interface for managing user operations.
 */
public interface UserService {
    /**
     * Creates a new user.
     *
     * @param user the user object to be created
     * @return the created user object
     */
    User create(User user);

    /**
     * Retrieves a user by their ID.
     *
     * @param id the ID of the user
     * @return the user object
     */
    User getById(Long id);

    /**
     * Retrieves a user by their username.
     *
     * @param username the username of the user
     * @return the user object
     */
    User getByUsername(String username);
}
