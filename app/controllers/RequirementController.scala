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


object RequirementController extends BaseController {

  def requirementList = AuthenticatedLoggingAction(UserRole.USER) {
    implicit request =>
    // TODO not load all projects
      val projects = Neo4JServiceProvider.get().projectRepository.findByAuthorOrContributor(PlaySession.getUser)
      if (projects.size() > 0) {
        Ok(views.html.require.requireListPage(projects.get(0)))
      } else {
        Ok(views.html.require.requireListPage())
      }
  }

  def requirementListId(id: Long) = AuthenticatedLoggingAction(UserRole.USER) {
    implicit request =>
      val project = Neo4JServiceProvider.get().projectRepository.findOne(id)
      val user = PlaySession.getUser
      // TODO contributors
      if (project != null && project.author.id == user.id) {
        Ok(views.html.require.requireListPage(project))
      } else {
        Redirect(routes.RequirementController.requirementList())
      }
  }

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

  def requirementEditPanel(projectId: Long, id: Long) = AuthenticatedLoggingAction(UserRole.USER) {
    implicit request =>
      if(id > 0) {
        val requirement = Neo4JServiceProvider.get().requirementRepository.findOne(id)
        val user = PlaySession.getUser
        // TODO contributors
        if (requirement != null && requirement.author.id == user.id) {
          val caseRequirement = CaseRequirement(requirement.id, requirement.name, requirement.description,
            if(requirement.parent != null) requirement.parent.id else -1L,
            requirement.project.id, "" + requirement.estimatedEffort)
          Ok(views.html.require.requirementEditDialog(id, projectId, requireForm.fill(caseRequirement), "edit"))
        } else {
          Ok(views.html.require.requirementEditDialog(-1L, projectId, requireForm, "create"))
        }
      } else {
        Ok(views.html.require.requirementEditDialog(-1L, projectId, requireForm, "create"))
      }
  }

