package abdulgazizov.dev.cloudstoragedemo.services;

import io.minio.messages.Item;
import org.apache.coyote.BadRequestException;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Service interface for file storage operations.
 */
public interface FileStorageService {
    /**
     * Uploads a file to the storage.
     *
     * @param file     the file to be uploaded
     * @param fileName the desired file name (optional, if null, a unique name will be generated)
     * @return the name of the uploaded file
     */
    String upload(MultipartFile file, String fileName);

    /**
     * Downloads a file from the storage.
     *
     * @param filename the name of the file to be downloaded
     * @return a Resource representing the downloaded file
     * @throws IOException if an I/O error occurs while downloading the file
     */
    Resource download(String filename) throws IOException;

    /**
     * Deletes a file from the storage.
     *
     * @param fileName the name of the file to be deleted
     * @throws IOException if an I/O error occurs while deleting the file
     */
    void delete(String fileName) throws IOException;

    /**
     * Renames a file in the storage.
     *
     * @param newFileName the new name for the file
     * @param oldFileName the current name of the file
     * @throws IOException if an I/O error occurs while renaming the file
     */
    void editFileName(String newFileName, String oldFileName) throws IOException;

    /**
     * Retrieves a list of files from the storage, limited by the specified count.
     *
     * @param limit the maximum number of files to retrieve
     * @return a list of FileDto objects representing the retrieved files
     * @throws BadRequestException if the limit is less than or equal to zero
     */
    List<Item> getFiles(int limit) throws BadRequestException;
}
