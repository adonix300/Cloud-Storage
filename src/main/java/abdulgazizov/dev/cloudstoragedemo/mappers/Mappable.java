package abdulgazizov.dev.cloudstoragedemo.mappers;

public interface Mappable<Entity, Response> {
    Entity toEntity(Response response);

    Response toResponse(Entity entity);
}
