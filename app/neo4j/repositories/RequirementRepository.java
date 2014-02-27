package neo4j.repositories;

import neo4j.models.require.Requirement;
import neo4j.models.user.User;
import org.springframework.data.neo4j.repository.GraphRepository;

import java.util.Set;

public interface RequirementRepository extends GraphRepository<Requirement> {

  public Set<Requirement> findByAuthorOrderByIdAsc(User author);
}
