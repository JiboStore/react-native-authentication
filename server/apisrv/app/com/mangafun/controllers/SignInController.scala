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
import play.api.libs.json.JsValue
import reactivemongo.api.commands.WriteResult
import com.mangafun.models.PayloadUserFactory
import com.mangafun.models.PayloadUser

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
    var fApiResult = Future {
      ApiResult(
        ReturnCode.TEST_FETCHPOST.id,
        ReturnCode.TEST_FETCHPOST.toString(),
        ReturnResult.RESULT_ERROR.toString(),
        "init"
      )
    }
    try {
      val oReq = request.body.asJson
      val jsReq = oReq.getOrElse(JsString("null"))
      str += jsReq
      fApiResult = Future {
        ApiResult(
          ReturnCode.TEST_FETCHPOST.id,
          "success",
          ReturnResult.RESULT_SUCCESS.toString(),
          str
        )
      }
      LogManager.DebugLog(this, str)
    } catch {
      case t: Throwable => {
        LogManager.DebugException(this, "ex: ", t)
        fApiResult = Future {
          ApiResult(
            ReturnCode.TEST_FETCHPOST.id,
            ReturnCode.TEST_FETCHPOST.toString(),
            ReturnResult.RESULT_ERROR.toString(),
            t.getMessage
          )
        }
      }
    }
    fApiResult.map( apiRes => {
      Ok(com.mangafun.views.html.common.apiresult.render(apiRes))
    })
  }
  
  def signinsession(): Action[AnyContent] = Action.async { implicit request =>
    var str = ""
    var fApiResult = Future {
      ApiResult(
        ReturnCode.TEST_FETCHPOST.id,
        ReturnCode.TEST_FETCHPOST.toString(),
        ReturnResult.RESULT_ERROR.toString(),
        "init"
      )
    }
    try {
      val oReq = request.body.asJson
      val jsReq = oReq.getOrElse(JsString("null"))
      str += jsReq
      fApiResult = Future {
        ApiResult(
          ReturnCode.TEST_FETCHPOST.id,
          "success",
          ReturnResult.RESULT_SUCCESS.toString(),
          str
        )
      }
      LogManager.DebugLog(this, str)
    } catch {
      case t: Throwable => {
        LogManager.DebugException(this, "ex: ", t)
        fApiResult = Future {
          ApiResult(
            ReturnCode.TEST_FETCHPOST.id,
            ReturnCode.TEST_FETCHPOST.toString(),
            ReturnResult.RESULT_ERROR.toString(),
            t.getMessage
          )
        }
      }
    }
    fApiResult.map( apiRes => {
      Ok(com.mangafun.views.html.common.apiresult.render(apiRes))
    })
  }
  
  def signinuser(): Action[AnyContent] = Action.async { implicit request =>
    var str = ""
    var apiRes = ApiResult(
        ReturnCode.SIGNIN_USER.id,
        ReturnCode.SIGNIN_USER.toString(),
        ReturnResult.RESULT_ERROR.toString(),
        "init"
    )
    var fApiRes: Future[ApiResult] = null.asInstanceOf[Future[ApiResult]]
    fApiRes = Future {
      apiRes
    }
    var payloadUser = PayloadUser("", "", "")
    var fTuple: Future[(ApiResult, PayloadUser)] = Future {
      (apiRes, payloadUser)
    }
    try {
      val oReq = request.body.asJson
      val jsReq = oReq.get
      val email = (jsReq \ "email").getOrElse(JsString("null")).as[String]
      val password = (jsReq \ "pwd").getOrElse(JsString("null")).as[String]
      val deviceinfo = (jsReq \ "deviceinfo").getOrElse(JsString("null")).as[JsValue]
      LogManager.DebugLog(this, "deviceinfo: " + deviceinfo);
      val deviceid = (jsReq \ "deviceinfo" \ "deviceId").getOrElse(JsString("null")).as[String]
      LogManager.DebugLog(this, "deviceid: " + deviceid);
      val fUser = userRepo.getUserByEmailAndPassword(email, password)
      fTuple = fUser.flatMap(
        (oUser) => {
          oUser match {
            case Some(u) => {
              userRepo.updateSigninTime(u)
              // create new session for the user
              val fwrsid = userRepo.createNewSessionForUser(u, deviceid, deviceinfo.toString())
              val fPayloadUser = fwrsid.map( (tuple) => {
                val sesid = tuple._2
                val payloadUser = PayloadUserFactory.createWithUserAndSession(u, sesid)
                payloadUser
              })
              
              val apiRes = ApiResult(
                ReturnCode.SIGNIN_USER.id,
                ReturnCode.SIGNIN_USER.toString(),
                ReturnResult.RESULT_SUCCESS.toString(),
                "user found and password valid"
              )
              val fres = for {
                pu <- fPayloadUser;
                ar <- Future{ apiRes }
              } yield {
                (ar, pu)
              }
              fres
            }
            case (None) => {
              val apiRes = ApiResult(
                ReturnCode.SIGNIN_USER.id,
                ReturnCode.SIGNIN_USER.toString(),
                ReturnResult.RESULT_FAILED.toString(),
                "user not found / password not valid"
              )
              val tup = (apiRes, payloadUser)
              Future {
                tup
              }
            }
          }
        }
      )
      str += jsReq
      LogManager.DebugLog(this, str)
    } catch {
      case t: Throwable => {
        LogManager.DebugException(this, "ex: ", t)
        val apiRes = ApiResult(
          ReturnCode.SIGNIN_USER.id,
          ReturnCode.SIGNIN_USER.toString(),
          ReturnResult.RESULT_ERROR.toString(),
          t.getMessage
        )
        fTuple = Future { (apiRes, payloadUser) }
      }
    }
    fTuple.map( (tuple) => {
      val ar = tuple._1
      val pu = tuple._2
      Ok(com.mangafun.views.html.signin.createuser.render(ar, pu))
    })
//    fApiRes.map( apiRes => {
//      Ok(com.mangafun.views.html.common.apiresult.render(apiRes))
//    })
  }
  
  def createuser(): Action[AnyContent] = Action.async { implicit request =>
    var str = ""
    var apiRes = ApiResult(
        ReturnCode.CREATE_USER.id,
        ReturnCode.CREATE_USER.toString(),
        ReturnResult.RESULT_ERROR.toString(),
        "init"
    )
    var fApiRes: Future[ApiResult] = null.asInstanceOf[Future[ApiResult]]
    fApiRes = Future {
      apiRes
    }
    var payloadUser = PayloadUser("", "", "")
    var fTuple: Future[(ApiResult, PayloadUser)] = Future {
      (apiRes, payloadUser)
    }
    try {
      val oReq = request.body.asJson
      val jsReq = oReq.getOrElse(JsString("null"))
      val firstname = (jsReq \ "firstname").getOrElse(JsString("null")).as[String]
      val lastname = (jsReq \ "lastname").getOrElse(JsString("null")).as[String]
      val bday = (jsReq \ "bday").getOrElse(JsString("01-Jan-1970")).as[String]
      val sex = (jsReq \ "sex").getOrElse(JsString("null")).as[String]
      val email = (jsReq \ "email").getOrElse(JsString("null")).as[String]
      val password = (jsReq \ "pwd").getOrElse(JsString("null")).as[String]
      val deviceinfo = (jsReq \ "deviceinfo").getOrElse(JsString("null")).as[JsValue]
      LogManager.DebugLog(this, "deviceinfo: " + deviceinfo);
      val deviceid = (jsReq \ "deviceinfo" \ "deviceId").getOrElse(JsString("null")).as[String]
      LogManager.DebugLog(this, "deviceid: " + deviceid);
      val sdt = new SimpleDateFormat("dd-MMM-yyyy")
      val gender = if ( sex.equalsIgnoreCase("female") ) 0 else 1
      val birthday = sdt.parse(bday)
      val fUserExists = userRepo.getUserByEmail(email).map( oUser => {
        oUser match {
          case Some(u) => { true }
          case None => { false }
        }
      })
      fTuple = fUserExists.flatMap( bExists => {
        str += jsReq
        if ( bExists ) {
          LogManager.DebugLog(this, "USER EXISTS! " + str)
          val fAr = Future {
            ApiResult( ReturnCode.CREATE_USER.id, ReturnCode.CREATE_USER.toString(), ReturnResult.RESULT_FAILED.toString(), "user already exists!")
          }
          val fPu = Future {
            PayloadUser("", "", "")
          }
          val res: Future[(ApiResult, PayloadUser)] = {
            val ar = fAr
            val pu = fPu
            for { v1 <- ar; v2 <- pu } yield (v1, v2)
          }
          res
        } else {
          LogManager.DebugLog(this, "CREATING! " + str)
          val fres = userRepo.createNewUser(firstname, lastname, birthday, gender, email, password, deviceid, deviceinfo.toString)
          val fPu = fres.map {
            case ( wr, user, sessionId ) => {
              PayloadUserFactory.createWithUserAndSession(user, sessionId)
            }
          }
          val fAr = Future {
            ApiResult( ReturnCode.CREATE_USER.id, "success", ReturnResult.RESULT_SUCCESS.toString(), str)
          }
          // https://stackoverflow.com/a/11158557/474330
          val res: Future[(ApiResult, PayloadUser)] = {
            val ar = fAr
            val pu = fPu
            for { v1 <- ar; v2 <- pu } yield (v1, v2)
          }
          res
        }
      })
    } catch {
      case t: Throwable => {
        LogManager.DebugException(this, "ex: ", t)
        fTuple = Future {
          (
            ApiResult(
              ReturnCode.CREATE_USER.id,
              ReturnCode.CREATE_USER.toString(),
              ReturnResult.RESULT_ERROR.toString(),
              t.getMessage
            ),
            payloadUser
          )
        }
      }
    }
    fTuple.map( (tuple) => {
      val ar = tuple._1
      val pu = tuple._2
      Ok(com.mangafun.views.html.signin.createuser.render(ar, pu))
    })
//    fApiRes.map( apiRes => {
//      Ok(com.mangafun.views.html.signin.createuser.render(apiRes, payloadUser))
//    })
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
        ReturnResult.RESULT_ERROR.toString(),
        "init"
    )
    try {
//      val req = request.body.asText // None
      val oReq = request.body.asJson
      str += oReq.getOrElse("nothing")
      apiRes = ApiResult(
          ReturnCode.TEST_FETCHPOST.id,
          "success",
          ReturnResult.RESULT_SUCCESS.toString(),
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
            ReturnResult.RESULT_ERROR.toString(),
            t.getMessage
        )
      }
    }
    Future {
      Ok(com.mangafun.views.html.common.apiresult.render(apiRes))
    }
  }  
}