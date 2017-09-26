package com.mangafun.controllers

import play.api.mvc._
import javax.inject.Inject
import play.api.libs.json._
import play.modules.reactivemongo.ReactiveMongoApi
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.ReactiveMongoComponents
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
import akka.stream.scaladsl.Sink
import akka.util.ByteString
import scala.util.Random

import com.google.gson.Gson

import play.twirl.api.Html
import com.mangafun.repos.MangaRepoImpl
import scala.collection.Map
import scala.collection.Seq

class ReactNativeFetchController @Inject() (reactiveMongoApi: ReactiveMongoApi)(wsClient: WSClient)(appProvider: Provider[Application])
  extends Controller with MongoController with ReactiveMongoComponents {
  
  lazy val app = appProvider.get
  
  def reactiveMongoApi() : ReactiveMongoApi = {
    return reactiveMongoApi;
  }
  
  def trainerRepo = new MangaRepoImpl(reactiveMongoApi)
  
  def index(): Action[AnyContent] = Action.async { implicit request =>
    Future {
      Ok("Hello")
    }
  }
  
  def fetchget(): Action[AnyContent] = Action.async { implicit request =>
    var str = "param is: ";
    try {
      val req: Map[String, Seq[String]] = request.queryString
      val rmap = req.map { case (k, v) => k -> v.mkString }
      for ( (k,v) <- rmap ) {
        str += k
        str += ", "
        str += v
      }
      Logger.error(str);
    } catch {
      case t: Throwable => {
        
      }
    }
    Future {
      Ok(str)
    }
  }
  
  def fetchpost(): Action[AnyContent] = Action.async { implicit request =>
    try {
      
    } catch {
      case t: Throwable => {
        
      }
    }
    Future {
      Ok("fetchpost")
    }
  }
  
//  def createGet(name: String): Action[AnyContent] = Action.async {
//    val szUuid = MonsterRancherUtils.generateTrainerId()
//    val fwr = trainerRepo.createNewTrainer(szUuid, name)
//    val fot = fwr.flatMap( wr => {
//      if ( wr.hasErrors ) {
//        Future {
//          None
//        }
//      } else {
//        val fot = trainerRepo.getTrainerById(szUuid)
//        fot
//      }
//    })
//    val fres = fot.map( ot => {
//      ot match {
//        case None => {
//          val apiRes = ApiResult(
//              ReturnCode.TRAINER_CREATE.id,
//              ReturnCode.TRAINER_CREATE.toString(),
//              "failed to create"
//          )
//          Ok(com.igg.mr.views.html.trainer.create.render(apiRes, null))
//        }
//        case Some(t) => {
//          val str = Html(new Gson().toJson(t)).toString()
//          val apiRes = ApiResult(
//             ReturnCode.TRAINER_CREATE.id,
//             "success",
//             ""
//          )
//          val ct = MonsterRancherUtils.clientSafeTrainer(t)
//          Ok(com.igg.mr.views.html.trainer.create.render(apiRes, ct))
//        }
//      }
//    })
//    fres
//  }
//  
//  def createPost(): Action[AnyContent] = Action.async { implicit request =>
//    try {
//      val oJson = request.body.asJson
//      if ( !oJson.isDefined ) {
//        // request error
//        Future {
//          val apiRes = ApiResult(
//              ReturnCode.TRAINER_CREATE.id,
//              ReturnCode.TRAINER_CREATE.toString(),
//              "invalid request"
//          )
//          Ok(com.igg.mr.views.html.common.requesterror.render(apiRes))
//        }
//      } else {
//        val jsReq = oJson.get
//        val szName = (jsReq \ "name").getOrElse(JsString("null")).as[String]
//        if ( szName == "null" ) {
//          // invalid name
//          Future {
//            val apiRes = ApiResult(
//                ReturnCode.TRAINER_CREATE.id,
//                ReturnCode.TRAINER_CREATE.toString(),
//                "invalid name not specified"
//            )
//            Ok(com.igg.mr.views.html.common.requesterror.render(apiRes))
//          }
//        } else {
//          val foexisting = trainerRepo.getTrainerByName(szName);
//          val fotrainer = foexisting.flatMap( oex => {
//            val ft = oex match {
//              case Some(t) => {
//                trainerRepo.updateTrainerLoginTime(t)
//                Future { Some(t) }
//              }
//              case None => {
//                val szUuid = MonsterRancherUtils.generateTrainerId()
//                val fwr = trainerRepo.createNewTrainer(szUuid, szName)
//                val fot = fwr.flatMap( wr => {
//                  if ( wr.hasErrors ) {
//                    Future {
//                      None
//                    }
//                  } else {
//                    val fotr = trainerRepo.getTrainerById(szUuid)
//                    fotr
//                  }
//                })
//                fot
//              }
//            }
//            ft
//          })
//          val fres = fotrainer.map( ot => {
//            ot match {
//              case None => {
//                val apiRes = ApiResult(
//                    ReturnCode.TRAINER_CREATE.id,
//                    ReturnCode.TRAINER_CREATE.toString(),
//                    "failed to create"
//                )
//                Ok(com.igg.mr.views.html.trainer.create.render(apiRes, null))
//              }
//              case Some(t) => {
//                val str = Html(new Gson().toJson(t)).toString()
//                val apiRes = ApiResult(
//                   ReturnCode.TRAINER_CREATE.id,
//                   "success",
//                   ""
//                )
//                val ct = MonsterRancherUtils.clientSafeTrainer(t)
//                Ok(com.igg.mr.views.html.trainer.create.render(apiRes, ct))
//              }
//            }
//          })
//          fres
//        }
//      }
//    } catch {
//      case t: Throwable => { 
//        LogManager.DebugError(this, " createPost exception: " + t.getMessage())
//        Future {
//          Ok(com.igg.mr.views.html.common.exception.render(t))
//        }
//      }
//    }
//  }
//  
//  def loginGet(name: String, password: String): Action[AnyContent] = Action.async { implicit request =>
//    LogManager.DebugLog(this, "debug login: " + name + " password: "+ password)
//    val fot = trainerRepo.getTrainerById(password)
//    val fres = fot.map( ot => {
//      ot match {
//        case None => {
//          val apiRes = ApiResult(
//              ReturnCode.TRAINER_LOGIN.id,
//              ReturnCode.TRAINER_LOGIN.toString(),
//              "failed to login"
//          )
//          Ok(com.igg.mr.views.html.trainer.login.render(apiRes, null))
//        }
//        case Some(t) => {
//          trainerRepo.updateTrainerLoginTime(t)
//          val apiRes = ApiResult(
//              ReturnCode.TRAINER_LOGIN.id,
//              "success",
//              ""
//          )
//          val ct = MonsterRancherUtils.clientSafeTrainer(t)
//          Ok(com.igg.mr.views.html.trainer.login.render(apiRes, ct))
//        }
//      }
//    })
//    fres
//  }
//  
//  def loginPost(): Action[AnyContent] = Action.async { implicit request =>
//    try {
//      val oJson = request.body.asJson
//      if ( !oJson.isDefined ) {
//        // request error
//        Future {
//          val apiRes = ApiResult(
//              ReturnCode.TRAINER_LOGIN.id,
//              ReturnCode.TRAINER_LOGIN.toString(),
//              "invalid request"
//          )
//          Ok(com.igg.mr.views.html.trainer.login.render(apiRes, null))
//        }
//      } else {
//        val jsReq = oJson.get
//        val szName = (jsReq \ "name").getOrElse(JsString("null")).as[String]
//  //      val szTrainerId = (jsReq \ "trainerId").getOrElse(JsString("null")).toString()
//        val szTrainerId = (jsReq \ "trainerId").getOrElse(JsString("null")).as[String]
//        LogManager.DebugLog(this, "trainerId: " + szTrainerId)
//        val fot = trainerRepo.getTrainerById(szTrainerId)
//        val fres = fot.map( ot => {
//          ot match {
//            case None => {
//              val apiRes = ApiResult(
//                  ReturnCode.TRAINER_LOGIN.id,
//                  ReturnCode.TRAINER_LOGIN.toString(),
//                  "failed to login"
//              )
//              Ok(com.igg.mr.views.html.trainer.login.render(apiRes, null))
//            }
//            case Some(t) => {
//              trainerRepo.updateTrainerLoginTime(t)
//              val apiRes = ApiResult(
//                  ReturnCode.TRAINER_LOGIN.id,
//                  "success",
//                  ""
//              )
//              val ct = MonsterRancherUtils.clientSafeTrainer(t)
//              Ok(com.igg.mr.views.html.trainer.login.render(apiRes, ct))
//            }
//          }
//        })
//        fres
//      }
//    }
//    catch {
//      case t: Throwable => { 
//        LogManager.DebugError(t, " exception: " + t.getMessage())
//        Future {
//          Ok(com.igg.mr.views.html.common.exception.render(t))
//        }
//      }
//    }
//  }
//  
//  object LoggingAction extends ActionBuilder[Request] {
//    def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]) = {
//      val auth = request.headers.get("Authentication")
//      LogManager.DebugError(this, "LoggingAction! " + auth)
//      block(request)
//    }
//  }
//  
//  def testret(): Action[AnyContent] = LoggingAction.async { implicit request =>
//    try {
//      val oJson = request.body.asJson
//      if ( !oJson.isDefined ) {
//        // request error
//        Future {
//          val apiRes = ApiResult(
//              ReturnCode.TRAINER_LOGIN.id,
//              ReturnCode.TRAINER_LOGIN.toString(),
//              "invalid request"
//          )
//          Ok(com.igg.mr.views.html.trainer.login.render(apiRes, null))
//        }
//      } else {
//        val jsReq = oJson.get
//        val szName = (jsReq \ "name").getOrElse(JsString("null")).as[String]
//  //      val szTrainerId = (jsReq \ "trainerId").getOrElse(JsString("null")).toString()
//        val szTrainerId = (jsReq \ "trainerId").getOrElse(JsString("null")).as[String]
//        LogManager.DebugLog(this, "trainerId: " + szTrainerId)
//        val fot = trainerRepo.getTrainerById(szTrainerId)
//        val fres = fot.map( ot => {
//          ot match {
//            case None => {
//              val apiRes = ApiResult(
//                  ReturnCode.TRAINER_LOGIN.id,
//                  ReturnCode.TRAINER_LOGIN.toString(),
//                  "failed to login"
//              )
//              Ok(com.igg.mr.views.html.trainer.login.render(apiRes, null))
//            }
//            case Some(t) => {
//              trainerRepo.updateTrainerLoginTime(t)
//              val apiRes = ApiResult(
//                  ReturnCode.TRAINER_LOGIN.id,
//                  "success",
//                  ""
//              )
//              val ct = MonsterRancherUtils.clientSafeTrainer(t)
//              Ok(com.igg.mr.views.html.trainer.login.render(apiRes, ct))
//            }
//          }
//        })
//        fres
//      }
//    }
//    catch {
//      case t: Throwable => { 
//        LogManager.DebugError(t, " exception: " + t.getMessage())
//        Future {
//          Ok(com.igg.mr.views.html.common.exception.render(t))
//        }
//      }
//    }
//  }
  
}