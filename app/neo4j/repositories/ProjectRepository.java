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
import neo4j.models.user.User;
import neo4j.relations.Relations;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.annotation.QueryResult;
import org.springframework.data.neo4j.annotation.ResultColumn;
import org.springframework.data.neo4j.repository.GraphRepository;

import java.util.List;

public interface ProjectRepository extends GraphRepository<Project> {

  @Query("START user=node({0}) MATCH user-[:" + Relations.MODEL_AUTHOR + "|" + Relations.PROJECT_CONTRIBUTOR + "]->project WHERE (project: Project) RETURN project ORDER BY project.id ASC")
  public List<Project> findByAuthorOrContributor(User author);

  @Query("START project=node({0}) MATCH project<-[:" + Relations.PROJECT_REQUIREMENT + "*]-requirement, project<-[:" + Relations.PROJECT_EFFORT + "]-effort RETURN sum(requirement.estimatedEffort) as estimatedEffort, sum(requirement.realEffort) as realEffort, count(requirement) as requirementAmount, collect(effort.effort) as efforts")
  public ProjectInfo calcProjectInfo(Project project);

  @QueryResult
  public static interface ProjectInfo {

    @ResultColumn("estimatedEffort")
    public double getTotalEstimatedEfforts();

    @ResultColumn("realEffort")
    public double getTotalRealEfforts();

    @ResultColumn("requirementAmount")
    public int getRequirementAmount();

    @ResultColumn("efforts")
    public List<String> getEfforts();
  }
}
