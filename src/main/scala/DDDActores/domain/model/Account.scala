package DDDActores.domain.model

import DDDActores.infrastructure.AccountPersistentActor
import akka.actor.SupervisorStrategy.{ Escalate, Restart, Resume }
import akka.actor.{ Actor, ActorLogging, ActorRef, ActorSystem, OneForOneStrategy, Props }
import akka.persistence.{ PersistentActor, SnapshotOffer }
import com.datastax.driver.core.exceptions.NoHostAvailableException

object AccountActor {

  def props(mount: Long, active: String): Props = { Props(new AccountActor(mount, active)) }
  sealed trait Event

  case class AddMonetAccount(account: Long, amount: Long) extends Event
  case class MoneyAddTo(account: Long, amount: Long) extends Event
  case class FrozenAccount(account: Long, amount: Long) extends Event
  case class ConfirmMoneyFrozenSuc(account: Long) extends Event
  case class ConfirmAddMoneySuc(account: Long, amount: Long, state: String) extends Event
  case class FinishTransaction(account: Long, amount: Long) extends Event
  case class ConfirmFinishTransactionSuc(account: Long, amount: Long) extends Event

  case class AccountState(account: String, state: String, amount: Long) {

    def updated(evt: Event): AccountState = evt match {
      case FrozenAccount(account, mount) => {
        copy(amount = amount - 20)
      }
      case AddMonetAccount(account, mount) => {
        copy(amount = amount + mount)
      }
      case FinishTransaction(account, mount) => {
        copy(amount = amount)
      }
      case _ => copy(amount = 0L)
    }
  }
}

class AccountActor(mount: Long, active: String) extends PersistentActor with ActorLogging {
  import AccountActor._
  def fromActor = context.actorSelection(s"/user/tm2")
  def _account: String = self.path.name
  override val persistenceId: String = _account
  log.info(s"---MOUNT: --state is ${mount}---")
  var state = AccountState(_account, active, mount)
  log.info(s"---account: --state is ${state.toString}---")

  override def preRestart(reason: Throwable, message: Option[Any]) = {
    super.preRestart(reason, message)
  }

  def updateState(event: Event): Unit =
    state = state.updated(event)

  val receiveRecover: Receive = {
    case evt: Event => updateState(evt)
    case SnapshotOffer(_, snapshot: AccountState) => state = snapshot
  }

  val receiveCommand: Receive = {
    case FrozenAccount(account, mount) => {
      println("---AMOUNT---" + state.amount)
      println("---AMOUNT PARAMETER---" + mount)
      if (state.amount > 0) {
        println("Receive Command FrozenAccount actor Account.....")
        persist(FrozenAccount(account, mount)) { event =>
          updateState(event)
        }
        fromActor ! ConfirmMoneyFrozenSuc(account)
      }
    }

    case MoneyAddTo(account, amount) => {
      println(s"---STATE---${state.state}")
      if (state.state.equals("A")) {
        persist(AddMonetAccount(account, amount))(updateState)
        fromActor ! ConfirmAddMoneySuc(account, amount, state.state)
      }
    }

    case FinishTransaction(account, amount) => {
      persist(FinishTransaction(account, amount))(updateState)
      fromActor ! ConfirmFinishTransactionSuc(account, amount)
    }

    case "initial" =>
      log.info(s"---account: ${_account} state is ${state.toString}---")
  }

  // Implementar el (BackoffSupervisor), para supervisar cuando un actor realiza la persistencia
  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10) {
      case _: ArithmeticException => Resume
      case _: NullPointerException => Restart
      case _: NoHostAvailableException => Restart
      case _: IllegalArgumentException => Restart
      case _: Exception => Escalate
      case t => super.supervisorStrategy.decider.applyOrElse(t, (_: Any) => Escalate)
    }
}

object AccountActorApp extends App {
  val system = ActorSystem("Account")
  val diana = system.actorOf(AccountActor.props(mount  = 20000, active = "A"), "Diana")
  diana.!("initial")
  Thread.sleep(6000)
  system.terminate()

}
