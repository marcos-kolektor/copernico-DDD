package DDDActores.infrastructure

import java.util.UUID

import DDDActores.domain.model.AccountActor
import akka.actor.SupervisorStrategy.{ Escalate, Restart, Resume, Stop }
import akka.actor._
import akka.persistence._
import com.datastax.driver.core.exceptions.NoHostAvailableException

object AccountPersistentActor {

  def props(): Props = { Props(new AccountPersistentActor) }
  sealed trait Event

  case class AddMonetAccount(idTransaction: Long, account: Long, state: String, amount: Long) extends Event
  case class FrozenAccount(idTransaction: Long, account: Long, amount: Long) extends Event
  case class MoneyAddTo(idTransaction: Long, account: Long, amount: Long) extends Event
  case class ConfirmMoneyFrozenSucc(idTransaction: Long) extends Event
  case class TransactionInitial(idTransaction: Long, account: Long, amount: Long) extends Event
  case class FinishTransaction(account: Long, amount: Long) extends Event

  case class AccountState(idTransaction: Long = 1L, account: Long = 0L, amount: Long = 0L, failure: String = "", nameAccount: String = "Diana") {
    def updated(event: Event): AccountState = event match {
      case TransactionInitial(idTransaction, account, amount) =>
        AccountState(idTransaction, account, amount)

      case FrozenAccount(idTransaction, account, amount) =>
        AccountState(idTransaction, account, amount)

      case MoneyAddTo(idTransaction, account, amount) =>
        AccountState(idTransaction, account, amount)

      case FinishTransaction(account, amount) =>
        AccountState(idTransaction, account, amount)

      case _ => AccountState(idTransaction, account, amount)
    }
  }
}

class AccountPersistentActor extends PersistentActor with ActorLogging {
  import AccountPersistentActor._

  def _account: String = self.path.name
  override val persistenceId: String = _account

  var state = AccountState()
  def fromActor = context.actorSelection(s"/user/${state.nameAccount}")

  override def preRestart(reason: Throwable, message: Option[Any]) = {
    super.preRestart(reason, message)
  }

  def updateState(event: Event): Unit =
    state = state.updated(event)

  val receiveRecover: Receive = {
    case evt: Event =>
      log.info(s"replay event: $evt")
      updateState(evt)
    case SnapshotOffer(_, snapshot: AccountState) => state = snapshot
  }

  val receiveCommand: Receive = {
    case AddMonetAccount(idTransaction, account, state, amount) => {
      println("Receive Command AddMoneyAccount.....")
      persist(TransactionInitial(idTransaction, account, amount))(updateState)
      fromActor ! AccountActor.FrozenAccount(account, amount)
    }

    case AccountActor.ConfirmMoneyFrozenSuc(idTransaction) => {
      println("Receive Command ConfirmMoneyFrozenSuc actor ManagerPersistence.....")
      persist(FrozenAccount(idTransaction, state.account, state.amount))(updateState)
      fromActor ! AccountActor.MoneyAddTo(state.account, state.amount)
    }

    case AccountActor.ConfirmAddMoneySuc(account, amount, state) => {
      println("---Receive Command ConfirmAddMoneySuc actor ManagerPersistence.....")
      persist(MoneyAddTo(account, amount, account))(updateState)
      fromActor ! AccountActor.FinishTransaction(account, amount)
    }

    case AccountActor.ConfirmFinishTransactionSuc(account, amount) => {
      println("---Receive Command ConfirmFinishTransactionSuc actor ManagerPersistence.....")
      persist(FinishTransaction(account, amount))(updateState)
      context.system.eventStream.publish(s"----------------Finish transaction...${state.idTransaction}")
    }
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

object PersistentActorExample extends App {
  val system = ActorSystem("example")
  val persistentActor = system.actorOf(AccountPersistentActor.props(), "persistentActor-4-scala")
  // persistentActor ! Send(UUID.fromString("333f33c3-333b-3f33-3333-ce3333d33c33"), persistentActor)
  Thread.sleep(5000)
  system.terminate()

}
