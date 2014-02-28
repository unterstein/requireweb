package neo4j.repositories;

import neo4j.models.require.Project;
import neo4j.models.user.User;
import neo4j.relations.Relations;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

import java.util.List;

public interface ProjectRepository extends GraphRepository<Project> {

  @Query("START user=node({0}) MATCH user-[:" + Relations.MODEL_AUTHOR + "|" + Relations.PROJECT_CONTRIBUTOR + "]->project WHERE (project: Project) RETURN project ORDER BY project.id ASC")
  public List<Project> findByAuthorOrContributor(User author);
}
