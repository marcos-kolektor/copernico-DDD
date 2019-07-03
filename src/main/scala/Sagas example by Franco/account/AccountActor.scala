package account

import akka.actor.{ ActorLogging, Props }
import akka.persistence.{ PersistentActor, SnapshotOffer }
import domain.Account

object AccountActor {

  sealed trait Msg

  final case class FreezeMoney(deliveryId: Long, transactionId: Long, to: Account, amount: Long) extends Msg

  final case class ConfirmMoneyFrozenSucc(deliveryId: Long) extends Msg

  final case class ConfirmMoneyFrozenFail(deliveryId: Long, reason: String) extends Msg

  final case class AddMoney(deliveryId: Long, transaction: Long, from: Account, amount: Long) extends Msg

  final case class ConfirmMoneyAddedSucc(deliveryId: Long) extends Msg

  final case class ConfirmMoneyAddedFail(deliveryId: Long, reason: String) extends Msg

  final case class FinishTransaction(deliveryId: Long, transactionId: Long) extends Msg

  final case class ConfirmTransactionFinished(deliveryId: Long) extends Msg

  final case class UnfreezeMoney(deliveryId: Long, transactionId: Long) extends Msg

  final case class ConfirmMoneyUnfrozen(deliveryId: Long) extends Msg

  sealed trait Event

  final case class MoneyFrozen(transactionId: Long, to: Account, amount: Long) extends Event

  final case class MoneyAdded(transactionId: Long, from: Account, amount: Long) extends Event

  final case class TransactionFinished(transactionId: Long) extends Event

  final case class MoneyUnfrozen(transactionId: Long) extends Event

  def props(active: Boolean, balance: Long): Props = Props(classOf[AccountActor], active, balance)

  final case class AccountState(
      account:              String,
      active:               Boolean,
      balance:              Long,
      finishedTransactions: Map[Long, Transaction],
      inFlightTransaction:  Map[Long, Transaction]
  ) {

    def updated(event: Event): AccountState = event match {
      case MoneyFrozen(id, to, amount) =>
        val transaction = Transaction(id, account, to, amount)
        copy(balance             = balance - amount, inFlightTransaction = inFlightTransaction + (id -> transaction))

      case MoneyAdded(id, from, amount) =>
        val transaction = Transaction(id, from, account, amount)
        copy(balance              = balance + amount, finishedTransactions = finishedTransactions + (id -> transaction))

      case TransactionFinished(id) =>
        val transaction = inFlightTransaction(id)
        copy(
          finishedTransactions = finishedTransactions + (id -> transaction),
          inFlightTransaction  = inFlightTransaction - id
        )

      case MoneyUnfrozen(id) =>
        val transaction = inFlightTransaction(id)
        copy(balance             = balance + transaction.amount, inFlightTransaction = inFlightTransaction - id)

    }
  }
}

final case class Transaction(transactionId: Long, from: Account, to: Account, amount: Long)

class AccountActor(_active: Boolean, _balance: Long) extends PersistentActor with ActorLogging {

  import AccountActor._

  var state = AccountState(
    account = _account,
    active  = _active,
    balance = _balance,
    Map.empty, Map.empty
  )

  def _account: String = self.path.name

  override val persistenceId: String = _account

  def updateState(event: Event): Unit =
    state = state.updated(event)

  val receiveRecover: Receive = {
    case evt: Event => updateState(evt)
    case SnapshotOffer(_, snapshot: AccountState) => state = snapshot
  }

  override def receiveCommand: Receive = {
    case msg @ FreezeMoney(deliveryId, transactionId, to, amount) =>
      log.info(s"received $msg ")
      if (state.finishedTransactions.contains(transactionId)
        || state.inFlightTransaction.contains(transactionId)) {
        sender() ! ConfirmMoneyFrozenSucc(deliveryId)
      }
      else if (state.balance < amount) {
        sender() ! ConfirmMoneyFrozenFail(
          deliveryId,
          s"insufficient balance, ${_account} have ${state.balance}, which is less than $amount"
        )
      }
      else if (!state.active) {
        sender() ! ConfirmMoneyFrozenFail(deliveryId, s"account ${_account} is not active")
      }
      else {
        persist(MoneyFrozen(transactionId, to, amount)) { e =>
          updateState(e)
          sender() ! ConfirmMoneyFrozenSucc(deliveryId)
        }
      }

    case msg @ AddMoney(deliveryId, transactionId, from, amount) =>
      log.info(s"received $msg ")
      if (state.finishedTransactions.contains(transactionId)) {
        sender() ! ConfirmMoneyAddedSucc(deliveryId)
      }
      else if (!state.active) {
        sender() ! ConfirmMoneyAddedFail(deliveryId, s"account ${_account} is not active")
      }
      else {
        persist(MoneyAdded(transactionId, from, amount)) { e =>
          updateState(e)
          sender() ! ConfirmMoneyAddedSucc(deliveryId)
        }
      }

    case msg @ FinishTransaction(deliveryId, transactionId) =>
      log.info(s"received $msg ")
      if (state.finishedTransactions.contains(transactionId)) {
        sender() ! ConfirmTransactionFinished(deliveryId)
      }
      else {
        persist(TransactionFinished(transactionId)) { e =>
          updateState(e)
          sender() ! ConfirmTransactionFinished(deliveryId)
        }
      }

    case msg @ UnfreezeMoney(deliveryId, transactionId) =>
      log.info(s"received $msg ")
      if (!state.inFlightTransaction.contains(transactionId)) {
        sender() ! ConfirmMoneyUnfrozen(deliveryId)
      }
      else {
        persist(MoneyUnfrozen(transactionId)) { e =>
          updateState(e)
          sender() ! ConfirmMoneyUnfrozen(deliveryId)
        }
      }

    case "print" =>
      log.info(s"account: ${_account} state is ${state.toString}")
  }

}
