package Domain

import Model.Model._
import Validations._

trait Controller {

    def _create(accountInfomation: AccountInfomation, state: AccountState) = {
      if (cheackAll(accountInfomation)) {
        // TODO: Call persistence creation (?)
        state.addAccount(accountInfomation)
      } else {
        println("Informacion no validada: Create command")
      }
    }

    def _update(newInfoAccount: AccountInfomation, state: AccountState) = {
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

    def _updateState(nroAccount: Long, _state: Char, state: AccountState) = {
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

    def _accredit(nroAccount: Long, saldo: Int, state: AccountState) = {

      if (checkNumberAccount(nroAccount)) {
        var newSaldo: Int = 0 
        // TODO: Call persistence update (?)
        state.accounts.map { oldInfoAccount =>
          if (oldInfoAccount.nroCuenta.equals(nroAccount)){
            newSaldo = saldo + oldInfoAccount.saldo
            oldInfoAccount.updateSaldo(newSaldo)
          }
        } 
      } else { 
        println("Informacion no validada: UpdateSaldo command") 
      }

    }

    def _debit(nroAccount: Long, saldo: Int, state: AccountState) = {
      if (checkNumberAccount(nroAccount)) {

        var newSaldo: Int = 0

        // TODO: Call persistence update (?)
        state.accounts.map { oldInfoAccount =>
          if (oldInfoAccount.nroCuenta.equals(nroAccount)){
            newSaldo = oldInfoAccount.saldo - saldo
            oldInfoAccount.updateSaldo(newSaldo)
          }
        }
      } else {
        println("Informacion no validada: UpdateSaldo command")
      }
    }

}