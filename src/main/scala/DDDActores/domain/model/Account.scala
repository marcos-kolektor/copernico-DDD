package DDDActores.domain.model

import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef}

class Account(uuid: UUID, eventPublisher: ActorRef) extends Actor with ActorLogging{



  override def receive: Receive = ???

}
