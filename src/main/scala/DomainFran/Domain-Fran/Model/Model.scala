package Model

object Model {

    /* ?????????????????????
    def props(uuid: UUID, eventPublisher: ActorRef): Props = {
        Props(new Account(uuid, eventPublisher))
    } ?????????????????????
    */

    final case class AccountInfomation(
        nroCuenta : Long = 0,
        var saldo : Int = 0, 
        var state : Char = 'A'
    ) {  
        // No funciona el copy o insmutabilidad. Inmutabilidad quitada para poder cambiar los datos
        def updateSaldo(newSaldo: Int) = saldo = saldo + newSaldo
        def updateState(_state: Char) = copy(state = _state)
    }

    case class AccountState(
       var accounts: Array[AccountInfomation] = Array[AccountInfomation]()
    ){ def addAccount(accountInfomation: AccountInfomation) = accounts = accounts :+ accountInfomation }

    sealed trait Command

        final case object Show extends Command
        final case class Create(accountState: AccountInfomation) extends Command
        final case class Update(accountState: AccountInfomation) extends Command
        final case class UpdateSaldo(nroAccount: Long, saldo: Int) extends Command
        final case class UpdateState(nroAccount: Long, state: Char) extends Command
    
    sealed trait Event

        final case class AccountCreated(accountState: AccountInfomation) extends Event
        final case class AccountUpdated(accountState: AccountInfomation) extends Event
        final case class SaldoUpdated(accountState: AccountInfomation) extends Event
        final case class StateUpdated(accountState: AccountInfomation) extends Event
 

}