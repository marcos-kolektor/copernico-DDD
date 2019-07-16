package Domain

import akka.actor.{ Actor }

import Model.Model._ 

class AccountActor(
      accounts: Array[AccountInfomation] = Array[AccountInfomation]()
) extends Actor with Controller {
 
  val state = AccountState(accounts)

  override def preStart(): Unit =
    println("Hola AccountActor") 

  override def postStop(): Unit =
    println("Chau AccountActor") 

  override def receive: Receive = {

    case Create(accountInfomation: AccountInfomation) => {
      val result: Boolean = _create(accountInfomation, state)
      sender() ! result
    }

    case Update(newInfoAccount: AccountInfomation) => {
      val result: Boolean = _update(newInfoAccount, state)
      sender() ! true
    }

    case Accredit(nroAccount: Long, saldo: Int) => {
      val response: Int  = _accredit(nroAccount, saldo, state)
      sender() ! response
    }

    case Debit(nroAccount: Long, saldo: Int) => {
      val response: Int  = _debit(nroAccount, saldo, state)
      sender() ! response
    }

    case UpdateState(nroAccount: Long, _state: Char) => { _updateState(nroAccount, _state, state) }

    case ShowOne(nroAccount: Long) => {
      val response: AccountInfomation  = _showOne(nroAccount, state)
      sender() ! response
    }

    case Show => { sender() ! state }

  }

}