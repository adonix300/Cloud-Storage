package abdulgazizov.dev.cloudstoragedemo.mappers;

import abdulgazizov.dev.cloudstoragedemo.entity.User;
import abdulgazizov.dev.cloudstoragedemo.responses.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper extends Mappable<User, UserResponse> {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
}
