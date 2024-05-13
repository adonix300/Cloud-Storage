package abdulgazizov.dev.cloudstoragedemo.mappers;

public interface Mapper<Entity, Response> {
    Entity toEntity(Response response);

    Response toResponse(Entity entity);
}
