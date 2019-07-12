package Aplication

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer

import Router.Router._

object Api {

    def main(args: Array[String]) {

        // * Akka HTTP is on top of the other two layers
        // * Akka Stream (ActorMaterializer) handles internal processing of Akka HTTP
        // * Akka Actor (ActorSystem) is the base for running Akka Stream


        // remember to make them implicit!!
        implicit val system = ActorSystem("Main")       // for Akka Actor
        implicit val materializer = ActorMaterializer() // for Akka Stream

        Http().bindAndHandle(routes, "localhost", 8080)
    }

}