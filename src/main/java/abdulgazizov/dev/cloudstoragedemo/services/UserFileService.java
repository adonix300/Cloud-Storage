package abdulgazizov.dev.cloudstoragedemo.services;

public interface UserFileService {
    void addFileToUser(Long id, String fileMame);

    void removeFileFromUser(Long id, String fileMame);
}
