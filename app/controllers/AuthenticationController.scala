/**
 * Copyright (C) 2014 Johannes Unterstein, unterstein@me.com, unterstein.info
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 * http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package controllers

import play.api.data._
import play.api.data.Forms._
import play.api.i18n.Messages
import views._
import play.api.mvc._
import neo4j.models.user.User
import neo4j.models.log._
import neo4j.services.Neo4JServiceProvider
import _root_.global.Global

object AuthenticationController extends BaseController {

  def login = LoggingAction {
    implicit request =>
      if (PlaySession.isLoggedIn == true) {
        Redirect(routes.ApplicationController.index)
      } else {
        Ok(html.signin.loginPage(loginForm, registerForm))
      }
  }

  def registerRedirect = LoggingAction {
    implicit request =>
      Redirect(routes.ApplicationController.index)
  }

  def loginUser = LoggingAction {
    implicit request =>
      loginForm.bindFromRequest.fold(
        formWithErrors => Ok(html.signin.loginPage(formWithErrors, registerForm)),
        value => {
          val user = Neo4JServiceProvider.get().userRepository.findByEmail(value.loginEmail)
          PlayLog.create("User logged in", LogLevel.INFO, LogCategory.SESSION, request, user)
          Redirect(value.loginUrl).withSession(PlaySession.AUTH_SESSION -> value.loginEmail)
        }
      )
  }


  def registerUser = LoggingAction {
    implicit request =>
      if(Global.registerAllowed) {
        registerForm.bindFromRequest.fold(
          formWithErrors => Ok(html.signin.loginPage(loginForm, formWithErrors)),
          value => {
            val user = User.create(value.registerEmail, value.registerPassword, null) // create the user
            PlayLog.create("User registered", LogLevel.INFO, LogCategory.SESSION, request, user)
            Redirect(value.registerUrl).withSession(PlaySession.AUTH_SESSION -> value.registerEmail) // redirect please
          }
        )
      } else {
        Redirect(routes.AuthenticationController.login)
      }
  }

  def logout = LoggingAction {
    implicit request =>
      PlayLog.create("User logged out", LogLevel.INFO, LogCategory.SESSION, request, PlaySession.getUser)
      Redirect(routes.ApplicationController.index()).withNewSession
  }

  case class Register(registerUrl: String, registerEmail: String, registerPassword: String, repassword: String)

  val registerForm: Form[Register] = Form(
    mapping(
      "registerUrl" -> text,
      "registerEmail" -> email,
      "registerPassword" -> nonEmptyText(minLength = 5),
      "repassword" -> nonEmptyText(minLength = 5)
    )(Register.apply)(Register.unapply)
      .verifying("error.passwordNotMatch", register => register.registerPassword == register.repassword)
      .verifying("error.duplicateEmail", register => {
      User.checkIfUserExsists(register.registerEmail) == false // return value
    })
  )

  case class Login(loginUrl: String, loginEmail: String, loginPassword: String)

  val loginForm: Form[Login] = Form(
    mapping(
      "loginUrl" -> text,
      "loginEmail" -> email,
      "loginPassword" -> nonEmptyText
    )(Login.apply)(Login.unapply)
      .verifying("error.signInFailed", login => {
      val user = User.authenticate(login.loginEmail, login.loginPassword)
      user != null // return value
    })
  )

}

