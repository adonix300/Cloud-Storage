package abdulgazizov.dev.cloudstoragedemo.services.impl;

import abdulgazizov.dev.cloudstoragedemo.entity.User;
import abdulgazizov.dev.cloudstoragedemo.repositories.UserRepository;
import abdulgazizov.dev.cloudstoragedemo.services.UserFileService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserFileServiceImpl implements UserFileService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void addFileToUser(Long id, String fileName) {
        log.debug("Adding file {} to user with id {}", fileName, id);
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        user.getFiles().add(fileName);
        userRepository.save(user);
        log.info("File {} added to user with id {}", fileName, id);
    }

    @Override
    @Transactional
    public void removeFileFromUser(Long id, String fileName) {
        log.debug("Removing file {} from user with id {}", fileName, id);
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        user.getFiles().remove(fileName);
        userRepository.save(user);
        log.info("File {} removed from user with id {}", fileName, id);
    }
}
