package Router

// importing akka...Directives._ makes `get` and `complete` avaialable in scope
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.actor.ActorSystem

object Router {

    // complete("Hello World") // The complete method is what is typically used in Akka HTTP when we want to return an HTTP response to the client.

    lazy val routes: Route =
        pathPrefix("accounts") {
            path("account1") {
                get {
                    complete("Cuenta 1")
                }
            } ~ //don't forget `~`
            path("account2") {
                get {
                      complete("Cuenta 2")
                }
            } ~
            path("account3") {
                get {
                      complete("Cuenta List")
                }
            }
        }


}