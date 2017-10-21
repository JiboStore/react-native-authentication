package com.mangafun.models

import scala.collection.immutable.List

import play.api.libs.json.Json
import reactivemongo.bson.Macros
import com.mangafun.repos.MangafunUser

case class PayloadUser (
//  var userid: String,
  var firstname: String,
  var lastname: String,
  var session: String
//  var birthday: Date,
//  var gender: Int,
//  var email: String,
//  var pwsalt: String,
//  var pwhash: String,
//  var deviceinfo: String,
//  var sessions: List[PayloadSession],
//  var createdDate: Date,
//  var lastLogin: Date
) {
}

object PayloadUserFactory {
  def createWithUserAndSession(user: MangafunUser, sesid: String): PayloadUser = {
    val pu = PayloadUser(
        user.firstname,
        user.lastname,
        sesid
    )
    pu
  }
  
  def createEmptyPayloadUser() = {
    PayloadUser("", "", "")
  }
}

//case class PayloadSession (
//  var sessionid: String,
//  var deviceid: String,
//  var deviceinfo: String,
//  var lastdeviceinfo: String,
//  var createdDate: Date,
//  var lastLogin: Date
//) {
//}

//object PayloadSession {
//  // Generates Writes and Reads
//  implicit var sessionJsonFormats = Json.format[PayloadSession]
//  implicit var sessionBsonFormats = Macros.handler[PayloadSession]
//}

object PayloadUser {
  // Generates Writes and Reads
  implicit val mangafunuserJsonFormats = Json.format[PayloadUser]
  implicit val mangafunuserBsonFormats = Macros.handler[PayloadUser]
}
