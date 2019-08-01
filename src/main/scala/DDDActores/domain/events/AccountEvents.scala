package DDDActores.domain.events

import DDDActores.domain.model.AccountVO.AccountState
import akka.actor.ActorRef

object AccountEvents {

  sealed  trait AccountEvent extends DomainEvent {
    val classifier: String = "events-Account"
  }

  case class Created(accountState: AccountState)(implicit val origin: ActorRef) extends AccountEvent

  case class Updated(newInfoAccount: AccountState, oldInfoAccount: AccountState)(implicit val origin: ActorRef) extends AccountEvent

  case class Stored(accountState: AccountState)(implicit val origin: ActorRef) extends AccountEvent
}
