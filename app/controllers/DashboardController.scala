package controllers

import neo4j.models.user.UserRole


object DashboardController extends BaseController {

  def dashboard = AuthenticatedLoggingAction(UserRole.USER) {
    implicit request =>
      Ok(views.html.dashboard.dashboardPage())
  }

}