package abdulgazizov.dev.cloudstoragedemo.services;

import abdulgazizov.dev.cloudstoragedemo.entity.User;

public interface UserFileService {
    User addFileToUser(Long id, String fileMame);
    User removeFileFromUser(Long id, String fileMame);
}
