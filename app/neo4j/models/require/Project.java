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
package neo4j.models.require;

import neo4j.models.CommentAbleModel;
import neo4j.models.user.User;
import neo4j.relations.Relations;
import neo4j.services.Neo4JServiceProvider;
import org.neo4j.graphdb.Direction;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.util.Set;

@NodeEntity
@TypeAlias("Project")
public class Project extends CommentAbleModel {

  @RelatedTo(type = Relations.PROJECT_REQUIREMENT, direction = Direction.INCOMING)
  public Set<Requirement> requirements;

  @RelatedTo(type = Relations.PROJECT_CONTRIBUTOR, direction = Direction.INCOMING)
  public Set<User> contributors;

  public ProjectState projectState;

  public String shortName;

  public static Project create(String shortName, String name, String description, User author) {
    Project result = new Project();
    result.shortName = shortName;
    result.name = name;
    result.description = description;
    result.author = author;
    result.projectState = ProjectState.IN_PLANNING;
    Neo4JServiceProvider.get().projectRepository.save(result);
    return result;
  }
}
