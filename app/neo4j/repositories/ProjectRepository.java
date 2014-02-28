package neo4j.repositories;

import neo4j.models.require.Project;
import neo4j.models.user.User;
import neo4j.relations.Relations;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

import java.util.Set;

public interface ProjectRepository extends GraphRepository<Project> {

  @Query("START user=node({0}) MATCH user-[:" + Relations.MODEL_AUTHOR + "|" + Relations.PROJECT_CONTRIBUTOR + "]->project return project")
  public Set<Project> findByAuthorOrderByIdAsc(User author);
}
