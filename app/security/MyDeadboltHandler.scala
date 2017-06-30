package security

import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global

import be.objectify.deadbolt.scala.AuthenticatedRequest
import be.objectify.deadbolt.scala.DeadboltHandler
import be.objectify.deadbolt.scala.DynamicResourceHandler
import be.objectify.deadbolt.scala.models.Subject
import models.User
import play.api.mvc.Request
import play.api.mvc.Result
import play.api.mvc.Results
import play.api.mvc.Results.Status

/**
 *
 * @author Steve Chaloner (steve@objectify.be)
 */
class MyDeadboltHandler(dynamicResourceHandler: Option[DynamicResourceHandler] = None) extends DeadboltHandler {

  def beforeAuthCheck[A](request: Request[A]) = Future(None)

  override def getDynamicResourceHandler[A](request: Request[A]): Future[Option[DynamicResourceHandler]] = {
    Future(dynamicResourceHandler.orElse(Some(new MyDynamicResourceHandler())))
  }

  override def getSubject[A](request: AuthenticatedRequest[A]): Future[Option[Subject]] = {
    // e.g. request.session.get("user")
    Future(Some(new User("steve")))
  }

  def onAuthFailure[A](request: AuthenticatedRequest[A]): Future[Result] = {
    Future.successful{Status(404)("not found")}
  }
}