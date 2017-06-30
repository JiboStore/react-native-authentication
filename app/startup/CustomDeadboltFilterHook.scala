package startup

import javax.inject._
import play.api._
import play.api.mvc._
import scala.concurrent.Future
import play.api.inject.Module
import play.api.inject.Binding
import security.MyAuthorizedRoutes
import be.objectify.deadbolt.scala.filters.AuthorizedRoutes
import be.objectify.deadbolt.scala.cache.HandlerCache
import security.MyHandlerCache

class CustomDeadboltFilterHook extends Module {
  override def bindings(env: Environment, conf: Configuration): Seq[Binding[_]] = Seq(
      bind[AuthorizedRoutes].to[MyAuthorizedRoutes],
      bind[HandlerCache].to[MyHandlerCache]
  );
}
