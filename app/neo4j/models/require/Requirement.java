package neo4j.models.require;

import neo4j.models.CommentAbleModel;
import neo4j.models.user.User;
import neo4j.relations.Relations;
import neo4j.services.Neo4JServiceProvider;
import org.neo4j.graphdb.Direction;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.neo4j.annotation.GraphProperty;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.util.Date;
import java.util.Iterator;
import java.util.Set;

@NodeEntity
@TypeAlias("Requirement")
public class Requirement extends CommentAbleModel {

  @RelatedTo(type = Relations.REQUIREMENT_REQUIREMENT, direction = Direction.OUTGOING)
  public Requirement parent;

  @RelatedTo(type = Relations.REQUIREMENT_REQUIREMENT, direction = Direction.INCOMING)
  public Set<Requirement> children;

  public String headline;

  public String text;

  public boolean accepted;

  @GraphProperty(propertyType = Long.class)
  public Date dueDate;

  public boolean implemented;

  public double effort;

  public static Iterator<Requirement> findListForUser(User user) {
    return Neo4JServiceProvider.get().requirementRepository.findByAuthorOrderByIdAsc(user).iterator();
  }
}