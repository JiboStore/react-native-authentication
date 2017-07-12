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
  
  case class MangaSearchData(var mangaId: String, var name: String)
  
  implicit val mangaSearchDataJson = Json.format[MangaSearchData]
  
  def index(): Action[AnyContent] = Action.async {
    val fString = readMangaToString()
    val fMangas: Future[List[MangaSearchData]] = fString.map( strArray => {
        Json.parse(strArray).as[List[MangaSearchData]]
    })
    fMangas.map( lMangas => {
      Ok(lMangas.mkString(" :: "))
    })
  }
  
  def index_string() : Action[AnyContent] = Action.async {
//    val result = readMangaToList()
//    result.map( list => {
//      Ok("here: " + list)
//    })
    val r = readMangaToString()
    r.map( s => {
      Ok(s)
    })
  }
  
  def readMangaToList(): Future[List[String]] = {
    Future {
      blocking {
//        var res = new ListBuffer[String]
//        var f = app.getFile("/resources/manga.json") // ok
//        var f = FileUtils.getFile("/resources/manga.json") // not working
//        var f = new File("/resources/manga.json") // not working
//        var f = new File("resources/manga.json") // ok
        var f = FileUtils.getFile("resources/manga.json") // ok, so we don't need to inject Play.application() anymore
        var list: java.util.List[String] = FileUtils.readLines(f, "UTF-8")
        var scalaList = list.toList
        scalaList
      }
    }
  }
  
  def readMangaToString(): Future[String] = {
    Future {
      blocking {
        var f = FileUtils.getFile("resources/manga.json")
        FileUtils.readFileToString(f, "UTF-8")
      }
    }
  }
  
}