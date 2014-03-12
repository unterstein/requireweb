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
package neo4j.repositories;

import neo4j.models.require.OtherEffort;
import neo4j.models.require.Project;
import neo4j.relations.Relations;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

import java.util.List;

public interface OtherEffortRepository extends GraphRepository<OtherEffort> {

  @Query("START start=node({0}) MATCH project<-[:" + Relations.PROJECT_EFFORT + "]-effort return effort")
  public List<OtherEffort> findForProject(Project project);

}
