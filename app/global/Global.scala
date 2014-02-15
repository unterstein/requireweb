package global

import play.api._
import scala.Boolean
import java.io.File

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
    performanceLogging = Play.isProd(app) || config().getBoolean("dev.performance.logging").get
    playLogLogging = Play.isProd(app) || config().getBoolean("dev.playLog.logging").get
  }

  def config(): Configuration = app.configuration

  def isDev: Boolean = Play.isDev(app)

  def isPerformanceLogging = performanceLogging

  def isPlayLogLogging = playLogLogging
}