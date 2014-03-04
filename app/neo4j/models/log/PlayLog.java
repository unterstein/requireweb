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
import neo4j.relations.Relations;
import neo4j.services.Neo4JServiceProvider;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.neo4j.graphdb.Direction;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;
import play.Logger;
import play.api.mvc.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@NodeEntity
@TypeAlias("PlayLog")
public class PlayLog extends AbstractLog {

  public String message;

  public String postParams;

  public String exception;

  @RelatedTo(type = Relations.ENTITY_LOG, direction = Direction.INCOMING)
  public Set<AbstractModel> relatedTo;

  public String httpQueryParams;

  public static void create(String message, LogLevel logLevel, LogCategory logCategory, RequestHeader request, AbstractModel... relatedTo) {
    create(message, logLevel, logCategory, request, Arrays.asList(relatedTo));
  }

  public static void create(String message, LogLevel logLevel, LogCategory logCategory, RequestHeader request, Throwable exception, AbstractModel... relatedTo) {
    create(message, logLevel, logCategory, request, exception, Arrays.asList(relatedTo));
  }

  public static void create(String message, LogLevel logLevel, LogCategory logCategory, RequestHeader request, List<AbstractModel> relatedTo) {
    create(message, logLevel, logCategory, request, null, relatedTo);
  }

  public static void create(String message, LogLevel logLevel, LogCategory logCategory, RequestHeader request, Throwable exception, List<AbstractModel> relatedTo) {
    if (Global.isPlayLogLogging() == true) {
      PlayLog result = new PlayLog();
      result.message = message;

      abstractCreate(result, logCategory, logLevel, request, relatedTo);

      if (exception != null) {
        result.exception = ExceptionUtils.getStackTrace(exception);
      }

      if (request instanceof Request) {
        Request<AnyContent> requestAny = (Request<AnyContent>) request;
        if (requestAny.body() instanceof AnyContentAsFormUrlEncoded) {
          result.postParams = requestAny.body().asFormUrlEncoded().toString();
        }
        if (requestAny.body() instanceof AnyContentAsText) {
          result.postParams = requestAny.body().asText().toString();
        }
        if (requestAny.body() instanceof AnyContentAsJson) {
          result.postParams = requestAny.body().asJson().toString();
        }
        if (requestAny.body() instanceof AnyContentAsXml) {
          result.postParams = requestAny.body().asXml().toString();
        }
        if (requestAny.body() instanceof AnyContentAsMultipartFormData) {
          result.postParams = requestAny.body().asMultipartFormData().toString();
        }
      }

      Neo4JServiceProvider.get().playLogRepository.save(result);
    }

    if (Global.isDev() == true) {
      switch (logLevel) {
        case ERROR:
          Logger.error(message, exception);
          break;
        case IMPORTANT:
          Logger.info(message, exception);
          break;
        case INFO:
          Logger.info(message, exception);
          break;
        case WARN:
          Logger.warn(message, exception);
          break;
        default:
          break;
      }
    }
  }
}
