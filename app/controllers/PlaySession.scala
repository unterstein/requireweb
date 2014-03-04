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

import neo4j.models.user.{UserRole, User}
import scala.Boolean

object PlaySession extends BaseController {
  def AUTH_SESSION: String = "EMAIL"

  def getUser(implicit request: play.api.mvc.RequestHeader): User = {
    val token = session.get(AUTH_SESSION)
    if (token == None || token.get == null) {
      null
    } else {
      User.getUserByEMail(token.get)
    }
  }

  def isLoggedIn(implicit request: play.api.mvc.RequestHeader): Boolean = {
    getUser != null
  }

  def isAdmin(implicit request: play.api.mvc.RequestHeader): Boolean = hasMinRole(UserRole.ADMIN)

  def hasMinRole(role: UserRole)(implicit request: play.api.mvc.RequestHeader): Boolean = getUser.hasMinRole(role)

}