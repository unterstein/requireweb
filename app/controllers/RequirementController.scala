package controllers

import neo4j.models.user.UserRole
import neo4j.models.require.Project
import neo4j.services.Neo4JServiceProvider


object RequirementController extends BaseController {

  def requirementList = AuthenticatedLoggingAction(UserRole.USER) {
    implicit request =>
      Ok(views.html.require.requireListPage())
  }

  def requirementListId(id: Long) = AuthenticatedLoggingAction(UserRole.USER) {
    implicit request =>
      val project = Neo4JServiceProvider.get().projectRepository.findOne(id)
      val user = PlaySession.getUser
      // TODO contributors
      if(project != null && project.author.id == user.id) {
        Ok(views.html.require.requireListPage(project))
      } else {
        Redirect(routes.RequirementController.requirementList)
      }

  }

  def addProject(name: String, description: String) = AuthenticatedLoggingAction(UserRole.USER) {
    implicit request =>
      val project = Project.create(name, description, PlaySession.getUser)
      Ok(routes.RequirementController.requirementListId(project.id).url)
  }

}