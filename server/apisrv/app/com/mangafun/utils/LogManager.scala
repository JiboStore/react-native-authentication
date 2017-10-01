package com.mangafun.utils

import play.Logger
import scala.concurrent.duration._

import scala.concurrent.Future


object LogManager {
  def DebugLog(obj: Any, message: String) = {
    Logger.debug(obj.getClass().getName() + " > " + message)
    Logger.error(obj.getClass().getName() + " > " + message)
  }
  
  def DebugError(obj: Any, message: String) = {
    Logger.debug(obj.getClass().getName() + " >> " + message)
    Logger.error(obj.getClass().getName() + " >> " + message)
  }
  
  def DebugException(obj: Any, message: String, ex: Throwable) = {
    Logger.debug(obj.getClass().getName() + " >>> " + message + ex.getMessage)
    Logger.error(obj.getClass().getName() + " >>> " + message + ex.getMessage)
  }
}
