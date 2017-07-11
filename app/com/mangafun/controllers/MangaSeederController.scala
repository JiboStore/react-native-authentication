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
import scala.concurrent.Future
import scala.util.Failure
import scala.util.Success
import play.Play
import play.Application
import com.google.inject.Provider


/** TODO: https://stackoverflow.com/a/37180103/474330 */

class MangaSeederController @Inject() (reactiveMongoApi: ReactiveMongoApi)(appProvider: Provider[Application])
  extends Controller with MongoController with ReactiveMongoComponents {
  
  lazy val app = appProvider.get
  
  def reactiveMongoApi() : ReactiveMongoApi = {
    return reactiveMongoApi;
  }
  
  def index() : Action[AnyContent] = Action.async {
    Future {
      Ok("hello")
    }
  }
  
}