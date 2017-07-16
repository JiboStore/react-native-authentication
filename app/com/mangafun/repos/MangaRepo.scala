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
case class Manga (
//    var index: Int,                   // 0 based sequence number
    var mangaTitle: String,           // title [resultName]
    var mangaUrl: String,             // [resultFullUrl]
    var mangaThumbUrl: String,        // [resultThumbImageUrl]
    var mangaChaptersInfo: String,    // [resultChapters]
    var mangaReadType: String,        // [resultType]
    var mangaGenre: String,           // [resultGenre]
    var chapterCount: Int,            // [chapterCount] comic?c=mangaUrl
    var chapters: List[Chapter]
) {
  
}

/** http://localhost:3003/comic?c=http://www.mangareader.net/video-girl-ai */
case class Chapter (
    var chapterUrl: String,            // [chapterFullUrl]
    var chapterTitle: String,          // [chapterTitle]
    var chapterDescription: String,    // [chapterDescription]
    var chapterDate: String,           // [chapterDate]
    var pageCount: Int,                // [pageCount] chapters?c=http://www.mangareader.net/video-girl-ai/1
    var pages: List[Page]
) {

}

/** http://localhost:3003/chapters?c=http://www.mangareader.net/video-girl-ai/1 */
case class Page (
    var pageNumber: Int,                // [pageNumber]
    var pageUrl: String,                // [pageFullUrl]
    var images: List[Image]
) {
  
}

/** http://localhost:3003/page?p=http://www.mangareader.net/video-girl-ai/1/2 */
case class Image(
    var imageWidth: Int,                 // [imageWidth]
    var imageHeight: Int,                // [imageHeight]
    var imageUrl: String,                // [imageSource]
    var imageAlt: String                 // [imageAlt]
) {
}

/** http://localhost:3003/page?p=http://www.mangareader.net/video-girl-ai/1/2 */

object Manga {
  import play.api.libs.json.Json
  
  // Generates Writes and Reads
  implicit val imageJsonFormats = Json.format[Image]
  implicit val pageJsonFormats = Json.format[Page]
  implicit val chapterJsonFormats = Json.format[Chapter]
  implicit val mangaJsonFormats = Json.format[Manga]
  
  implicit val imageBsonFormats = Macros.handler[Image]
  implicit val pageBsonFormats = Macros.handler[Page]
  implicit val chapterBsonFormats = Macros.handler[Chapter]
  implicit val mangaBsonFormats = Macros.handler[Manga]
}

trait MangaRepo {
  
}

class MangaRepoImpl @Inject() (reactiveMongoApi: ReactiveMongoApi) extends MangaRepo {
  
  import play.api.libs.concurrent.Execution.Implicits.defaultContext
 
  def jsonCollection: JSONCollection = reactiveMongoApi.db.collection[JSONCollection]("manga");
  def bsonCollection: BSONCollection = reactiveMongoApi.db.collection[BSONCollection]("manga");
  
  def constructMangaFromApiResponse(searchResponse: ResultSearchResponse): Manga = {
    var manga = new Manga(
        searchResponse.results(0).resultName,
        searchResponse.results(0).resultFullUrl,
        searchResponse.results(0).resultThumbImageUrl,
        searchResponse.results(0).resultChapters,
        searchResponse.results(0).resultType,
        searchResponse.results(0).resultGenre,
        0,
        List[Chapter]()
    )
    manga
  }
  
  def updateMangaFromApiResponse(manga: Manga, comicResponse: ResultComicResponse): Manga = {
    manga.chapterCount = comicResponse.chapterCount
    val lCh = for ( chRes <- comicResponse.chapters ) yield {
      new Chapter(
          chRes.chapterFullUrl,
          chRes.chapterTitle,
          chRes.chapterDescription.getOrElse(""),
          chRes.chapterDate,
          0,
          List[Page]()
      )
    }
    manga.chapters = lCh
    manga
  }
  
  def updateMangaFromApiResponse(manga: Manga, chapterResponse: ResultChapterResponse): Manga = {
    var oChapter = manga.chapters.find( ch => {
      ch.chapterUrl == chapterResponse.chapterUrl
    })
    var lPages = oChapter match {
      case Some(chapter) => {
        chapter.pageCount = chapterResponse.pageCount
        val pages = for ( pageRes <- chapterResponse.pages ) yield {
          new Page(
              pageRes.pageNumber.toInt,
              pageRes.pageFullUrl,
              List[Image]()
          )
        }
        chapter.pages = pages
        pages
      }
      case None => {
        List[Page]()
      }
    }
    manga
  }
  
  def updateMangaFromApiResponse(manga: Manga, pageResponse: ResultPageResponse): Manga = {
    // TODO: update List[Image] in pages
    val llPages = for( ch <- manga.chapters ) yield {
      ch.pages
    }
    val lPages = llPages.flatten
    val oPage = lPages.find( p => {
      p.pageUrl == pageResponse.pageUrl
    })
//    val lImages = for ( pageInfo <- pageResponse.pageImage ) yield {
      val image = new Image(
          pageResponse.pageImage.imageWidth.toInt,
          pageResponse.pageImage.imageHeight.toInt,
          pageResponse.pageImage.imageSource,
          pageResponse.pageImage.imageAlt
      )
//      image
//    }
    val lImages = List(image)
    val page = oPage match {
      case Some(p) => {
        p.images = lImages
        p
      }
    }
    
    var oChapter = manga.chapters.find( ch => {
      var oPage = ch.pages.find( p => {
        p.pageUrl == page.pageUrl
      })
      oPage.isDefined
    })
    val newChapter = oChapter match {
      case Some(chapter) => {
        val lPages = for ( p <- chapter.pages ) yield {
          if ( p.pageUrl == page.pageUrl ) {
            page
          } else {
            p
          }
        }
        chapter.pages = lPages
        chapter
      }
    }
   val lChapters = for ( ch <- manga.chapters ) yield {
     if ( ch.chapterUrl == newChapter.chapterUrl ) {
       newChapter
     }
     ch
   }
   manga.chapters = lChapters
   manga
  }
}