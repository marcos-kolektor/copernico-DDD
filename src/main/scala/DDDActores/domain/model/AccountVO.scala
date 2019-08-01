package DDDActores.domain.model

import java.util.UUID

import akka.actor.{ActorRef, Props}

object AccountVO {
  def props(uuid: UUID, eventPublisher: ActorRef): Props = {
    Props(new Account(uuid, eventPublisher))
  }
  case class AccountState(nroCuenta : Int, saldo : Int , state : Char = 'A')
  case class Create (accountState: AccountState)
  case class Update (accountState: AccountState)
  case class StateUpdate(state: Char)
}
