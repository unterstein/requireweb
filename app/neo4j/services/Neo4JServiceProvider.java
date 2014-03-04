/**
 * Copyright (C) 2014 Johannes Unterstein, unterstein@me.com, unterstein.info
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 * http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

