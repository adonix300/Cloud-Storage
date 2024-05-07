package abdulgazizov.dev.cloudstoragedemo.services;

/**
 * Service interface for managing the association between users and files.
 */
public interface UserFileService {
    /**
     * Associates a file with a user.
     *
     * @param id       the ID of the user
     * @param fileName the name of the file to be associated with the user
     */
    void addFileToUser(Long id, String fileName);

    /**
     * Disassociates a file from a user.
     *
     * @param id       the ID of the user
     * @param fileName the name of the file to be disassociated from the user
     */
    void removeFileFromUser(Long id, String fileName);
}
