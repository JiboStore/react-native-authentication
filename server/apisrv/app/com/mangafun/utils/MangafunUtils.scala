package com.mangafun.utils

import java.security.SecureRandom
import java.math.BigInteger
import java.util.UUID
import play.api.mvc._
import play.api.cache.CacheApi
import scala.concurrent.duration._

import scala.concurrent.Future

object MangafunUtils {
  
  val secureRandom: SecureRandom = new SecureRandom()
  
  def generateSessionId() : String = {
    // https://stackoverflow.com/questions/41107/how-to-generate-a-random-alpha-numeric-string
    new BigInteger(130, secureRandom).toString(32)
  }
  
//  def generateTrainerId() : String = {
//    var uuid = UUID.randomUUID().toString()
//    uuid = uuid.replace("-", "")
//    uuid
//  }
//  
//  def clientSafeTrainer(trainer: Trainer): Trainer = {
//    val t = trainer.copy(pwhash="", pwsalt="")
//    t
//  }
  
//  def createPassword(clearString: String) : String = {
//   "unused"
//  }
  
//  def getPersonFromCache(implicit session: Session, cache: CacheApi): Option[Person] = {
//    val optionSid = session.get("sessionId")
//    val sId = optionSid.map({ s => s }).getOrElse("")
//    cache.get[Person](sId)
//  }
//  
//  def setPersonToCache(sessionId: String, person: Person)(implicit cache: CacheApi) = {
//    cache.set(sessionId, person, 2.hours)
//  }
  
  // mapping an Option: http://www.nurkiewicz.com/2014/06/optionfold-considered-unreadable.html

}
