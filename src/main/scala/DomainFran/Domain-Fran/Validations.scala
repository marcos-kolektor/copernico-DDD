package Domain

import Model.Model.AccountInfomation

object Validations {
    
    def checkNumberAccount (nroAccount: Long): Boolean = {
        if(nroAccount.isNaN) { return false }
        if(nroAccount.toString.length <= 13) { return false }
        if(nroAccount.toString.length > 14) { return false }
        return true
    }

    def checkSaldoAccount (saldo: Int): Boolean = {
        if(saldo.isNaN) { return false }
        return true
    }

    def checkStateAccount (state: Char): Boolean = {
        if(state == 'A' || state == 'D') { return true }
        return false
    }

    def cheackAll (accountInfomation: AccountInfomation): Boolean = {
        if(
            checkNumberAccount(accountInfomation.nroCuenta) &&
            checkSaldoAccount(accountInfomation.saldo) &&
            checkStateAccount(accountInfomation.state)
        ){
            return true
        } else {
            return false
        }
    }

}


