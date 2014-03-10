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

import neo4j.models.require.Project;
import neo4j.models.require.Requirement;
import neo4j.relations.Relations;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

import java.util.List;

public interface RequirementRepository extends GraphRepository<Requirement> {

  @Query("START project=node({0}) MATCH project<-[:" + Relations.PROJECT_REQUIREMENT + "]-requirement WHERE NOT requirement-[:" + Relations.REQUIREMENT_REQUIREMENT + "]->() RETURN requirement")
  public List<Requirement> findMainRequirementsByProject(Project project);

  @Query("START start=node({1}), current=node({0}) MATCH project<-[:" + Relations.PROJECT_REQUIREMENT + "]-requirement WHERE NOT requirement-[:" + Relations.REQUIREMENT_REQUIREMENT + "*]->current return requirement")
  public List<Requirement> findPossibleParents(Project project, Requirement requirement);

  @Query("START start=node({0}) MATCH project<-[:" + Relations.PROJECT_REQUIREMENT + "]-requirement return requirement")
  public List<Requirement> findForProject(Project project);

  @Query("START requirement=node({0}) MATCH requirement<-[:" + Relations.REQUIREMENT_REQUIREMENT + "*]-children RETURN sum(children.estimatedEffort)")
  public double findChildEstimatedEffort(Requirement requirement);

  @Query("START requirement=node({0}) MATCH requirement<-[:" + Relations.REQUIREMENT_REQUIREMENT + "*]-children RETURN sum(children.realEffort)")
  public double findChildRealEffort(Requirement requirement);

  public static class RequirementInfo {

    public double childEstimatedEffort;

    public double childRealEffort;
  }
}
