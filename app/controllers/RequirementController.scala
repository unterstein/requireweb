package controllers

import neo4j.models.user.UserRole


object RequirementController extends BaseController {

  def requirementList = AuthenticatedLoggingAction(UserRole.USER) {
      implicit request =>
        Ok(views.html.require.requireListPage())
  }

}