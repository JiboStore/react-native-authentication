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
  
  def constructMangaPagesFromApiResponse(chapter: AonhubChapter, lPages: List[String]) = {
    val lCleanedPages = for ( strUrl <- lPages ) yield {
      cleanUrl(strUrl)
    }
    chapter.pages = lCleanedPages
    chapter
  }
//  def constructMangaFromApiResponse(searchResponse: ResultSearchResponse): Manga = {
//    var manga = new Manga(
//        searchResponse.results(0).resultName,
//        searchResponse.results(0).resultFullUrl,
//        searchResponse.results(0).resultThumbImageUrl,
//        searchResponse.results(0).resultChapters,
//        searchResponse.results(0).resultType,
//        searchResponse.results(0).resultGenre,
//        0,
//        List[Chapter]()
//    )
//    manga
//  }
//  
//  def updateMangaFromApiResponse(manga: Manga, comicResponse: ResultComicResponse): Manga = {
//    manga.chapterCount = comicResponse.chapterCount
//    val lCh = for ( chRes <- comicResponse.chapters ) yield {
//      new Chapter(
//          chRes.chapterFullUrl,
//          chRes.chapterTitle,
//          chRes.chapterDescription.getOrElse(""),
//          chRes.chapterDate,
//          0,
//          List[Page]()
//      )
//    }
//    manga.chapters = lCh
//    manga
//  }
//  
//  def updateMangaFromApiResponse(manga: Manga, chapterResponse: ResultChapterResponse): Manga = {
//    var oChapter = manga.chapters.find( ch => {
//      ch.chapterUrl == chapterResponse.chapterUrl
//    })
//    var lPages = oChapter match {
//      case Some(chapter) => {
//        chapter.pageCount = chapterResponse.pageCount
//        val pages = for ( pageRes <- chapterResponse.pages ) yield {
//          new Page(
//              pageRes.pageNumber.toInt,
//              pageRes.pageFullUrl,
//              List[Image]()
//          )
//        }
//        chapter.pages = pages
//        pages
//      }
//      case None => {
//        List[Page]()
//      }
//    }
//    manga
//  }
//  
//  def updateMangaFromApiResponse(manga: Manga, pageResponse: ResultPageResponse): Manga = {
//    // TODO: update List[Image] in pages
//    val llPages = for( ch <- manga.chapters ) yield {
//      ch.pages
//    }
//    val lPages = llPages.flatten
//    val oPage = lPages.find( p => {
//      p.pageUrl == pageResponse.pageUrl
//    })
////    val lImages = for ( pageInfo <- pageResponse.pageImage ) yield {
//      val image = new Image(
//          pageResponse.pageImage.imageWidth.toInt,
//          pageResponse.pageImage.imageHeight.toInt,
//          pageResponse.pageImage.imageSource,
//          pageResponse.pageImage.imageAlt
//      )
////      image
////    }
//    val lImages = List(image)
//    val page = oPage match {
//      case Some(p) => {
//        p.images = lImages
//        p
//      }
//    }
//    
//    var oChapter = manga.chapters.find( ch => {
//      var oPage = ch.pages.find( p => {
//        p.pageUrl == page.pageUrl
//      })
//      oPage.isDefined
//    })
//    val newChapter = oChapter match {
//      case Some(chapter) => {
//        val lPages = for ( p <- chapter.pages ) yield {
//          if ( p.pageUrl == page.pageUrl ) {
//            page
//          } else {
//            p
//          }
//        }
//        chapter.pages = lPages
//        chapter
//      }
//    }
//   val lChapters = for ( ch <- manga.chapters ) yield {
//     if ( ch.chapterUrl == newChapter.chapterUrl ) {
//       newChapter
//     }
//     ch
//   }
//   manga.chapters = lChapters
//   manga
//  }
}