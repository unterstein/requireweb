package controllers

import neo4j.models.user.UserRole
import neo4j.models.require.Project
import neo4j.services.Neo4JServiceProvider


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
      // TODO param access
      val project = Project.create(request.body.asFormUrlEncoded.get("name")(0), request.body.asFormUrlEncoded.get("description")(0), PlaySession.getUser)
      Ok(routes.RequirementController.requirementListId(project.id).url)
  }

  def editProject(id: Long) = AuthenticatedLoggingAction(UserRole.USER) {
    implicit request =>
      val project = Neo4JServiceProvider.get().projectRepository.findOne(id)
      val user = PlaySession.getUser
      // TODO contributors
      if (project != null && project.author.id == user.id) {
        // TODO param access
        project.name = request.body.asFormUrlEncoded.get("name")(0)
        project.description = request.body.asFormUrlEncoded.get("description")(0)
        Neo4JServiceProvider.get().projectRepository.save(project)
        Ok(routes.RequirementController.requirementListId(project.id).url)
      } else {
        Ok(routes.RequirementController.requirementList().url)
      }
  }

  def delete(id: Long) = AuthenticatedLoggingAction(UserRole.USER) {
    implicit request =>
      val project = Neo4JServiceProvider.get().projectRepository.findOne(id)
      val user = PlaySession.getUser
      if (project != null && project.author.id == user.id) {
        Neo4JServiceProvider.get().projectRepository.delete(project)
      }
      Ok(views.html.require.requireListPage())
  }

}