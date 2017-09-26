package com.mangafun.repos

import scala.concurrent._

import javax.inject.Inject
import play.api.libs.json._
//import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.ReadPreference
import reactivemongo.api.commands.WriteResult
import reactivemongo.bson._
import be.objectify.deadbolt.scala.models.Subject
import reactivemongo.api.Cursor
import scala.collection.immutable.HashMap
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api.commands.bson.BSONCountCommand.{ Count, CountResult }
import reactivemongo.api.commands.bson.BSONCountCommandImplicits._
import reactivemongo.bson.BSONDocument
import reactivemongo.play.json.collection.JSONCollection
import scala.collection.mutable.ListBuffer

/** http://localhost:3003/search?t=video */
case class AonhubManga (
    var id: String,
    var rank: String,
    var name: String,
    var categories: String,
    var author: String,
    var imageUrl: String,
    var status: String,
    var description: String,
    var chapters: List[AonhubChapter]
) {  
}

case class AonhubChapter (
    var id: String,            // eg: http:\/\/mr.aonhub.com\/apiv1\/1\/2940?cid=1
    var name: String,          // eg: 1
    var pages: List[String]    // eg: http:\/\/i2.mangapanda.com\/is\/113\/is-1262168.jpg
) {
}

object AonhubManga {
  implicit val aonhubChapterJsonFormats = Json.format[AonhubChapter]
  implicit val aonhubMangaJsonFormats = Json.format[AonhubManga]
  
  implicit val aonhubChapterBsonFormats = Macros.handler[AonhubChapter]
  implicit val aonhubMangaBsonFormats = Macros.handler[AonhubManga]
}

trait AonhubMangaRepo {
  
}

class AonhubMangaRepoImpl @Inject() (reactiveMongoApi: ReactiveMongoApi) extends AonhubMangaRepo {
  
  import play.api.libs.concurrent.Execution.Implicits.defaultContext
 
  def jsonCollection: JSONCollection = reactiveMongoApi.db.collection[JSONCollection]("aonhubmanga");
  def bsonCollection: BSONCollection = reactiveMongoApi.db.collection[BSONCollection]("aonhubmanga");
  
  // @dirtyUrl : http:\/\/mr.aonhub.com\/apiv1\/1\/2940?cid=1
  def cleanUrl(dirtyUrl:String): String = {
    val cleaned = dirtyUrl.filter(!"\\".contains(_))
    cleaned
  }
  
  def constructMangaFromApiResponse(mangaId: String, mangaResponse: AonhubMangaEntry): AonhubManga = {
    val lChapters = for ( chapterInfo <- mangaResponse.chapters ) yield {
      constructMangaChaptersFromApiResponse(chapterInfo)
    }
    val manga = new AonhubManga(
        mangaId,
        mangaResponse.rank,
        mangaResponse.name,
        mangaResponse.categories,
        mangaResponse.author,
        cleanUrl(mangaResponse.image),
        mangaResponse.status,
        mangaResponse.des,
        lChapters
    )
    manga
  }
  
  def constructMangaChaptersFromApiResponse(chapterInfo: AonhubChapterEntry): AonhubChapter = {
    val chapter = new AonhubChapter(
        cleanUrl(chapterInfo.id),
        chapterInfo.name,
        List()
    )
    chapter
  }
  
  def constructMangaPagesFromApiResponse(chapter: AonhubChapter, lPages: List[String]): AonhubChapter = {
    val lCleanedPages = for ( strUrl <- lPages ) yield {
      cleanUrl(strUrl)
    }
    chapter.pages = lCleanedPages
    chapter
  }
}