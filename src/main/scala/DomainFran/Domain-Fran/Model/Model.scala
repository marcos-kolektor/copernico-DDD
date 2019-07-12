package Model

object Model {

    case class AccountInfomation(
        nroCuenta : Long = 0,
        var saldo : Int = 0,
        state : Char = 'A'
    ) {
        // No funciona el copy o insmutabilidad. Inmutabilidad quitada para poder cambiar los datos
        def updateSaldo(newSaldo: Int) = saldo = newSaldo
        def updateState(_state: Char) = copy(state = _state)
    }

    case class AccountState(
       var accounts: Array[AccountInfomation] = Array[AccountInfomation]()
    ){ def addAccount(accountInfomation: AccountInfomation) = accounts = accounts :+ accountInfomation }

    sealed trait Command

        final case object Show extends Command
        final case class Create(accountState: AccountInfomation) extends Command
        final case class Update(accountState: AccountInfomation) extends Command
        final case class Accredit(nroAccount: Long, saldo: Int) extends Command
        final case class Debit(nroAccount: Long, saldo: Int) extends Command
        final case class UpdateState(nroAccount: Long, state: Char) extends Command
    
    sealed trait Event

        final case class AccountCreated(accountState: AccountInfomation) extends Event
        final case class AccountUpdated(accountState: AccountInfomation) extends Event
        final case class SaldoAccredited(accountState: AccountInfomation) extends Event
        final case class SaldoDebited(accountState: AccountInfomation) extends Event
        final case class StateUpdated(accountState: AccountInfomation) extends Event
 

}