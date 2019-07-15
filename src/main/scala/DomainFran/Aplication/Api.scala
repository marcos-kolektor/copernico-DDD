package Aplication

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import Router.Router._
import Domain._
import Aplication._

import scala.io.StdIn

object Api {

    def main(args: Array[String]) {

        // * Akka HTTP is on top of the other two layers
        // * Akka Stream (ActorMaterializer) handles internal processing of Akka HTTP
        // * Akka Actor (ActorSystem) is the base for running Akka Stream


        // remember to make them implicit!!
        implicit val system = ActorSystem("Main")       // for Akka Actor
        implicit val materializer = ActorMaterializer() // for Akka Stream

        val accountsActor = system.actorOf(Props(new AccountActor(Array())), name = "accountsActor")

        // needed for the future flatMap/onComplete in the end
        implicit val executionContext = system.dispatcher
        val bindingFuture = Http().bindAndHandle(routes(accountsActor), "localhost", 8080)

        println(s"Server online at http://localhost:8080/accounts/\nPress RETURN to stop...")

        StdIn.readLine() // let it run until user presses return

        bindingFuture
          .flatMap(_.unbind()) // trigger unbinding from the port
          .onComplete(_ => system.terminate()) // and shutdown when done
    }

}