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
package global

import play.api._
import scala.Boolean
import java.io.File
import neo4j.services.Neo4JServiceProvider
import neo4j.models.user.{UserRole, User}

object Global extends GlobalSettings {
  private var app: Application = null

  private var performanceLogging: Boolean = false
  private var playLogLogging: Boolean = false
  private var fileSystemPath: String = null
  private var fileSystemImplementation: String = null

  override def onStart(app: Application) = {
    this.app = app
    //    Neo4JServiceProvider.init()
    initDevLogging()
    initFileSystem()
    initInitialData()
  }

  private def initInitialData() = {
    if (Neo4JServiceProvider.get().userRepository.count() == 0) {
      val johannes = User.create("unterstein@me.com", "password", null)
      johannes.userRole = UserRole.ADMIN
      johannes.name = "Johannes Unterstein"
      Neo4JServiceProvider.get().userRepository.save(johannes)
      Neo4JServiceProvider.get().userRepository.save(User.create("test@test.de", "test", johannes))
    }
  }

  private def initFileSystem() = {
    var fileSystemPath: String = config().getString("fileSystem.path").get
    if (fileSystemPath.contains("~")) {
      fileSystemPath = fileSystemPath.replace("~", System.getProperty("user.home"))
    }
    val file: File = new File(fileSystemPath)
    Logger.info("FileSystem path is: " + file.getAbsolutePath)
    if (file != null && file.exists == false) {
      file.mkdirs
    }
    if (file != null && file.exists == true && file.isDirectory == false) {
      Logger.error("FileSystem path does exist, but is not a folder!")
    }
    if (fileSystemPath.endsWith("/") == false) {
      fileSystemPath += "/"
    }
    this.fileSystemPath = fileSystemPath
    this.fileSystemImplementation = config().getString("fileSystem.implementation").get
  }

  def getFileSystemPath(): String = fileSystemPath

  def getFileSystemImplementation(): String = fileSystemImplementation

  private def initDevLogging() = {
    performanceLogging = Play.isProd(app) || config().getBoolean("dev.performance.logging").getOrElse(false)
    playLogLogging = Play.isProd(app) || config().getBoolean("dev.playLog.logging").getOrElse(false)
  }

  def config(): Configuration = app.configuration

  def isDev: Boolean = Play.isDev(app)

  def registerAllowed: Boolean = config().getBoolean("register.allowed").getOrElse(false)

  def isPerformanceLogging = performanceLogging

  def isPlayLogLogging = playLogLogging
}