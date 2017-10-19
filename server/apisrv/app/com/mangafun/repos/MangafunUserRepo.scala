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
import scala.collection.immutable.List
import com.mangafun.utils.MangafunUtils

case class MangafunUser (
  var userid: String,
  var firstname: String,
  var lastname: String,
  var birthday: Date,
  var gender: Int,
  var email: String,
  var pwsalt: String,
  var pwhash: String,
  var deviceinfo: String,
  var sessions: List[MangafunUserSession],
  var createdDate: Date,
  var lastLogin: Date
) {
  
}

case class MangafunUserSession (
  var sessionid: String,
  var deviceid: String,
  var deviceinfo: String,
  var lastdeviceinfo: String,
  var createdDate: Date,
  var lastLogin: Date
) {
  
}

object MangafunUserSession {
  import play.api.libs.json.Json
  // Generates Writes and Reads
  implicit var sessionJsonFormats = Json.format[MangafunUserSession]
  implicit var sessionBsonFormats = Macros.handler[MangafunUserSession]
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
  
  def updateSigninTime(user: MangafunUser): Future[WriteResult] = {
    val selector = BSONDocument("email" -> user.email)
    val modifier = BSONDocument(
        "$set" -> BSONDocument(
            "lastLogin" -> new Date()
        )
    )
    bsonCollection.flatMap( db => {
      db.update(selector, modifier)
    })
  }
  
  def getUserByEmailAndPassword(email: String, password: String): Future[(Option[MangafunUser], String)] = {
    val sesid = MangafunUtils.generateSessionId()
    val foUser = getUserByEmail(email)
    foUser.map(oUser => {
      oUser match {
        case Some(u) => {
          val bAuthenticated = BCrypt.checkpw(password, u.pwhash)
          if ( bAuthenticated ) {
            (Some(u), sesid)
          } else {
            (None, "")
          }
        }
        case None => (None, "")
      }
    })
  }
  
  def getUserByEmail(email: String): Future[Option[MangafunUser]] = {
    val queryParams: BSONDocument = BSONDocument("email" -> email)
    val fres = bsonCollection.flatMap( db => {
      db.find(queryParams).one[MangafunUser]
    })
    fres
  }
  
  def createNewUser(firstname: String, lastname: String, birthday: Date, gender: Int, email: String, password: String, devid: String, devinfostr: String): Future[(WriteResult, MangafunUser, String)] = {
    val now = new Date()
    val sesid = MangafunUtils.generateSessionId()
    val usercount = countAllUsers()
    val userid = email // for now
    val pwsalt = BCrypt.gensalt()
    val pwhash = BCrypt.hashpw(password, pwsalt)
    usercount.flatMap( count => {
      val session = MangafunUserSession(
          sesid,
          devid,
          devinfostr,
          devinfostr,
          now,
          now
      )
      val user = MangafunUser(
        count + 1 + "",
        firstname,
        lastname,
        birthday,
        gender,
        email,
        pwsalt,
        pwhash,
        devinfostr,
        List(session),
        now,
        now
      )
      bsonCollection.flatMap( db => {
        db.insert(user).map((_, user, sesid))
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