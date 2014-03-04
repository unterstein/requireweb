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

import neo4j.models.log.LogCategory;
import neo4j.models.log.PerformanceLog;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.annotation.QueryResult;
import org.springframework.data.neo4j.annotation.ResultColumn;
import org.springframework.data.neo4j.repository.GraphRepository;

import java.util.List;

public interface PerformanceLogRepository extends GraphRepository<PerformanceLog> {

  List<PerformanceLog> findByLogCategory(LogCategory logCategory);

  @Query("MATCH(perf: PerformanceLog) RETURN min(perf.duration) as min, max(perf.duration) as max, avg(perf.duration) as avg, count(perf) as total")
  RequestResult findRequestResult();

  @Query("MATCH(perf: PerformanceLog) WHERE has(perf.httpUrl) WITH distinct perf.httpUrl as url, perf WHERE perf.httpUrl = url WITH url, perf as matches RETURN url, count(matches) as total, min(matches.duration) as min, max(matches.duration) as max, avg(matches.duration) as avg")
  List<UrlMap> findRequestMaps();

  @Query("MATCH(perf: PerformanceLog) WHERE perf.created >= \"\" + (timestamp()- {0}) return count(perf) as result")
  Long findTimeResult(long diff);

  @QueryResult
  public interface UrlMap {

    @ResultColumn("url")
    String getUrl();

    @ResultColumn("max")
    long getMax();

    @ResultColumn("min")
    long getMin();

    @ResultColumn("avg")
    long getAvg();

    @ResultColumn("total")
    long getTotal();
  }

  @QueryResult
  public interface RequestResult {

    @ResultColumn("min")
    long getMin();

    @ResultColumn("max")
    long getMax();

    @ResultColumn("avg")
    long getAvg();

    @ResultColumn("total")
    long getTotal();
  }
}
