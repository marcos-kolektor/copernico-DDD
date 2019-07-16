package Router

// importing akka...Directives._ makes `get` and `complete` avaialable in scope
import Controller._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.actor.{Actor, ActorRef, Props}

// TODO: Transform router in actor
object Router {

    // complete("Hello World") // The complete method is what is typically used in Akka HTTP when we want to return an HTTP response to the client.

    def routes(actorRef: ActorRef): Route = {
        pathPrefix("accounts") {
            path("AllAccounts") { _allAccounts(actorRef) } ~ //don't forget `~`
            path("CreateAccount") { _createAccount(actorRef) } ~
            path("EditAccount") { _editAccount(actorRef) } ~
            path("Accredit") { _accredit(actorRef) } ~
            path("Debit") { _debit(actorRef) } ~
            path("SearchAccount" / LongNumber) { numberAccount =>  _searchAccount(numberAccount, actorRef) }
        }
    }

}