package controllers

import neo4j.services.Neo4JServiceProvider
import neo4j.models.user.{UserState, UserRole, User}
import play.api.data.Form
import play.api.data.Forms._
import org.apache.commons.lang.StringUtils
import helper.HashHelper

object UserController extends BaseController {

  def userList = AuthenticatedLoggingAction(UserRole.USER) {
    implicit request =>
      Ok(views.html.user.userListPage())
  }

  def create = AuthenticatedLoggingAction(UserRole.ADMIN) {
    implicit request =>
      Ok(views.html.user.userEditPage(-1L, userForm, "create"))
  }

  def delete(id: Long) = AuthenticatedLoggingAction(UserRole.ADMIN) {
    implicit request =>
      val user = Neo4JServiceProvider.get().userRepository.findOne(id)
      Neo4JServiceProvider.get().userRepository.delete(user)
      Ok(views.html.user.userListPage())
  }

  def postEdit(id: Long) = AuthenticatedLoggingAction(UserRole.USER) {
    implicit request =>
      userForm.bindFromRequest.fold(
        formWithErrors => {
          Ok(views.html.user.userEditPage(id, formWithErrors, "edit"))
        },
        value => {
          val dbUser = Neo4JServiceProvider.get().userRepository.findOne(id)
          // admins are allowed to change all user, others are allowed to change only them self
          if (PlaySession.hasMinRole(UserRole.ADMIN) || PlaySession.getUser.id == dbUser.id) {

            if (StringUtils.isNotBlank(value.password) && (PlaySession.isAdmin || PlaySession.getUser.id == dbUser.id)) {
              dbUser.password = HashHelper.getInstance.sha(value.password)
            }
            dbUser.email = value.email
            dbUser.name = value.name
            if (PlaySession.isAdmin) {
              dbUser.userRole = UserRole.valueOf(value.userRole)
              dbUser.userState = UserState.valueOf(value.userState)
            }
            dbUser.description = value.description
            // TODO history data + changer
            Neo4JServiceProvider.get().userRepository.save(dbUser)
            Redirect(routes.UserController.userList)
          } else {
            Ok(views.html.user.userEditPage(0L, userForm.fill(value).withError("", "error.notRight"), "edit"))
          }
        }
      )
  }

  def postCreate = AuthenticatedLoggingAction(UserRole.ADMIN) {
    implicit request =>
      userForm.bindFromRequest.fold(
        formWithErrors => {
          Ok(views.html.user.userEditPage(0L, formWithErrors, "create"))
        },
        value => {
          if (Neo4JServiceProvider.get().userRepository.findByEmail(value.email) == null) {
            val user = User.create(value.email, value.password, PlaySession.getUser)
            user.name = value.name
            if (PlaySession.isAdmin) {
              user.userRole = UserRole.valueOf(value.userRole)
              user.userState = UserState.valueOf(value.userState)
            } else {
              user.userRole = UserRole.USER
              user.userState = UserState.ACTIVE
            }
            user.description = value.description
            Neo4JServiceProvider.get().userRepository.save(user)
            Redirect(routes.UserController.userList)
          }
          else {
            Ok(views.html.user.userEditPage(0L, userForm.fill(value).withError("email", "error.duplicateEmail"), "create"))
          }
        }
      )
  }

  def edit(id: Long) = AuthenticatedLoggingAction(UserRole.USER) {
    implicit request =>
      val user = Neo4JServiceProvider.get().userRepository.findOne(id)
      // admins are allowed to change all user, others are allowed to change only them self
      if (user != null && (PlaySession.hasMinRole(UserRole.ADMIN) || user.id == PlaySession.getUser.id)) {
        val caseUser = CaseUser(user.name, user.email, "", user.userState.toString, user.userRole.toString, user.description)
        Ok(views.html.user.userEditPage(id, userForm.fill(caseUser), "edit"))
      } else {
        Redirect(routes.UserController.userList)
      }
  }

  case class CaseUser(name: String, email: String, password: String, userState: String, userRole: String, description: String)

  val userForm: Form[CaseUser] = Form(
    mapping(
      "name" -> text,
      "email" -> email,
      "password" -> text,
      "userState" -> nonEmptyText.verifying("error.choose", userState => UserState.enumModel().keys.contains(userState.toString)),
      "userRole" -> nonEmptyText.verifying("error.choose", userRole => UserRole.enumModel().keys.contains(userRole.toString)),
      "description" -> text
    )(CaseUser.apply)(CaseUser.unapply))

}