package controllers

import neo4j.models.user.UserRole
import neo4j.models.require.{Requirement, Project}
import neo4j.services.Neo4JServiceProvider
import play.api.data.Form
import play.api.data.Forms._


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
        formWithErrors => Ok(formWithErrors.errorsAsJson),
        value => {
          val project = Project.create(value.projectName, value.projectDescription, PlaySession.getUser)
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
          formWithErrors => Ok(formWithErrors.errorsAsJson),
          value => {
            project.name = value.projectName
            project.description = value.projectDescription
            Neo4JServiceProvider.get().projectRepository.save(project)
            Ok(routes.RequirementController.requirementListId(project.id).url)
          }
        )
      } else {
        Ok(routes.RequirementController.requirementList().url)
      }
  }

  def deleteProject(id: Long) = AuthenticatedLoggingAction(UserRole.USER) {
    implicit request =>
      val project = Neo4JServiceProvider.get().projectRepository.findOne(id)
      val user = PlaySession.getUser
      if (project != null && project.author.id == user.id) {
        Neo4JServiceProvider.get().projectRepository.delete(project)
      }
      Ok(views.html.require.requireListPage())
  }

  def addRequirement(id: Long) = AuthenticatedLoggingAction(UserRole.USER) {
    implicit request =>
      val project = Neo4JServiceProvider.get().projectRepository.findOne(id)
      val user = PlaySession.getUser
      // TODO contributors ?
      if (project != null && project.author.id == user.id) {
        requireForm.bindFromRequest.fold(
          formWithErrors => Ok(formWithErrors.errorsAsJson),
          value => {
            val requirement = Requirement.create(value.requireName, value.requireDescription, user, project)
            Ok(routes.RequirementController.requirementListId(id).url)
          }
        )
      } else {
        Ok(routes.RequirementController.requirementList().url)
      }
  }


  case class CaseProject(projectName: String, projectDescription: String)

  val projectForm: Form[CaseProject] = Form(
    mapping(
      "projectName" -> nonEmptyText,
      "projectDescription" -> text
    )(CaseProject.apply)(CaseProject.unapply))

  case class CaseRequirement(requireName: String, requireDescription: String)

  val requireForm: Form[CaseRequirement] = Form(
    mapping(
      "requireName" -> nonEmptyText,
      "requireDescription" -> text
    )(CaseRequirement.apply)(CaseRequirement.unapply))
}