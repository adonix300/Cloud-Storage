package abdulgazizov.dev.cloudstoragedemo.responses;

import abdulgazizov.dev.cloudstoragedemo.entity.Role;
import lombok.Data;

import java.util.Set;

@Data
public class UserResponse {
    private String username;
    private Set<Role> roles;
    private Set<String> files;
}
