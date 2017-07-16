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
import play.api.libs.ws.WSClient
import play.api.libs.ws.WSRequest
import play.api.libs.ws.WSResponse
import com.mangafun.repos._

/** TODO: https://stackoverflow.com/a/37180103/474330 */

class MangaSeederController @Inject() (reactiveMongoApi: ReactiveMongoApi)(wsClient: WSClient)(appProvider: Provider[Application])
  extends Controller with MongoController with ReactiveMongoComponents {
  
  lazy val app = appProvider.get
  
  val urlHost = "http://localhost:3003"
  
  def reactiveMongoApi() : ReactiveMongoApi = {
    return reactiveMongoApi;
  }
  
  def mangaRepo = new MangaRepoImpl(reactiveMongoApi)
  
  case class MangaSearchData(var mangaId: String, var name: String)
  
  implicit val mangaSearchDataJson = Json.format[MangaSearchData]
  
  def index_obj1(): Action[AnyContent] = Action.async {
    val fString = readMangaToString()
    val fMangas: Future[List[MangaSearchData]] = fString.map( strArray => {
//        Json.parse(strArray).as[List[MangaSearchData]]
      val jsv: JsValue = Json.parse(strArray)
      val jsl: List[MangaSearchData] = jsv.as[List[MangaSearchData]]
      jsl
    })
    
    val fResults = fMangas.flatMap( lMangas => {
      val lRes: List[Future[ResultSearchResponse]] = for ( m <- lMangas ) yield {
        // TODO: do not request the whole list, only request a range
        requestSearchInfo(m)
      }
      Future.sequence(lRes)
    })
    
    var sRes = ""
    var fList = fResults.map( lObj => {
      lObj.foreach( obj => { 
          sRes += (obj.searchTerm + " " + obj.resultCount + " > " + obj.resultPageCount + " > " +
          obj.results(0).resultName + " >> " + obj.results(0).resultUrl + " >> " + obj.results(0).resultFullUrl + " >> " +
          obj.results(0).resultThumbImageUrl + " >> " + obj.results(0).resultChapters + " >> " +
          obj.results(0).resultType + " >> " + obj.results(0).resultGenre) 
      })
      Ok(sRes)
    })
    
    fList
    
    // TODO: fList is still a Unit!
    
//    fResults.map( str => {
//      Ok( str.mkString(", "))
//    })
    
//    fMangas.map( lMangas => {
//      Ok(lMangas.mkString(" :: "))
//    })
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
  
//  def requestSearchInfo(mangaSearchData: MangaSearchData): Future[String] = {
////    val urlApi = urlHost + "/search?t="
////    val urlReq = urlApi + mangaSearchData.mangaId
//    val urlReq = urlHost + "/search"
//    // TODO: make the request
//    val wsRequest: WSRequest = wsClient.url(urlReq)
//      .withHeaders(("Accept" -> "application/json"))
//      .withQueryString(("t" -> mangaSearchData.mangaId))
//    val fResponse: Future[WSResponse] = wsRequest.get()
//    val fRet = fResponse.map( res => {
//      res.body
////      val jsVal = res.json
////      jsVal.as[String]
//    })
//    fRet
//  }
  
  def index(): Action[AnyContent] = Action.async {
    val fString = readMangaToString()
    val fMangas: Future[List[MangaSearchData]] = fString.map( strArray => {
      val jsv: JsValue = Json.parse(strArray)
      val jsl: List[MangaSearchData] = jsv.as[List[MangaSearchData]]
      jsl
    })
    
    val flResults = fMangas.flatMap( lMangas => {
      val lRes: List[Future[ResultSearchResponse]] = for ( m <- lMangas ) yield {
        // TODO: do not request the whole list, only request a range
        requestSearchInfo(m)
      }
      Future.sequence(lRes)
    })
    
    val flManga = flResults.map( lResults => {
      Logger.debug("size results: " + lResults.count( r => true ))
      val lMangas = for ( resultSearchResponse <- lResults ) yield {
        val manga: Manga = mangaRepo.constructMangaFromApiResponse(resultSearchResponse)
        manga
      }
      Logger.debug("size manga constructed: " + lMangas.count(m => true))
      lMangas
    })
    
    val flfManga = for {
        lResultSearchResponse <- flResults
        lManga <- flManga
    } yield {
      Logger.debug("size manga 1: " + lManga.count(m => true))
      Logger.debug("size ResultSearchResponse: " + lResultSearchResponse.count(m => true))
      for ( m <- lManga ) yield {
        val rsr = lResultSearchResponse.find( r => {
          r.results(0).resultFullUrl == m.mangaUrl
        })
        val fManga = rsr match { 
            case Some(res) => {
              val flResultComicResponse = requestComicInfo(res)
              val fM = flResultComicResponse.map( lRcr => {
                lRcr.foreach( r => {
                  mangaRepo.updateMangaFromApiResponse(m, r)
                })
                m
              })
              fM
            }
            case None => {
              Future {
                m
              }
            }
        }
        fManga
      }
    } // end yield
    
    val fManga = flfManga.flatMap( lfManga => {
      Logger.debug("size lfManga: " + lfManga.count(m => true))
      Future.sequence( lfManga )
    })
    
    val flUpdatedManga = fManga.flatMap( lManga => {
      val llManga = for ( manga <- lManga ) yield {
        Logger.debug("size lManga.flatten: " + lManga.count(m => true))
        val lfManga = for ( ch <- manga.chapters ) yield {
          val fResponse = requestChapterInfo( ch.chapterUrl )
          val fUpdatedManga = fResponse.map ( chapterResponse => {
            mangaRepo.updateMangaFromApiResponse(manga, chapterResponse)
          })
          fUpdatedManga
        }
        lfManga.get(0)
      }
      val lRes = llManga
      Logger.debug("size llManga.flatten: " + lRes.count(m => true)) // here is the problem, size is 63
      Future.sequence(lRes)
    })
    
    val floUpdatedManga = flUpdatedManga.flatMap( listManga => {
      Logger.debug("size listManga: " + listManga.count(m => true))
      val llfoManga = for ( manga <- listManga ) yield {
        val lfoManga = for ( ch <- manga.chapters ) yield {
          val lfResult = for ( p <- ch.pages ) yield {
            requestPageInfo(p.pageUrl)
          }
          val flResult = Future.sequence(lfResult)
          val fManga = flResult.map( lResult => {
            val listUpdatedManga = for ( r <- lResult ) yield {
              mangaRepo.updateMangaFromApiResponse(manga, r)
            }
            val opM = listUpdatedManga.find( m => {
              m.mangaUrl == manga.mangaUrl
            })
            opM
          })
          fManga
        }
        Logger.debug("size lfoManga: " + lfoManga.count(m => true))
        lfoManga
      }
      
      val floManga = Future.sequence(llfoManga.flatten)
      floManga
    })
    
    var strRes = ""
    floUpdatedManga.map( loManga => {
      Logger.debug("size loManga: " + loManga.count(m => true))
      loManga.foreach( oM => {
        oM match {
          case Some(m) => {
            strRes += Json.toJson(m)
          }
        }
      })
      Ok(strRes)
    })
    
//    var strRes = ""
//    flUpdatedManga.map( lManga => {
//      val lsManga = for ( m <- lManga ) yield {
//        val strManga = Json.toJson(m).toString() + ", "
//        strRes += strManga
//        strManga
//      }
//      Logger.debug("total manga objects: " + lManga.count( m => true))
//      Ok(strRes)
//    })
    
//    var strRes = ""    
//    val futureResult = fManga.flatMap( lManga => {
//        var lStr = for ( m <- lManga ) yield {
//          var strManga = Json.toJson(m).toString() + ", "
//          strRes += strManga
//          strManga
//        }
//        Future {
//          Ok(strRes)
//        }
//    })
//    futureResult
    
    // working
//    val flManga = flResults.map( lResults => {
//      val lMangas = for ( r <- lResults ) yield {
//        mangaRepo.constructMangaFromApiResponse(r)
//      }
//      lMangas
//    })
//    flManga.map( lManga => {
//      var strRes = ""
//      lManga.foreach( manga => {
//        val js = Json.toJson(manga)
//        strRes += js.toString()
//      })
//      Logger.debug("result: " + strRes)
//      Ok(strRes)
//    })

  }
  
    def requestSearchInfo(mangaSearchData: MangaSearchData): Future[ResultSearchResponse] = {
      val urlReq = urlHost + "/search"
      val wsRequest: WSRequest = wsClient.url(urlReq)
        .withHeaders(("Accept" -> "application/json"))
        .withQueryString(("t" -> mangaSearchData.mangaId))
      val fResponse: Future[WSResponse] = wsRequest.get()
      val fRet = fResponse.map( res => {
//        res.body
        res.json.as[ResultSearchResponse]
      })
      fRet
    }
    
    def requestComicInfo(resultSearchResponse: ResultSearchResponse): Future[List[ResultComicResponse]] = {
      val urlReq = urlHost + "/comic"
      val wsRequest: WSRequest = wsClient.url(urlReq)
      .withHeaders(("Accept" -> "application/json"))
      val lfResultComicResponse = for ( info <- resultSearchResponse.results ) yield {
        val fResponse = wsRequest.withQueryString(("c" -> info.resultFullUrl)).get()
        val fResultComicResponse = fResponse.map( wsres => {
//          Logger.debug("wsres: " + wsres.body)
          wsres.json.as[ResultComicResponse]
        })
        fResultComicResponse
      }
      Future.sequence(lfResultComicResponse)
    }
    
    def requestChapterInfo(chapterFullUrl: String): Future[ResultChapterResponse] = {
      val urlReq = urlHost + "/chapters"
      val wsRequest: WSRequest = wsClient.url(urlReq)
      .withHeaders(("Accept" -> "application/json"))
      val fwsResponse = wsRequest.withQueryString(("c" -> chapterFullUrl)).get()
      val fResultChapterResponse = fwsResponse.map( wsres => {
//        Logger.debug("wsres: " + wsres.body)
        wsres.json.as[ResultChapterResponse]
      })
      fResultChapterResponse
    }
    
    def requestPageInfo(pageFullUrl: String): Future[ResultPageResponse] = {
      val urlReq = urlHost + "/page"
      val wsRequest = wsClient.url(urlReq)
      .withHeaders(("Accept" -> "application/json"))
      val fwsResponse = wsRequest.withQueryString(("p" -> pageFullUrl)).get()
      val fResultPageResponse = fwsResponse.map( wsres => {
//        Logger.debug("wsRes: " + wsres.body)
        wsres.json.as[ResultPageResponse]
      })
      fResultPageResponse
    }
    
    // unused and untested
//    def requestChapterInfo(resultComicResponse: ResultComicResponse): Future[List[ResultChapterResponse]] = {
//      val urlReq = urlHost + "/chapters"
//      val wsRequest: WSRequest = wsClient.url(urlReq)
//      .withHeaders(("Accept" -> "application/json"))
//      val lfResultChapterResponse = for ( info <- resultComicResponse.chapters ) yield {
//        val fResponse = wsRequest.withQueryString(("c" -> info.chapterFullUrl)).get()
//        val fResultChapterResponse = fResponse.map( wsres => {
//          Logger.debug("chapters: " + wsres.body)
//          wsres.json.as[ResultChapterResponse]
//        })
//        fResultChapterResponse
//      }
//      Future.sequence(lfResultChapterResponse)
//    }
    
//    def requestComicInfo(resultSearch: Future[ResultSearchResponse]): Future[List[ResultComicResponse]] = {
//      val urlReq = urlHost + "/comic"
//      val wsRequest: WSRequest = wsClient.url(urlReq)
//      .withHeaders(("Accept" -> "application/json"))
//      val f = resultSearch.flatMap( rsr => {
//        val allResponse: List[Future[ResultComicResponse]] = for( info <- rsr.results ) yield {
//          val fResponse = wsRequest.withQueryString(("c" -> info.resultFullUrl)).get()
//          val fRet = fResponse.map( res => {
////            System.err.println(res.body)
//            Logger.debug("res => " + res.body)
//            res.json.as[ResultComicResponse]
//          })
//          fRet
//        }
//        Future.sequence(allResponse)
//      })
//      return f
//    }
//    
//    // TODO: do and test this
//    def requestChapterInfo(resultComic: Future[ResultComicResponse]) = {
//      val urlReq = urlHost + "/chapters"
//      val wsRequest: WSRequest = wsClient.url(urlReq)
//      .withHeaders(("Accept" -> "application/json"))
//    }
  
}