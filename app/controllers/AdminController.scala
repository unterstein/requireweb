package controllers

import views._
import neo4j.models.log._
import neo4j.models.user.UserRole

object AdminController extends BaseController {

  def performance = AuthenticatedLoggingAction(UserRole.ADMIN) {
    implicit request =>
      Ok(html.admin.performancePage())
  }

  def allPerformance = AuthenticatedLoggingAction(UserRole.ADMIN) {
    implicit request =>
      Ok(html.admin.allPerformancePage())
  }

  def logs = AuthenticatedLoggingAction(UserRole.ADMIN) {
    implicit request =>
      Ok(html.admin.logPage(null))
  }

  def logsByLogLevel(logLevel: String) = AuthenticatedLoggingAction(UserRole.ADMIN) {
    implicit request =>
      try {
        Ok(html.admin.logPage(LogLevel.valueOf(logLevel)))
      } catch {
        case e: Exception => Redirect(routes.AdminController.logs)
      }
  }

}