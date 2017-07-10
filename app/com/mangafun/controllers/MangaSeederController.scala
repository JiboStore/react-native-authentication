package com.playfairy.controllers

import play.api.mvc._
import javax.inject.Inject
import play.modules.reactivemongo.ReactiveMongoApi
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.ReactiveMongoComponents
import play.api.libs.json._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import reactivemongo.bson._
import reactivemongo.api.commands.WriteResult
import com.playfairy.models._
import play.Logger
import scala.concurrent.Future
import scala.util.Failure
import scala.util.Success


/** TODO: https://stackoverflow.com/a/37180103/474330 */

class BooksTestController @Inject() (reactiveMongoApi: ReactiveMongoApi) 
  extends Controller with MongoController with ReactiveMongoComponents {
  
  import com.playfairy.controllers.WidgetFields._
  
  def reactiveMongoApi() : ReactiveMongoApi = {
    return reactiveMongoApi;
  }
  
  def booksRepo = new BooksRepoImpl(reactiveMongoApi)
  
  def findByName(name: String) = Action.async {
    Logger.debug("findByName: " + name);
    var future = booksRepo.findByName(name)
    future.map( listBooks => {
//      listBooks.map( u => Ok( Json.toJson(u) ) )
      Ok( Json.toJson(listBooks) )
    });
  }
  
  def updateByName(name: String) = Action.async {
    var newRole = List("one", "two", "three")
    var future = booksRepo.updateByName("hello", newRole)
    future.map(writeResult => {
      if ( writeResult.ok ) {
        Ok("ok")
      } else {
        Ok("problem")
      }
    });
  }
  
  def seederPopulate = Action.async {    
    val count: Future[Int] = booksRepo.countAllRecords()
    val countResult: Future[Boolean] = count.map( c => {
      if ( c > 0 ) {
        true
      } else {
        false
      }
    });
    
    val createResult = count.map( c => {
      if ( c < 1 ) {
        val future = booksRepo.createByName("hello")
        future;
      } else {
        throw new Exception("already created")
      }
    });
    
    createResult.map(
       res => Ok("ok")
    ).recover{ 
//      case t => Ok("error")
      case t => Ok("error: " + t)
    }
    
//    createResult.map( res => {
//      case w : WriteResult => {
//        if ( w.ok ) {
//          Ok("ok")
//        } else {
//          Ok("no")
//        }
//      }
//      case e : Exception => {
//        Ok("no no")
//      }
//      case _ => {
//        Ok("unknown")
//      }
//    });
    
    
//    val future = booksRepo.createByName("hello")
//    future.map(writeResult => {
//      if ( writeResult.ok ) {
////        Redirect("http://www.apple.com/sg");
//        Ok("ok");
//      } else {
////        Redirect("http://www.microsoft.com/");
//        Ok("problem")
//      }
//    });
  }
  
  def seederClean = Action.async {
    val count: Future[Int] = booksRepo.countAllRecords()
   
    val clean = count.flatMap( c => {
      if ( c > 0 ) {
        val future = booksRepo.cleanDatabase();
        future;
      } else {
        throw new Exception("nothing to clean")
      }
    });
    
    // this is ok
//    clean.map ( res => {
//      Ok("success")
//    }).recover {
//      case t => Ok("error: " + t)
//    }
    
//    // this is also ok: https://stackoverflow.com/a/44976467/474330
//    clean.map{
//       case true => Ok("success")
//       case false => Ok("failed")
//    }.recover {
//      case t => Ok("error: " + t)
//    }
    
    // this is also ok: https://stackoverflow.com/a/44977295/474330
    clean.map( b => b match {
      case true => Ok("success")
      case false => Ok("failed")
    }).recover {
      case t => Ok("error: " + t)
    }
    
//    clean.map( b => {
//      case true => Ok("success")
//      case false => Ok("failed")
//    }).recover {
//      case t => Ok("error: " + t)
//    }
    
//    clean.map( (b : Boolean) => b match {
//      case true => Ok("clean succeeded")
//      case false => Ok("not cleaned")
//      case e : Exception => Ok("Exception: " + e)
//    });
    
//    val future = booksRepo.cleanDatabase();
//    future.map(result => {
//      if ( result ) {
////        Redirect("http://www.apple.com/sg");
//        Ok("ok")
//      } else {
////        Redirect("http://www.microsoft.com/");
//        Ok("problem")
//      }
//    });
  }
  
}