package com.mangafun.controllers
import play.api.mvc._

import play.api.mvc._
import javax.inject.Inject
import play.modules.reactivemongo.ReactiveMongoApi
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.ReactiveMongoComponents
import com.mangafun.repos._
import play.api.libs.json._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import reactivemongo.bson._
import reactivemongo.api.commands.WriteResult
import com.mangafun.repos._

class UsersTestController @Inject() (reactiveMongoApi: ReactiveMongoApi) 
  extends Controller with MongoController with ReactiveMongoComponents {
  
  import com.mangafun.controllers.UsersField._
  
  def reactiveMongoApi() : ReactiveMongoApi = {
    return reactiveMongoApi;
  }
  
  def usersRepo = new UsersRepoImpl(reactiveMongoApi)
  
  def index = Action.async { implicit request =>
    usersRepo.find().map(users => Ok(Json.toJson(users)))
  }
  
  def create = Action.async(BodyParsers.parse.json) {
    implicit request => {
      val name = (request.body \ Name).as[String]
      val description = (request.body \ Description).as[String]
      val author = (request.body \ Author).as[String]
      usersRepo.save(BSONDocument(
          Name -> name,
          Description -> description,
          Author -> author )).map( result => Created)
    }
  }
  
  def read(id: String) = Action.async {
    implicit request => {
      usersRepo.select(BSONDocument(Id -> BSONObjectID(id)))
      .map(widget => Ok(Json.toJson(widget)))
    }
}
  
  def update(id: String) = Action.async(BodyParsers.parse.json) {
    implicit request => {
      val name = (request.body \ Name).as[String]
      val description = (request.body \ Description).as[String]
      val author = (request.body \ Author).as[String]
      usersRepo.update(BSONDocument(Id -> BSONObjectID(id)),
        BSONDocument("$set" -> BSONDocument(
            Name -> name,
            Description -> description,
            Author -> author))).map( result => Accepted)
    }
  }
  
  def delete(id: String) = Action.async {
    usersRepo.remove(BSONDocument(Id -> BSONObjectID(id)))
    .map( result => Accepted)
  }
  
}

object UsersField {
  val Id = "_id"
  val Name = "name"
  val Description = "description"
  val Author = "author"
}