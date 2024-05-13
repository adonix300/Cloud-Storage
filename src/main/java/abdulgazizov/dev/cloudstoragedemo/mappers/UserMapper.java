package abdulgazizov.dev.cloudstoragedemo.mappers;

import abdulgazizov.dev.cloudstoragedemo.entity.User;
import abdulgazizov.dev.cloudstoragedemo.responses.UserResponse;
import org.mapstruct.factory.Mappers;

@org.mapstruct.Mapper(componentModel = "spring")
public interface UserMapper extends Mapper<User, UserResponse> {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
}
