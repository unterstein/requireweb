package neo4j.models;

import neo4j.relations.Relations;
import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.util.Set;

@NodeEntity
public class CommentAbleModel extends AbstractAuthoredModel {

  @RelatedTo(type = Relations.COMMENTABLE_COMMENT, direction = Direction.INCOMING)
  public Set<Comment> comments;

}
