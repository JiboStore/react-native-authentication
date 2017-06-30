package security

import javax.inject.Inject
import be.objectify.deadbolt.scala.filters.FilterConstraints
import be.objectify.deadbolt.scala.filters.AuthorizedRoutes
import be.objectify.deadbolt.scala.filters.AuthorizedRoute
import be.objectify.deadbolt.scala.filters._

class MyAuthorizedRoutes @Inject() (filterConstraints: FilterConstraints) extends AuthorizedRoutes {
  override val routes: Seq[AuthorizedRoute] = Seq(
      AuthorizedRoute(Get, "/deadbolt/subjectpresent", filterConstraints.subjectPresent)
  );
}