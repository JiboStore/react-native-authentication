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
import com.mangafun.repos._
import com.mangafun.models._
import com.mangafun.utils._
import scala.collection.Map
import scala.collection.Seq

class SignInController @Inject() (reactiveMongoApi: ReactiveMongoApi)(wsClient: WSClient)(appProvider: Provider[Application])
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
  
  def createuser(): Action[AnyContent] = Action.async { implicit request =>
    var str = ""
    var apiRes = ApiResult(
        ReturnCode.CREATE_USER.id,
        ReturnCode.CREATE_USER.toString(),
        "init"
    )
    try {
      val oReq = request.body.asJson
      str += oReq.getOrElse("nothing")
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