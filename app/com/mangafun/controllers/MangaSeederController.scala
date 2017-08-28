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

class MangaSeederController @Inject() (reactiveMongoApi: ReactiveMongoApi)(wsClient: WSClient)(appProvider: Provider[Application])
  extends Controller with MongoController with ReactiveMongoComponents {
  
  lazy val app = appProvider.get
  
  val urlHost = "http://localhost:3003"
  
  val listhosts = List(
//      "http://localhost:3003",
      "https://mangaapi-175103.appspot.com",
      "https://mangaapi-170728.herokuapp.com",
      "https://mangaapi-170729.herokuapp.com"
  )
  
  def getUrlHost(): String = {
    val iIndex = Random.nextInt(listhosts.size)
    listhosts.get(iIndex)
  }
  
  def reactiveMongoApi() : ReactiveMongoApi = {
    return reactiveMongoApi;
  }
  
  def mangaRepo = new MangaRepoImpl(reactiveMongoApi)
  
  case class MangaSearchData(var mangaId: String, var name: String)
  
  implicit val mangaSearchDataJson = Json.format[MangaSearchData]
  
  val lThreadSleep = 500
  val dTimeout = Duration.Inf
  
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
  
  def writeMangaToString(manga: Manga) = {
    val strPath = "resources/json/"
    val jsv = Json.toJson(manga)
    val strContent = Json.prettyPrint(jsv)
    val fileSave = FileUtils.getFile(strPath + manga.mangaTitle)
    FileUtils.writeStringToFile(fileSave, strContent)
    
  }
  
  def readMangaFromFile(filename: String): Option[Manga] = {
    val strPath = "resources/json/"
    val fileSaved = FileUtils.getFile(strPath + filename)
    if ( !fileSaved.exists() ) {
      return None
    } else {
      val strContent = FileUtils.readFileToString(fileSaved)
      val jsv = Json.parse(strContent)
      val manga = jsv.as[Manga]
      Some(manga)
    }
  }
  
  def downloadImageToDir(dirName: String, imageName: String, imageUrl: String) = {
    val strFile = dirName + "/" + imageName
    val fileDest = FileUtils.getFile(strFile)
    val wsRequest = wsClient.url(imageUrl)
    val fwsResponse = wsRequest.get()
    val fImageBytes = fwsResponse.map( wsres => {
      wsres.bodyAsBytes
    })
    val bytes = Await.result(fImageBytes, dTimeout)
    FileUtils.writeByteArrayToFile(fileDest, bytes.toArray[Byte])
  }
  
//  def requestSearchInfo(mangaSearchData: MangaSearchData): Future[String] = {
////    val urlApi = getUrlHost() + "/search?t="
////    val urlReq = urlApi + mangaSearchData.mangaId
//    val urlReq = getUrlHost() + "/search"
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
  
  def imagefromto(startingIndex: Int, endingIndex: Int): Action[AnyContent] = Action.async {    
    val fMangaList = readMangaToString()
    val flMangaSearchData: Future[List[MangaSearchData]] = fMangaList.map( strJsonArray => {
      val jsv: JsValue = Json.parse(strJsonArray)
      val jsl: List[MangaSearchData] = jsv.as[List[MangaSearchData]]
      jsl
    });
    
    val lMangaSearchData = Await.result(flMangaSearchData, dTimeout)
    
    val iStartingIndex = if (startingIndex < 0 || startingIndex >= lMangaSearchData.length)  0 else startingIndex
    var iEndingIndex = if (endingIndex < 0 || endingIndex >= lMangaSearchData.length ) lMangaSearchData.length-1 else endingIndex
    if ( iEndingIndex < iStartingIndex ) {
      iEndingIndex = iStartingIndex+1
    }
    var iCurrentIndex = iStartingIndex
    var iCount = 0
    for ( iCurrentIndex <- iStartingIndex to iEndingIndex ) { // iEndingIndex inclusive
      val mangaSearchData = lMangaSearchData.get(iCurrentIndex)
      val oManga = readMangaFromFile(mangaSearchData.name)
      if ( !oManga.isDefined ) {
        Logger.error("manga json not found: " + mangaSearchData.name)
      } else {
        val m = oManga.get
        downloadMangaPages(m)
      } // end if oManga.isDefined
      iCount = iCount + 1
      Logger.error("downloaded pages for manga: " + iCurrentIndex + " : " + mangaSearchData.name)
    }
    
    Future {
      Ok("ok: downloaded " + iCount + " manga")
    }
  }
  
  def downloadMangaPages(m: Manga) = {
    val strMangaRootDir = "resources/manga/"
    var iLastSlash = m.mangaUrl.lastIndexOf("/")
    val mangaDirName = m.mangaUrl.substring(iLastSlash+1)
    iLastSlash = m.mangaThumbUrl.lastIndexOf("/")
    val mangaThumbName = m.mangaThumbUrl.substring(iLastSlash+1)
    downloadImageToDir(strMangaRootDir + mangaDirName, mangaThumbName, m.mangaThumbUrl)
    Thread.sleep(lThreadSleep + Random.nextInt(lThreadSleep))
    m.chapters.foreach( ch => {
      iLastSlash = ch.chapterUrl.lastIndexOf("/")
      val chapterDirName = ch.chapterUrl.substring(iLastSlash+1)
      ch.pages.foreach( p => {
        iLastSlash = p.pageUrl.lastIndexOf("/")
        val pageDirName = p.pageNumber //p.pageUrl.substring(iLastSlash+1)
        val fullDirName = strMangaRootDir + mangaDirName + "/" + chapterDirName + "/" + pageDirName + "/"
        val dir = FileUtils.getFile(fullDirName)
        if ( !dir.exists() ) {
          FileUtils.forceMkdir(dir)
        }
        val image = p.images.get(0)
        if ( image != null ) {
          iLastSlash = image.imageUrl.lastIndexOf("/")
          val imageName = image.imageUrl.substring(iLastSlash+1)
          downloadImageToDir(fullDirName, imageName, image.imageUrl)
          Thread.sleep(lThreadSleep + Random.nextInt(lThreadSleep))
        }
      })
    })
  }
  
  def image(mangaIndex: Int): Action[AnyContent] = Action.async {
    val strMangaRootDir = "resources/manga/"
    
    val fMangaList = readMangaToString()
    val flMangaSearchData: Future[List[MangaSearchData]] = fMangaList.map( strJsonArray => {
      val jsv: JsValue = Json.parse(strJsonArray)
      val jsl: List[MangaSearchData] = jsv.as[List[MangaSearchData]]
      jsl
    });
    
    val lMangaSearchData = Await.result(flMangaSearchData, dTimeout)
    val iMangaIndex = if ( mangaIndex < 0 || mangaIndex >= lMangaSearchData.length ) 0 else mangaIndex
    val mangaSearchData = lMangaSearchData.get(iMangaIndex)
    val oManga = readMangaFromFile(mangaSearchData.name)
    
    if ( !oManga.isDefined ) {
      Future {
        Ok("manga: file not found")
      }
    } else {
      val m = oManga.get
      var iLastSlash = m.mangaUrl.lastIndexOf("/")
      val mangaDirName = m.mangaUrl.substring(iLastSlash+1)
      iLastSlash = m.mangaThumbUrl.lastIndexOf("/")
      val mangaThumbName = m.mangaThumbUrl.substring(iLastSlash+1)
      downloadImageToDir(strMangaRootDir + mangaDirName, mangaThumbName, m.mangaThumbUrl)
      Thread.sleep(lThreadSleep + Random.nextInt(lThreadSleep))
      m.chapters.foreach( ch => {
        iLastSlash = ch.chapterUrl.lastIndexOf("/")
        val chapterDirName = ch.chapterUrl.substring(iLastSlash+1)
        ch.pages.foreach( p => {
          iLastSlash = p.pageUrl.lastIndexOf("/")
          val pageDirName = p.pageNumber //p.pageUrl.substring(iLastSlash+1)
          val fullDirName = strMangaRootDir + mangaDirName + "/" + chapterDirName + "/" + pageDirName + "/"
          val dir = FileUtils.getFile(fullDirName)
          if ( !dir.exists() ) {
            FileUtils.forceMkdir(dir)
          }
          val image = p.images.get(0)
          if ( image != null ) {
            iLastSlash = image.imageUrl.lastIndexOf("/")
            val imageName = image.imageUrl.substring(iLastSlash+1)
            downloadImageToDir(fullDirName, imageName, image.imageUrl)
            Thread.sleep(lThreadSleep + Random.nextInt(lThreadSleep))
          }
        })
      })
    } // end if oManga.isDefined
    
    Future {
      Ok("ok")
    }
  }
  
  def index(mangaIndex: Int): Action[AnyContent] = Action.async {
//    val lThreadSleep = 10
//    val dTimeout = Duration.Inf
    
    val fMangaList = readMangaToString()
    val flMangaSearchData: Future[List[MangaSearchData]] = fMangaList.map( strJsonArray => {
      val jsv: JsValue = Json.parse(strJsonArray)
      val jsl: List[MangaSearchData] = jsv.as[List[MangaSearchData]]
      jsl
    });
    
    val fResultSearchResponse = flMangaSearchData.flatMap( lMangaSearchData => {
      val iMangaIndex = if (mangaIndex < 0 || mangaIndex >= lMangaSearchData.length)  0 else mangaIndex
      val msd = lMangaSearchData.get(iMangaIndex)
      requestSearchInfo(msd)
    })
    
    val resultSearchResponse = Await.result(fResultSearchResponse, dTimeout)
    Thread.sleep(lThreadSleep + Random.nextInt(lThreadSleep))
    
    val mangaFirst = mangaRepo.constructMangaFromApiResponse(resultSearchResponse)
    
    val fResultComicResponse = requestComicInfo(resultSearchResponse)
    
    val resultComicResponse = Await.result(fResultComicResponse, dTimeout)
    Thread.sleep(lThreadSleep + Random.nextInt(lThreadSleep))
    
    resultComicResponse.foreach( rcr => {
      mangaRepo.updateMangaFromApiResponse(mangaFirst, rcr)
    })
    
    mangaFirst.chapters.foreach( ch => {
      val fResultChapterResponse = requestChapterInfo(ch.chapterUrl)
      val resultChapterResponse = Await.result(fResultChapterResponse, dTimeout)
      Thread.sleep(lThreadSleep + Random.nextInt(lThreadSleep))
      mangaRepo.updateMangaFromApiResponse(mangaFirst, resultChapterResponse)
    })
    
    mangaFirst.chapters.foreach( ch => {
      ch.pages.foreach( p => {
        val fResultPageResponse = requestPageInfo(p.pageUrl)
        val resultPageResponse = Await.result(fResultPageResponse, dTimeout)
        Thread.sleep(lThreadSleep + Random.nextInt(lThreadSleep))
        mangaRepo.updateMangaFromApiResponse(mangaFirst, resultPageResponse)
      })
    })
    
    writeMangaToString(mangaFirst)
    
    Future {
      Ok(Json.toJson(mangaFirst))
    }
  }
  
  def getfromto(startingIndex: Int, endingIndex: Int): Action[AnyContent] = Action.async {
//    val lThreadSleep = 10
//    val dTimeout = Duration.Inf
    
    val fMangaList = readMangaToString()
    val flMangaSearchData: Future[List[MangaSearchData]] = fMangaList.map( strJsonArray => {
      val jsv: JsValue = Json.parse(strJsonArray)
      val jsl: List[MangaSearchData] = jsv.as[List[MangaSearchData]]
      jsl
    });
    
    val lMangaSearchData = Await.result(flMangaSearchData, dTimeout)
    
    val iStartingIndex = if (startingIndex < 0)  0 else startingIndex
    var iEndingIndex = if (endingIndex < 0 || endingIndex >= lMangaSearchData.length ) lMangaSearchData.length-1 else endingIndex
    if ( iEndingIndex < iStartingIndex ) {
      iEndingIndex = iStartingIndex+1
    }
    var iCurrentIndex = iStartingIndex
    var iCount = 0
    for ( iCurrentIndex <- iStartingIndex to iEndingIndex ) {
    
      try {
        val fResultSearchResponse = flMangaSearchData.flatMap( lMangaSearchData => {
          val msd = lMangaSearchData.get(iCurrentIndex)
          requestSearchInfo(msd)
        })
        
        val resultSearchResponse = Await.result(fResultSearchResponse, dTimeout)
        Thread.sleep(lThreadSleep + Random.nextInt(lThreadSleep))
        
        val mangaFirst = mangaRepo.constructMangaFromApiResponse(resultSearchResponse)
        
        val fResultComicResponse = requestComicInfo(resultSearchResponse)
        
        val resultComicResponse = Await.result(fResultComicResponse, dTimeout)
        Thread.sleep(lThreadSleep + Random.nextInt(lThreadSleep))
        
        resultComicResponse.foreach( rcr => {
          mangaRepo.updateMangaFromApiResponse(mangaFirst, rcr)
        })
        
        mangaFirst.chapters.foreach( ch => {
          val fResultChapterResponse = requestChapterInfo(ch.chapterUrl)
          val resultChapterResponse = Await.result(fResultChapterResponse, dTimeout)
          Thread.sleep(lThreadSleep + Random.nextInt(lThreadSleep))
          mangaRepo.updateMangaFromApiResponse(mangaFirst, resultChapterResponse)
        })
        
        mangaFirst.chapters.foreach( ch => {
          ch.pages.foreach( p => {
            val fResultPageResponse = requestPageInfo(p.pageUrl)
            val resultPageResponse = Await.result(fResultPageResponse, dTimeout)
            Thread.sleep(lThreadSleep + Random.nextInt(lThreadSleep))
            mangaRepo.updateMangaFromApiResponse(mangaFirst, resultPageResponse)
          })
        })
        
        writeMangaToString(mangaFirst)
        
        iCount = iCount + 1
        Logger.error("Written manga: " + iCurrentIndex + " : " + mangaFirst.mangaUrl)
      }
      catch {
        case ex: Exception => {
          Logger.error("Exception: " + iCurrentIndex)
        }
      }
    }
    
    Future {
      Ok(Json.toJson("done: " + iCount + "/" + lMangaSearchData.length))
    }
  }
  
  def index_old(): Action[AnyContent] = Action.async {
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
      val resListManga = for ( manga <- listManga ) yield {
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
          fManga // lfoManga
        }
        Logger.debug("size lfoManga: " + lfoManga.count(m => true)) // wrong here
        lfoManga.get(0)
      }
      Logger.debug("size resListManga: " + resListManga.count(m => true)) // wrong here
      Future.sequence(resListManga)
    })
    
    var strRes = ""
    floUpdatedManga.map( loManga => {
      Logger.debug("size loManga: " + loManga.count(m => true))
      loManga.foreach( oM => {
        oM match {
          case Some(m) => {
            strRes += Json.toJson(m) + ", "
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
      val urlReq = getUrlHost() + "/search"
      val wsRequest: WSRequest = wsClient.url(urlReq)
        .withHeaders(("Accept" -> "application/json"))
        .withQueryString(("t" -> mangaSearchData.mangaId))
      Logger.debug("requestSearchInfo: " + urlReq + "?t=" + mangaSearchData.mangaId);
      val fResponse: Future[WSResponse] = wsRequest.get()
      val fRet = fResponse.map( res => {
//        res.body
        res.json.as[ResultSearchResponse]
      })
      fRet
    }
    
    def requestComicInfo(resultSearchResponse: ResultSearchResponse): Future[List[ResultComicResponse]] = {
      val urlReq = getUrlHost() + "/comic"
      val wsRequest: WSRequest = wsClient.url(urlReq)
      .withHeaders(("Accept" -> "application/json"))
      val lfResultComicResponse = for ( info <- resultSearchResponse.results ) yield {
        Logger.debug("requestComicInfo: " + urlReq + "?c=" + info.resultFullUrl)
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
      val urlReq = getUrlHost() + "/chapters"
      val wsRequest: WSRequest = wsClient.url(urlReq)
      .withHeaders(("Accept" -> "application/json"))
      Logger.debug("requestChapterInfo: " + urlReq + "?c=" + chapterFullUrl)
      val fwsResponse = wsRequest.withQueryString(("c" -> chapterFullUrl)).get()
      val fResultChapterResponse = fwsResponse.map( wsres => {
//        Logger.debug("wsres: " + wsres.body)
        wsres.json.as[ResultChapterResponse]
      })
      fResultChapterResponse
    }
    
    def requestPageInfo(pageFullUrl: String): Future[ResultPageResponse] = {
      val urlReq = getUrlHost() + "/page"
      val wsRequest = wsClient.url(urlReq)
      .withHeaders(("Accept" -> "application/json"))
      Logger.debug("requestPageInfo: " + urlReq + "?p=" + pageFullUrl)
      val fwsResponse: Future[WSResponse] = wsRequest.withQueryString(("p" -> pageFullUrl)).get()
      val fResultPageResponse = fwsResponse.map( wsres => {
//        Logger.debug("wsRes: " + wsres.body)
        wsres.json.as[ResultPageResponse]
      })
      fResultPageResponse
    }
    
    // unused and untested
//    def requestChapterInfo(resultComicResponse: ResultComicResponse): Future[List[ResultChapterResponse]] = {
//      val urlReq = getUrlHost() + "/chapters"
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
//      val urlReq = getUrlHost() + "/comic"
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
//      val urlReq = getUrlHost() + "/chapters"
//      val wsRequest: WSRequest = wsClient.url(urlReq)
//      .withHeaders(("Accept" -> "application/json"))
//    }
  
}