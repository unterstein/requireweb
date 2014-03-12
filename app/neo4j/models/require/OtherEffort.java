package neo4j.models.require;

import neo4j.models.user.User;
import neo4j.relations.Relations;
import neo4j.services.Neo4JServiceProvider;
import org.neo4j.graphdb.Direction;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

@NodeEntity
@TypeAlias("OtherEffort")
public class OtherEffort extends EffortBasedModel {

  @RelatedTo(type = Relations.PROJECT_EFFORT, direction = Direction.OUTGOING)
  public Project project;

  public String effort;

  public static OtherEffort create(String name, String description, String effort, User author, Project project) {
    OtherEffort result = new OtherEffort();
    result.name = name;
    result.description = description;
    result.author = author;
    result.project = project;
    result.effort = effort;
    Neo4JServiceProvider.get().otherEffortRepository.save(result);
    return result;
  }
}
