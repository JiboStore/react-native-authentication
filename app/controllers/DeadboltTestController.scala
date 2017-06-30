package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import scala.concurrent.Future

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class DeadboltTestController @Inject() extends Controller {
  def subjectpresent = Action.async {
    Future.successful(Ok("hello world"))
  }
}
