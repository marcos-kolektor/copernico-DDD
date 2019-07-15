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

    case Create(accountInfomation: AccountInfomation) => { _create(accountInfomation, state) }

    case Update(newInfoAccount: AccountInfomation) => { _update(newInfoAccount, state) }

    case Accredit(nroAccount: Long, saldo: Int) => { _accredit(nroAccount, saldo, state) }

    case Debit(nroAccount: Long, saldo: Int) => { _debit(nroAccount, saldo, state) }

    case UpdateState(nroAccount: Long, _state: Char) => { _updateState(nroAccount, _state, state) }

    case ShowOne(nroAccount: Long) => { _showOne(nroAccount, state) }

    case Show => {

      // TODO: Retun status data
      println(state.accounts.mkString("\n"))

    }

  }

}