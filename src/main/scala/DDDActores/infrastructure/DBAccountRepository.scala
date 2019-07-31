package DDDActores.infrastructure

import DDDActores.domain.events.AccountEventsPublish.Subscribe
import DDDActores.domain.repository.AccountRepository.{ Send }
import akka.actor.{ Actor, ActorLogging, ActorRef }

class DBAccountRepository(eventPublisher: ActorRef) extends Actor with ActorLogging {

  override def preStart(): Unit = {
    eventPublisher ! Subscribe("events-Account")
  }

  override def receive: Receive = {
    case str: String => ???
  }

}
