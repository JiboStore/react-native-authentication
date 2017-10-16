package com.mangafun.controllers

import scala.collection.Map
import scala.collection.Seq
import scala.concurrent._

import com.google.inject.Provider
import com.mangafun.repos._
import com.mangafun.utils._

import javax.inject.Inject
import play.Application
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import play.api.mvc._
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.ReactiveMongoApi
import play.modules.reactivemongo.ReactiveMongoComponents
import com.mangafun.models.ApiResult
import java.text.SimpleDateFormat
import play.api.libs.json.JsString

class SignInController @Inject() (reactiveMongoApi: ReactiveMongoApi)(wsClient: WSClient)(appProvider: Provider[Application])
  extends Controller with MongoController with ReactiveMongoComponents {
  
  lazy val app = appProvider.get
  
  def reactiveMongoApi() : ReactiveMongoApi = {
    return reactiveMongoApi;
  }
  
  def trainerRepo = new MangaRepoImpl(reactiveMongoApi)
  def userRepo = new MangafunUserRepo(reactiveMongoApi)
  
  def index(): Action[AnyContent] = Action.async { implicit request =>
    var str = ""
    var apiRes = ApiResult(
        ReturnCode.TEST_FETCHPOST.id,
        ReturnCode.TEST_FETCHPOST.toString(),
        "init"
    )
    try {
      val oReq = request.body.asJson
      val jsReq = oReq.getOrElse(JsString("null"))
      str += jsReq
      apiRes = ApiResult(
          ReturnCode.TEST_FETCHPOST.id,
          "success",
          str
      )
      LogManager.DebugLog(this, str)
    } catch {
      case t: Throwable => {
        LogManager.DebugException(this, "ex: ", t)
        apiRes = ApiResult(
            ReturnCode.TEST_FETCHPOST.id,
            ReturnCode.TEST_FETCHPOST.toString(),
            t.getMessage
        )
      }
    }
    Future {
      Ok(com.mangafun.views.html.common.apiresult.render(apiRes))
    }
  }
  
  def signinuser(): Action[AnyContent] = Action.async { implicit request =>
    var str = ""
    var apiRes = ApiResult(
        ReturnCode.SIGNIN_USER.id,
        ReturnCode.SIGNIN_USER.toString(),
        "init"
    )
    var fApiRes: Future[ApiResult] = null.asInstanceOf[Future[ApiResult]]
    fApiRes = Future {
      apiRes
    }
    try {
      val oReq = request.body.asJson
      val jsReq = oReq.get
      val email = (jsReq \ "email").getOrElse(JsString("null")).as[String]
      val password = (jsReq \ "pwd").getOrElse(JsString("null")).as[String]
      val fUser = userRepo.getUserByEmail(email)
      fApiRes = fUser.map( oUser => {
        oUser match {
          case Some(u) => {
            // TODO: verify password
            ApiResult(
              ReturnCode.SIGNIN_USER.id,
              ReturnCode.SIGNIN_USER.toString(),
              "user found"
            )
          }
          case None => {
            ApiResult(
              ReturnCode.SIGNIN_USER.id,
              ReturnCode.SIGNIN_USER.toString(),
              "user not found"
            )
          }
        }
      })
      str += jsReq
      LogManager.DebugLog(this, str)
    } catch {
      case t: Throwable => {
        LogManager.DebugException(this, "ex: ", t)
        fApiRes = Future {
          ApiResult(
            ReturnCode.SIGNIN_USER.id,
            ReturnCode.SIGNIN_USER.toString(),
            t.getMessage
          )
        }
      }
    }
    fApiRes.map( apiRes => {
      Ok(com.mangafun.views.html.common.apiresult.render(apiRes))
    })
  }
  
  def createuser(): Action[AnyContent] = Action.async { implicit request =>
    var str = ""
    var apiRes = ApiResult(
        ReturnCode.CREATE_USER.id,
        ReturnCode.CREATE_USER.toString(),
        "init"
    )
    try {
      val oReq = request.body.asJson
      val jsReq = oReq.getOrElse(JsString("null"))
      val firstname = (jsReq \ "firstname").getOrElse(JsString("null")).as[String]
      val lastname = (jsReq \ "lastname").getOrElse(JsString("null")).as[String]
      val bday = (jsReq \ "bday").getOrElse(JsString("01-Jan-1970")).as[String]
      val sex = (jsReq \ "sex").getOrElse(JsString("null")).as[String]
      val email = (jsReq \ "email").getOrElse(JsString("null")).as[String]
      val password = (jsReq \ "pwd").getOrElse(JsString("null")).as[String]
      val sdt = new SimpleDateFormat("dd-MMM-yyyy")
      val gender = if ( sex.equalsIgnoreCase("female") ) 0 else 1
      val birthday = sdt.parse(bday)
      userRepo.createNewUser(firstname, lastname, birthday, gender, email, password)
      str += jsReq
      apiRes = ApiResult(
          ReturnCode.CREATE_USER.id,
          "success",
          str
      )
      LogManager.DebugLog(this, str)
    } catch {
      case t: Throwable => {
        LogManager.DebugException(this, "ex: ", t)
        apiRes = ApiResult(
            ReturnCode.CREATE_USER.id,
            ReturnCode.CREATE_USER.toString(),
            t.getMessage
        )
      }
    }
    Future {
      Ok(com.mangafun.views.html.common.apiresult.render(apiRes))
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
      LogManager.DebugLog(this, str)
//      throw new Exception("throwing ioe")
    } catch {
      case t: Throwable => {
        LogManager.DebugException(this, "force ex: ", t)
      }
    }
    Future {
      Ok(str)
    }
  }
  
  def fetchpost(): Action[AnyContent] = Action.async { implicit request =>
    var str = ""
    var apiRes = ApiResult(
        ReturnCode.TEST_FETCHPOST.id,
        ReturnCode.TEST_FETCHPOST.toString(),
        "init"
    )
    try {
//      val req = request.body.asText // None
      val oReq = request.body.asJson
      str += oReq.getOrElse("nothing")
      apiRes = ApiResult(
          ReturnCode.TEST_FETCHPOST.id,
          "success",
          str
      )
      LogManager.DebugLog(this, str)
//      throw new Exception("throwing ioe")
    } catch {
      case t: Throwable => {
        LogManager.DebugException(this, "ex: ", t)
        apiRes = ApiResult(
            ReturnCode.TEST_FETCHPOST.id,
            ReturnCode.TEST_FETCHPOST.toString(),
            t.getMessage
        )
      }
    }
    Future {
      Ok(com.mangafun.views.html.common.apiresult.render(apiRes))
    }
  }  
}