package neo4j.models.require;

import neo4j.models.CommentAbleModel;
import org.springframework.data.neo4j.annotation.NodeEntity;

@NodeEntity
public class EffortBasedModel extends CommentAbleModel {

  public double estimatedEffort;

  public double realEffort;

}
