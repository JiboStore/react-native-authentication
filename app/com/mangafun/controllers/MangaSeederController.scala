package com.mangafun.controllers

import play.api.mvc._
import javax.inject.Inject
import play.modules.reactivemongo.ReactiveMongoApi
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.ReactiveMongoComponents
import play.api.libs.json._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import reactivemongo.bson._
import reactivemongo.api.commands.WriteResult
import play.Logger
import scala.concurrent._
import scala.util.Failure
import scala.util.Success
import play.Play
import play.Application
import com.google.inject.Provider
import scala.collection.mutable.ListBuffer
import java.io.File
import java.nio.charset.Charset
import org.apache.commons.io.FileUtils
import scala.collection.JavaConversions._

/** TODO: https://stackoverflow.com/a/37180103/474330 */

class MangaSeederController @Inject() (reactiveMongoApi: ReactiveMongoApi)(appProvider: Provider[Application])
  extends Controller with MongoController with ReactiveMongoComponents {
  
  lazy val app = appProvider.get
  
  def reactiveMongoApi() : ReactiveMongoApi = {
    return reactiveMongoApi;
  }
  
  def index() : Action[AnyContent] = Action.async {
    val result = readMangaJson()
    result.map( list => {
      Ok("here: " + list)
    })
  }
  
  def readMangaJson(): Future[List[String]] = {
    Future {
      blocking {
//        var res = new ListBuffer[String]
        var f = app.getFile("/resources/manga.json")
        var list: java.util.List[String] = FileUtils.readLines(f, "UTF-8")
        var scalaList = list.toList
        scalaList
      }
    }
  }
  
}