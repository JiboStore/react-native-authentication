package com.mangafun.repos

import scala.concurrent._

import java.util.Date
import javax.inject.Inject
import play.api.libs.json._
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.play.json._
import reactivemongo.play.json.collection._
import reactivemongo.api.ReadPreference
import reactivemongo.api.commands.WriteResult
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api.commands.bson.BSONCountCommand.{ Count, CountResult }
import reactivemongo.api.commands.bson.BSONCountCommandImplicits._
import reactivemongo.bson._

//import reactivemongo.api.Cursor
//import scala.collection.immutable.HashMap
//import reactivemongo.api.commands.bson.BSONCountCommandImplicits._
//import reactivemongo.bson.BSONDocument

import org.mindrot.jbcrypt.BCrypt

case class MangafunUser (
  var userid: String,
  var firstname: String,
  var lastname: String,
  var birthday: Date,
  var gender: Int,
  var email: String,
  var pwsalt: String,
  var pwhash: String,
  var createdDate: Date,
  var lastLogin: Date
) {
  
}

object MangafunUser {
  import play.api.libs.json.Json
  
  // Generates Writes and Reads
  implicit val mangafunuserJsonFormats = Json.format[MangafunUser]
  
  implicit val mangafunuserBsonFormats = Macros.handler[MangafunUser]
}

class MangafunUserRepo @Inject() (reactiveMongoApi: ReactiveMongoApi) {
  
  import play.api.libs.concurrent.Execution.Implicits.defaultContext
 
  def jsonCollection: Future[JSONCollection] = reactiveMongoApi.database.map( db => {
    db.collection[JSONCollection]("mfuser")
  })
  def bsonCollection: Future[BSONCollection] = reactiveMongoApi.database.map( db => {
    db.collection[BSONCollection]("mfuser")
  })
  
  def getUserByEmail(email: String): Future[Option[MangafunUser]] = {
    val queryParams: BSONDocument = BSONDocument("email" -> email)
    val fres = bsonCollection.flatMap( db => {
      db.find(queryParams).one[MangafunUser]
    })
    fres
  }
  
  def createNewUser(firstname: String, lastname: String, birthday: Date, gender: Int, email: String, password: String): Future[WriteResult] = {
    val now = new Date()
    val usercount = countAllUsers()
    val userid = email // for now
    val pwsalt = BCrypt.gensalt()
    val pwhash = BCrypt.hashpw(password, pwsalt)
    usercount.flatMap( count => {
      val user = MangafunUser(
        count + 1 + "",
        firstname,
        lastname,
        birthday,
        gender,
        email,
        pwsalt,
        pwhash,
        now,
        now
      )
      bsonCollection.flatMap( db => {
        db.insert(user)
      })
    })
  }
  
  def countAllUsers(): Future[Int] = {
    val query = BSONDocument("_id" -> BSONDocument("$exists" -> true))
    val command = Count(query)
    val result: Future[CountResult] = bsonCollection.flatMap( db => {
      db.runCommand(command)
    })
    result.map( res => {
      val count: Int = res.value
      count
    })
  }

}