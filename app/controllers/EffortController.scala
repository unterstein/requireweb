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

import neo4j.models.user.UserRole
import neo4j.models.require.{Requirement, Project}
import neo4j.services.Neo4JServiceProvider
import play.api.data.Form
import play.api.data.Forms._
import scala.collection.JavaConversions._
import org.apache.commons.lang.StringUtils
import neo4j.repositories.RequirementRepository.RequirementInfo
import play.api.mvc.AnyContent


object EffortController extends BaseController {

  def addEffort(id: Long) = AuthenticatedLoggingAction(UserRole.USER) {
    implicit request =>
      val project = Neo4JServiceProvider.get().projectRepository.findOne(id)
      val user = PlaySession.getUser
      // TODO contributors ?
      if (project != null && project.author.id == user.id) {
        Ok("test")
      } else {
        Ok(routes.RequirementController.requirementList().url)
      }
  }

  case class CaseEffort(effortId: Long, effortName: String, effortDescription: String, effortEffort: String)

  val projectForm: Form[CaseEffort] = Form(
    mapping(
      "effortId" -> default(longNumber, -1L),
      "effortName" -> nonEmptyText,
      "effortDescription" -> text,
      "effortEffort" -> text
    )(CaseEffort.apply)(CaseEffort.unapply))

}