  def requirementInfoPanel(id: Long) = AuthenticatedLoggingAction(UserRole.USER) {
    implicit request =>
      if(id > 0) {
        val requirement = Neo4JServiceProvider.get().requirementRepository.findOne(id)
        val user = PlaySession.getUser
        // TODO contributors
        if (requirement != null && requirement.author.id == user.id) {
          val info = new RequirementInfo
          info.childRealEffort = Neo4JServiceProvider.get().requirementRepository.findChildRealEffort(requirement)
          info.childEstimatedEffort = Neo4JServiceProvider.get().requirementRepository.findChildEstimatedEffort(requirement)
          Ok(views.html.require.requirementInfoDialog(requirement, info))
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
            deleteRequirementRecursive(requirement)
          }
        }
        Neo4JServiceProvider.get().projectRepository.delete(project)
      }
      Ok(routes.RequirementController.requirementList().url)
  }

  def deleteRequirement(id: Long) = AuthenticatedLoggingAction(UserRole.USER) {
    implicit request =>
      val requirement = Neo4JServiceProvider.get().requirementRepository.findOne(id)
      val projectId = requirement.project.id
      val user = PlaySession.getUser
      if (requirement != null && requirement.author.id == user.id) {
        deleteRequirementRecursive(requirement)
      }
      Ok(routes.RequirementController.requirementListId(projectId).url)
  }

  private def deleteRequirementRecursive(inputRequirement: Requirement): Unit = {
    val requirement = Neo4JServiceProvider.get().requirementRepository.findOne(inputRequirement.id)
    if(requirement.children != null) {
      requirement.children.map { children =>
        deleteRequirementRecursive(children)
      }
    }
    Neo4JServiceProvider.get().requirementRepository.delete(requirement)
  }

  def addRequirement(id: Long) = AuthenticatedLoggingAction(UserRole.USER) {
    implicit request =>
      val project = Neo4JServiceProvider.get().projectRepository.findOne(id)
      val user = PlaySession.getUser
      // TODO contributors ?
      if (project != null && project.author.id == user.id) {
        requireForm.bindFromRequest.fold(
          formWithErrors => Ok(views.html.require.requirementEditDialog(-1L, project.id, formWithErrors, "create")),
          value => {
            val requirement = Requirement.create(value.requireName, value.requireDescription, user, project)
            if (value.requireParent > 0) {
              val parent = Neo4JServiceProvider.get().requirementRepository.findOne(value.requireParent)
              if (parent != null && parent.project.id == requirement.project.id) {
                requirement.parent = parent
              }
            }
            if (StringUtils.isNotBlank(value.requireEstimatedEffort)) {
              requirement.estimatedEffort = java.lang.Double.parseDouble(value.requireEstimatedEffort.replace(",", "."))
            }
            Neo4JServiceProvider.get().requirementRepository.save(requirement)
            Ok(routes.RequirementController.requirementListId(id).url)
          }
        )
      } else {
        Ok(routes.RequirementController.requirementList().url)
      }
  }

  def editRequirement(id: Long) = AuthenticatedLoggingAction(UserRole.USER) {
    implicit request =>
      val requirement = Neo4JServiceProvider.get().requirementRepository.findOne(id)
      val user = PlaySession.getUser
      // TODO contributors
      if (requirement != null && requirement.author.id == user.id) {
        requireForm.bindFromRequest.fold(
          formWithErrors => Ok(views.html.require.requirementEditDialog(id, requirement.project.id, formWithErrors, "edit")),
          value => {
            // TODO compare requirement.project with id
            requirement.name = value.requireName
            requirement.description = value.requireDescription
            if (StringUtils.isNotBlank(value.requireEstimatedEffort)) {
              requirement.estimatedEffort = java.lang.Double.parseDouble(value.requireEstimatedEffort.replace(",", "."))
            }

            var parent: Requirement = null
            // change parent id
            if (value.requireParent == -1) {
              // change to root requirement
              parent = null
            } else if (value.requireParent > 0 && requirement.parent == null) {
              // change from root requirement to sub requirement
              parent = Neo4JServiceProvider.get().requirementRepository.findOne(value.requireParent)
            } else if (requirement.parent != null && value.requireParent != requirement.parent.id) {
              // change parent requirement
              parent = Neo4JServiceProvider.get().requirementRepository.findOne(value.requireParent)
            }
            if (parent != null) {
              // TODO contributors
              if (parent.author.id == user.id) {
                requirement.parent = parent
              }
            } else {
              requirement.parent = parent
            }

            Neo4JServiceProvider.get().requirementRepository.save(requirement)
            Ok(routes.RequirementController.requirementListId(requirement.project.id).url)
          }
        )
      } else {
        Ok(routes.RequirementController.requirementList().url)
      }
  }

  def toggleExpandedState(id: Long, expanded: Boolean) = AuthenticatedLoggingAction(UserRole.USER) {
    implicit request =>
      val requirement = Neo4JServiceProvider.get().requirementRepository.findOne(id)
      val user = PlaySession.getUser
      // TODO contributors
      if (requirement != null && requirement.author.id == user.id) {
        requirement.expanedInUi = expanded
        Neo4JServiceProvider.get().requirementRepository.save(requirement)
      }
      Ok("")
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

  case class CaseRequirement(requireId: Long, requireName: String, requireDescription: String, requireParent: Long, requireProjectId: Long, requireEstimatedEffort: String)

  val requireForm: Form[CaseRequirement] = Form(
    mapping(
      "requireId" -> default(longNumber, -1L),
      "requireName" -> nonEmptyText,
      "requireDescription" -> text,
      "requireParent" -> longNumber,
      "requireProjectId" -> longNumber,
      "requireEstimatedEffort" -> text.verifying("error.format", effort => {
        if(StringUtils.isBlank(effort)) { true } else { isDoubleNumber(effort.replace("," ,".")) }
      })
    )(CaseRequirement.apply)(CaseRequirement.unapply))
}