package abdulgazizov.dev.cloudstoragedemo.mappers;

import abdulgazizov.dev.cloudstoragedemo.dtos.FileDto;
import io.minio.messages.Item;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

@Component
public class FileMapper {

    public static FileDto toFileDto(Item fileItem) {
        ZonedDateTime lastModified = fileItem.lastModified();
        return FileDto.builder()
                .fileName(fileItem.objectName())
                .size(fileItem.size())
                .fileType(determineFileType(fileItem.objectName()))
                .editedAt(lastModified.toInstant().toEpochMilli())
                .build();
    }

    private static String determineFileType(String fileName) {
        if (fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        }
        return "unknown";
    }
}
