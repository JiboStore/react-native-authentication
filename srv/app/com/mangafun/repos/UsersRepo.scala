package com.mangafun.repos

import scala.concurrent._

import javax.inject.Inject
import play.api.libs.json._
import play.modules.reactivemongo.ReactiveMongoApi
import play.modules.reactivemongo.json._
import play.modules.reactivemongo.json.collection._
import reactivemongo.api.ReadPreference
import reactivemongo.api.commands.WriteResult
import reactivemongo.bson._
import be.objectify.deadbolt.scala.models.Subject
import be.objectify.deadbolt.scala.models.Role
import be.objectify.deadbolt.scala.models.Permission

class Users extends Subject {
  /** inherited from Subject */
  def identifier: String = ""

  /** inherited from Subject */
  def roles: List[Role] = List[Role]()

  /** inherited from Subject */
  def permissions: List[Permission] = List[Permission]()
}

trait UsersRepo {
  
  def find() (implicit ec: ExecutionContext) : Future[List[JsObject]]
  
  def select(selector: BSONDocument) (implicit ec: ExecutionContext) : Future[Option[JsObject]]
  
  def update(selector: BSONDocument, update: BSONDocument) (implicit ec: ExecutionContext) : Future[WriteResult]
  
  def remove(document: BSONDocument) (implicit ec: ExecutionContext) : Future[WriteResult]
  
  def save(document: BSONDocument) (implicit ec: ExecutionContext) : Future[WriteResult]
  
}

class UsersRepoImpl @Inject() (reactiveMongoApi: ReactiveMongoApi) extends UsersRepo {
 
  def collection = reactiveMongoApi.db.collection[JSONCollection]("users");
 
  override def find()(implicit ec: ExecutionContext): Future[List[JsObject]] = {
    val genericQueryBuilder = collection.find(Json.obj());
    val cursor = genericQueryBuilder.cursor[JsObject](ReadPreference.Primary);
    cursor.collect[List]()
  }
 
  override def select(selector: BSONDocument)(implicit ec: ExecutionContext): Future[Option[JsObject]] = {
    collection.find(selector).one[JsObject]
  }
 
  override def update(selector: BSONDocument, update: BSONDocument)(implicit ec: ExecutionContext): Future[WriteResult] = {
    collection.update(selector, update)
  }
 
  override def remove(document: BSONDocument)(implicit ec: ExecutionContext): Future[WriteResult] = {
    collection.remove(document)
  }
 
  override def save(document: BSONDocument)(implicit ec: ExecutionContext): Future[WriteResult] = {
    collection.update(BSONDocument("_id" -> document.get("_id").getOrElse(BSONObjectID.generate)), document, upsert = true)
  }
 
}