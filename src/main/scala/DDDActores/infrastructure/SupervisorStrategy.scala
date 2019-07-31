package DDDActores.infrastructure

import akka.actor.{ Actor, OneForOneStrategy, Props }
import akka.actor.SupervisorStrategy._
import com.datastax.driver.core.exceptions.NoHostAvailableException

import scala.concurrent.duration._

class SupervisorStrategy extends Actor {

  val printer = context.actorOf(Props(new AccountPersistentActor), "printer-actor")

  override def receive: Receive = {
    case msg => printer forward msg
  }
}
