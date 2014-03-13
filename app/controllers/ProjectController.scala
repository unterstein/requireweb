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
import neo4j.models.require.Project
import neo4j.services.Neo4JServiceProvider
import play.api.data.Form
import play.api.data.Forms._
import scala.collection.JavaConversions._
import org.apache.commons.lang.StringUtils

object ProjectController extends BaseController {

  def addProject() = AuthenticatedLoggingAction(UserRole.USER) {
    implicit request =>
      projectForm.bindFromRequest.fold(
        formWithErrors => Ok(views.html.require.projectEditDialog(-1L, formWithErrors, "create")),
        value => {
          val project = Project.create(value.shortName.toUpperCase, value.projectName, value.projectDescription, PlaySession.getUser)
          if (StringUtils.isNotBlank(value.projectHourlyRate)) {
            project.hourlyRate = java.lang.Double.parseDouble(value.projectHourlyRate.replace(",", "."))
            Neo4JServiceProvider.get().projectRepository.save(project)
          }
          Ok(routes.RequirementController.requirementListId(project.id).url)
        }
      )
  }

  def editProject(id: Long) = AuthenticatedLoggingAction(UserRole.USER) {
    implicit request =>
      val project = Neo4JServiceProvider.get().projectRepository.findOne(id)
      val user = PlaySession.getUser
      // TODO contributors
      if (project != null && project.author.id == user.id) {
        projectForm.bindFromRequest.fold(
          formWithErrors => Ok(views.html.require.projectEditDialog(id, formWithErrors, "edit")),
          value => {
            project.shortName = value.shortName.toUpperCase
            project.name = value.projectName
            project.description = value.projectDescription
            if (StringUtils.isNotBlank(value.projectHourlyRate)) {
              project.hourlyRate = java.lang.Double.parseDouble(value.projectHourlyRate.replace(",", "."))
            }
            Neo4JServiceProvider.get().projectRepository.save(project)
            Ok(routes.RequirementController.requirementListId(project.id).url)
          }
        )
      } else {
        Ok(routes.RequirementController.requirementList().url)
      }
  }

  def projectInfoPanel(id: Long) = AuthenticatedLoggingAction(UserRole.USER) {
    implicit request =>
      if(id > 0) {
        val project = Neo4JServiceProvider.get().projectRepository.findOne(id)
        val user = PlaySession.getUser
        // TODO contributors
        if (project != null && project.author.id == user.id) {
          val info = Neo4JServiceProvider.get().projectRepository.calcProjectInfo(project)
          var otherEfforts = 0.0
          info.getEfforts.map { effort =>
            otherEfforts += EffortController.calcGroovyExpression(effort, project.hourlyRate)
          }
          Ok(views.html.require.projectInfoDialog(project, info, otherEfforts))
        } else {
          Ok("") // TODO error dialog
        }
      } else {
        Ok("") // TODO error dialog
      }
  }

  def projectEditPanel(id: Long) = AuthenticatedLoggingAction(UserRole.USER) {
    implicit request =>
      if(id > 0) {
        val project = Neo4JServiceProvider.get().projectRepository.findOne(id)
        val user = PlaySession.getUser
        // TODO contributors
        if (project != null && project.author.id == user.id) {
          val caseProject = CaseProject(project.id, project.shortName, project.name, project.description, if(project.hourlyRate > 0) { "" + project.hourlyRate } else { "" })
          Ok(views.html.require.projectEditDialog(-1L, projectForm.fill(caseProject), "edit"))
        } else {
          Ok(views.html.require.projectEditDialog(-1L, projectForm, "create"))
        }
      } else {
        Ok(views.html.require.projectEditDialog(-1L, projectForm, "create"))
      }
  }

  def deleteProject(id: Long) = AuthenticatedLoggingAction(UserRole.USER) {
    implicit request =>
      val project = Neo4JServiceProvider.get().projectRepository.findOne(id)
      val user = PlaySession.getUser
      if (project != null && project.author.id == user.id) {
        if(project.requirements != null) {
          project.requirements.map { requirement =>
            RequirementController.deleteRequirementRecursive(requirement)
          }
        }
        Neo4JServiceProvider.get().projectRepository.delete(project)
      }
      Ok(routes.RequirementController.requirementList().url)
  }

  case class CaseProject(id: Long, shortName: String, projectName: String, projectDescription: String, projectHourlyRate: String)

  val projectForm: Form[CaseProject] = Form(
    mapping(
      "projectId" -> default(longNumber, -1L),
      "shortName" -> nonEmptyText(minLength = 3, maxLength = 10),
      "projectName" -> nonEmptyText,
      "projectDescription" -> text,
      "projectHourlyRate" ->  text.verifying("error.format", hourlyRate => {
        if(StringUtils.isBlank(hourlyRate)) { true } else { isDoubleNumber(hourlyRate.replace("," ,".")) }
      })
    )(CaseProject.apply)(CaseProject.unapply))
}