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
import neo4j.models.require.{OtherEffort, Requirement, Project}
import neo4j.services.Neo4JServiceProvider
import play.api.data.Form
import play.api.data.Forms._
import scala.collection.JavaConversions._
import org.apache.commons.lang.StringUtils
import neo4j.repositories.RequirementRepository.RequirementInfo
import play.api.mvc.AnyContent
import groovy.lang.{Binding,GroovyShell,Script}


object EffortController extends BaseController {

  val groovyEngine = new GroovyShell(new Binding())

  def addEffort(id: Long) = AuthenticatedLoggingAction(UserRole.USER) {
    implicit request =>
      val project = Neo4JServiceProvider.get().projectRepository.findOne(id)
      val user = PlaySession.getUser
      // TODO contributors ?
      if (project != null && project.author.id == user.id) {
        effortForm.bindFromRequest.fold(
          formWithErrors => Ok(views.html.require.effortEditDialog(-1L, project.id, formWithErrors, "create")),
          value => {
            val effort = OtherEffort.create(value.effortName, value.effortDescription, value.effortEffort, user, project)
            Ok(routes.RequirementController.requirementListIdEffort(project.id).url)
          })
      } else {
        Ok(routes.RequirementController.requirementList().url)
      }
  }

  def effortEditPanel(projectId: Long, id: Long) = AuthenticatedLoggingAction(UserRole.USER) {
    implicit request =>
      if(id > 0) {
        val effort = Neo4JServiceProvider.get().otherEffortRepository.findOne(id)
        val user = PlaySession.getUser
        // TODO contributors
        if (effort != null && effort.author.id == user.id) {
          val caseEffort = CaseEffort(effort.id, effort.name, effort.description, effort.effort)
          Ok(views.html.require.effortEditDialog(id, projectId, effortForm.fill(caseEffort), "edit"))
        } else {
          Ok(views.html.require.effortEditDialog(-1L, projectId, effortForm, "create"))
        }
      } else {
        Ok(views.html.require.effortEditDialog(-1L, projectId, effortForm, "create"))
      }
  }

  def editEffort(id: Long) = AuthenticatedLoggingAction(UserRole.USER) {
    implicit request =>
      val effort = Neo4JServiceProvider.get().otherEffortRepository.findOne(id)
      val user = PlaySession.getUser
      // TODO contributors
      if (effort != null && effort.author.id == user.id) {
        effortForm.bindFromRequest.fold(
          formWithErrors => Ok(views.html.require.effortEditDialog(id, effort.project.id, formWithErrors, "edit")),
          value => {
            effort.name = value.effortName
            effort.description = value.effortDescription
            effort.effort = value.effortEffort
            Neo4JServiceProvider.get().otherEffortRepository.save(effort)
            Ok(routes.RequirementController.requirementListIdEffort(effort.project.id).url)
          }
        )
      } else {
        Ok(routes.RequirementController.requirementList().url)
      }
  }

  def deleteEffort(id: Long) = AuthenticatedLoggingAction(UserRole.USER) {
    implicit request =>
      val effort = Neo4JServiceProvider.get().otherEffortRepository.findOne(id)
      val user = PlaySession.getUser
      val projectId = effort.project.id
        if (effort != null && effort.author.id == user.id) {
        Neo4JServiceProvider.get().otherEffortRepository.delete(effort)
      }
      Ok(routes.RequirementController.requirementListIdEffort(projectId).url)
  }

  case class CaseEffort(effortId: Long, effortName: String, effortDescription: String, effortEffort: String)

  val effortForm: Form[CaseEffort] = Form(
    mapping(
      "effortId" -> default(longNumber, -1L),
      "effortName" -> nonEmptyText,
      "effortDescription" -> text,
      "effortEffort" -> text.verifying("error.format", effort => {
        if(StringUtils.isBlank(effort)) { true } else {
          try {
            calcGroovyExpression(effort, 10)
            true
          } catch {
            case e: Exception => false
          }
        }
      })
    )(CaseEffort.apply)(CaseEffort.unapply))

  def calcGroovyExpression(effort: String, hourlyRate: Double): Double = {
    val script = groovyEngine.parse(effort.replace("," ,".").replace("h" ,"*h"))
    script.setProperty("h", hourlyRate)
    script.run().asInstanceOf[Double]
  }
}