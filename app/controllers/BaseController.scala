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
package controllers

import play.api.mvc._
import neo4j.models.log.PerformanceLog
import neo4j.models.log.LogCategory
import scala.{None, Option}
import scala.Predef._
import views.html
import play.api.i18n.Lang
import play.api.Play.current
import neo4j.models.user.{UserRole, User}
import neo4j.services.Neo4JServiceProvider
import scala.util.control.Exception.allCatch

trait BaseController extends Controller {

  def LoggingAction(f: Request[AnyContent] => Result): Action[AnyContent] = {
    Action {
      implicit request =>
        val serviceProvider: Neo4JServiceProvider = Neo4JServiceProvider.get()
        val tx = serviceProvider.template.getGraphDatabase.beginTx
        try {
          val start = System.currentTimeMillis()
          val result = f(request)
          val end = System.currentTimeMillis()
          PerformanceLog.create(end - start, LogCategory.REQUEST, request)
          tx.success()
          result
        }
        catch {
          case t: Throwable => {
            tx.failure()
            throw t
          }
        } finally {
          tx.finish()
        }
    }
  }

  private def userUsername(request: RequestHeader): Option[String] = {
    val username = request.session.get(PlaySession.AUTH_SESSION)
    if (username == None) {
      None
    } else {
      val checkIfUserExsists = User.checkIfUserExsists(username.get)
      if (checkIfUserExsists == true) {
        username // return value
      } else {
        None // return value
      }
    }
  }

  private def userOnUnauthorized(requestHeader: RequestHeader) = {
    val request = Request.apply(requestHeader, null)
    Results.Ok(html.signin.loginPage(AuthenticationController.loginForm, AuthenticationController.registerForm)(request, calcLang(request)))
  }

  def AuthenticatedLoggingAction(role: UserRole)(f: => Request[AnyContent] => Result) = Security.Authenticated(userUsername, userOnUnauthorized) {
    user => {
      val dbUser = Neo4JServiceProvider.get().userRepository.findByEmail(user)
      if(dbUser.hasMinRole(role)) {
        LoggingAction(request => f(request).withSession(PlaySession.AUTH_SESSION -> user))
      } else {
        LoggingAction(request => Redirect("404"))
      }
    }
  }

  def calcLang(request: Request[Any]): Lang = {
    Lang.preferred(request.acceptLanguages)
  }

  def isLongNumber(s: String): Boolean = (allCatch opt s.toLong).isDefined
  // or
  def isDoubleNumber(s: String): Boolean = (allCatch opt s.toDouble).isDefined
}