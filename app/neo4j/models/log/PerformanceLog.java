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
package neo4j.models.log;

import global.Global;
import neo4j.models.AbstractModel;
import neo4j.services.Neo4JServiceProvider;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.neo4j.annotation.NodeEntity;
import play.Logger;
import play.api.mvc.AnyContent;
import play.api.mvc.Request;

import java.util.LinkedList;

@NodeEntity
@TypeAlias("PerformanceLog")
public class PerformanceLog extends AbstractLog {

  public long duration;

  public static void create(long duration, LogCategory logCategory, String text) {
    create(duration, logCategory, null, text);
  }

  public static void create(long duration, LogCategory logCategory, Request<AnyContent> request) {
    create(duration, logCategory, request, null);
  }

  public static void create(long duration, LogCategory logCategory, Request<AnyContent> request, String text) {
    if (Global.isPerformanceLogging() == true) {
      PerformanceLog result = new PerformanceLog();
      result.duration = duration;
      abstractCreate(result, logCategory, LogLevel.INFO, request, new LinkedList<AbstractModel>());
      if (text != null) {
        result.httpUrl += " - " + text;
      }
      Neo4JServiceProvider.get().performanceLogRepository.save(result);
    } else {
      String requestString = logCategory.toString();
      if (request != null) {
        requestString += " " + request.uri();
      }
      if (text != null) {
        requestString += " - " + text;
      }
      Logger.info(requestString + " took " + duration + " ms");
    }
  }
}
