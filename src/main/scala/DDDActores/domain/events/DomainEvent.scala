package DDDActores.domain.events

import akka.actor.ActorRef

trait DomainEvent {
  implicit val origin: ActorRef
  val classifier: String
}

