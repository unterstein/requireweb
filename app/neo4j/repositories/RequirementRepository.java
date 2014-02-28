package neo4j.repositories;

import neo4j.models.require.Project;
import neo4j.models.require.Requirement;
import neo4j.relations.Relations;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

import java.util.List;

public interface RequirementRepository extends GraphRepository<Requirement> {

  @Query("START project=node({0}) MATCH project<-[:" + Relations.PROJECT_REQUIREMENT + "]-requirement WHERE NOT requirement<-[:" + Relations.REQUIREMENT_REQUIREMENT + "]-() RETURN requirement")
  public List<Requirement> findMainRequirementsByProject(Project project);
}
