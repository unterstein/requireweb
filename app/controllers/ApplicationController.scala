package controllers

import play.api._
import play.api.mvc._

object ApplicationController extends BaseController {

  def index = LoggingAction {
    implicit request =>
      Redirect(routes.DashboardController.dashboard)
  }

  // -- Javascript routing
  /**
   * You must add all routes here, which you want to access via javascript in ajax way.
   */
  def jsRoutes = Action {
      implicit request =>
        import routes.javascript._
        Ok(
          Routes.javascriptRouter("jsRoutes")(
            
          )
        ).as("text/javascript")
  }
}