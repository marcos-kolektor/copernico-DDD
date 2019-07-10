package Domain

import akka.actor.{ Actor }

import Model.Model._

class AccountActor(accounts: Array[AccountInfomation] = Array[AccountInfomation]()) extends Actor with Validations {
 
  val state = AccountState(accounts)

  override def preStart(): Unit =
    println("Hola AccountActor") 

  override def postStop(): Unit =
    println("Chau AccountActor") 

  override def receive: Receive = {

    case Create(accountInfomation: AccountInfomation) => {

      if (cheackAll(accountInfomation)) { 
        // TODO: Call persistence creation (?)
        state.addAccount(accountInfomation) 
      } else { 
        println("Informacion no validada: Create command") 
      }

    }

    case Update(newInfoAccount: AccountInfomation) => {

      if (cheackAll(newInfoAccount)) { 
        // TODO: Call persistence update (?)
        state.accounts.map { oldInfoAccount =>
          if (oldInfoAccount.nroCuenta.equals(newInfoAccount.nroCuenta)){
            oldInfoAccount.updateSaldo(newInfoAccount.saldo)
            oldInfoAccount.updateState(newInfoAccount.state)
          }
        } 
      } else { 
        println("Informacion no validada: Update command") 
      }

    }

    case UpdateSaldo(nroAccount: Long, saldo: Int) => {

      if (checkNumberAccount(nroAccount)) { 
        // TODO: Call persistence update (?)
        state.accounts.map { oldInfoAccount =>
          if (oldInfoAccount.nroCuenta.equals(nroAccount)){
            oldInfoAccount.updateSaldo(saldo)
          }
        } 
      } else { 
        println("Informacion no validada: UpdateSaldo command") 
      }

    }

    case UpdateState(nroAccount: Long, _state: Char) => {

      if (checkNumberAccount(nroAccount)) { 
        // TODO: Call persistence update (?)
        state.accounts.map { oldInfoAccount =>
          if (oldInfoAccount.nroCuenta.equals(nroAccount) && !oldInfoAccount.state.equals(_state)){
            oldInfoAccount.updateState(_state)
          }
        } 
      } else { 
        println("Informacion no validada: UpdateState command") 
      }

    }

    case Show => {
      println(state.accounts.mkString("\n"))
    }

  }

}