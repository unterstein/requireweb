package controllers

import neo4j.models.user.UserRole
import neo4j.models.require.Project


object RequirementController extends BaseController {

  def requirementList = AuthenticatedLoggingAction(UserRole.USER) {
    implicit request =>
      Ok(views.html.require.requireListPage())
  }

  def addProject(name: String, description: String) = AuthenticatedLoggingAction(UserRole.USER) {
    implicit request =>
      val project = Project.create(name, description, PlaySession.getUser)
      Ok("")
  }

}