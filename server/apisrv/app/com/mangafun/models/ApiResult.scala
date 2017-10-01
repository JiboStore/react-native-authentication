package com.mangafun.models

import scala.concurrent._

import javax.inject.Inject
import java.util.Date
import play.api.libs.json._
//import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.ReadPreference
import reactivemongo.api.commands.WriteResult
import reactivemongo.bson._
import reactivemongo.api.Cursor
import scala.collection.immutable.HashMap
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api.commands.bson.BSONCountCommand.{ Count, CountResult }
import reactivemongo.api.commands.bson.BSONCountCommandImplicits._
import reactivemongo.bson.BSONDocument
import reactivemongo.play.json.collection.JSONCollection
import scala.collection.mutable.ListBuffer

import org.mindrot.jbcrypt.BCrypt

case class ApiResult (
    var code: Int,
    var error: String,
    var message: String
) {
  
}

object ApiResult {
  import play.api.libs.json.Json
  
  // Generates Writes and Reads
  implicit val apiJsonFormats = Json.format[ApiResult]

}
