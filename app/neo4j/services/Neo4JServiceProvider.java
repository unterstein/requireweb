package neo4j.services;

import neo4j.repositories.*;
import neo4jplugin.Neo4JPlugin;
import neo4jplugin.ServiceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Neo4JServiceProvider extends ServiceProvider {

  @Autowired
  public UserRepository userRepository;

  @Autowired
  public PlayLogRepository playLogRepository;

  @Autowired
  public PerformanceLogRepository performanceLogRepository;

  @Autowired
  public RequirementRepository requirementRepository;

  @Autowired
  public ProjectRepository projectRepository;

  public static Neo4JServiceProvider get() {
    return Neo4JPlugin.get();
  }
}

