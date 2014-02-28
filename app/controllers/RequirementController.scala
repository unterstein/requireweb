package controllers

import neo4j.models.user.UserRole
import neo4j.models.require.Project
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
          val project = Project.create(value.requireName, value.requireDescription, PlaySession.getUser)
          Ok(routes.RequirementController.requirementListId(project.id).url)
        }
      )
      Ok(routes.RequirementController.requirementList().url)
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
            project.name = value.requireName
            project.description = value.requireDescription
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

      }
      Ok(views.html.require.requireListPage())
  }


  case class CaseProject(requireName: String, requireDescription: String)

  val projectForm: Form[CaseProject] = Form(
    mapping(
      "requireName" -> nonEmptyText,
      "requireDescription" -> text
    )(CaseProject.apply)(CaseProject.unapply))
}