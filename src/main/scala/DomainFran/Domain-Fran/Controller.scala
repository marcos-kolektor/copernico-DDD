package Domain

import Model.Model._
import Validations._

import akka._

trait Controller {

    def _create(accountInfomation: AccountInfomation, state: AccountState): Boolean = {
      if (cheackAll(accountInfomation)) {
        // TODO: Call persistence creation (?)
        state.addAccount(accountInfomation)
        return true
      } else {
        println("Informacion no validada: Create command")
        return false
      }
    }

    def _update(newInfoAccount: AccountInfomation, state: AccountState): Boolean = {
      if (cheackAll(newInfoAccount)) {
        // TODO: Call persistence update (?)
        state.accounts.map { oldInfoAccount =>
          if (oldInfoAccount.nroCuenta.equals(newInfoAccount.nroCuenta)){
            oldInfoAccount.updateSaldo(newInfoAccount.saldo)
            oldInfoAccount.updateState(newInfoAccount.state)
            println("Update command successfully completed")
            return true
          }
        }
        return false
      } else {
        println("Informacion no validada: Update command")
        return false
      }
    }

    def _updateState(nroAccount: Long, _state: Char, state: AccountState) = {
      if (checkNumberAccount(nroAccount)) {
        // TODO: Call persistence update (?)
        state.accounts.map { oldInfoAccount =>
          if (oldInfoAccount.nroCuenta.equals(nroAccount) && !oldInfoAccount.state.equals(_state)){
            oldInfoAccount.updateState(_state)
            println("UpdateState command successfully completed")
          }
        }
      } else {
        println("Informacion no validada: UpdateState command")
      }
    }

    def _accredit(nroAccount: Long, saldo: Int, state: AccountState): Int  = {

      if (checkNumberAccount(nroAccount)) {
        var newSaldo: Int = 0 
        // TODO: Call persistence update (?)
        state.accounts.map { oldInfoAccount =>
          if (oldInfoAccount.nroCuenta.equals(nroAccount)){
            newSaldo = saldo + oldInfoAccount.saldo
            oldInfoAccount.updateSaldo(newSaldo)
            println("Accredit command successfully completed")
            return oldInfoAccount.saldo
          }
        }
        return 0
      } else { 
        println("Informacion no validada: Accredit command")
        return 0
      }

    }

    def _debit(nroAccount: Long, saldo: Int, state: AccountState): Int = {
      if (checkNumberAccount(nroAccount)) {

        var newSaldo: Int = 0

        // TODO: Call persistence update (?)
        state.accounts.map { oldInfoAccount =>
          if (oldInfoAccount.nroCuenta.equals(nroAccount)){
            newSaldo = oldInfoAccount.saldo - saldo
            oldInfoAccount.updateSaldo(newSaldo)
            println("Debit command successfully completed")
            return oldInfoAccount.saldo
          }
        }
        return 0
      } else {
        println("Informacion no validada: Debit command")
        return 0
      }
    }

    def _showOne(nroAccount: Long, state: AccountState): AccountInfomation  = {
    if (checkNumberAccount(nroAccount)) {
      state.accounts.map { oldInfoAccount =>
        if (oldInfoAccount.nroCuenta.equals(nroAccount)){
          return oldInfoAccount
        }
      }
      return new AccountInfomation()
    } else {
      println("Informacion no validada: ShowOne command")
      return new AccountInfomation()
    }
  }

}