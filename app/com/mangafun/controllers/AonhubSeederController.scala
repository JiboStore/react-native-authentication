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
import scala.concurrent.duration._
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
import play.api.libs.ws.WSClient
import play.api.libs.ws.WSRequest
import play.api.libs.ws.WSResponse
import com.mangafun.repos._
import akka.stream.scaladsl.Sink
import akka.util.ByteString
import scala.util.Random

/** TODO: https://stackoverflow.com/a/37180103/474330 */

class AonhubSeederController @Inject() (reactiveMongoApi: ReactiveMongoApi)(wsClient: WSClient)(appProvider: Provider[Application])
  extends Controller with MongoController with ReactiveMongoComponents {
  
  lazy val app = appProvider.get
  
  val urlHost = "http://mr.aonhub.com/apiv1/1/"
  
  def reactiveMongoApi() : ReactiveMongoApi = {
    return reactiveMongoApi;
  }
  
  def mangaRepo = new AonhubMangaRepoImpl(reactiveMongoApi)
  
//  // @dirtyUrl : http:\/\/mr.aonhub.com\/apiv1\/1\/2940?cid=1
//  def cleanUrl(dirtyUrl:String): String = {
//    val cleaned = dirtyUrl.filter(!"\\".contains(_))
//    Logger.error("cleaned url: " + cleaned)
//    cleaned
//  }
  

  
//  object ResultComicInfo {
//  import play.api.libs.json.Json
//  implicit val resultComicInfo = Json.format[ResultComicInfo]
//}
//
//object ResultComicResponse {
//  import play.api.libs.json.Json
//  implicit val resultComicResponse = Json.format[ResultComicResponse]
//}
  
  def getmangainfo(mangaId: Int): Action[AnyContent] = Action.async {
    val fManga = requestMangaAndChapterInfo(mangaId)
    val mangaEntry = Await.result(fManga, Duration.Inf)
    val aonhubManga = mangaRepo.constructMangaFromApiResponse(mangaId.toString(), mangaEntry)
    val lChapterEntries = for ( chapterInfo <- aonhubManga.chapters ) yield {
      val flString = requestPageInfo( chapterInfo.id )
      val lString = Await.result(flString, Duration.Inf)
      mangaRepo.constructMangaPagesFromApiResponse(chapterInfo, lString)
    }
    aonhubManga.chapters = lChapterEntries
    val jsValue = Json.toJson(aonhubManga)
    val strAll = Json.prettyPrint(jsValue)
    Logger.debug(strAll)
//    val lChapterEntries = for ( chapterInfo <- mangaEntry.chapters ) yield {
//      val chUrl = cleanUrl( chapterInfo.id )
//      val fPages = requestPageInfo(chUrl)
//      val lPages = Await.result(fPages, Duration.Inf)
//      chapterInfo.pages = lPages
//      chapterInfo
//    }
//    mangaEntry.chapters = lChapterEntries
//    val jsv = Json.toJson(mangaEntry)
//    val strAll = Json.prettyPrint(jsv)
//    Logger.debug(strAll)
    
    Future {
      Ok(strAll)
    }
  }
  
  def requestMangaAndChapterInfo(mangaId: Int): Future[AonhubMangaEntry] = {
    val urlReq = urlHost + mangaId
    val wsRequest = wsClient.url(urlReq)
    .withHeaders(("Accept" -> "application/json"))
    val fResponse = wsRequest.get()
    val fResult = fResponse.map( wsres => {
      wsres.json.as[AonhubMangaEntry]
    })
    fResult
  }
  
  def requestPageInfo(chapterUrl: String): Future[List[String]] = {
    val wsRequest = wsClient.url(chapterUrl)
    .withHeaders(("Accept" -> "application/json"))
    val fResponse = wsRequest.get()
    val fResult = fResponse.map( wsres => {
      wsres.json.as[List[String]]
    })
    fResult
  }
  
//      def requestComicInfo(resultSearchResponse: ResultSearchResponse): Future[List[ResultComicResponse]] = {
//      val urlReq = getUrlHost() + "/comic"
//      val wsRequest: WSRequest = wsClient.url(urlReq)
//      .withHeaders(("Accept" -> "application/json"))
//      val lfResultComicResponse = for ( info <- resultSearchResponse.results ) yield {
//        Logger.debug("requestComicInfo: " + urlReq + "?c=" + info.resultFullUrl)
//        val fResponse = wsRequest.withQueryString(("c" -> info.resultFullUrl)).get()
//        val fResultComicResponse = fResponse.map( wsres => {
////          Logger.debug("wsres: " + wsres.body)
//          wsres.json.as[ResultComicResponse]
//        })
//        fResultComicResponse
//      }
//      Future.sequence(lfResultComicResponse)
//    }
  
}