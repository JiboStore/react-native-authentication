package com.mangafun.repos

import scala.concurrent._

import java.util.Date
import javax.inject.Inject
import play.api.libs.json._
import play.modules.reactivemongo.ReactiveMongoApi
import play.modules.reactivemongo.json._
import play.modules.reactivemongo.json.collection._
import reactivemongo.api.ReadPreference
import reactivemongo.api.commands.WriteResult
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson._

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
  
  def createNewUser(firstname: String, lastname: String, birthday: Date, gender: Int, email: String, password: String) = {
    val now = new Date()
    val userid = email // for now
    val pwsalt = BCrypt.gensalt()
    val pwhash = BCrypt.hashpw(password, pwsalt)
    val user = MangafunUser(
        userid,
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
    val fres = bsonCollection.flatMap( db => {
      db.insert(user)
    })
    fres
  }

}