package DDDActores.infrastructure

import akka.actor.{ ActorRef, Props }

object DBAccountRepositoryVO {

  def props(eventPublisher: ActorRef): Props = {
    Props(new DBAccountRepository(eventPublisher))
  }

}
