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
  
  val resourcesRoot = "resources/sources/aonhub/"
  
  def reactiveMongoApi() : ReactiveMongoApi = {
    return reactiveMongoApi;
  }
  
  def mangaRepo = new AonhubMangaRepoImpl(reactiveMongoApi)
  
  def readMangaList(): String = {
    var f = FileUtils.getFile( resourcesRoot + "manga.json")
    FileUtils.readFileToString(f, "UTF-8")
  }
  
  def writeMangaToString(manga: AonhubManga) = {
    val strPath = resourcesRoot + "json/" // "resources/sources/aonhub/json/"
    val jsv = Json.toJson(manga)
    val strContent = Json.prettyPrint(jsv)
    val fileSave = FileUtils.getFile(strPath + manga.id)
    FileUtils.writeStringToFile(fileSave, strContent)
    
  }
  
  def updatemangajson(): Action[AnyContent] = Action.async {
    val wsRequest = wsClient.url(urlHost)
    .withHeaders(("Accept" -> "application/json"))
    val fResponse = wsRequest.get()
    val fResult = fResponse.map( wsres => {
      val strResult = Json.prettyPrint(wsres.json)
      val strPath = resourcesRoot + "manga.json" //"resources/sources/aonhub/manga.json"
      val fileSave = FileUtils.getFile(strPath)
      FileUtils.writeStringToFile(fileSave, strResult)
      strResult
    })
    fResult.map( res => {
      Ok(res)
    })
  }
  
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
    writeMangaToString(aonhubManga)
    val jsValue = Json.toJson(aonhubManga)
    val strAll = Json.prettyPrint(jsValue)
    Logger.debug(strAll)
    
    Future {
      Ok(strAll)
    }
  }
  
  def getfromto(startingIndex: Int, endingIndex: Int): Action[AnyContent] = Action.async {
    val strManga = readMangaList()
    val jsv = Json.parse(strManga)
    val lMangaSearchData = jsv.as[List[AonhubSearchData]]
    val iStartingIndex = if (startingIndex < 0 || startingIndex >= lMangaSearchData.length) 0 else startingIndex
    var iEndingIndex = if ( endingIndex < 0 || endingIndex >= lMangaSearchData.length ) lMangaSearchData.length-1 else endingIndex
    if ( iEndingIndex < iStartingIndex ) {
      iEndingIndex = iStartingIndex+1
    }
    var iCurrentIndex = iStartingIndex
    var iCount = 0
    for ( iCurrentIndex <- iStartingIndex to iEndingIndex ) {
      try {
        val oData = lMangaSearchData.find( e => {
          e.id == iCurrentIndex // todo: change rank to id
        })
        if ( oData.isDefined ) {
          val data = oData.get
          val mangaId = data.id // todo: change rank to id
          val fEntry = requestMangaAndChapterInfo(mangaId)
          val mangaEntry = Await.result(fEntry, Duration.Inf)
          val aonhubManga = mangaRepo.constructMangaFromApiResponse(mangaId.toString(), mangaEntry)
          val lChapterEntries = for ( chapterInfo <- aonhubManga.chapters ) yield {
            val flString = requestPageInfo( chapterInfo.id )
            val lString = Await.result(flString, Duration.Inf)
            mangaRepo.constructMangaPagesFromApiResponse(chapterInfo, lString)
          }
          aonhubManga.chapters = lChapterEntries
          writeMangaToString(aonhubManga)
        } else {
          Logger.error("Unable to find: " + iCurrentIndex)
        }
        iCount = iCount+1
      } catch {
        case ex: Exception => {
          Logger.error("Exception: " + iCurrentIndex + ex.getMessage())
          Logger.error("Stack trace: " + ex.getStackTrace.toString())
        }
      }
    }
    Future {
      Ok("done")
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

  
}