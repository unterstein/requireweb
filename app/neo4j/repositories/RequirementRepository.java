package neo4j.repositories;

import neo4j.models.require.Requirement;
import org.springframework.data.neo4j.repository.GraphRepository;

public interface RequirementRepository extends GraphRepository<Requirement> {

}
